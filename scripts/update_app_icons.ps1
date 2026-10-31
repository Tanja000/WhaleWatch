# PowerShell script to resize whaleradar_icon_512x512.png into Android mipmap drawables
Add-Type -AssemblyName System.Drawing

$srcPath = "C:\Users\tanja\.gemini\antigravity\brain\0711fa42-4a45-4963-b810-141dbcc3b855\whaleradar_icon_512x512.png"
if (-not (Test-Path $srcPath)) {
    Write-Error "Source icon not found at $srcPath"
    exit 1
}

$srcBmp = [System.Drawing.Bitmap]::FromFile($srcPath)
Write-Host "Source image loaded: $($srcBmp.Width)x$($srcBmp.Height)"

$resDir = "android-app/app/src/main/res"

$densities = @(
    @{ Name = "mipmap-mdpi";    Square = 48;  Fg = 108 },
    @{ Name = "mipmap-hdpi";    Square = 72;  Fg = 162 },
    @{ Name = "mipmap-xhdpi";   Square = 96;  Fg = 216 },
    @{ Name = "mipmap-xxhdpi";  Square = 144; Fg = 324 },
    @{ Name = "mipmap-xxxhdpi"; Square = 192; Fg = 432 }
)

function Resize-Square([System.Drawing.Bitmap]$source, [int]$size, [string]$outPath, [bool]$round = $false) {
    $dest = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($dest)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    $g.Clear([System.Drawing.Color]::Transparent)

    if ($round) {
        $path = New-Object System.Drawing.Drawing2D.GraphicsPath
        $path.AddEllipse(0, 0, $size, $size)
        $g.SetClip($path)
    }

    $g.DrawImage($source, 0, 0, $size, $size)
    $g.Dispose()

    $dest.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $dest.Dispose()
}

function Resize-Foreground([System.Drawing.Bitmap]$source, [int]$size, [string]$outPath) {
    $dest = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($dest)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $g.CompositingQuality = [System.Drawing.Drawing2D.CompositingQuality]::HighQuality
    $g.Clear([System.Drawing.Color]::Transparent)

    # In Android adaptive icons, safe zone is inner 72dp out of 108dp viewport (66.6% to 75%)
    # Scale image to 76% and center it so the full whale and radar circles are visible without clipping
    $innerSize = [int]($size * 0.78)
    $offset = [int](($size - $innerSize) / 2)

    $g.DrawImage($source, $offset, $offset, $innerSize, $innerSize)
    $g.Dispose()

    $dest.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $dest.Dispose()
}

foreach ($d in $densities) {
    $dir = Join-Path $resDir $d.Name
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }

    $sqPath = Join-Path $dir "ic_launcher.png"
    $rndPath = Join-Path $dir "ic_launcher_round.png"
    $fgPath = Join-Path $dir "ic_launcher_foreground.png"

    Resize-Square -source $srcBmp -size $d.Square -outPath $sqPath -round $false
    Resize-Square -source $srcBmp -size $d.Square -outPath $rndPath -round $true
    Resize-Foreground -source $srcBmp -size $d.Fg -outPath $fgPath

    Write-Host "Generated $($d.Name): $($d.Square)x$($d.Square) square/round and $($d.Fg)x$($d.Fg) foreground"
}

$srcBmp.Dispose()
Write-Host "App icons successfully updated with the Google Play Store icon!"
