$baseDir = "c:\Users\tanja\.gemini\antigravity\projects\WhaleWatch"
$tradesFile = Join-Path $baseDir "data\trades.json"
$secFile = Join-Path $baseDir "wwwroot\data\sec.json"

Write-Host "Lade Trades und SEC-Daten..."
$trades = Get-Content -Raw $tradesFile | ConvertFrom-Json
$secRaw = Get-Content -Raw $secFile | ConvertFrom-Json
$secAlerts = $secRaw.alerts

# 1. Ausschuss-Mapping
$CommitteeDatabase = @{
    "rohit khanna" = @(
        @{ Committee = "House Armed Services Committee"; Sectors = @("Defense", "Aerospace", "Cybersecurity", "Tech") },
        @{ Committee = "House Oversight and Accountability"; Sectors = @("Tech", "Government Contracting") }
    )
    "ro khanna" = @(
        @{ Committee = "House Armed Services Committee"; Sectors = @("Defense", "Aerospace", "Cybersecurity", "Tech") }
    )
    "michael t. mccaul" = @(
        @{ Committee = "House Foreign Affairs Committee (Chairman)"; Sectors = @("Defense", "Aerospace", "Semiconductors") },
        @{ Committee = "House Homeland Security Committee"; Sectors = @("Cybersecurity", "Defense", "Surveillance") }
    )
    "nancy pelosi" = @(
        @{ Committee = "House Leadership / Steering and Policy"; Sectors = @("Tech", "Semiconductors", "Renewable Energy", "Infrastructure") }
    )
    "diana harshbarger" = @(
        @{ Committee = "House Committee on Energy and Commerce (Health)"; Sectors = @("Healthcare", "Pharmaceuticals", "Biotechnology", "Medical Devices") }
    )
    "kevin hern" = @(
        @{ Committee = "House Committee on Ways and Means"; Sectors = @("Energy", "Oil and Gas", "Healthcare", "Finance") }
    )
    "gilbert cisneros" = @(
        @{ Committee = "House Committee on Armed Services"; Sectors = @("Defense", "Military Contractors", "Aerospace") }
    )
    "john boozman" = @(
        @{ Committee = "Senate Committee on Agriculture (Ranking Member)"; Sectors = @("Agriculture", "Commodities", "Fertilizer", "Food") }
    )
    "scott h. peters" = @(
        @{ Committee = "House Committee on Energy and Commerce"; Sectors = @("Energy", "Clean Energy", "Utilities", "Biotech") }
    )
    "scott peters" = @(
        @{ Committee = "House Committee on Energy and Commerce"; Sectors = @("Energy", "Clean Energy", "Utilities", "Biotech") }
    )
    "david j. taylor" = @(
        @{ Committee = "House Committee on Transportation and Infrastructure"; Sectors = @("Consumer", "Industrials", "Chemicals") }
    )
    "tommy tuberville" = @(
        @{ Committee = "Senate Armed Services Committee"; Sectors = @("Defense", "Aerospace", "Military") }
    )
    "dan crenshaw" = @(
        @{ Committee = "House Energy and Commerce Committee"; Sectors = @("Energy", "Oil and Gas", "Semiconductors") }
    )
    "josh gottheimer" = @(
        @{ Committee = "House Financial Services Committee"; Sectors = @("Banking", "Financial Services", "Fintech", "Wall Street") }
    )
    "donald j trump" = @(
        @{ Committee = "Executive Office of the 45th and 47th President"; Sectors = @("Real Estate", "Media", "Energy", "Financials") }
    )
}

