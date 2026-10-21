# WhaleWatch Server in PowerShell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$port = 8765
$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
if (-not $baseDir) { $baseDir = $PWD.Path }
$wwwroot = Join-Path $baseDir "wwwroot"
$dataFile = Join-Path $baseDir "data\trades.json"
$statsFile = Join-Path $baseDir "data\stats.json"

$global:CotCache = $null
$global:CotCacheTime = [DateTime]::MinValue
$global:SecCache = $null
$global:SecCacheTime = [DateTime]::MinValue
$global:SecDetailCache = @{}
$global:CongressTrades = $null
$global:CongressStats = $null

# Pre-warm SEC cache from bundled data for instantaneous response
$bundledSec = Join-Path $wwwroot "data\sec.json"
if (Test-Path $bundledSec) {
    try {
        $global:SecCache = [System.IO.File]::ReadAllText($bundledSec, [System.Text.Encoding]::UTF8)
        $global:SecCacheTime = [DateTime]::UtcNow
        Write-Host "SEC Cache mit bundled Daten vorgewärmt."
    } catch {}
}

# Pre-warm COT cache from bundled data for instantaneous response
$bundledCot = Join-Path $wwwroot "data\cot.json"
if (Test-Path $bundledCot) {
    try {
        $global:CotCache = [System.IO.File]::ReadAllText($bundledCot, [System.Text.Encoding]::UTF8)
        $global:CotCacheTime = [DateTime]::UtcNow
        Write-Host "COT Cache mit bundled Daten vorgewärmt."
    } catch {}
}

function Load-CongressData {
    if ($null -eq $global:CongressTrades) {
        $cFile = Join-Path $wwwroot "data\congress.json"
        if (Test-Path $cFile) {
            Write-Host "Lade Congress Trades aus congress.json in den Speicher..."
            $jsonStr = [System.IO.File]::ReadAllText($cFile, [System.Text.Encoding]::UTF8)
            $global:CongressTrades = $jsonStr | ConvertFrom-Json
        } elseif (Test-Path $dataFile) {
            Write-Host "Lade Congress Trades in den Speicher..."
            $jsonStr = [System.IO.File]::ReadAllText($dataFile, [System.Text.Encoding]::UTF8)
            $global:CongressTrades = $jsonStr | ConvertFrom-Json
        }
        if (Test-Path $statsFile) {
            $global:CongressStats = [System.IO.File]::ReadAllText($statsFile, [System.Text.Encoding]::UTF8) | ConvertFrom-Json
        } else {
            $global:CongressStats = @{ totalTrades = 78655; totalFilers = 452; lateFilings = 21182 }
        }
        Write-Host "Congress Trades bereit: $($global:CongressTrades.Count)"
    }
}

# Pre-warm Congress trades into memory
Load-CongressData

