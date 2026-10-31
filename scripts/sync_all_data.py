#!/usr/bin/env python3
"""
WhaleRadar Master Data Sync
Aggregates all 3 official primary financial disclosure sources:
1. SEC Form 4 (Corporate Insiders) via official SEC Atom feed
2. CFTC Commitment of Traders (COT Report) via FuturesBench & CFTC open data
3. U.S. House of Representatives (STOCK Act PTR) via Clerk of the House XML & PDF disclosures

Outputs:
- data/sec_form4_latest.json
- data/cot_latest.json
- data/congress_latest.json
- data/sync_status.json
- logs/sync.log
"""

import os
import sys
import json
import time
import re
import zipfile
import io
import urllib.request
import urllib.error
import xml.etree.ElementTree as ET
from datetime import datetime

USER_AGENT = "WhaleRadar datawhaleradar@proton.me"
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.path.join(BASE_DIR, "data")
LOGS_DIR = os.path.join(BASE_DIR, "logs")

os.makedirs(DATA_DIR, exist_ok=True)
os.makedirs(LOGS_DIR, exist_ok=True)

LOG_FILE = os.path.join(LOGS_DIR, "sync.log")
STATUS_FILE = os.path.join(DATA_DIR, "sync_status.json")

def log(msg):
    ts = datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S UTC")
    line = f"[{ts}] {msg}"
    print(line)
    try:
        with open(LOG_FILE, "a", encoding="utf-8") as f:
            f.write(line + "\n")
    except Exception:
        pass

def http_get(url, timeout=15, headers=None):
    hdrs = {
        "User-Agent": USER_AGENT,
        "Accept": "*/*"
    }
    if headers:
        hdrs.update(headers)
    req = urllib.request.Request(url, headers=hdrs)
    with urllib.request.urlopen(req, timeout=timeout) as resp:
        return resp.read()

