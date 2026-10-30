#!/usr/bin/env python3
"""
WhaleRadar SEC Form 4 Data Sync Script
Runs as a GitHub Action on schedule to fetch recent Form 4 filings from SEC EDGAR,
parse the XML filings, and write a consolidated sec_form4_latest.json file.
"""

import os
import sys
import json
import time
import urllib.request
import urllib.error
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta

USER_AGENT = "WhaleRadarBot/1.0 (https://github.com/Tanja000/WhaleWatch; contact@whaleradar.app)"
OUTPUT_FILE = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "data", "sec_form4_latest.json")

def http_get(url, timeout=10):
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": USER_AGENT,
            "Accept": "application/json, application/xml, text/xml, */*"
        }
    )
    with urllib.request.urlopen(req, timeout=timeout) as resp:
        return resp.read()

def parse_form4_xml(xml_bytes, adsh, f_date, index_url):
    try:
        root = ET.fromstring(xml_bytes)
    except Exception:
        return None

    # Ticker & Issuer
    issuer = root.find(".//issuer")
    if issuer is None:
        return None
    ticker_el = issuer.find("issuerTradingSymbol")
    ticker = (ticker_el.text or "").strip().upper() if ticker_el is not None else ""
    issuer_name_el = issuer.find("issuerName")
    issuer_name = (issuer_name_el.text or "").strip() if issuer_name_el is not None else ""

    if not ticker or ticker == "NONE" or len(ticker) > 6 or "." in ticker:
        return None

    # Reporting Owner
    owner_el = root.find(".//reportingOwner")
    owner_name = ""
    role = "Insider"
    if owner_el is not None:
        rpt_name_el = owner_el.find(".//rptOwnerName")
        if rpt_name_el is not None and rpt_name_el.text:
            owner_name = rpt_name_el.text.strip()
        rel_el = owner_el.find(".//reportingOwnerRelationship")
        if rel_el is not None:
            is_dir = (rel_el.findtext("isDirector") or "").strip() in ("1", "true")
            is_off = (rel_el.findtext("isOfficer") or "").strip() in ("1", "true")
            is_ten = (rel_el.findtext("isTenPercentOwner") or "").strip() in ("1", "true")
            off_title = (rel_el.findtext("officerTitle") or "").strip()
            if is_dir and is_off:
                role = f"Director, {off_title}" if off_title else "Director & Officer"
            elif is_off:
                role = off_title if off_title else "Officer"
            elif is_dir:
                role = "Director"
            elif is_ten:
                role = "10% Owner"

    # Non-derivative Transactions
    transactions = []
    total_shares = 0
    total_value = 0.0
    is_buy = True
    is_10b5_1 = False
    tx_code = "P"
    security_title = "Common Stock"

    for tx in root.findall(".//nonDerivativeTransaction"):
        sec_title_el = tx.find(".//securityTitle/value")
        if sec_title_el is not None and sec_title_el.text:
            security_title = sec_title_el.text.strip()

        coding = tx.find(".//transactionCoding")
        code = "P"
        if coding is not None:
            code = (coding.findtext("transactionCode") or "P").strip().upper()
            rule10b = (coding.findtext("equitySwapInvolved") or "0").strip()
            if rule10b == "1":
                is_10b5_1 = True

        amounts = tx.find(".//transactionAmounts")
        shares = 0
        price = 0.0
        ad_code = "A"
        if amounts is not None:
            try:
                shares = int(float(amounts.findtext(".//transactionShares/value") or "0"))
            except ValueError:
                shares = 0
            try:
                price = float(amounts.findtext(".//transactionPricePerShare/value") or "0.0")
            except ValueError:
                price = 0.0
            ad_code = (amounts.findtext(".//transactionAcquiredDisposedCode/value") or "A").strip().upper()

        val = shares * price
        total_shares += shares
        total_value += val
        tx_code = code
        is_buy = (code == "P" or ad_code == "A") and (code != "S" and code != "F" and code != "D")

        transactions.append({
            "security_title": security_title,
            "transaction_code": code,
            "acquired_disposed": ad_code,
            "shares": shares,
            "price_per_share": round(price, 2),
            "value": round(val, 2),
            "shares_after": 0
        })

    if not transactions and total_shares == 0:
        return None

    avg_price = (total_value / total_shares) if total_shares > 0 else 0.0

    return {
        "accession_number": adsh,
        "ticker": ticker,
        "issuer": issuer_name,
        "reporting_owner": owner_name or "Reporting Owner",
        "role": role,
        "code": tx_code,
        "is_buy": is_buy,
        "is_10b5_1": is_10b5_1,
        "tx_type_de": "Offenmarkt-Kauf" if is_buy else "Verkauf",
        "tx_type_en": "Open Market Purchase" if is_buy else "Sale",
        "shares": total_shares,
        "shares_formatted": f"{total_shares:,}",
        "price": round(avg_price, 2),
        "total_value": round(total_value, 2),
        "filing_date": f_date,
        "filing_time": f_date,
        "filing_url": index_url or "https://www.sec.gov/edgar/search/",
        "security_title": security_title,
        "cluster_count": 1,
        "transactions": transactions
    }