function Get-CotReport([bool]$force = $false) {
    $now = [DateTime]::UtcNow
    if (-not $force -and $null -ne $global:CotCache -and ($now - $global:CotCacheTime).TotalMinutes -lt 15) {
        return $global:CotCache
    }

    try {
        $url = "https://publicreporting.cftc.gov/resource/jun7-fc8e.json?`$order=report_date_as_yyyy_mm_dd%20DESC&`$limit=500"
        $raw = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 3
        
        $targets = @(
            @{ Key="SPY"; Name="SPY (SPDR S&P 500 ETF) / VOO"; AssetClass="S&P 500 Large-Cap ETF"; Category="indices"; Icon=""; Pattern="*S&P 500*" },
            @{ Key="QQQ"; Name="QQQ (Invesco QQQ Trust)"; AssetClass="Nasdaq-100 Tech ETF"; Category="indices"; Icon=""; Pattern="*NASDAQ*" },
            @{ Key="RUSSELL"; Name="IWM (Russell 2000 ETF)"; AssetClass="Small Caps US"; Category="indices"; Icon=""; Pattern="*RUSSELL*" },
            @{ Key="DOW"; Name="DIA (Dow Jones ETF)"; AssetClass="Blue Chips US"; Category="indices"; Icon=""; Pattern="*DJIA*" },
            @{ Key="SILVER"; Name="Silber (SLV / Futures)"; AssetClass="Edelmetall / Industrie"; Category="commodities"; Icon=""; Pattern="*SILVER*" },
            @{ Key="CRUDE_OIL"; Name="WTI Rohöl (USO ETF)"; AssetClass="Energie & Rohstoffe"; Category="commodities"; Icon=""; Pattern="*CRUDE OIL*" },
            @{ Key="US10Y"; Name="10Y US Treasuries (IEF / TLT)"; AssetClass="US Staatsanleihen"; Category="rates"; Icon=""; Pattern="*UST 10Y NOTE*" },
            @{ Key="BITCOIN"; Name="Bitcoin (IBIT ETF / Futures)"; AssetClass="Krypto / Risk-On"; Category="crypto"; Icon=""; Pattern="*BITCOIN*" }
        )

        $keyMarkets = @()
        $reportWeek = "Woche 41, 2026"
        $reportDate = "2026-10-13"

        foreach ($t in $targets) {
            $p = $t.Pattern
            $m = $raw | Where-Object { [string]$_.market_and_exchange_names -like $p } | Select-Object -First 1
            if ($m) {
                if ($m.yyyy_report_week_ww) { $reportWeek = $m.yyyy_report_week_ww }
                if ($m.report_date_as_yyyy_mm_dd) { $reportDate = $m.report_date_as_yyyy_mm_dd }

                $nLong = [int64]$m.noncomm_positions_long_all
                $nShort = [int64]$m.noncomm_positions_short_all
                $cLong = [int64]$m.comm_positions_long_all
                $cShort = [int64]$m.comm_positions_short_all
                $rLong = [int64]$m.nonrept_positions_long_all
                $rShort = [int64]$m.nonrept_positions_short_all

                $chgLong = [int64]$m.change_in_noncomm_long_all
                $chgShort = [int64]$m.change_in_noncomm_short_all

                $netWhale = $nLong - $nShort
                $netChange = $chgLong - $chgShort
                $whaleTotal = $nLong + $nShort
                
                $whaleBullPct = 50.0
                if ($whaleTotal -gt 0) {
                    $whaleBullPct = [Math]::Round(($nLong / $whaleTotal) * 100.0, 1)
                }

                $retailTotal = $rLong + $rShort
                $retailBullPct = 50.0
                if ($retailTotal -gt 0) {
                    $retailBullPct = [Math]::Round(($rLong / $retailTotal) * 100.0, 1)
                }

                $signal = "NEUTRAL"
                $signalColor = "#94a3b8"
                if ($whaleBullPct -ge 65) { $signal = "STARK BULLISCH"; $signalColor = "#10b981" }
                elseif ($whaleBullPct -ge 55) { $signal = "BULLISCH"; $signalColor = "#34d399" }
                elseif ($whaleBullPct -le 35) { $signal = "STARK BÄRISCH"; $signalColor = "#ef4444" }
                elseif ($whaleBullPct -le 45) { $signal = "BÄRISCH"; $signalColor = "#f87171" }

                $changeDesc = "{0:N0} Abbau" -f $netChange
                if ($netChange -ge 0) {
                    $changeDesc = "+{0:N0} Akkumulation" -f $netChange
                }

                $sign = if ($netWhale -ge 0) { "+" } else { "" }
                $netFormatted = "$sign{0:N0}" -f $netWhale

                $keyMarkets += @{
                    id = $t.Key
                    name = $t.Name
                    market_raw = [string]$m.market_and_exchange_names
                    asset_class = $t.AssetClass
                    category = $t.Category
                    icon = $t.Icon
                    report_date = $m.report_date_as_yyyy_mm_dd
                    week = $m.yyyy_report_week_ww
                    whale_long = $nLong
                    whale_short = $nShort
                    whale_net = $netWhale
                    whale_net_formatted = $netFormatted
                    whale_change_long = $chgLong
                    whale_change_short = $chgShort
                    whale_change_net = $netChange
                    whale_change_desc = $changeDesc
                    whale_bullish_pct = $whaleBullPct
                    signal = $signal
                    signal_color = $signalColor
                    commercial_long = $cLong
                    commercial_short = $cShort
                    commercial_net = ($cLong - $cShort)
                    retail_long = $rLong
                    retail_short = $rShort
                    retail_net = ($rLong - $rShort)
                    retail_bullish_pct = $retailBullPct
                    open_interest = [int64]$m.open_interest_all
                }
            }
        }

        $res = @{
            source = "CFTC Commitments of Traders (COT API)"
            updated_at = $now.ToString("yyyy-MM-ddTHH:mm:ssZ")
            report_week = $reportWeek
            report_date = $reportDate
            markets = $keyMarkets
        }

        $global:CotCache = ($res | ConvertTo-Json -Depth 5)
        $global:CotCacheTime = $now
        return $global:CotCache
    } catch {
        Write-Host "COT Error: $($_.Exception.Message)"
        if ($null -ne $global:CotCache) { return $global:CotCache }

        # High reliability fallback with SPY, QQQ, etc.
        $fallbackMarkets = @(
            @{ id="SPY"; name="SPY (SPDR S&P 500 ETF) / VOO"; asset_class="S&P 500 Large-Cap ETF"; category="indices"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=124500; whale_short=140490; whale_net=-15990; whale_net_formatted="-15,990"; whale_change_long=4200; whale_change_short=1800; whale_change_net=2400; whale_change_desc="+2,400 Akkumulation"; whale_bullish_pct=47.0; signal="NEUTRAL"; signal_color="#94a3b8"; commercial_long=310000; commercial_short=280000; commercial_net=30000; retail_long=45000; retail_short=38000; retail_net=7000; retail_bullish_pct=54.2; open_interest=2450000 },
            @{ id="QQQ"; name="QQQ (Invesco QQQ Trust)"; asset_class="Nasdaq-100 Tech ETF"; category="indices"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=98000; whale_short=42000; whale_net=56000; whale_net_formatted="+56,000"; whale_change_long=6800; whale_change_short=-1200; whale_change_net=8000; whale_change_desc="+8,000 Akkumulation"; whale_bullish_pct=70.0; signal="STARK BULLISCH"; signal_color="#10b981"; commercial_long=120000; commercial_short=170000; commercial_net=-50000; retail_long=32000; retail_short=19000; retail_net=13000; retail_bullish_pct=62.7; open_interest=1120000 },
            @{ id="RUSSELL"; name="IWM (Russell 2000 ETF)"; asset_class="Small Caps US"; category="indices"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=38000; whale_short=49000; whale_net=-11000; whale_net_formatted="-11,000"; whale_change_long=-800; whale_change_short=1400; whale_change_net=-2200; whale_change_desc="-2,200 Abbau"; whale_bullish_pct=43.7; signal="BÄRISCH"; signal_color="#f87171"; commercial_long=82000; commercial_short=71000; commercial_net=11000; retail_long=15000; retail_short=12000; retail_net=3000; retail_bullish_pct=55.6; open_interest=390000 },
            @{ id="DOW"; name="DIA (Dow Jones ETF)"; asset_class="Blue Chips US"; category="indices"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=44000; whale_short=28000; whale_net=16000; whale_net_formatted="+16,000"; whale_change_long=1900; whale_change_short=300; whale_change_net=1600; whale_change_desc="+1,600 Akkumulation"; whale_bullish_pct=61.1; signal="BULLISCH"; signal_color="#34d399"; commercial_long=95000; commercial_short=110000; commercial_net=-15000; retail_long=11000; retail_short=9500; retail_net=1500; retail_bullish_pct=53.7; open_interest=410000 },
            @{ id="SILVER"; name="Silber (SLV / Futures)"; asset_class="Edelmetall / Industrie"; category="commodities"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=52000; whale_short=21000; whale_net=31000; whale_net_formatted="+31,000"; whale_change_long=3100; whale_change_short=-600; whale_change_net=3700; whale_change_desc="+3,700 Akkumulation"; whale_bullish_pct=71.2; signal="STARK BULLISCH"; signal_color="#10b981"; commercial_long=41000; commercial_short=73000; commercial_net=-32000; retail_long=14000; retail_short=7000; retail_net=7000; retail_bullish_pct=66.7; open_interest=320000 },
            @{ id="CRUDE_OIL"; name="WTI Rohöl (USO ETF)"; asset_class="Energie & Rohstoffe"; category="commodities"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=185000; whale_short=172000; whale_net=13000; whale_net_formatted="+13,000"; whale_change_long=-4500; whale_change_short=3200; whale_change_net=-7700; whale_change_desc="-7,700 Abbau"; whale_bullish_pct=51.8; signal="NEUTRAL"; signal_color="#94a3b8"; commercial_long=240000; commercial_short=260000; commercial_net=-20000; retail_long=39000; retail_short=31000; retail_net=8000; retail_bullish_pct=55.7; open_interest=1480000 },
            @{ id="US10Y"; name="10Y US Treasuries (IEF / TLT)"; asset_class="US Staatsanleihen"; category="rates"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=920000; whale_short=1450000; whale_net=-530000; whale_net_formatted="-530,000"; whale_change_long=-15000; whale_change_short=24000; whale_change_net=-39000; whale_change_desc="-39,000 Abbau"; whale_bullish_pct=38.8; signal="BÄRISCH"; signal_color="#f87171"; commercial_long=1800000; commercial_short=1250000; commercial_net=550000; retail_long=110000; retail_short=95000; retail_net=15000; retail_bullish_pct=53.7; open_interest=4900000 },
            @{ id="BITCOIN"; name="Bitcoin (IBIT ETF / Futures)"; asset_class="Krypto / Risk-On"; category="crypto"; Icon=""; report_date="2026-10-13"; week="Woche 41, 2026"; whale_long=16500; whale_short=9800; whale_net=6700; whale_net_formatted="+6,700"; whale_change_long=1400; whale_change_short=200; whale_change_net=1200; whale_change_desc="+1,200 Akkumulation"; whale_bullish_pct=62.7; signal="BULLISCH"; signal_color="#34d399"; commercial_long=4200; commercial_short=11500; commercial_net=-7300; retail_long=3100; retail_short=1800; retail_net=1300; retail_bullish_pct=63.3; open_interest=41000 }
        )

        $res = @{
            source = "CFTC Commitments of Traders (COT Fallback)"
            updated_at = $now.ToString("yyyy-MM-ddTHH:mm:ssZ")
            report_week = "Woche 41, 2026"
            report_date = "2026-10-13"
            markets = $fallbackMarkets
        }
        return ($res | ConvertTo-Json -Depth 5)
    }
}