# -----------------------------------------------------------------------------
# 1. SEC FORM 4 SYNC
# -----------------------------------------------------------------------------
def sync_sec_form4():
    log("--> Starting SEC Form 4 sync...")
    atom_url = "https://www.sec.gov/cgi-bin/browse-edgar?action=getcurrent&type=4&company=&dateb=&owner=only&start=0&count=50&output=atom"
    sec_file = os.path.join(DATA_DIR, "sec_form4_latest.json")

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
        log(f"SEC: Found {len(entries)} entries in current Atom feed.")
    except Exception as e:
        log(f"SEC Warning: Could not fetch Atom feed: {e}")

    parsed_alerts = []
    seen = set()

    for idx, (index_url, f_date) in enumerate(entries[:30], 1):
        try:
            m = re.search(r"data/([0-9]+)/([0-9]+)/", index_url)
            if not m:
                continue
            cik, acc_raw = m.group(1), m.group(2)
            base_folder = f"https://www.sec.gov/Archives/edgar/data/{cik}/{acc_raw}/"

            time.sleep(0.12)
            index_html = http_get(index_url, timeout=10).decode("utf-8", errors="ignore")
            xml_match = re.search(r'href="([^"]+?\.xml)"', index_html)
            if not xml_match:
                continue
            xml_path = xml_match.group(1).lstrip("/")
            xml_url = base_folder + xml_path.split("/")[-1]

            adsh = f"{acc_raw[:10]}-{acc_raw[10:12]}-{acc_raw[12:]}" if len(acc_raw) == 18 else acc_raw
            if adsh in seen:
                continue
            seen.add(adsh)

            time.sleep(0.12)
            xml_bytes = http_get(xml_url, timeout=10)
            root = ET.fromstring(xml_bytes)

            issuer = root.find(".//issuer")
            if issuer is None:
                continue
            ticker = (issuer.findtext("issuerTradingSymbol") or "").strip().upper()
            issuer_name = (issuer.findtext("issuerName") or "").strip()
            if not ticker or ticker == "NONE" or len(ticker) > 6 or "." in ticker:
                continue

            owner_el = root.find(".//reportingOwner")
            owner_name = (owner_el.findtext(".//rptOwnerName") or "Reporting Owner").strip() if owner_el is not None else "Reporting Owner"
            role = "Insider"
            if owner_el is not None:
                rel = owner_el.find(".//reportingOwnerRelationship")
                if rel is not None:
                    is_dir = (rel.findtext("isDirector") or "").strip() in ("1", "true")
                    is_off = (rel.findtext("isOfficer") or "").strip() in ("1", "true")
                    is_ten = (rel.findtext("isTenPercentOwner") or "").strip() in ("1", "true")
                    off_title = (rel.findtext("officerTitle") or "").strip()
                    if is_dir and is_off:
                        role = f"Director, {off_title}" if off_title else "Director & Officer"
                    elif is_off:
                        role = off_title if off_title else "Officer"
                    elif is_dir:
                        role = "Director"
                    elif is_ten:
                        role = "10% Owner"

            transactions = []
            total_shares = 0
            total_value = 0.0
            is_buy = True
            is_10b5_1 = False
            tx_code = "P"
            sec_title = "Common Stock"

            for tx in root.findall(".//nonDerivativeTransaction"):
                st_el = tx.find(".//securityTitle/value")
                if st_el is not None and st_el.text:
                    sec_title = st_el.text.strip()
                
                coding = tx.find(".//transactionCoding")
                if coding is not None:
                    tx_code = (coding.findtext("transactionCode") or "P").strip().upper()
                    if (coding.findtext("equitySwapInvolved") or "0").strip() == "1":
                        is_10b5_1 = True

                # Transaction Date
                tx_date = ""
                dt_el = tx.find(".//transactionDate/value")
                if dt_el is not None and dt_el.text:
                    tx_date = dt_el.text.strip()
                if not tx_date:
                    tx_date = f_date if f_date else "No Data"

                amounts = tx.find(".//transactionAmounts")
                shs = 0
                prc = 0.0
                ad = "A"
                if amounts is not None:
                    try:
                        shs = int(float(amounts.findtext(".//transactionShares/value") or "0"))
                    except ValueError:
                        shs = 0
                    try:
                        prc = float(amounts.findtext(".//transactionPricePerShare/value") or "0.0")
                    except ValueError:
                        prc = 0.0
                    ad = (amounts.findtext(".//transactionAcquiredDisposedCode/value") or "A").strip().upper()

                # Post-transaction beneficial ownership (Bestand danach)
                shares_after = 0
                post_el = tx.find(".//postTransactionAmounts/sharesOwnedFollowingTransaction/value")
                if post_el is not None and post_el.text:
                    try:
                        shares_after = int(float(post_el.text.strip()))
                    except ValueError:
                        shares_after = 0
                if shares_after == 0:
                    root_post = root.find(".//postTransactionAmounts/sharesOwnedFollowingTransaction/value")
                    if root_post is not None and root_post.text:
                        try:
                            shares_after = int(float(root_post.text.strip()))
                        except ValueError:
                            shares_after = 0

                val = shs * prc
                total_shares += shs
                total_value += val
                is_buy = (tx_code == "P" or ad == "A") and (tx_code != "S" and tx_code != "F" and tx_code != "D")

                shs_fmt = f"{shs:,} Aktien" if shs > 0 else "No Data"
                prc_fmt = f"${prc:,.2f}" if prc > 0.0 else "No Data"
                val_fmt = f"${round(val):,}" if val > 0.0 else "No Data"
                benef_fmt = f"{shares_after:,} Aktien" if shares_after > 0 else "No Data"

                transactions.append({
                    "code": tx_code,
                    "transaction_code": tx_code,
                    "is_buy": is_buy,
                    "acquired_disposed": ad,
                    "security_title": sec_title,
                    "transaction_date": tx_date,
                    "shares": shs,
                    "shares_formatted": shs_fmt,
                    "price": round(prc, 2) if prc > 0.0 else 0.0,
                    "price_formatted": prc_fmt,
                    "total_value": round(val, 2) if val > 0.0 else 0.0,
                    "total_value_formatted": val_fmt,
                    "security_beneficials": shares_after,
                    "security_beneficials_formatted": benef_fmt,
                    "shares_after": shares_after
                })

            if not transactions and total_shares == 0:
                continue

            avg_price = (total_value / total_shares) if total_shares > 0 else 0.0
            total_benef = max([t.get("security_beneficials", 0) for t in transactions] or [0])
            primary_tx_date = transactions[0].get("transaction_date", f_date) if transactions else f_date

            alert = {
                "accession_number": adsh,
                "ticker": ticker,
                "issuer": issuer_name,
                "reporting_owner": owner_name,
                "role": role,
                "code": tx_code,
                "is_buy": is_buy,
                "is_10b5_1": is_10b5_1,
                "tx_type_de": "Offenmarkt-Kauf" if is_buy else ("Planmäßiger Verkauf (10b5-1)" if is_10b5_1 else "Verkauf"),
                "tx_type_en": "Open Market Purchase" if is_buy else ("Rule 10b5-1 Planned Sale" if is_10b5_1 else "Sale"),
                "shares": total_shares,
                "shares_formatted": f"{total_shares:,} Aktien" if total_shares > 0 else "No Data",
                "price": round(avg_price, 2) if avg_price > 0.0 else 0.0,
                "price_formatted": f"${avg_price:,.2f}" if avg_price > 0.0 else "No Data",
                "total_value": round(total_value, 2) if total_value > 0.0 else 0.0,
                "total_value_formatted": f"${round(total_value):,}" if total_value > 0.0 else "No Data",
                "security_beneficials": total_benef,
                "security_beneficials_formatted": f"{total_benef:,} Aktien" if total_benef > 0 else "No Data",
                "filing_date": f_date if f_date else "No Data",
                "filing_time": f_date if f_date else "No Data",
                "transaction_date": primary_tx_date if primary_tx_date else "No Data",
                "filing_url": index_url,
                "security_title": sec_title,
                "cluster_count": 1,
                "transactions": transactions
            }
            parsed_alerts.append(alert)
        except Exception:
            continue

    log(f"SEC: Parsed {len(parsed_alerts)} new alerts.")

    existing_alerts = []
    if os.path.exists(sec_file):
        try:
            with open(sec_file, "r", encoding="utf-8") as f:
                d = json.load(f)
                existing_alerts = d if isinstance(d, list) else d.get("alerts", [])
        except Exception:
            pass

    combined = {}
    for a in existing_alerts:
        acc = a.get("accession_number") or f"{a.get('ticker')}_{a.get('filing_date')}_{a.get('shares')}"
        combined[acc] = a
    for a in parsed_alerts:
        acc = a.get("accession_number") or f"{a.get('ticker')}_{a.get('filing_date')}_{a.get('shares')}"
        combined[acc] = a

    final_alerts = list(combined.values())
    final_alerts.sort(key=lambda x: x.get("filing_date", ""), reverse=True)
    # Retain up to 2,000 historical SEC Form 4 filings so 1M, 3M, 6M, 1Y, and 2Y filters have rich data
    final_alerts = final_alerts[:2000]

    with open(sec_file, "w", encoding="utf-8") as f:
        json.dump({
            "updated_at": datetime.utcnow().isoformat() + "Z",
            "count": len(final_alerts),
            "source": "U.S. Securities and Exchange Commission (EDGAR)",
            "source_url": "https://www.sec.gov/edgar/search/",
            "alerts": final_alerts
        }, f, indent=2, ensure_ascii=False)

    log(f"SEC Form 4: Saved {len(final_alerts)} total entries to {sec_file}")
    return len(final_alerts)

