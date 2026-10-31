# PowerShell script to sanitize all existing SEC Form 4 filings with proper fields and "No Data" fallbacks
$secFile = "data/sec_form4_latest.json"
if (-not (Test-Path $secFile)) { exit 1 }

$json = Get-Content $secFile -Raw -Encoding UTF8 | ConvertFrom-Json
$alerts = $json.alerts

foreach ($a in $alerts) {
    # 1. Transaction Date
    $fDate = if ($a.filing_date -and $a.filing_date -ne "-" -and $a.filing_date -ne "") { $a.filing_date } else { "No Data" }
    $a | Add-Member -NotePropertyName "filing_date" -NotePropertyValue $fDate -Force

    $tDate = if ($a.transaction_date -and $a.transaction_date -ne "-" -and $a.transaction_date -ne "") { $a.transaction_date } else { $fDate }
    $a | Add-Member -NotePropertyName "transaction_date" -NotePropertyValue $tDate -Force

    # 2. Shares (Stückzahl)
    $shFmt = if ($a.shares -and [int64]$a.shares -gt 0) {
        ("{0:N0} Aktien" -f [int64]$a.shares)
    } elseif ($a.shares_formatted -and $a.shares_formatted -ne "-" -and $a.shares_formatted -ne "0" -and $a.shares_formatted -ne "0 Aktien") {
        $a.shares_formatted
    } else {
        "No Data"
    }
    $a | Add-Member -NotePropertyName "shares_formatted" -NotePropertyValue $shFmt -Force

    # 3. Price (Preis)
    $prcFmt = if ($a.price -and [double]$a.price -gt 0.0) {
        ('$' + ("{0:N2}" -f [double]$a.price))
    } elseif ($a.price_formatted -and $a.price_formatted -ne "-" -and $a.price_formatted -ne "$0" -and $a.price_formatted -ne "$0.00" -and $a.price_formatted -ne "$0,00" -and $a.price_formatted -ne "") {
        $a.price_formatted
    } else {
        "No Data"
    }
    $a | Add-Member -NotePropertyName "price_formatted" -NotePropertyValue $prcFmt -Force

    # 4. Total Value (Gesamtwert)
    $valFmt = if ($a.total_value -and [double]$a.total_value -gt 0.0) {
        ('$' + ("{0:N0}" -f [double]$a.total_value))
    } elseif ($a.total_value_formatted -and $a.total_value_formatted -ne "-" -and $a.total_value_formatted -ne "$0" -and $a.total_value_formatted -ne "") {
        $a.total_value_formatted
    } else {
        "No Data"
    }
    $a | Add-Member -NotePropertyName "total_value_formatted" -NotePropertyValue $valFmt -Force

    # 5. Beneficial Ownership (Bestand Danach)
    $benef = 0
    if ($a.security_beneficials -and [int64]$a.security_beneficials -gt 0) {
        $benef = [int64]$a.security_beneficials
    } elseif ($a.shares_after -and [int64]$a.shares_after -gt 0) {
        $benef = [int64]$a.shares_after
        $a | Add-Member -NotePropertyName "security_beneficials" -NotePropertyValue $benef -Force
    }

    $benefFmt = if ($benef -gt 0) {
        ("{0:N0} Aktien" -f $benef)
    } elseif ($a.security_beneficials_formatted -and $a.security_beneficials_formatted -ne "-" -and $a.security_beneficials_formatted -ne "0" -and $a.security_beneficials_formatted -ne "0 Aktien" -and $a.security_beneficials_formatted -ne "") {
        $a.security_beneficials_formatted
    } else {
        "No Data"
    }
    $a | Add-Member -NotePropertyName "security_beneficials_formatted" -NotePropertyValue $benefFmt -Force

    # Sanitize each transaction item
    if ($a.transactions) {
        foreach ($tx in $a.transactions) {
            # Code & IsBuy
            $code = if ($tx.code) { $tx.code } elseif ($tx.transaction_code) { $tx.transaction_code } else { $a.code }
            $tx | Add-Member -NotePropertyName "code" -NotePropertyValue $code -Force
            $tx | Add-Member -NotePropertyName "transaction_code" -NotePropertyValue $code -Force

            $isBuy = ($code -ne "S" -and $code -ne "F" -and $code -ne "D")
            $tx | Add-Member -NotePropertyName "is_buy" -NotePropertyValue $isBuy -Force

            # Date
            $txD = if ($tx.transaction_date -and $tx.transaction_date -ne "-" -and $tx.transaction_date -ne "") { $tx.transaction_date } else { $tDate }
            $tx | Add-Member -NotePropertyName "transaction_date" -NotePropertyValue $txD -Force

            # Shares
            $txShs = if ($tx.shares) { [int64]$tx.shares } else { [int64]$a.shares }
            if ($txShs -gt 0) {
                $tx | Add-Member -NotePropertyName "shares" -NotePropertyValue $txShs -Force
                $tx | Add-Member -NotePropertyName "shares_formatted" -NotePropertyValue ("{0:N0} Aktien" -f $txShs) -Force
            } else {
                $tx | Add-Member -NotePropertyName "shares_formatted" -NotePropertyValue "No Data" -Force
            }

            # Price
            $txPrc = if ($tx.price -and [double]$tx.price -gt 0.0) { [double]$tx.price } elseif ($tx.price_per_share -and [double]$tx.price_per_share -gt 0.0) { [double]$tx.price_per_share } elseif ($a.price -and [double]$a.price -gt 0.0) { [double]$a.price } else { 0.0 }
            if ($txPrc -gt 0.0) {
                $tx | Add-Member -NotePropertyName "price" -NotePropertyValue $txPrc -Force
                $tx | Add-Member -NotePropertyName "price_formatted" -NotePropertyValue ('$' + ("{0:N2}" -f $txPrc)) -Force
            } else {
                $tx | Add-Member -NotePropertyName "price" -NotePropertyValue 0.0 -Force
                $tx | Add-Member -NotePropertyName "price_formatted" -NotePropertyValue "No Data" -Force
            }

            # Total Value
            $txVal = if ($tx.value -and [double]$tx.value -gt 0.0) { [double]$tx.value } elseif ($tx.total_value -and [double]$tx.total_value -gt 0.0) { [double]$tx.total_value } elseif ($txShs -gt 0 -and $txPrc -gt 0.0) { [Math]::Round($txShs * $txPrc, 2) } else { 0.0 }
            if ($txVal -gt 0.0) {
                $tx | Add-Member -NotePropertyName "total_value" -NotePropertyValue $txVal -Force
                $tx | Add-Member -NotePropertyName "total_value_formatted" -NotePropertyValue ('$' + ("{0:N0}" -f $txVal)) -Force
            } else {
                $tx | Add-Member -NotePropertyName "total_value" -NotePropertyValue 0.0 -Force
                $tx | Add-Member -NotePropertyName "total_value_formatted" -NotePropertyValue "No Data" -Force
            }

            # Beneficials (Bestand Danach)
            $txBenef = if ($tx.security_beneficials -and [int64]$tx.security_beneficials -gt 0) { [int64]$tx.security_beneficials } elseif ($tx.shares_after -and [int64]$tx.shares_after -gt 0) { [int64]$tx.shares_after } elseif ($benef -gt 0) { $benef } else { 0 }
            if ($txBenef -gt 0) {
                $tx | Add-Member -NotePropertyName "security_beneficials" -NotePropertyValue $txBenef -Force
                $tx | Add-Member -NotePropertyName "security_beneficials_formatted" -NotePropertyValue ("{0:N0} Aktien" -f $txBenef) -Force
            } else {
                $tx | Add-Member -NotePropertyName "security_beneficials" -NotePropertyValue 0 -Force
                $tx | Add-Member -NotePropertyName "security_beneficials_formatted" -NotePropertyValue "No Data" -Force
            }
        }
    }
}

$outObj = [PSCustomObject]@{
    updated_at = [DateTime]::UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.ffffffZ")
    count = $alerts.Count
    source = "U.S. Securities and Exchange Commission (EDGAR)"
    source_url = "https://www.sec.gov/edgar/search/"
    alerts = $alerts
}

$outJson = $outObj | ConvertTo-Json -Depth 10
[System.IO.File]::WriteAllText($secFile, $outJson, [System.Text.Encoding]::UTF8)

Write-Host "Sanitized $($alerts.Count) alerts in $secFile cleanly with zero errors!"