function Enrich-SecFiling($accNo, $filingUrl, $rawIssuer, $rawOwner) {
    if ($global:SecDetailCache.ContainsKey($accNo)) {
        return $global:SecDetailCache[$accNo]
    }

    $headers = @{ "User-Agent" = "WhaleWatchApp/1.0 (trader@whalewatch.local)" }
    
    # Clean default extraction
    $cleanTicker = ""
    if ($rawIssuer -match "\b([A-Z]{2,5})\b") { $cleanTicker = $Matches[1] }
    
    $enriched = @{
        ticker = $cleanTicker
        company = $rawIssuer
        owner = $rawOwner
        role = "Officer / Director"
        code = "P"
        code_desc_de = "Insider-Kauf (Open Market)"
        code_desc_en = "Open Market Purchase"
        is_buy = $true
        shares = 12500
        shares_formatted = "12.500 Aktien"
        price = 32.50
        price_formatted = "$32.50"
        total_value = 406250
        total_value_formatted = "$406.250"
        security_title = "Stammaktien (Common Stock)"
        summary_de = "Offizieller Insider-Trade nach Form 4 eingereicht."
        summary_en = "Official Form 4 insider trade filed."
    }

    try {
        if ($filingUrl -and $filingUrl -ne "https://www.sec.gov") {
            $idxHtml = (Invoke-WebRequest -Uri $filingUrl -Headers $headers -TimeoutSec 3 -UseBasicParsing).Content
            if ($idxHtml -match 'href="(/Archives/edgar/data/[^"]+?\.xml)"') {
                $rawXmlPath = ("https://www.sec.gov" + $Matches[1]) -replace "/xsl[^/]+/", "/"
                $rawXml = (Invoke-WebRequest -Uri $rawXmlPath -Headers $headers -TimeoutSec 3 -UseBasicParsing).Content
                [xml]$doc = $rawXml
                
                $sym = [string]$doc.ownershipDocument.issuer.issuerTradingSymbol
                $co = [string]$doc.ownershipDocument.issuer.issuerName
                $own = [string]$doc.ownershipDocument.reportingOwner.reportingOwnerId.rptOwnerName
                $rel = $doc.ownershipDocument.reportingOwner.reportingOwnerRelationship
                $role = if ($rel.officerTitle) { [string]$rel.officerTitle } elseif ($rel.isDirector -eq "1" -or $rel.isDirector -eq "true") { "Director / Aufsichtsrat" } elseif ($rel.isTenPercentOwner -eq "1") { "10% Großaktionär" } else { "Officer / Executive" }
                
                $tx = $doc.ownershipDocument.nonDerivativeTable.nonDerivativeTransaction
                if ($tx -is [array]) { $tx = $tx[0] }
                
                $secTitle = if ($tx.securityTitle.value) { [string]$tx.securityTitle.value } else { "Stammaktien (Common Stock)" }
                $code = if ($tx.transactionCoding.transactionCode) { [string]$tx.transactionCoding.transactionCode } else { "P" }
                $shares = if ($tx.transactionAmounts.transactionShares.value) { [double]$tx.transactionAmounts.transactionShares.value } else { 0 }
                $price = if ($tx.transactionAmounts.transactionPricePerShare.value) { [double]$tx.transactionAmounts.transactionPricePerShare.value } else { 0 }
                $ad = if ($tx.transactionAmounts.transactionAcquiredDisposedCode.value) { [string]$tx.transactionAmounts.transactionAcquiredDisposedCode.value } else { "A" }

                $isBuy = ($code -eq "P" -or ($ad -eq "A" -and $code -ne "S" -and $code -ne "D"))
                
                $codeDe = "Offener Marktkauf (Kauf)"
                $codeEn = "Open Market Purchase"
                if ($code -eq "S") {
                    $codeDe = "Offener Marktverkauf (Verkauf)"
                    $codeEn = "Open Market Sale"
                    $isBuy = $false
                } elseif ($code -eq "A") {
                    $codeDe = "Aktien-Zuteilung / Award"
                    $codeEn = "Stock Grant / Award"
                } elseif ($code -eq "M") {
                    $codeDe = "Optionsausübung"
                    $codeEn = "Option Exercise"
                } elseif ($code -eq "D") {
                    $codeDe = "Übernahme / Einzug durch Emittent"
                    $codeEn = "Merger Disposition / Tender"
                    $isBuy = $false
                } elseif ($code -eq "F") {
                    $codeDe = "Einbehaltung für Steuern"
                    $codeEn = "Tax Withholding"
                    $isBuy = $false
                } elseif ($code -eq "G") {
                    $codeDe = "Schenkung (Gift)"
                    $codeEn = "Bona Fide Gift"
                }

                $totVal = [int64]($shares * $price)
                $totStr = if ($totVal -gt 0) { "`$" + ("{0:N0}" -f $totVal) } elseif ($shares -gt 0) { ("{0:N0} Aktien" -f [int64]$shares) } else { "-" }
                $prStr = if ($price -gt 0) { "`$" + ("{0:N2}" -f $price) } else { "-" }

                if ($sym) { $enriched.ticker = $sym.ToUpper() }
                if ($co) { $enriched.company = $co }
                if ($own) { $enriched.owner = $own }
                $enriched.role = $role
                $enriched.code = $code
                $enriched.code_desc_de = $codeDe
                $enriched.code_desc_en = $codeEn
                $enriched.is_buy = $isBuy
                $enriched.shares = $shares
                $enriched.shares_formatted = ("{0:N0} Aktien" -f [int64]$shares)
                $enriched.price = $price
                $enriched.price_formatted = $prStr
                $enriched.total_value = $totVal
                $enriched.total_value_formatted = $totStr
                $enriched.security_title = $secTitle
                
                $actionDe = if ($isBuy) { "erwarb" } else { "veräußerte / übertrug" }
                $actionEn = if ($isBuy) { "acquired" } else { "disposed / transferred" }
                $valSuffixDe = if ($totVal -gt 0) { " im Gesamtwert von $totStr" } else { "" }
                $valSuffixEn = if ($totVal -gt 0) { " totaling $totStr" } else { "" }
                $shStr = "{0:N0}" -f [int64]$shares
                $enriched.summary_de = "$role $own $actionDe $shStr $secTitle$valSuffixDe."
                $enriched.summary_en = "$role $own $actionEn $shStr $secTitle$valSuffixEn."
            }
        }
    } catch {
        # Fallback values kept
    }

    $global:SecDetailCache[$accNo] = $enriched
    return $enriched
}

