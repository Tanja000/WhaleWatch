Add-Type -AssemblyName System.Drawing

function Create-WhaleIcon {
    param([int]$size, [string]$path)
    
    $bmp = New-Object System.Drawing.Bitmap($size, $size)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic

    # Dark background with rounded effect
    $bgBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.Point(0, 0)),
        (New-Object System.Drawing.Point($size, $size)),
        [System.Drawing.Color]::FromArgb(255, 11, 19, 32),
        [System.Drawing.Color]::FromArgb(255, 5, 8, 15)
    )
    $g.FillRectangle($bgBrush, 0, 0, $size, $size)

    # Radar concentric circles
    $radarPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(40, 0, 240, 255), [Math]::Max(1.0, $size * 0.015))
    $center = $size / 2.0
    $g.DrawEllipse($radarPen, [float]($size * 0.15), [float]($size * 0.15), [float]($size * 0.70), [float]($size * 0.70))
    $g.DrawEllipse($radarPen, [float]($size * 0.28), [float]($size * 0.28), [float]($size * 0.44), [float]($size * 0.44))

    # Glowing radar sweep line
    $sweepPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(90, 0, 255, 180), [Math]::Max(1.5, $size * 0.02))
    $g.DrawLine($sweepPen, [float]$center, [float]$center, [float]($size * 0.8), [float]($size * 0.25))

    # Whale body path
    $whalePath = New-Object System.Drawing.Drawing2D.GraphicsPath
    
    # Coordinates normalized to 100x100
    $pts = @(
        [System.Drawing.PointF]::new(20.0 * $size / 100.0, 56.0 * $size / 100.0),
        [System.Drawing.PointF]::new(28.0 * $size / 100.0, 42.0 * $size / 100.0),
        [System.Drawing.PointF]::new(50.0 * $size / 100.0, 36.0 * $size / 100.0),
        [System.Drawing.PointF]::new(72.0 * $size / 100.0, 44.0 * $size / 100.0),
        [System.Drawing.PointF]::new(85.0 * $size / 100.0, 40.0 * $size / 100.0), # tail top
        [System.Drawing.PointF]::new(78.0 * $size / 100.0, 54.0 * $size / 100.0), # tail fluke notch
        [System.Drawing.PointF]::new(84.0 * $size / 100.0, 64.0 * $size / 100.0), # tail bottom
        [System.Drawing.PointF]::new(68.0 * $size / 100.0, 58.0 * $size / 100.0), # tail peduncle
        [System.Drawing.PointF]::new(52.0 * $size / 100.0, 66.0 * $size / 100.0), # belly
        [System.Drawing.PointF]::new(34.0 * $size / 100.0, 66.0 * $size / 100.0), # jaw
        [System.Drawing.PointF]::new(20.0 * $size / 100.0, 56.0 * $size / 100.0)
    )
    $whalePath.AddCurve($pts, 0.4)

    # Whale gradient fill (Cyan to Emerald Green)
    $whaleBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.Point(0, 0)),
        (New-Object System.Drawing.Point($size, $size)),
        [System.Drawing.Color]::FromArgb(255, 0, 229, 255),
        [System.Drawing.Color]::FromArgb(255, 16, 185, 129)
    )
    $g.FillPath($whaleBrush, $whalePath)

    # Whale outline
    $whaleOutline = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(240, 255, 255, 255), [Math]::Max(1.0, $size * 0.02))
    $g.DrawPath($whaleOutline, $whalePath)

    # Eye
    $eyeBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255, 255))
    $g.FillEllipse($eyeBrush, [float](28.0 * $size / 100.0), [float](50.0 * $size / 100.0), [float]($size * 0.035), [float]($size * 0.035))

    # Water spout / Radar blips
    $blipBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 0, 255, 200))
    $g.FillEllipse($blipBrush, [float](42.0 * $size / 100.0), [float](27.0 * $size / 100.0), [float]($size * 0.03), [float]($size * 0.03))
    $g.FillEllipse($blipBrush, [float](46.0 * $size / 100.0), [float](21.0 * $size / 100.0), [float]($size * 0.035), [float]($size * 0.035))
    $g.FillEllipse($blipBrush, [float](52.0 * $size / 100.0), [float](16.0 * $size / 100.0), [float]($size * 0.04), [float]($size * 0.04))

    # Candlestick chart accent near bottom
    $candleGreen = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 34, 197, 94))
    $candlePen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 34, 197, 94), [Math]::Max(1.0, $size * 0.015))
    
    # Candle 1
    $g.DrawLine($candlePen, [float]($size * 0.25), [float]($size * 0.76), [float]($size * 0.25), [float]($size * 0.88))
    $g.FillRectangle($candleGreen, [float]($size * 0.23), [float]($size * 0.79), [float]($size * 0.04), [float]($size * 0.06))
    
    # Candle 2 (higher)
    $g.DrawLine($candlePen, [float]($size * 0.35), [float]($size * 0.72), [float]($size * 0.35), [float]($size * 0.86))
    $g.FillRectangle($candleGreen, [float]($size * 0.33), [float]($size * 0.74), [float]($size * 0.04), [float]($size * 0.08))

    # Candle 3 (highest)
    $g.DrawLine($candlePen, [float]($size * 0.45), [float]($size * 0.68), [float]($size * 0.45), [float]($size * 0.84))
    $g.FillRectangle($candleGreen, [float]($size * 0.43), [float]($size * 0.70), [float]($size * 0.04), [float]($size * 0.10))

    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
}

Create-WhaleIcon 512 "wwwroot\icon-512.png"
Create-WhaleIcon 192 "wwwroot\icon-192.png"
Create-WhaleIcon 256 "assets\whale-256.png"
Create-WhaleIcon 64 "assets\whale-64.png"
Create-WhaleIcon 32 "assets\whale-32.png"
Create-WhaleIcon 16 "assets\whale-16.png"

# Convert to standard Windows .ico file with multiple resolutions
$iconFile = "assets\whale.ico"
$fs = [System.IO.File]::Create($iconFile)
$bw = New-Object System.IO.BinaryWriter($fs)

# ICONDIR header: 0, 1 (icon type), count
$sizes = @(16, 32, 64, 256)
$bw.Write([uint16]0)
$bw.Write([uint16]1)
$bw.Write([uint16]$sizes.Count)

$pngBytesList = @()
foreach ($s in $sizes) {
    $bytes = [System.IO.File]::ReadAllBytes("assets\whale-$s.png")
    $pngBytesList += ,$bytes
}

$offset = 6 + ($sizes.Count * 16)
for ($i = 0; $i -lt $sizes.Count; $i++) {
    $s = $sizes[$i]
    $bWidth = if ($s -eq 256) { 0 } else { [byte]$s }
    $bHeight = if ($s -eq 256) { 0 } else { [byte]$s }
    $bw.Write([byte]$bWidth)
    $bw.Write([byte]$bHeight)
    $bw.Write([byte]0) # palette
    $bw.Write([byte]0) # reserved
    $bw.Write([uint16]1) # color planes
    $bw.Write([uint16]32) # bpp
    $bw.Write([uint32]$pngBytesList[$i].Length) # image size
    $bw.Write([uint32]$offset) # offset
    $offset += $pngBytesList[$i].Length
}

for ($i = 0; $i -lt $sizes.Count; $i++) {
    $bw.Write($pngBytesList[$i])
}

$bw.Close()
$fs.Close()

Copy-Item "assets\whale.ico" "wwwroot\favicon.ico"
Write-Host "Icons generated successfully!"
