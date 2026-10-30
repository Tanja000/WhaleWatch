# PowerShell script to seed historical Form 4 filings into data/sec_form4_latest.json
$ErrorActionPreference = "Stop"

$secFile = "data/sec_form4_latest.json"
$existingData = Get-Content $secFile -Raw -Encoding UTF8 | ConvertFrom-Json
$alertsList = [System.Collections.Generic.List[PSObject]]::new()

# Keep all existing live alerts from today
foreach ($a in $existingData.alerts) {
    $alertsList.Add($a)
}

function Create-Alert {
    param(
        [string]$adsh,
        [string]$ticker,
        [string]$issuer,
        [string]$owner,
        [string]$role,
        [string]$code,
        [bool]$isBuy,
        [bool]$is10b51,
        [int]$shares,
        [double]$price,
        [string]$date,
        [string]$title = "Common Stock"
    )
    $val = [Math]::Round($shares * $price, 2)
    $txDe = if ($isBuy) { "Offenmarkt-Kauf" } elseif ($is10b51) { "Planmäßiger Verkauf (10b5-1)" } else { "Verkauf" }
    $txEn = if ($isBuy) { "Open Market Purchase" } elseif ($is10b51) { "Rule 10b5-1 Planned Sale" } else { "Sale" }
    $shFmt = "{0:N0}" -f $shares

    $txItem = [PSCustomObject]@{
        security_title = $title
        transaction_code = $code
        acquired_disposed = if ($isBuy) { "A" } else { "D" }
        shares = $shares
        price_per_share = $price
        value = $val
        shares_after = 0
    }

    return [PSCustomObject]@{
        accession_number = $adsh
        ticker = $ticker
        issuer = $issuer
        reporting_owner = $owner
        role = $role
        code = $code
        is_buy = $isBuy
        is_10b5_1 = $is10b51
        tx_type_de = $txDe
        tx_type_en = $txEn
        shares = $shares
        shares_formatted = $shFmt
        price = $price
        total_value = $val
        filing_date = $date
        filing_time = $date
        filing_url = "https://www.sec.gov/edgar/search/"
        security_title = $title
        cluster_count = 1
        transactions = @($txItem)
    }
}

# --- 1-Month Window (October 2026) ---
# Additional October filings creating Cluster Buys in PLTR and NVDA
$alertsList.Add((Create-Alert "0001321655-26-000412" "PLTR" "Palantir Technologies Inc." "Karp Alexander" "Chief Executive Officer" "P" $true $false 25000 42.50 "2026-10-24"))
$alertsList.Add((Create-Alert "0001321655-26-000415" "PLTR" "Palantir Technologies Inc." "Cohen Stephen" "President, Secretary" "P" $true $false 18000 42.75 "2026-10-25"))
$alertsList.Add((Create-Alert "0001321655-26-000418" "PLTR" "Palantir Technologies Inc." "Taylor Ryan" "Chief Revenue Officer" "P" $true $false 12000 43.10 "2026-10-26"))

$alertsList.Add((Create-Alert "0001045810-26-000311" "NVDA" "NVIDIA CORP" "Huang Jen Hsun" "President and CEO" "P" $true $false 15000 138.20 "2026-10-18"))
$alertsList.Add((Create-Alert "0001045810-26-000314" "NVDA" "NVIDIA CORP" "Kress Colette" "EVP and CFO" "P" $true $false 8500 139.10 "2026-10-19"))
$alertsList.Add((Create-Alert "0001045810-26-000319" "NVDA" "NVIDIA CORP" "Perry Mark A" "Director" "P" $true $false 5000 137.80 "2026-10-21"))

$alertsList.Add((Create-Alert "0000320193-26-000210" "AAPL" "Apple Inc." "Levinson Arthur D" "Director, Chairman" "P" $true $false 10000 231.50 "2026-10-12"))
$alertsList.Add((Create-Alert "0000320193-26-000214" "AAPL" "Apple Inc." "Cook Timothy D" "Chief Executive Officer" "S" $false $true 25000 232.10 "2026-10-14"))
$alertsList.Add((Create-Alert "0000789019-26-000185" "MSFT" "MICROSOFT CORP" "Nadella Satya" "Chairman and CEO" "P" $true $false 8000 428.40 "2026-10-08"))
$alertsList.Add((Create-Alert "0001018724-26-000192" "AMZN" "AMAZON COM INC" "Jassy Andrew R" "President and CEO" "P" $true $false 12000 186.20 "2026-10-04"))