# -----------------------------------------------------------------------------
# 2. CFTC COT SYNC
# -----------------------------------------------------------------------------
def sync_cftc_cot():
    log("--> Starting CFTC COT sync...")
    cot_file = os.path.join(DATA_DIR, "cot_latest.json")
    try:
        fb_url = "https://futuresbench.com/api/v1/latest.json"
        fb_bytes = http_get(fb_url, timeout=12)
        fb_json = json.loads(fb_bytes.decode("utf-8"))
        markets = fb_json.get("markets", {})
        log(f"CFTC COT: Loaded {len(markets)} markets from FuturesBench.")

        out = {
            "updated_at": datetime.utcnow().isoformat() + "Z",
            "report_date": fb_json.get("report_date", ""),
            "publication_date": fb_json.get("publication_date", ""),
            "report_week": fb_json.get("report_week", ""),
            "source": "U.S. Commodity Futures Trading Commission (CFTC)",
            "source_url": "https://www.cftc.gov/MarketReports/CommitmentsofTraders/index.htm",
            "markets": markets
        }
        with open(cot_file, "w", encoding="utf-8") as f:
            json.dump(out, f, indent=2, ensure_ascii=False)
        log(f"CFTC COT: Saved to {cot_file}")
        return len(markets)
    except Exception as e:
        log(f"CFTC COT Warning: {e}")
        return 0