def main():
    print("=" * 60)
    print("WhaleRadar SEC Form 4 Sync")
    print(f"Timestamp: {datetime.utcnow().isoformat()}Z")
    print("=" * 60)

    # We fetch Form 4 filings for the last 14 days
    today = datetime.utcnow().date()
    start_date = today - timedelta(days=14)
    start_dt_str = start_date.strftime("%Y-%m-%d")
    end_dt_str = today.strftime("%Y-%m-%d")

    search_url = f"https://efts.sec.gov/LATEST/search-index?forms=4&startdt={start_dt_str}&enddt={end_dt_str}"
    print(f"Querying SEC search index: {start_dt_str} to {end_dt_str}")

    try:
        raw_index = http_get(search_url, timeout=12)
        index_json = json.loads(raw_index.decode("utf-8"))
    except Exception as e:
        print(f"Error querying SEC EDGAR search index: {e}")
        sys.exit(1)

    hits = index_json.get("hits", {}).get("hits", [])
    print(f"Found {len(hits)} matching Form 4 hits in SEC index.")

    candidates = []
    company_counts = {}
    max_filings = 80
    max_per_company = 4

    for hit in hits:
        if len(candidates) >= max_filings:
            break
        hit_id = hit.get("_id", "")
        parts = hit_id.split(":")
        if len(parts) < 2:
            continue
        adsh = parts[0].strip()
        xml_file = parts[1].strip()

        src = hit.get("_source", {})
        ciks = src.get("ciks", [])
        if not ciks:
            continue
        cik_raw = ciks[1] if len(ciks) > 1 else ciks[0]
        cik_int = cik_raw.lstrip("0")
        if not cik_int:
            continue

        if company_counts.get(cik_raw, 0) >= max_per_company:
            continue
        company_counts[cik_raw] = company_counts.get(cik_raw, 0) + 1

        adsh_clean = adsh.replace("-", "")
        xml_url = f"https://www.sec.gov/Archives/edgar/data/{cik_int}/{adsh_clean}/{xml_file}"
        index_url = f"https://www.sec.gov/Archives/edgar/data/{cik_int}/{adsh_clean}/{adsh}-index.htm"
        f_date = src.get("file_date", end_dt_str)[:10]

        candidates.append((adsh, xml_url, index_url, f_date))

    print(f"Selected {len(candidates)} filings to download and parse.")

    parsed_alerts = []
    # Strict polite rate limiting: 0.15s pause = ~6 requests per second (well below SEC limit of 10/s)
    for idx, (adsh, xml_url, index_url, f_date) in enumerate(candidates, 1):
        try:
            xml_bytes = http_get(xml_url, timeout=8)
            alert = parse_form4_xml(xml_bytes, adsh, f_date, index_url)
            if alert:
                parsed_alerts.append(alert)
                print(f"[{idx}/{len(candidates)}] Parsed: {alert['ticker']} ({alert['reporting_owner']}) - {alert['shares_formatted']} shares")
        except Exception as err:
            print(f"[{idx}/{len(candidates)}] Skip {adsh}: {err}")
        time.sleep(0.15)

    print(f"Successfully parsed {len(parsed_alerts)} Form 4 alerts.")

    # Load existing alerts to merge and preserve history
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    existing_alerts = []
    if os.path.exists(OUTPUT_FILE):
        try:
            with open(OUTPUT_FILE, "r", encoding="utf-8") as f:
                old_data = json.load(f)
                if isinstance(old_data, list):
                    existing_alerts = old_data
                elif isinstance(old_data, dict) and "alerts" in old_data:
                    existing_alerts = old_data["alerts"]
        except Exception as e:
            print(f"Notice: Could not read existing file: {e}")

    # Merge & Deduplicate by accession_number
    combined_map = {}
    for a in existing_alerts:
        acc = a.get("accession_number") or f"{a.get('ticker')}_{a.get('filing_date')}_{a.get('shares')}"
        combined_map[acc] = a
    for a in parsed_alerts:
        acc = a.get("accession_number") or f"{a.get('ticker')}_{a.get('filing_date')}_{a.get('shares')}"
        combined_map[acc] = a

    # Sort descending by filing_date
    final_alerts = list(combined_map.values())
    final_alerts.sort(key=lambda x: x.get("filing_date", ""), reverse=True)

    # Keep latest 250 entries
    final_alerts = final_alerts[:250]

    output_payload = {
        "updated_at": datetime.utcnow().isoformat() + "Z",
        "count": len(final_alerts),
        "source": "U.S. Securities and Exchange Commission (EDGAR)",
        "source_url": "https://www.sec.gov/edgar/search/",
        "alerts": final_alerts
    }

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        json.dump(output_payload, f, indent=2, ensure_ascii=False)

    print(f"Saved {len(final_alerts)} alerts to {OUTPUT_FILE}")
    print("Done!")

if __name__ == "__main__":
    main()