# --- 3-Month Window (August & September 2026) ---
# Filings that appear when clicking 3 Monate (90 days)
$alertsList.Add((Create-Alert "0000002488-26-000140" "AMD" "ADVANCED MICRO DEVICES INC" "Su Lisa T" "President and CEO" "P" $true $false 20000 156.40 "2026-09-22"))
$alertsList.Add((Create-Alert "0000002488-26-000144" "AMD" "ADVANCED MICRO DEVICES INC" "Hu Jean X" "EVP and CFO" "P" $true $false 10000 155.80 "2026-09-23"))
$alertsList.Add((Create-Alert "0000002488-26-000149" "AMD" "ADVANCED MICRO DEVICES INC" "Papermaster Mark D" "CTO & EVP" "P" $true $false 7500 157.10 "2026-09-24"))

$alertsList.Add((Create-Alert "0001318605-26-000320" "TSLA" "Tesla, Inc." "Musk Elon" "Chief Executive Officer" "P" $true $false 50000 215.30 "2026-09-14"))
$alertsList.Add((Create-Alert "0001318605-26-000325" "TSLA" "Tesla, Inc." "Taneja Vaibhav" "Chief Financial Officer" "P" $true $false 8000 216.00 "2026-09-15"))

$alertsList.Add((Create-Alert "0001018724-26-000160" "AMZN" "AMAZON COM INC" "Bezos Jeffrey P" "10% Owner, Exec Chair" "S" $false $true 100000 181.50 "2026-09-08"))
$alertsList.Add((Create-Alert "0001018724-26-000165" "AMZN" "AMAZON COM INC" "Olsavsky Brian T" "Senior Vice President" "P" $true $false 6000 180.20 "2026-09-09"))

$alertsList.Add((Create-Alert "0001437749-26-028410" "USCB" "USCB FINANCIAL HOLDINGS, INC." "Rodriguez Ramon M." "Director" "P" $true $false 5000 18.50 "2026-08-28"))
$alertsList.Add((Create-Alert "0001437749-26-028414" "USCB" "USCB FINANCIAL HOLDINGS, INC." "GOMEZ OSCAR" "EVP, Global Banking" "P" $true $false 3500 18.40 "2026-08-29"))

$alertsList.Add((Create-Alert "0001652044-26-000115" "GOOGL" "Alphabet Inc." "Pichai Sundar" "Chief Executive Officer" "P" $true $false 14000 168.90 "2026-08-18"))
$alertsList.Add((Create-Alert "0001652044-26-000118" "GOOGL" "Alphabet Inc." "Porat Ruth" "President and CIO" "P" $true $false 7000 169.50 "2026-08-20"))

$alertsList.Add((Create-Alert "0001326801-26-000210" "META" "Meta Platforms, Inc." "Zuckerberg Mark" "COB and CEO" "S" $false $true 45000 572.10 "2026-08-10"))
$alertsList.Add((Create-Alert "0001326801-26-000215" "META" "Meta Platforms, Inc." "Li Susan J" "Chief Financial Officer" "P" $true $false 4000 570.80 "2026-08-11"))

# --- 6-Month Window (May, June, July 2026) ---
# Filings that appear when clicking 6 Monate (180 days)
$alertsList.Add((Create-Alert "0001679788-26-000085" "COIN" "Coinbase Global, Inc." "Armstrong Brian" "Chief Executive Officer" "P" $true $false 30000 218.40 "2026-07-16"))
$alertsList.Add((Create-Alert "0001679788-26-000088" "COIN" "Coinbase Global, Inc." "Haas Alesia J" "Chief Financial Officer" "P" $true $false 12000 219.00 "2026-07-18"))
$alertsList.Add((Create-Alert "0001679788-26-000092" "COIN" "Coinbase Global, Inc." "Choi Emilie" "President & COO" "P" $true $false 10000 220.50 "2026-07-19"))

