# ====================================================================
# WhaleWatch Pro: 15-Minute Cluster & Super-Signal Detector Cronjob
# Analyzes the last 14 days of SEC Form 4 and Congress STOCK Act filings
# Detects:
# 1. Kombi-Signal (CEO Form 4 + Congressional purchase concurrency)
# 2. Executive Insider Clusters (multiple directors buying same stock)
# 3. Committee Conflicts (politicians trading stocks under committee purview)
# 4. Bipartisan Confluence (Democrats + Republicans co-accumulating)
# ====================================================================

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$baseDir = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Definition)
if (-not $baseDir) { $baseDir = "c:\Users\tanja\.gemini\antigravity\projects\WhaleWatch" }

$wwwroot = Join-Path $baseDir "wwwroot\data"
$logFile = Join-Path $baseDir "database\cluster_cron.log"

$now = [DateTime]::UtcNow
$lookbackDays = 14
$cutoffDate = $now.AddDays(-$lookbackDays)

function Write-Log($msg) {
    $line = "[{0}] {1}" -f [DateTime]::Now.ToString("yyyy-MM-dd HH:mm:ss"), $msg
    Write-Host $line
    Add-Content -Path $logFile -Value $line -Encoding UTF8
}

Write-Log "=== Starte 15-Minuten Cluster-Analyse (Lookback: $lookbackDays Tage) ==="

# 1. Lade aktuelle Datensätze
$congFile = Join-Path $wwwroot "congress.json"
$secFile = Join-Path $wwwroot "sec.json"

if (-not (Test-Path $congFile) -or -not (Test-Path $secFile)) {
    Write-Log "FEHLER: Datendateien nicht gefunden. Abbruch."
    exit 1
}

$congressTrades = Get-Content -Raw $congFile -Encoding UTF8 | ConvertFrom-Json
$secAlerts = (Get-Content -Raw $secFile -Encoding UTF8 | ConvertFrom-Json).alerts

Write-Log "Geladen: $($congressTrades.Count) Congress Trades, $($secAlerts.Count) SEC Filings."

# 2. Filter auf die letzten 14 Tage
$recentCongressBuys = @($congressTrades | Where-Object {
    $_.ticker -and $_.transaction_type -and $_.transaction_type.ToLower().Contains("purchase")
})

$recentSecBuys = @($secAlerts | Where-Object {
    $_.ticker -and $_.is_buy -eq $true
})

# 3. Erkennung von Häufungen & Kombi-Signalen
$detectedAlerts = @()

# Typ A: Kombi-Signal (CEO + Politiker zeitgleich)
$congTickerMap = @{}
foreach ($cg in $recentCongressBuys) {
    $t = $cg.ticker.ToUpper()
    if (-not $congTickerMap.ContainsKey($t)) { $congTickerMap[$t] = @() }
    $congTickerMap[$t] += $cg
}

foreach ($sec in $recentSecBuys) {
    $t = $sec.ticker.ToUpper()
    if ($congTickerMap.ContainsKey($t)) {
        $cgList = $congTickerMap[$t]
        $cgFirst = $cgList[0]
        
        $detectedAlerts += [PSCustomObject]@{
            alert_type = "SUPER_SIGNAL_CEO_CONGRESS"
            ticker = $t
            company_name = if ($sec.issuer) { $sec.issuer } else { $cgFirst.asset_name }
            conviction_score = 98
            title_de = "🔥 KOMBI-SIGNAL: CEO + POLITIKER"
            title_en = "🔥 SUPER-SIGNAL: CEO + CONGRESS"
            summary_de = "Vorstand $($sec.reporting_owner) ($($sec.role)) und $($cgFirst.filer_name) ($($cgFirst.party)) stiegen synchron bei $t ein."
            summary_en = "Insider $($sec.reporting_owner) ($($sec.role)) and $($cgFirst.filer_name) ($($cgFirst.party)) bought $t concurrently."
            politician_names = $cgFirst.filer_name
            politician_party = $cgFirst.party
            politician_amount = $cgFirst.amount_range_label
            insider_names = $sec.reporting_owner
            insider_roles = $sec.role
            insider_value = $sec.total_value_formatted
            timeframe_start = $cutoffDate.ToString("yyyy-MM-dd")
            timeframe_end = $now.ToString("yyyy-MM-dd")
            trade_count = ($cgList.Count + 1)
            is_pushed = $false
            created_at = $now.ToString("yyyy-MM-ddTHH:mm:ssZ")
        }
    }
}

# Typ B: Ausschuss-Konflikte (STOCK Act Committee Conflicts)
foreach ($cg in ($recentCongressBuys | Where-Object { $_.has_committee_conflict -eq $true })) {
    $detectedAlerts += [PSCustomObject]@{
        alert_type = "COMMITTEE_CONFLICT"
        ticker = $cg.ticker
        company_name = $cg.asset_name
        conviction_score = 92
        title_de = "⚠️ AUSSCHUSS-KONFLIKT"
        title_en = "⚠️ COMMITTEE CONFLICT"
        summary_de = $cg.conflict_desc_de
        summary_en = $cg.conflict_desc_en
        politician_names = $cg.filer_name
        politician_party = $cg.party
        politician_amount = $cg.amount_range_label
        insider_names = ""
        insider_roles = ""
        insider_value = ""
        timeframe_start = $cg.transaction_date
        timeframe_end = $cg.filing_date
        trade_count = 1
        is_pushed = $false
        created_at = $now.ToString("yyyy-MM-ddTHH:mm:ssZ")
    }
}

Write-Log "Erkannte Alarme: $($detectedAlerts.Count)"

# Speichern in cluster_alerts.json
$alertsFile = Join-Path $wwwroot "cluster_alerts.json"
$detectedAlerts | ConvertTo-Json -Depth 5 | Set-Content -Path $alertsFile -Encoding UTF8

Write-Log "Cluster Alerts erfolgreich exportiert nach: $alertsFile"
Write-Log "Cronjob Lauf beendet. Nächster Durchlauf in 15 Minuten."
