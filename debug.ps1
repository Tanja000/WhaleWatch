. .\server.ps1

Write-Host "--- Testing SEC ---"
$sec = Get-SecReport $true
$sObj = $sec | ConvertFrom-Json
Write-Host "SEC Total Alerts:" $sObj.total_alerts
if ($sObj.alerts -and $sObj.alerts.Count -gt 0) {
    Write-Host "Sample Alert:" ($sObj.alerts[0] | ConvertTo-Json -Compress)
}

Write-Host "--- Testing COT ---"
$cot = Get-CotReport $true
$cObj = $cot | ConvertFrom-Json
Write-Host "COT Markets Count:" $cObj.markets.Count
if ($cObj.markets -and $cObj.markets.Count -gt 0) {
    Write-Host "Sample Market:" ($cObj.markets[0] | ConvertTo-Json -Compress)
}