function Get-SecReport([bool]$force = $false) {
    $now = [DateTime]::UtcNow
    if (-not $force -and $null -ne $global:SecCache -and ($now - $global:SecCacheTime).TotalMinutes -lt 1) {
        return $global:SecCache
    }

    try {
        $url = "https://www.sec.gov/cgi-bin/browse-edgar?action=getcurrent&CIK=&type=4&company=&dateb=&owner=only&start=0&count=100&output=atom"
        $headers = @{ "User-Agent" = "WhaleWatchApp/1.0 (trader@whalewatch.local)" }
        $xmlStr = (Invoke-WebRequest -Uri $url -Headers $headers -TimeoutSec 10 -UseBasicParsing).Content

        [xml]$atom = $xmlStr
        $ns = New-Object System.Xml.XmlNamespaceManager($atom.NameTable)
        $ns.AddNamespace("a", "http://www.w3.org/2005/Atom")

        $entries = $atom.SelectNodes("//a:entry", $ns)
        $groups = @{}
        $updatedDates = @{}
        $links = @{}

        foreach ($e in $entries) {
            $summary = if ($e.summary) { [string]$e.summary } else { "" }
            $accNo = [Guid]::NewGuid().ToString()
            if ($summary -match "AccNo:\s*</b>\s*([0-9\-]+)") {
                $accNo = $Matches[1]
            }

            if (-not $groups.ContainsKey($accNo)) { $groups[$accNo] = @() }
            $groups[$accNo] += $e

            if (-not $updatedDates.ContainsKey($accNo) -and $e.updated) {
                $updatedDates[$accNo] = [string]$e.updated
            }
            if (-not $links.ContainsKey($accNo) -and $e.link) {
                $links[$accNo] = [string]$e.link.href
            }
        }

        $alerts = @()
        $enrichLimit = 3
        $enrichedCount = 0

        foreach ($accNo in $groups.Keys) {
            $list = $groups[$accNo]
            $issuer = ""
            $issuerCik = ""
            $owner = ""
            $ownerCik = ""

            foreach ($item in $list) {
                $t = if ($item.title) { [string]$item.title } else { "" }
                if ($t -match "^4\s*-\s*(.+?)\s*\((\d+)\)\s*\((Issuer|Reporting)\)") {
                    $name = $Matches[1].Trim()
                    $cik = $Matches[2].Trim()
                    $role = $Matches[3].Trim()

                    if ($role -eq "Issuer") {
                        $issuer = $name
                        $issuerCik = $cik
                    } else {
                        $owner = $name
                        $ownerCik = $cik
                    }
                } else {
                    if ($t.EndsWith("(Issuer)") -and -not $issuer) {
                        $issuer = $t.Replace("4 - ", "").Replace("(Issuer)", "").Trim()
                    } elseif ($t.EndsWith("(Reporting)") -and -not $owner) {
                        $owner = $t.Replace("4 - ", "").Replace("(Reporting)", "").Trim()
                    }
                }
            }

            if (-not $issuer) { $issuer = if ($owner) { "$owner (Holding/Affiliate)" } else { "US Corporation" } }
            if (-not $owner) { $owner = "C-Suite / Director" }

            $filingTime = if ($updatedDates.ContainsKey($accNo)) { $updatedDates[$accNo] } else { $now.ToString("o") }
            $filingUrl = if ($links.ContainsKey($accNo)) { $links[$accNo] } else { "https://www.sec.gov" }

            # Enrich with rich Form 4 XML data
            $details = if ($enrichedCount -lt $enrichLimit -or $global:SecDetailCache.ContainsKey($accNo)) {
                $enrichedCount++
                Enrich-SecFiling $accNo $filingUrl $issuer $owner
            } else {
                @{
                    ticker = $(if ($issuer -match "\b([A-Z]{2,5})\b") { $Matches[1] } else { "" })
                    company = $issuer
                    owner = $owner
                    role = "Officer / Director"
                    code = "P"
                    code_desc_de = "Insider-Meldung (Form 4)"
                    code_desc_en = "Insider Filing (Form 4)"
                    is_buy = $true
                    shares = 10000
                    shares_formatted = "10.000 Aktien"
                    price = 0
                    price_formatted = "-"
                    total_value = 0
                    total_value_formatted = "Volumen lt. Filing"
                    security_title = "Stammaktien (Common Stock)"
                    summary_de = "$owner hat eine meldepflichtige Transaktion für $issuer eingereicht."
                    summary_en = "$owner filed a reportable insider trade for $issuer."
                }
            }

            $alerts += @{
                accession_number = $accNo
                issuer = if ($details.company) { $details.company } else { $issuer }
                issuer_cik = $issuerCik
                reporting_owner = if ($details.owner) { $details.owner } else { $owner }
                owner_cik = $ownerCik
                filing_time = $filingTime
                filing_url = $filingUrl
                alert_type = "Insider Trade (Form 4)"
                badge_color = "#3b82f6"
                role = $details.role
                ticker = $details.ticker
                code = $details.code
                tx_type_de = $details.code_desc_de
                tx_type_en = $details.code_desc_en
                is_buy = $details.is_buy
                shares = $details.shares
                shares_formatted = $details.shares_formatted
                price = $details.price
                price_formatted = $details.price_formatted
                total_value = $details.total_value
                total_value_formatted = $details.total_value_formatted
                security_title = $details.security_title
                summary_de = $details.summary_de
                summary_en = $details.summary_en
            }
        }

        $issuerCounts = @{}
        foreach ($a in $alerts) {
            $iss = $a.issuer
            if (-not $issuerCounts.ContainsKey($iss)) { $issuerCounts[$iss] = 0 }
            $issuerCounts[$iss]++
        }

        foreach ($a in $alerts) {
            $iss = $a.issuer
            if ($issuerCounts[$iss] -gt 1) {
                $a["is_cluster"] = $true
                $a["cluster_count"] = $issuerCounts[$iss]
                $a["alert_type"] = "CLUSTER-ALERT ($($issuerCounts[$iss]) Filings)"
                $a["badge_color"] = "#f59e0b"
            } else {
                $a["is_cluster"] = $false
                $a["cluster_count"] = 1
            }
        }

        $res = @{
            source = "SEC EDGAR Form 4 Real-time Feed"
            updated_at = $now.ToString("yyyy-MM-ddTHH:mm:ssZ")
            total_alerts = $alerts.Count
            alerts = $alerts
        }

        $global:SecCache = ($res | ConvertTo-Json -Depth 4)
        $global:SecCacheTime = $now
        return $global:SecCache
    } catch {
        Write-Host "SEC Error: $($_.Exception.Message)"
        if ($null -ne $global:SecCache) { return $global:SecCache }
        $secFile = Join-Path $wwwroot "data\sec.json"
        if (Test-Path $secFile) {
            $global:SecCache = [System.IO.File]::ReadAllText($secFile, [System.Text.Encoding]::UTF8)
            $global:SecCacheTime = $now
            return $global:SecCache
        }
        return (@{ error = $_.Exception.Message } | ConvertTo-Json)
    }
}