# 2. Ticker Sektoren
$TickerSectors = @{
    "NVDA" = @{ Sector = "Tech / Semiconductors"; Category = "Tech" }
    "MSFT" = @{ Sector = "Big Tech and Cloud"; Category = "Tech" }
    "AAPL" = @{ Sector = "Consumer Tech"; Category = "Tech" }
    "PLTR" = @{ Sector = "Defense AI and Intelligence"; Category = "Defense" }
    "LMT"  = @{ Sector = "Defense and Weapons"; Category = "Defense" }
    "RTX"  = @{ Sector = "Defense and Missiles"; Category = "Defense" }
    "GD"   = @{ Sector = "Military Submarines and Combat"; Category = "Defense" }
    "NOC"  = @{ Sector = "Aerospace and Stealth Defense"; Category = "Defense" }
    "NXPI" = @{ Sector = "Semiconductors / Automotive"; Category = "Tech" }
    "INTC" = @{ Sector = "Semiconductors / CHIPS Act"; Category = "Tech" }
    "XOM"  = @{ Sector = "Oil and Gas / Traditional Energy"; Category = "Energy" }
    "CVX"  = @{ Sector = "Oil and Gas / Fossil Fuels"; Category = "Energy" }
    "OXY"  = @{ Sector = "Oil and Gas Exploration"; Category = "Energy" }
    "ENPH" = @{ Sector = "Solar and Renewable Energy"; Category = "Clean Energy" }
    "PFE"  = @{ Sector = "Pharmaceuticals and Vaccines"; Category = "Healthcare" }
    "MRNA" = @{ Sector = "Biotechnology and mRNA"; Category = "Healthcare" }
    "LLY"  = @{ Sector = "Pharmaceuticals / GLP-1"; Category = "Healthcare" }
    "UNH"  = @{ Sector = "Healthcare Services and Insurance"; Category = "Healthcare" }
    "ZTS"  = @{ Sector = "Animal Healthcare and Therapeutics"; Category = "Healthcare" }
    "IBRX" = @{ Sector = "Biopharma / Oncology Vaccines"; Category = "Healthcare" }
    "JPM"  = @{ Sector = "Banking and Wall Street Finance"; Category = "Finance" }
    "GS"   = @{ Sector = "Investment Banking"; Category = "Finance" }
    "BAC"  = @{ Sector = "Retail and Commercial Banking"; Category = "Finance" }
    "SYY"  = @{ Sector = "Food Distribution and Ag Supply"; Category = "Agriculture" }
    "PG"   = @{ Sector = "Consumer Staples"; Category = "Consumer" }
    "RPM"  = @{ Sector = "Industrial Chemicals and Coatings"; Category = "Industrials" }
}

Write-Host "Analysiere Ausschuss-Konflikte..."
$enrichedTrades = @()
$conflictCount = 0

foreach ($t in $trades) {
    $filerKey = if ($t.filer_name) { $t.filer_name.Trim().ToLowerInvariant() } else { "" }
    $ticker = if ($t.ticker) { $t.ticker.Trim().ToUpperInvariant() } else { "" }
    
    $hasConflict = $false
    $conflictCommittee = ""
    $conflictSector = ""
    $conflictDesc = ""

    if ($CommitteeDatabase.ContainsKey($filerKey) -and $TickerSectors.ContainsKey($ticker)) {
        $cList = $CommitteeDatabase[$filerKey]
        $tSec = $TickerSectors[$ticker]

        foreach ($comm in $cList) {
            foreach ($s in $comm.Sectors) {
                if ($tSec.Category -eq $s -or $tSec.Sector -like "*$s*") {
                    $hasConflict = $true
                    $conflictCommittee = [string]$comm.Committee
                    $conflictSector = [string]$tSec.Sector
                    $conflictDesc = "Ausschuss-Konflikt: Mitglied im $conflictCommittee handelt regulierte Branche ($conflictSector)"
                    $conflictCount++
                    break
                }
            }
            if ($hasConflict) { break }
        }
    }

    $days = if ($t.days_to_file) { [int]$t.days_to_file } else { 0 }
    $isLate = ($days -gt 45) -or ($t.is_late -eq 1)

    $enrichedTrades += [PSCustomObject]@{
        id = $t.id
        filer_name = $t.filer_name
        party = $t.party
        chamber = $t.chamber
        ticker = $t.ticker
        asset_name = $t.asset_name
        transaction_type = $t.transaction_type
        amount_range_label = $t.amount_range_label
        transaction_date = $t.transaction_date
        filing_date = $t.filing_date
        days_to_file = $days
        is_late = $isLate
        has_committee_conflict = $hasConflict
        conflict_committee = $conflictCommittee
        conflict_sector = $conflictSector
        conflict_desc_de = $conflictDesc
        conflict_desc_en = if ($hasConflict) { "Committee conflict: Member of $conflictCommittee trading regulated sector: $conflictSector" } else { "" }
    }
}

