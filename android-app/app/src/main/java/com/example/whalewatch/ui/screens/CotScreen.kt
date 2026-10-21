package com.example.whalewatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whalewatch.data.model.CotMarket
import com.example.whalewatch.data.model.RadarData
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccent
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell

@Composable
fun CotScreen(
    markets: List<CotMarket>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onMarketClick: (CotMarket) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier,
    radar: RadarData? = null
) {
    var showGuide by remember { mutableStateOf(false) }

    val categories = listOf(
        "all" to (if (isEn) "All Markets" else "Alle Märkte"),
        "indices" to (if (isEn) "Indices" else "Indizes"),
        "commodities" to (if (isEn) "Commodities" else "Rohstoffe"),
        "currencies" to (if (isEn) "Currencies" else "Währungen"),
        "crypto" to "Crypto",
        "treasuries" to (if (isEn) "Bonds" else "Anleihen")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // ================= 1. INTRO HEADER =================
        item {
            Text(
                text = if (isEn) "Where are the Whales?" else "Wo stehen die Wale?",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (isEn) "CFTC Commitments of Traders (COT). Official positioning of institutional hedge funds vs. retail traders."
                       else "Commitments of Traders (COT) der US-Aufsichtsbehörde CFTC. Reale Positionen institutioneller Hedge-Fonds vs. Kleinanleger.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }

        // ================= 2. FELD 1: HERO COT WHALE CARD (AUS RADAR TAB HIERHER VERLEGT) =================
        if (radar != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header: Title & Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEn) "CFTC COT WHALE RADAR" else "CFTC COT WAL-SENTIMENT",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )

                            val isBullish = radar.whaleLongPct >= 50.0
                            val badgeColor = if (isBullish) GreenBuy else RedSell
                            val badgeText = if (isEn) radar.badgeEn else radar.badgeDe
                            Surface(
                                color = badgeColor,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Circular Dial Meter: Green for Long, Red for Short
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(92.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(82.dp)) {
                                    val strokeWidth = 10.dp.toPx()
                                    val arcSize = size.width - strokeWidth
                                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                                    val longSweep = (radar.whaleLongPct / 100.0 * 360.0).toFloat().coerceIn(0f, 360f)
                                    val shortSweep = (360f - longSweep).coerceIn(0f, 360f)

                                    // Long Arc (GREEN)
                                    drawArc(
                                        color = GreenBuy,
                                        startAngle = -90f,
                                        sweepAngle = longSweep,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = Size(arcSize, arcSize),
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )

                                    // Short Arc (RED)
                                    drawArc(
                                        color = RedSell,
                                        startAngle = -90f + longSweep,
                                        sweepAngle = shortSweep,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = Size(arcSize, arcSize),
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                }

                                val isLongDominant = radar.whaleLongPct >= radar.whaleShortPct
                                val dominantPct = if (isLongDominant) radar.whaleLongPct else radar.whaleShortPct
                                val dominantLabel = if (isLongDominant) "LONG" else "SHORT"
                                val dominantColor = if (isLongDominant) GreenBuy else RedSell

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${dominantPct.toInt()}%",
                                        color = dominantColor,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = dominantLabel,
                                        color = dominantColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Mood Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isEn) radar.whaleMoodEn else radar.whaleMoodDe,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isEn) radar.whaleSubEn else radar.whaleSubDe,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Dual Ratio Bar: Long & Short
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(GreenBuy)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Long: ${radar.whaleLongPct}% ",
                                    color = GreenBuy,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "(${radar.whaleLongFormatted})",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.5.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "(${radar.whaleShortFormatted}) ",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.5.sp
                                )
                                Text(
                                    text = "Short: ${radar.whaleShortPct}%",
                                    color = RedSell,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(RedSell)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(radar.whaleLongPct.toFloat().coerceAtLeast(1f))
                                    .background(GreenBuy)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(radar.whaleShortPct.toFloat().coerceAtLeast(1f))
                                    .background(RedSell)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Stichtag Pill (Zeitangabe rechtsbündig!)
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "CFTC As of:" else "CFTC Stichtag:",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${radar.reportDate} · ${radar.reportWeek}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }

        // ================= 3. FELD 2: CFTC COT ZEITSPANNEN & BERICHTSZYKLUS CARD (RECHTSBÜNDIGE ZEITANGABEN) =================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏱️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEn) "CFTC COT TIMEFRAMES & RELEASE CYCLE" else "CFTC COT ZEITSPANNEN & BERICHTSZYKLUS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanAccent,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Stichtag Row (Rechtsbündig!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "📅 Cut-off Date (Data Freeze):" else "📅 Stichtag (Datenerhebung):",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (isEn) "Every Tuesday (15:30 EST)" else "Jeden Dienstag (Börsenschluss)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Veröffentlichung Row (Rechtsbündig!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "📢 Release Day (CFTC Portal):" else "📢 Offizielle Veröffentlichung:",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (isEn) "Every Friday (21:30 CET)" else "Jeden Freitag (21:30 Uhr MEZ)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenBuy,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    // Gültigkeit Row (Rechtsbündig!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "⏳ Validity Period:" else "⏳ Gültigkeitszeitspanne:",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = if (isEn) "1 Week (until next report)" else "1 Woche (bis zum nächsten Report)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberWhale,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEn) "CFTC data shows institutional positioning with a 3-day reporting lag. Whales position for multi-week macroeconomic trends."
                               else "CFTC-Daten zeigen reale Großpositionen mit 3 Tagen Meldeverzögerung. Institutionelle Wale positionieren sich auf mehrwöchige Trends.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // ================= 4. EDUCATIONAL KNOWLEDGE GUIDE CARD (COLLAPSIBLE) =================
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGuide = !showGuide }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "💡", fontSize = 14.sp)
                            Text(
                                text = if (isEn) "COT Guide: Whale Net, Flow & Retail" else "COT-Kompass: Wale Netto, Fluss & Retail",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = if (showGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    AnimatedVisibility(visible = showGuide) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            HorizontalDivider(
                                modifier = Modifier.padding(bottom = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            // 1. Wale Netto
                            Row(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text("🐋 ", fontSize = 12.sp)
                                Column {
                                    Text(
                                        text = if (isEn) "Whale Net (Wale Netto):" else "Wale Netto:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenBuy
                                    )
                                    Text(
                                        text = if (isEn) {
                                            "Difference between Long and Short contracts held by institutional giants. Positive = net bullish; Negative = hedging or net short."
                                        } else {
                                            "Die Netto-Differenz zwischen Kauf- (Long) und Verkauf- (Short) Kontrakten der institutionellen Großanleger. Positiv = netto bullisch; Negativ = Absicherung oder Short."
                                        },
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            // 2. Wochen-Fluss
                            Row(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text("🌊 ", fontSize = 12.sp)
                                Column {
                                    Text(
                                        text = if (isEn) "Weekly Flow (Wochen-Fluss):" else "Wochen-Fluss:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccentGlow
                                    )
                                    Text(
                                        text = if (isEn) {
                                            "Weekly change in open contracts. Strong inflows demonstrate active accumulation, while position decreases warn of momentum fatigue."
                                        } else {
                                            "Wöchentliche Veränderung der Positionen. Zeigt das dynamische Momentum: Starke Zukäufe belegen frische Kaufkraft, während Kontrakt-Abbau vor Trendermüdung warnt."
                                        },
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            // 3. Retail
                            Row(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text("👥 ", fontSize = 12.sp)
                                Column {
                                    Text(
                                        text = if (isEn) "Retail (Kleinanleger):" else "Retail (Kleinanleger):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWhale
                                    )
                                    Text(
                                        text = if (isEn) {
                                            "Small non-reportable retail traders. Historically serves as a valuable contrarian indicator when positioning reaches extreme sentiment (>75%)."
                                        } else {
                                            "Kleinanleger-Kontingent. Dient an Wendepunkten als Kontra-Indikator: Extreme Einseitigkeit (>75% Retail Long) geht häufig Marktkorrekturen voraus."
                                        },
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ================= 5. CATEGORY FILTER TABS =================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { (catId, label) ->
                    val isSelected = selectedCategory == catId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(catId) },
                        label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // ================= 6. MARKETS LIST =================
        items(markets, key = { it.id }) { market ->
            CotMarketCard(
                market = market,
                onClick = { onMarketClick(market) },
                isEn = isEn
            )
        }
    }
}

@Composable
fun CotMarketCard(
    market: CotMarket,
    onClick: () -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Name & Signal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = market.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = market.assetClass,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val isBullish = market.signal.contains("BULLISCH", ignoreCase = true) || market.signal.contains("BULLISH", ignoreCase = true)
                val badgeColor = if (isBullish) GreenBuy else RedSell
                val signalText = if (isEn) (if (isBullish) "🟢 BULLISH" else "🔴 BEARISH") else market.signal
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = signalText,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row: Whale Net & Flow & Retail
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "WHALE NET" else "WALE NETTO",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = market.whaleNetFormatted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (market.whaleNetFormatted.startsWith("+")) GreenBuy else RedSell
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "WEEKLY FLOW" else "WOCHEN-FLUSS",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = market.whaleChangeDesc,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "RETAIL" else "RETAIL",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${market.retailBullishPct.toInt()}% Bull",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stichtag and Details Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEn) "Recorded: ${market.reportDate}" else "Stichtag: ${market.reportDate}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = "Details",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