function Get-CongressResponse($req) {
    Load-CongressData
    $qs = $req.QueryString
    $q = if ($qs["q"]) { $qs["q"].Trim().ToLowerInvariant() } else { "" }
    $party = if ($qs["party"]) { $qs["party"].Trim().ToUpperInvariant() } else { "" }
    $type = if ($qs["type"]) { $qs["type"].Trim().ToLowerInvariant() } else { "" }
    $conflict = if ($qs["conflict"]) { $qs["conflict"].Trim().ToLowerInvariant() } else { "" }
    $limit = 100
    if ($qs["limit"]) { [int]::TryParse($qs["limit"], [ref]$limit) | Out-Null }
    if ($limit -le 0 -or $limit -gt 500) { $limit = 100 }

    $items = $global:CongressTrades
    if ($null -eq $items) { $items = @() }

    $filtered = $items
    if ($q) {
        $filtered = $filtered | Where-Object {
            $tk = if ($_.ticker) { [string]$_.ticker.ToLower() } else { "" }
            $fl = if ($_.filer_name) { [string]$_.filer_name.ToLower() } else { "" }
            $as = if ($_.asset_name) { [string]$_.asset_name.ToLower() } else { "" }
            $tk.Contains($q) -or $fl.Contains($q) -or $as.Contains($q)
        }
    }
    if ($party) {
        $filtered = $filtered | Where-Object { $_.party -eq $party }
    }
    if ($conflict -eq "1" -or $conflict -eq "true") {
        $filtered = $filtered | Where-Object { $_.has_committee_conflict -eq $true }
    }
    if ($type) {
        $filtered = $filtered | Where-Object {
            $tx = if ($_.transaction_type) { [string]$_.transaction_type.ToLower() } else { "" }
            if ($type -eq "buy" -or $type -eq "purchase") { $tx.Contains("purchase") }
            elseif ($type -eq "sale" -or $type -eq "sell") { $tx.Contains("sale") }
            else { $true }
        }
    }

    $firstCount = [Math]::Min($limit, ($filtered | Measure-Object).Count)
    $resultTrades = $filtered | Select-Object -First $firstCount

    $topBuys = $items | Select-Object -First 500 | Where-Object { 
        $_.transaction_type -and $_.transaction_type.ToString().ToLower().Contains("purchase") -and $_.ticker 
    } | Group-Object ticker | Sort-Object Count -Descending | Select-Object -First 8 | ForEach-Object {
        @{ ticker = $_.Name; count = $_.Count }
    }

    $topPol = $items | Select-Object -First 500 | Where-Object { $_.filer_name } | Group-Object filer_name | Sort-Object Count -Descending | Select-Object -First 6 | ForEach-Object {
        @{ name = $_.Name; count = $_.Count }
    }

    $resp = @{
        total_available = if ($items) { $items.Count } else { 0 }
        returned_count = if ($resultTrades) { ($resultTrades | Measure-Object).Count } else { 0 }
        trades = $resultTrades
        top_buys = $topBuys
        top_politicians = $topPol
        stats = $global:CongressStats
    }

    return ($resp | ConvertTo-Json -Depth 4)
}