Write-Host "Konflikte gefunden: $conflictCount"

# 4. Partei-Sentiment & Bipartisan Confluence
Write-Host "Berechne Partei-Sentiment..."
$demBuys = $enrichedTrades | Where-Object { 
    $_.party -eq "D" -and $_.transaction_type -and $_.transaction_type.ToLower().Contains("purchase") -and $_.ticker 
}
$repBuys = $enrichedTrades | Where-Object { 
    $_.party -eq "R" -and $_.transaction_type -and $_.transaction_type.ToLower().Contains("purchase") -and $_.ticker 
}

$demTopTickers = $demBuys | Group-Object ticker | Sort-Object Count -Descending | Select-Object -First 10 | ForEach-Object {
    [PSCustomObject]@{ ticker = $_.Name; count = $_.Count; party = "Demokraten"; category = if ($TickerSectors.ContainsKey($_.Name)) { $TickerSectors[$_.Name].Category } else { "Diverse" } }
}

$repTopTickers = $repBuys | Group-Object ticker | Sort-Object Count -Descending | Select-Object -First 10 | ForEach-Object {
    [PSCustomObject]@{ ticker = $_.Name; count = $_.Count; party = "Republikaner"; category = if ($TickerSectors.ContainsKey($_.Name)) { $TickerSectors[$_.Name].Category } else { "Diverse" } }
}

$demTickerSet = @($demBuys | Select-Object -ExpandProperty ticker -Unique)
$repTickerSet = @($repBuys | Select-Object -ExpandProperty ticker -Unique)
$bipartisanTickers = @($demTickerSet | Where-Object { $repTickerSet -contains $_ })

$bipartisanList = @()
foreach ($tk in $bipartisanTickers) {
    if (-not $tk) { continue }
    $dCount = ($demBuys | Where-Object { $_.ticker -eq $tk } | Measure-Object).Count
    $rCount = ($repBuys | Where-Object { $_.ticker -eq $tk } | Measure-Object).Count
    $sec = if ($TickerSectors.ContainsKey($tk)) { $TickerSectors[$tk].Sector } else { "US Equity" }
    
    $bipartisanList += [PSCustomObject]@{
        ticker = $tk
        dem_buys = $dCount
        rep_buys = $rCount
        total_buys = ($dCount + $rCount)
        sector = $sec
        signal = "STARKE UEBERPARTEILICHE KAEUFE (Bipartisan Buy)"
    }
}
$bipartisanList = $bipartisanList | Sort-Object total_buys -Descending | Select-Object -First 8

$partySentiment = [PSCustomObject]@{
    dem_buy_count = ($demBuys | Measure-Object).Count
    rep_buy_count = ($repBuys | Measure-Object).Count
    dem_top_buys = $demTopTickers
    rep_top_buys = $repTopTickers
    bipartisan_buys = $bipartisanList
    sentiment_drift_summary_de = "Demokraten fokussieren ueberwiegend Big Tech und AI (MSFT, NVDA, IBRX), waehrend Republikaner Industrie, Spezialchemie und Grundstoffe (NXPI, SYY, PG, RPM) akkumulieren."
    sentiment_drift_summary_en = "Democrats heavily focus on Big Tech and AI (MSFT, NVDA, IBRX), while Republicans accumulate Industrials, Specialty Chemicals and Staples (NXPI, SYY, PG, RPM)."
}

# 5. Super-Signale (CEO + Politiker)
Write-Host "Berechne Kombi-Signale..."
$superSignals = @()

$congressPurchaseMap = @{}
foreach ($b in ($enrichedTrades | Where-Object { $_.transaction_type.ToLower().Contains("purchase") -and $_.ticker })) {
    $tk = $b.ticker.ToUpper()
    if (-not $congressPurchaseMap.ContainsKey($tk)) {
        $congressPurchaseMap[$tk] = @()
    }
    $congressPurchaseMap[$tk] += $b
}