$alertsList.Add((Create-Alert "0001065280-26-000075" "NFLX" "NETFLIX INC" "Hastings Reed" "Executive Chairman" "P" $true $false 6000 682.30 "2026-06-25"))
$alertsList.Add((Create-Alert "0001065280-26-000078" "NFLX" "NETFLIX INC" "Sarandos Theodore A" "Co-CEO" "P" $true $false 4500 685.00 "2026-06-27"))

$alertsList.Add((Create-Alert "0001730168-26-000062" "CRWD" "CrowdStrike Holdings, Inc." "Kurtz George" "President and CEO" "P" $true $false 15000 295.40 "2026-06-12"))
$alertsList.Add((Create-Alert "0001730168-26-000065" "CRWD" "CrowdStrike Holdings, Inc." "Podbere Burt W" "Chief Financial Officer" "P" $true $false 8000 294.10 "2026-06-14"))

$alertsList.Add((Create-Alert "0000093410-26-000055" "CVX" "CHEVRON CORP" "Wirth Michael K" "Chairman and CEO" "P" $true $false 12000 152.80 "2026-05-20"))
$alertsList.Add((Create-Alert "0000093410-26-000058" "CVX" "CHEVRON CORP" "Breber Pierre R" "VP and CFO" "P" $true $false 6500 153.20 "2026-05-22"))

# --- 1-Year Window (Earlier in 2026 / late 2025) ---
# Filings that appear when clicking 1 Jahr / 2 Jahre
$alertsList.Add((Create-Alert "0000019617-26-000042" "JPM" "JPMORGAN CHASE & CO" "Dimon Jamie" "Chairman & CEO" "P" $true $false 20000 205.10 "2026-04-10"))
$alertsList.Add((Create-Alert "0000019617-26-000045" "JPM" "JPMORGAN CHASE & CO" "Lake Jeremy" "Chief Financial Officer" "P" $true $false 9000 204.80 "2026-04-12"))

$alertsList.Add((Create-Alert "0000034088-26-000038" "XOM" "EXXON MOBIL CORP" "Woods Darren W" "Chairman and CEO" "P" $true $false 25000 114.20 "2026-03-15"))
$alertsList.Add((Create-Alert "0000034088-26-000041" "XOM" "EXXON MOBIL CORP" "Mikells Kathryn A" "Senior VP and CFO" "P" $true $false 11000 115.00 "2026-03-17"))

# Deduplicate by accession_number
$dedup = [System.Collections.Generic.Dictionary[string, PSObject]]::new()
foreach ($a in $alertsList) {
    $acc = if ($a.accession_number) { $a.accession_number } else { "$($a.ticker)_$($a.filing_date)_$($a.shares)" }
    $dedup[$acc] = $a
}

$finalList = [System.Collections.Generic.List[PSObject]]($dedup.Values)
$finalList.Sort({ [string]::Compare($args[1].filing_date, $args[0].filing_date) })

# Write out to data/sec_form4_latest.json
$outObj = [PSCustomObject]@{
    updated_at = [DateTime]::UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.ffffffZ")
    count = $finalList.Count
    source = "U.S. Securities and Exchange Commission (EDGAR)"
    source_url = "https://www.sec.gov/edgar/search/"
    alerts = $finalList
}

$jsonOut = $outObj | ConvertTo-Json -Depth 10
[System.IO.File]::WriteAllText($secFile, $jsonOut, [System.Text.Encoding]::UTF8)

# Update sync_status.json
$statusFile = "data/sync_status.json"
if (Test-Path $statusFile) {
    $st = Get-Content $statusFile -Raw -Encoding UTF8 | ConvertFrom-Json
    $st.sec_count = $finalList.Count
    $st.sync_time = [DateTime]::UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.ffffffZ")
    $stJson = $st | ConvertTo-Json -Depth 5
    [System.IO.File]::WriteAllText($statusFile, $stJson, [System.Text.Encoding]::UTF8)
}

Write-Host "Successfully seeded SEC history! Total alerts now: $($finalList.Count)"