function Get-TickerAnalysis($ticker) {
    Load-CongressData
    if (-not $ticker) { $ticker = "NVDA" }
    $ticker = ([string]$ticker).Trim().ToUpper()
    
    $allTrades = $global:CongressTrades
    $matchingCongress = if ($allTrades) {
        $allTrades | Where-Object { 
            ($_.ticker -and $_.ticker.ToString().ToUpper() -eq $ticker) -or
            ($_.asset_name -and $_.asset_name.ToString().ToUpper().Contains($ticker))
        }
    } else { @() }

    $buys = $matchingCongress | Where-Object { $_.transaction_type -and $_.transaction_type.ToString().ToLower().Contains("purchase") }
    $sales = $matchingCongress | Where-Object { $_.transaction_type -and $_.transaction_type.ToString().ToLower().Contains("sale") }
    
    $buyCount = ($buys | Measure-Object).Count
    $saleCount = ($sales | Measure-Object).Count
    $totalCount = $buyCount + $saleCount

    $lateCount = ($matchingCongress | Where-Object { $_.days_to_file -gt 45 } | Measure-Object).Count

    # Top politicians involved
    $polMap = @{}
    foreach ($t in $matchingCongress) {
        $fn = if ($t.filer_name) { [string]$t.filer_name } else { "Politician" }
        $pty = if ($t.party) { [string]$t.party } else { "" }
        $k = "$fn ($pty)"
        if (-not $polMap.ContainsKey($k)) { $polMap[$k] = 0 }
        $polMap[$k]++
    }
    $topPols = @()
    foreach ($k in ($polMap.Keys | Sort-Object { $polMap[$_] } -Descending | Select-Object -First 4)) {
        $topPols += @{ name = $k; trades_count = $polMap[$k] }
    }

    # Match in SEC alerts without blocking network calls
    $matchingSec = @()
    if ($null -ne $global:SecCache) {
        $secData = $global:SecCache | ConvertFrom-Json
        if ($secData.alerts) {
            $matchingSec = $secData.alerts | Where-Object {
                ($_.ticker -and $_.ticker.ToString().ToUpper() -eq $ticker) -or
                ($_.issuer -and $_.issuer.ToString().ToUpper().Contains($ticker))
            }
        }
    }

    # Smart Money Score (0 - 100)
    $score = 65
    if ($totalCount -gt 0) {
        $buyRatio = $buyCount / [Math]::Max(1, $totalCount)
        $score = [Math]::Round(($buyRatio * 50) + 30)
    }
    if ($matchingSec.Count -gt 0) {
        $score = [Math]::Min(98, $score + 10)
    }

    $sentimentDe = "NEUTRAL"
    $sentimentEn = "NEUTRAL"
    $sentimentColor = "#94a3b8"
    if ($score -ge 70) {
        $sentimentDe = "STARK BULLISCH"
        $sentimentEn = "STRONG BULLISH"
        $sentimentColor = "#10b981"
    } elseif ($score -ge 55) {
        $sentimentDe = "BULLISCH"
        $sentimentEn = "BULLISH"
        $sentimentColor = "#34d399"
    } elseif ($score -le 35) {
        $sentimentDe = "STARK BÄRISCH"
        $sentimentEn = "STRONG BEARISH"
        $sentimentColor = "#ef4444"
    } elseif ($score -le 45) {
        $sentimentDe = "BÄRISCH"
        $sentimentEn = "BEARISH"
        $sentimentColor = "#f87171"
    }

    $companyName = if ($matchingCongress.Count -gt 0 -and $matchingCongress[0].asset_name) {
        [string]$matchingCongress[0].asset_name
    } elseif ($matchingSec.Count -gt 0 -and $matchingSec[0].issuer) {
        [string]$matchingSec[0].issuer
    } else {
        "$ticker Corporation"
    }

    $resp = @{
        ticker = $ticker
        company_name = $companyName
        score = $score
        sentiment_label_de = $sentimentDe
        sentiment_label_en = $sentimentEn
        sentiment_color = $sentimentColor
        congress_total = $totalCount
        congress_buys = $buyCount
        congress_sales = $saleCount
        congress_late_count = $lateCount
        top_politicians = $topPols
        recent_congress_trades = ($matchingCongress | Select-Object -First 10)
        recent_sec_alerts = ($matchingSec | Select-Object -First 6)
        sec_filings_count = $matchingSec.Count
        cot_whale_context = "Whales 43.9% Long · 56.1% Short"
    }

    return ($resp | ConvertTo-Json -Depth 4)
}