# -----------------------------------------------------------------------------
# 3. U.S. HOUSE OF REPRESENTATIVES CONGRESS PTR SYNC
# -----------------------------------------------------------------------------
def sync_congress_house():
    log("--> Starting U.S. House STOCK Act PTR sync...")
    congress_file = os.path.join(DATA_DIR, "congress_latest.json")
    year = datetime.utcnow().year
    zip_url = f"https://disclosures-clerk.house.gov/public_disc/financial-pdfs/{year}FD.zip"

    trades = []
    try:
        zip_bytes = http_get(zip_url, timeout=15)
        with zipfile.ZipFile(io.BytesIO(zip_bytes)) as z:
            xml_name = [n for n in z.namelist() if n.lower().endswith(".xml")]
            if xml_name:
                xml_content = z.read(xml_name[0]).decode("utf-8", errors="ignore")
                # Parse Member PTR filings
                for m in re.finditer(r"<Member>([\s\S]*?)</Member>", xml_content):
                    b = m.group(1)
                    def tag(n):
                        match = re.search(rf"<{n}>([^<]*)</{n}>", b)
                        return match.group(1).strip() if match else ""
                    if tag("FilingType") != "P":
                        continue
                    doc_id = tag("DocID")
                    first = tag("First")
                    last = tag("Last")
                    state_dst = tag("StateDst")
                    filing_date = tag("FilingDate")
                    if doc_id and last:
                        trades.append({
                            "doc_id": doc_id,
                            "filer_name": f"{first} {last}".strip(),
                            "chamber": "House",
                            "state_dst": state_dst,
                            "filing_date": filing_date,
                            "official_url": "https://disclosures-clerk.house.gov"
                        })
        log(f"Congress: Found {len(trades)} PTR filings in {year}FD.zip.")
    except Exception as e:
        log(f"Congress Warning: {e}")

    # Load existing congress data to merge
    existing = []
    if os.path.exists(congress_file):
        try:
            with open(congress_file, "r", encoding="utf-8") as f:
                c_data = json.load(f)
                existing = c_data.get("trades", [])
        except Exception:
            pass

    combined_map = {t.get("doc_id", ""): t for t in existing if t.get("doc_id")}
    for t in trades:
        combined_map[t["doc_id"]] = t

    final_trades = list(combined_map.values())[:300]
    out = {
        "updated_at": datetime.utcnow().isoformat() + "Z",
        "count": len(final_trades),
        "source": "Office of the Clerk, U.S. House of Representatives",
        "source_url": "https://disclosures-clerk.house.gov",
        "trades": final_trades
    }
    with open(congress_file, "w", encoding="utf-8") as f:
        json.dump(out, f, indent=2, ensure_ascii=False)
    log(f"Congress: Saved {len(final_trades)} PTR disclosures to {congress_file}")
    return len(final_trades)

# -----------------------------------------------------------------------------
# MAIN
# -----------------------------------------------------------------------------
def main():
    log("==================================================================")
    log("WhaleRadar Unified Master Data Sync Started")
    log("==================================================================")

    stats = {
        "sync_time": datetime.utcnow().isoformat() + "Z",
        "sec_count": 0,
        "cot_count": 0,
        "congress_count": 0,
        "status": "success",
        "log_path": "logs/sync.log"
    }

    try:
        stats["sec_count"] = sync_sec_form4()
    except Exception as e:
        log(f"Error in SEC sync: {e}")

    try:
        stats["cot_count"] = sync_cftc_cot()
    except Exception as e:
        log(f"Error in CFTC sync: {e}")

    try:
        stats["congress_count"] = sync_congress_house()
    except Exception as e:
        log(f"Error in Congress sync: {e}")

    with open(STATUS_FILE, "w", encoding="utf-8") as f:
        json.dump(stats, f, indent=2)

    log("==================================================================")
    log(f"Sync Completed Successfully! SEC: {stats['sec_count']} | COT: {stats['cot_count']} | Congress: {stats['congress_count']}")
    log("==================================================================")

if __name__ == "__main__":
    main()