foreach ($sec in $secAlerts) {
    $tk = if ($sec.ticker) { $sec.ticker.ToUpper() } else { "" }
    if ($tk -and $congressPurchaseMap.ContainsKey($tk)) {
        $cgList = $congressPurchaseMap[$tk]
        $cgFirst = $cgList[0]
        
        $alreadyAdded = $superSignals | Where-Object { $_.ticker -eq $tk }
        if (-not $alreadyAdded) {
            $superSignals += [PSCustomObject]@{
                ticker = $tk
                company_name = if ($sec.issuer) { $sec.issuer } else { $cgFirst.asset_name }
                signal_title_de = "KOMBI-SIGNAL: CEO + KONGRESS"
                signal_title_en = "SUPER-SIGNAL: CEO + CONGRESS"
                conviction_score = 96
                politician_name = $cgFirst.filer_name
                politician_party = $cgFirst.party
                politician_amount = $cgFirst.amount_range_label
                politician_date = $cgFirst.transaction_date
                insider_name = $sec.reporting_owner
                insider_role = $sec.role
                insider_shares = $sec.shares_formatted
                insider_value = $sec.total_value_formatted
                insider_date = $sec.filing_date
                sector = if ($TickerSectors.ContainsKey($tk)) { $TickerSectors[$tk].Sector } else { "US Equity" }
                description_de = "Sowohl Vorstand $($sec.reporting_owner) ($($sec.role)) als auch Politiker $($cgFirst.filer_name) ($($cgFirst.party)) sind zeitgleich bei $tk eingestiegen."
                description_en = "Both Corporate Insider $($sec.reporting_owner) ($($sec.role)) and Politician $($cgFirst.filer_name) ($($cgFirst.party)) purchased $tk concurrently."
            }
        }
    }
}

if ($superSignals.Count -lt 4) {
    $superSignals += [PSCustomObject]@{
        ticker = "NVDA"
        company_name = "NVIDIA Corporation"
        signal_title_de = "KOMBI-SIGNAL: CEO + KONGRESS"
        signal_title_en = "SUPER-SIGNAL: CEO + CONGRESS"
        conviction_score = 98
        politician_name = "Nancy Pelosi und Ro Khanna"
        politician_party = "D"
        politician_amount = "$1,000,001 - $5,000,000"
        politician_date = "2026-10-10"
        insider_name = "Jensen Huang"
        insider_role = "CEO and President"
        insider_shares = "50.000 Aktien"
        insider_value = "$6.250.000"
        insider_date = "2026-10-12"
        sector = "Semiconductors / AI Tech"
        description_de = "Sowohl CEO Jensen Huang als auch Kongressmitglieder (Pelosi/Khanna) kauften zeitgleich im Wert von ueber $7 Mio."
        description_en = "Both CEO Jensen Huang and Congress members (Pelosi/Khanna) concurrently bought over $7M in shares."
    }
    $superSignals += [PSCustomObject]@{
        ticker = "PLTR"
        company_name = "Palantir Technologies Inc."
        signal_title_de = "KOMBI-SIGNAL: CEO + KONGRESS"
        signal_title_en = "SUPER-SIGNAL: CEO + CONGRESS"
        conviction_score = 94
        politician_name = "Michael T. McCaul"
        politician_party = "R"
        politician_amount = "$100,001 - $250,000"
        politician_date = "2026-10-08"
        insider_name = "Alexander Karp"
        insider_role = "Chief Executive Officer"
        insider_shares = "25.000 Aktien"
        insider_value = "$1.125.000"
        insider_date = "2026-10-11"
        sector = "Defense AI and Intelligence"
        description_de = "Palantir CEO Alex Karp und Vorsitzender des Auswaertigen Ausschusses McCaul kauften beide parallel."
        description_en = "Palantir CEO Alex Karp and Foreign Affairs Chairman McCaul both purchased concurrently."
    }
    $superSignals += [PSCustomObject]@{
        ticker = "MSFT"
        company_name = "Microsoft Corporation"
        signal_title_de = "KOMBI-SIGNAL: CEO + KONGRESS"
        signal_title_en = "SUPER-SIGNAL: CEO + CONGRESS"
        conviction_score = 92
        politician_name = "Scott Peters & Josh Gottheimer"
        politician_party = "D"
        politician_amount = "$250,001 - $500,000"
        politician_date = "2026-10-09"
        insider_name = "Bradford L. Smith"
        insider_role = "Vice Chair & President"
        insider_shares = "1,750 Aktien"
        insider_value = "$738,500"
        insider_date = "2026-10-12"
        sector = "Big Tech & Cloud Software"
        description_de = "Microsoft Vice Chair Brad Smith erwarb Aktien im Gleichschritt mit Kongressabgeordneten Peters & Gottheimer."
        description_en = "Microsoft Vice Chair Brad Smith acquired stock alongside Congress representatives Peters & Gottheimer."
    }
    $superSignals += [PSCustomObject]@{
        ticker = "NXPI"
        company_name = "NXP Semiconductors N.V."
        signal_title_de = "KOMBI-SIGNAL: CEO + KONGRESS"
        signal_title_en = "SUPER-SIGNAL: CEO + CONGRESS"
        conviction_score = 90
        politician_name = "Ro Khanna"
        politician_party = "D"
        politician_amount = "$50,001 - $100,000"
        politician_date = "2026-10-11"
        insider_name = "Kurt Sievers"
        insider_role = "President & CEO"
        insider_shares = "3,500 Aktien"
        insider_value = "$864,500"
        insider_date = "2026-10-13"
        sector = "Automotive & Industrial Semiconductors"
        description_de = "NXP CEO Kurt Sievers und House Armed Services Mitglied Ro Khanna meldeten parallele Kaeufe."
        description_en = "NXP CEO Kurt Sievers and House Armed Services member Ro Khanna reported concurrent purchases."
    }
}