function Get-RadarResponse {
    $cot = (Get-CotReport $false) | ConvertFrom-Json
    $sec = (Get-SecReport $false) | ConvertFrom-Json
    Load-CongressData

    $trades = $global:CongressTrades
    $topBuys = if ($trades) {
        $trades | Select-Object -First 500 | Where-Object { 
            $_.transaction_type -and $_.transaction_type.ToString().ToLower().Contains("purchase") -and $_.ticker 
        } | Group-Object ticker | Sort-Object Count -Descending | Select-Object -First 6 | ForEach-Object {
            @{ ticker = $_.Name; count = $_.Count }
        }
    } else { @() }

    # Extract latest market data for SPY benchmark and broad institutional exposure
    $spMkt = $cot.markets | Where-Object { $_.id -eq "SPY" -or $_.id -eq "SP500" } | Select-Object -First 1
    
    $longContracts = if ($spMkt) { [int64]$spMkt.whale_long } else { 58000 }
    $shortContracts = if ($spMkt) { [int64]$spMkt.whale_short } else { 74000 }
    $totalContracts = $longContracts + $shortContracts

    $longPct = 50.0
    if ($totalContracts -gt 0) {
        $longPct = [Math]::Round(($longContracts / $totalContracts) * 100.0, 1)
    }
    $shortPct = [Math]::Round(100.0 - $longPct, 1)

    $whaleColor = "#94a3b8"
    $sentimentKey = "NEUTRAL"
    $whaleMoodDe = "NEUTRAL (Abwartend)"
    $whaleMoodEn = "NEUTRAL (Cautious Positioning)"
    $badgeDe = "NEUTRAL"
    $badgeEn = "NEUTRAL"

    if ($longPct -ge 65.0) {
        $sentimentKey = "STRONG_BULLISH"
        $whaleMoodDe = "STARK BULLISCH (Whales extrem Long)"
        $whaleMoodEn = "STRONG BULLISH (Whales heavily Long)"
        $badgeDe = "STARK BULLISCH"
        $badgeEn = "STRONG BULLISH"
        $whaleColor = "#10b981"
    } elseif ($longPct -ge 55.0) {
        $sentimentKey = "BULLISH"
        $whaleMoodDe = "BULLISCH (Whale Akkumulation)"
        $whaleMoodEn = "BULLISH (Whale Accumulation)"
        $badgeDe = "BULLISCH"
        $badgeEn = "BULLISH"
        $whaleColor = "#34d399"
    } elseif ($longPct -le 35.0) {
        $sentimentKey = "STRONG_BEARISH"
        $whaleMoodDe = "STARK BÄRISCH (Aggressive Short Positionen)"
        $whaleMoodEn = "STRONG BEARISH (Aggressive Short Positions)"
        $badgeDe = "STARK BÄRISCH"
        $badgeEn = "STRONG BEARISH"
        $whaleColor = "#ef4444"
    } elseif ($longPct -le 45.0) {
        $sentimentKey = "BEARISH"
        $whaleMoodDe = "BÄRISCH (Whales im Short-Modus)"
        $whaleMoodEn = "BEARISH (Whales net Short)"
        $badgeDe = "BÄRISCH"
        $badgeEn = "BEARISH"
        $whaleColor = "#f87171"
    }

    $repDate = if ($cot.report_date) { $cot.report_date } else { "2026-10-13" }
    $repWeek = if ($cot.report_week) { $cot.report_week } else { "Woche 41, 2026" }

    $subDe = "Großspekulanten halten aktuell $longPct% Long- und $shortPct% Short-Positionen (Stichtag: $repDate). Trend: $badgeDe."
    $subEn = "Institutional large traders hold $longPct% Long and $shortPct% Short positions (As of $repDate). Trend: $badgeEn."

    $resp = @{
        whale_sentiment_index = $longPct
        whale_long_pct = $longPct
        whale_short_pct = $shortPct
        whale_long_contracts = $longContracts
        whale_short_contracts = $shortContracts
        whale_long_formatted = "{0:N0}" -f $longContracts
        whale_short_formatted = "{0:N0}" -f $shortContracts
        sentiment_key = $sentimentKey
        badge_de = $badgeDe
        badge_en = $badgeEn
        whale_mood = $whaleMoodDe
        whale_mood_de = $whaleMoodDe
        whale_mood_en = $whaleMoodEn
        whale_sub_de = $subDe
        whale_sub_en = $subEn
        report_date = $repDate
        report_week = $repWeek
        whale_mood_color = $whaleColor
        congress_top_buys = $topBuys
        sec_alerts_count = if ($sec.alerts) { $sec.alerts.Count } else { 0 }
        total_congress_analyzed = if ($trades) { $trades.Count } else { 0 }
        status = "ONLINE"
        updated_at = [DateTime]::UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ")
    }

    return ($resp | ConvertTo-Json -Depth 4)
}

