#!/usr/bin/env python3
"""
WhaleRadar SEC Form 4 Data Sync Script
Uses the official SEC EDGAR Form 4 Feed to fetch, parse, and aggregate insider transactions.
"""

import os
import sys
import json
import time
import re
import urllib.request
import urllib.error
import xml.etree.ElementTree as ET
from datetime import datetime

USER_AGENT = "WhaleRadar datawhaleradar@proton.me"
OUTPUT_FILE = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "data", "sec_form4_latest.json")

def http_get(url, timeout=12):
    req = urllib.request.Request(
        url,
        headers={
            "User-Agent": USER_AGENT,
            "Accept": "application/atom+xml, application/xml, text/xml, text/html, */*"
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

    # Reporting Owner & Role
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
        "tx_type_de": "Offenmarkt-Kauf" if is_buy else ("Planmäßiger Verkauf (10b5-1)" if is_10b5_1 else "Verkauf"),
        "tx_type_en": "Open Market Purchase" if is_buy else ("Rule 10b5-1 Planned Sale" if is_10b5_1 else "Sale"),
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

    # Query official SEC EDGAR Current Form 4 Atom Feed
    atom_url = "https://www.sec.gov/cgi-bin/browse-edgar?action=getcurrent&type=4&company=&dateb=&owner=only&start=0&count=50&output=atom"
    print(f"Fetching official SEC Form 4 Feed: {atom_url}")

    entries = []
    try:
        atom_bytes = http_get(atom_url, timeout=15)
        feed_root = ET.fromstring(atom_bytes)
        ns = {"atom": "http://www.w3.org/2005/Atom"}
        for entry in feed_root.findall("atom:entry", ns):
            link_el = entry.find("atom:link", ns)
            updated_el = entry.find("atom:updated", ns)
            if link_el is not None and "href" in link_el.attrib:
                href = link_el.attrib["href"]
                f_date = (updated_el.text or "")[:10] if updated_el is not None else datetime.utcnow().strftime("%Y-%m-%d")
                entries.append((href, f_date))
    except Exception as e:
        print(f"Warning: Could not fetch Atom feed: {e}")

    print(f"Found {len(entries)} recent Form 4 filings in feed.")

    parsed_alerts = []
    seen_accessions = set()

    for idx, (index_url, f_date) in enumerate(entries[:35], 1):
        try:
            m = re.search(r"data/([0-9]+)/([0-9]+)/", index_url)
            if not m:
                continue
            cik, acc_raw = m.group(1), m.group(2)
            base_folder = f"https://www.sec.gov/Archives/edgar/data/{cik}/{acc_raw}/"
            
            index_html = http_get(index_url, timeout=10).decode("utf-8", errors="ignore")
            xml_match = re.search(r'href="([^"]+?\.xml)"', index_html)
            if not xml_match:
                continue
            xml_path = xml_match.group(1).lstrip("/")
            xml_url = base_folder + xml_path.split("/")[-1]

            adsh = f"{acc_raw[:10]}-{acc_raw[10:12]}-{acc_raw[12:]}" if len(acc_raw) == 18 else acc_raw
            if adsh in seen_accessions:
                continue
            seen_accessions.add(adsh)

            time.sleep(0.12)  # SEC polite rate limit
            xml_bytes = http_get(xml_url, timeout=10)
            alert = parse_form4_xml(xml_bytes, adsh, f_date, index_url)
            if alert:
                parsed_alerts.append(alert)
                print(f"[{idx}] {alert['ticker']}: {alert['reporting_owner']} ({alert['role']}) - {alert['shares_formatted']} shs @ ${alert['price']}")
        except Exception as err:
            pass

    print(f"Parsed {len(parsed_alerts)} Form 4 alerts from live feed.")

    # Load existing alerts to preserve history
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

    final_alerts = list(combined_map.values())
    final_alerts.sort(key=lambda x: x.get("filing_date", ""), reverse=True)
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
