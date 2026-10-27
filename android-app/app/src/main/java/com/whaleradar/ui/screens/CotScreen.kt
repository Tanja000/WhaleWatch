package com.whaleradar.ui.screens

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
import com.whaleradar.data.model.CotMarket
import com.whaleradar.data.model.signalEn
import com.whaleradar.data.model.signalLevel
import com.whaleradar.data.model.RadarData
import com.whaleradar.data.model.buildMarketBreadth
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccent
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.RedSell

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
    val categories = listOf(
        "all" to (if (isEn) "All Markets" else "Alle Märkte"),
        "indices" to (if (isEn) "Indices" else "Indizes"),
        "commodities" to (if (isEn) "Commodities" else "Rohstoffe"),
        "rates" to (if (isEn) "Bonds" else "Anleihen"),
        "crypto" to (if (isEn) "Crypto" else "Krypto"),
        "currencies" to (if (isEn) "Currencies" else "Währungen")
    )

    // Long/short picture is calculated ONLY from the markets of the selected category
    val heroRadar: RadarData? = if (markets.isNotEmpty()) buildMarketBreadth(markets) else null

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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEn) {
                    "In financial markets, 'Whales' refer to the world's largest and most powerful institutional players – such as major investment banks, hedge funds, and asset managers. Every Tuesday, these institutional giants are legally required to disclose to the US regulator CFTC in the 'Commitments of Traders' (COT) report how many contracts they hold betting on rising prices (Long) versus falling prices (Short). Because these professionals command billions in capital and massive research teams, their positioning often drives major market trends over several weeks. When Whales heavily position on one side while retail traders bet in the opposite direction, it historically serves as one of the most reliable early indicators of a trend reversal. Here you can see at a glance whether big institutional money is currently betting on rising or falling markets."
                } else {
                    "Als „Wale“ bezeichnet man an der Börse die größten und finanzstärksten Marktteilnehmer der Welt – wie große Investmentbanken, Hedge-Fonds und institutionelle Vermögensverwalter. Jeden Dienstag müssen diese Großanleger der US-Börsenaufsicht CFTC im sogenannten „Commitments of Traders“-Bericht (COT) offenlegen, wie viele Verträge sie auf steigende Kurse (Long) oder fallende Kurse (Short) halten. Da diese Profis über riesige Forschungsabteilungen und Milliardenkapital verfügen, bewegen ihre Käufe und Verkäufe oft über Wochen hinweg die gesamten Finanzmärkte. Wenn sich die Wale massiv auf eine Seite stellen, während Kleinanleger (Retail) in die Gegenrichtung wetten, gilt dies als eines der zuverlässigsten Warnsignale für bevorstehende Trendwenden. Hier siehst du auf einen Blick, ob das große Geld aktuell auf steigende oder fallende Kurse setzt."
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }

        // ================= SIGNAL LOGIC EXPLANATION =================
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isEn) "🧭 HOW IS THE SIGNAL CALCULATED?" else "🧭 WIE ENTSTEHT DAS SIGNAL?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isEn)
                            "We compare the current net position of the large speculators with their own range of the last 52 weeks (COT index, 0–100): 80 or higher = strongly bullish, 60–80 = bullish, 40–60 = neutral, 20–40 = bearish, 20 or lower = strongly bearish. In addition the net position must match the direction (bullish only if net long, bearish only if net short), otherwise the signal stays neutral. Extreme values can also announce trend reversals. This is not a buy or sell recommendation."
                        else
                            "Wir vergleichen die aktuelle Netto-Position der Großspekulanten mit ihrer eigenen Spanne der letzten 52 Wochen (COT-Index, 0–100): ab 80 = stark bullisch, 60–80 = bullisch, 40–60 = neutral, 20–40 = bärisch, bis 20 = stark bärisch. Zusätzlich muss die Netto-Position zur Richtung passen (bullisch nur bei Netto-Long, bärisch nur bei Netto-Short), sonst bleibt das Signal neutral. Extreme Werte können auch Trendwenden ankündigen. Das ist keine Kauf- oder Verkaufsempfehlung.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 14.5.sp
                    )
                }
            }
        }

        // ================= 4. CATEGORY FILTER TABS =================
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

        // ================= LONG/SHORT OVERVIEW (only the markets of the selected category) =================
        if (heroRadar != null) {
            item {
                val catLabel = categories.firstOrNull { it.first == selectedCategory && it.first != "all" }?.second
                val shortCount = (heroRadar.whaleShortContracts).toInt()
                val longCount = (heroRadar.whaleLongContracts).toInt()
                val total = (longCount + shortCount).coerceAtLeast(1)
                val longPctInt = Math.round(longCount.toDouble() / total * 100.0).toInt()
                val badgeColor = when { heroRadar.badgeDe == "NEUTRAL" -> Color(0xFF94A3B8); heroRadar.whaleLongPct >= 50.0 -> GreenBuy; else -> RedSell }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = (if (isEn) "📊 CFTC COT WHALE SENTIMENT" else "📊 CFTC COT WAL-SENTIMENT") + (if (catLabel != null) " · " + catLabel.uppercase() else ""),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )
                            Surface(color = badgeColor, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    text = if (isEn) heroRadar.badgeEn else heroRadar.badgeDe,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    softWrap = false,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = if (isEn) "🟢 Net long: $longCount of $total markets" else "🟢 Netto Long: $longCount von $total Märkten",
                                fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = GreenBuy
                            )
                            Text(
                                text = if (isEn) "🔴 Net short: $shortCount" else "🔴 Netto Short: $shortCount",
                                fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = RedSell
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))) {
                            Box(modifier = Modifier.weight(longCount.toFloat().coerceAtLeast(0.01f)).fillMaxHeight().background(GreenBuy))
                            Box(modifier = Modifier.weight(shortCount.toFloat().coerceAtLeast(0.01f)).fillMaxHeight().background(RedSell))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isEn) "Share of markets: Long $longPctInt% · Short ${100 - longPctInt}%"
                                   else "Anteil der Märkte: Long $longPctInt% · Short ${100 - longPctInt}%",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEn) heroRadar.whaleMoodEn else heroRadar.whaleMoodDe,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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

        // ================= 5. MARKETS LIST =================
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
    val numFormat = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY)
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
            val localizedName = if (isEn) {
                market.name
                    .replace("Silber", "Silver")
                    .replace("WTI Rohöl", "WTI Crude Oil")
                    .replace("Erdgas", "Natural Gas")
                    .replace("Kupfer", "Copper")
            } else market.name
            val localizedAssetClass = if (isEn) {
                market.assetClass
                    .replace("Edelmetall / Safe Haven", "Precious Metal / Safe Haven")
                    .replace("Edelmetall / Industrie", "Precious Metal / Industrial")
                    .replace("Energie & Rohstoffe", "Energy & Commodities")
                    .replace("Industriemetall & Konjunktur", "Industrial Metal & Macro")
                    .replace("US Staatsanleihen (10J)", "US Treasury Notes (10Y)")
                    .replace("US Staatsanleihen (30J)", "US Treasury Bonds (30Y)")
                    .replace("US Staatsanleihen", "US Treasury Bonds")
                    .replace("Krypto / Digitales Gold", "Crypto / Digital Gold")
                    .replace("Krypto / Risk-On", "Crypto / Risk-On")
                    .replace("G10 Leitwährung", "G10 Major Currency")
                    .replace("G10 Währung / Carry", "G10 Currency / Carry")
            } else market.assetClass
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = localizedName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (market.cftcCode.isNotBlank()) "$localizedAssetClass · CFTC #${market.cftcCode}" else localizedAssetClass,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val sigLevel = market.signalLevel()
                val badgeColor = when { sigLevel > 0 -> GreenBuy; sigLevel < 0 -> RedSell; else -> Color(0xFF94A3B8) }
                val signalText = if (isEn) market.signalEn() else market.signal
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
                        softWrap = false,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 1: Primary FuturesBench Metrics: Whale Net (Large Spec Net), Weekly Change (Δ w/w), COT Index (3Y)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "LARGE SPEC NET" else "WALE NETTO",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = market.whaleNetFormatted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (market.whaleNet >= 0) GreenBuy else RedSell
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val chgFormatted = "${if (market.whaleChangeNet >= 0) "+" else ""}${numFormat.format(market.whaleChangeNet)}"
                    Text(
                        text = if (isEn) "WEEKLY Δ (W/W)" else "WOCHEN-FLUSS (Δ)",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = chgFormatted,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (market.whaleChangeNet >= 0) GreenBuy else RedSell
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "COT INDEX (3Y)",
                        fontSize = 9.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${market.cotIndex3y}%",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Secondary FuturesBench Metrics: Open Interest, Commercials Net, Small Spec (Retail) Net
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                    .padding(vertical = 6.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "OPEN INTEREST",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = numFormat.format(market.openInterest),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "COMMERCIAL NET" else "COMMERCIALS NETTO",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (market.commercialNet >= 0) "+" else ""}${numFormat.format(market.commercialNet)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (market.commercialNet >= 0) GreenBuy else RedSell
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "SMALL SPEC NET" else "RETAIL NETTO",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${if (market.retailNet >= 0) "+" else ""}${numFormat.format(market.retailNet)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberWhale
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
                    text = if (isEn) "Report Date: ${market.reportDate} · Legacy Futures-Only" else "Stichtag: ${market.reportDate} · Legacy Futures-Only",
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