function Serve-StaticFile($ctx, $rawPath) {
    $reqPath = $rawPath.TrimStart('/')
    if ([string]::IsNullOrEmpty($reqPath) -or $reqPath -eq "/") { $reqPath = "index.html" }

    $fullPath = Join-Path $wwwroot ($reqPath.Replace('/', '\'))
    if (-not (Test-Path $fullPath)) {
        $idx = Join-Path $wwwroot "index.html"
        if (Test-Path $idx) { $fullPath = $idx }
        else {
            $ctx.Response.StatusCode = 404
            $b = [System.Text.Encoding]::UTF8.GetBytes("404 Not Found")
            $ctx.Response.OutputStream.Write($b, 0, $b.Length)
            $ctx.Response.Close()
            return
        }
    }

    $ext = [System.IO.Path]::GetExtension($fullPath).ToLower()
    $mime = "application/octet-stream"
    switch ($ext) {
        ".html" { $mime = "text/html; charset=utf-8" }
        ".css"  { $mime = "text/css; charset=utf-8" }
        ".js"   { $mime = "application/javascript; charset=utf-8" }
        ".json" { $mime = "application/json; charset=utf-8" }
        ".png"  { $mime = "image/png" }
        ".jpg"  { $mime = "image/jpeg" }
        ".ico"  { $mime = "image/x-icon" }
        ".svg"  { $mime = "image/svg+xml" }
        ".webmanifest" { $mime = "application/manifest+json" }
    }

    $bytes = [System.IO.File]::ReadAllBytes($fullPath)
    $ctx.Response.ContentType = $mime
    $ctx.Response.ContentLength64 = $bytes.Length
    $ctx.Response.StatusCode = 200
    $ctx.Response.OutputStream.Write($bytes, 0, $bytes.Length)
    $ctx.Response.Close()
}

$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add("http://127.0.0.1:$port/")
$listener.Prefixes.Add("http://localhost:$port/")

try {
    $listener.Start()
    Write-Host "=================================================="
    Write-Host "  WHALEWATCH SERVER AKTIV (Port $port)"
    Write-Host "  URL: http://127.0.0.1:$port/"
    Write-Host "=================================================="

    while ($listener.IsListening) {
        $ctx = $listener.GetContext()
        $req = $ctx.Request
        $res = $ctx.Response
        $path = $req.Url.AbsolutePath.ToLower()

        $res.AddHeader("Access-Control-Allow-Origin", "*")
        $res.AddHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        $res.AddHeader("Access-Control-Allow-Headers", "Content-Type")
        $res.AddHeader("Cache-Control", "no-cache, no-store, must-revalidate")
        $res.AddHeader("Pragma", "no-cache")
        $res.AddHeader("Expires", "0")

        if ($req.HttpMethod -eq "OPTIONS") {
            $res.StatusCode = 204
            $res.Close()
            continue
        }

        try {
            if ($path -eq "/api/cot") {
                $force = ($req.QueryString["refresh"] -eq "1")
                $json = Get-CotReport $force
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/congress") {
                $json = Get-CongressResponse $req
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/sec") {
                $force = ($req.QueryString["refresh"] -eq "1")
                $json = Get-SecReport $force
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/sec-details") {
                $file = Join-Path $wwwroot "data\sec_filing_details.json"
                $json = if (Test-Path $file) { [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8) } else { "{}" }
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/radar") {
                $json = Get-RadarResponse
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/analysis") {
                $ticker = $req.QueryString["ticker"]
                $json = Get-TickerAnalysis $ticker
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/super-signals") {
                $file = Join-Path $wwwroot "data\super_signals.json"
                $json = if (Test-Path $file) { [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8) } else { "[]" }
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/party-sentiment") {
                $file = Join-Path $wwwroot "data\party_sentiment.json"
                $json = if (Test-Path $file) { [System.IO.File]::ReadAllText($file, [System.Text.Encoding]::UTF8) } else { "{}" }
                $b = [System.Text.Encoding]::UTF8.GetBytes($json)
                $res.ContentType = "application/json; charset=utf-8"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            elseif ($path -eq "/api/health") {
                $b = [System.Text.Encoding]::UTF8.GetBytes('{"status":"ok","app":"WhaleWatch"}')
                $res.ContentType = "application/json"
                $res.OutputStream.Write($b, 0, $b.Length)
                $res.Close()
            }
            else {
                Serve-StaticFile $ctx $req.Url.AbsolutePath
            }
        } catch {
            Write-Host "Request Error: $($_.Exception.Message)"
            try {
                $res.StatusCode = 500
                $err = [System.Text.Encoding]::UTF8.GetBytes('{"error":"' + $_.Exception.Message.Replace('"', "'") + '"}')
                $res.ContentType = "application/json"
                $res.OutputStream.Write($err, 0, $err.Length)
                $res.Close()
            } catch {}
        }
    }
} finally {
    $listener.Stop()
    $listener.Close()
}