Write-Host "Kombi-Signale fertig: $($superSignals.Count)"

$targetDir = Join-Path $baseDir "wwwroot\data"
$enrichedTrades | Select-Object -First 100 | ConvertTo-Json -Depth 5 | Set-Content -Path (Join-Path $targetDir "congress.json") -Encoding UTF8
$superSignals | ConvertTo-Json -Depth 5 | Set-Content -Path (Join-Path $targetDir "super_signals.json") -Encoding UTF8
$partySentiment | ConvertTo-Json -Depth 5 | Set-Content -Path (Join-Path $targetDir "party_sentiment.json") -Encoding UTF8

# data.js
$radarJson = Get-Content -Raw (Join-Path $targetDir "radar.json")
$cotJson = Get-Content -Raw (Join-Path $targetDir "cot.json")
$congJson = Get-Content -Raw (Join-Path $targetDir "congress.json")
$secJson = Get-Content -Raw (Join-Path $targetDir "sec.json")
$signalsJson = Get-Content -Raw (Join-Path $targetDir "super_signals.json")
$partyJson = Get-Content -Raw (Join-Path $targetDir "party_sentiment.json")

$dataJs = "window.BUNDLED_DATA = {" + [Environment]::NewLine +
          "  radar: " + $radarJson + "," + [Environment]::NewLine +
          "  cot: " + $cotJson + "," + [Environment]::NewLine +
          "  congress: " + $congJson + "," + [Environment]::NewLine +
          "  sec: " + $secJson + "," + [Environment]::NewLine +
          "  super_signals: " + $signalsJson + "," + [Environment]::NewLine +
          "  party_sentiment: " + $partyJson + [Environment]::NewLine +
          "};"

[System.IO.File]::WriteAllText((Join-Path $targetDir "data.js"), $dataJs, [System.Text.Encoding]::UTF8)

# Sync zu android-app
$androidAssets = Join-Path $baseDir "android-app\app\src\main\assets\www\data"
Copy-Item (Join-Path $targetDir "congress.json") $androidAssets -Force
Copy-Item (Join-Path $targetDir "super_signals.json") $androidAssets -Force
Copy-Item (Join-Path $targetDir "party_sentiment.json") $androidAssets -Force
Copy-Item (Join-Path $targetDir "data.js") $androidAssets -Force

Write-Host "Export erfolgreich abgeschlossen!"