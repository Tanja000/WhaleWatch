package com.example.whalewatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whalewatch.data.model.CotMarket
import com.example.whalewatch.theme.AmberWhale
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
    modifier: Modifier = Modifier
) {
    var showGuide by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Intro
        Text(
            text = if (isEn) "Where are the Whales?" else "Wo stehen die Wale?",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isEn) "CFTC Commitments of Traders (COT). Institutional hedge funds vs. retail traders."
                   else "Commitments of Traders (COT) der US-Aufsichtsbehörde CFTC. Zeigt Positionen institutioneller Hedge-Fonds.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Educational Knowledge Guide Card (Collapsible)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                        Divider(
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
                                        "Differenz zwischen Long- und Short-Kontrakten institutioneller Profis. Positiver Wert = Wale setzen netto auf steigende Kurse; negativer Wert = Absicherung oder Spekulation auf fallende Kurse."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
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
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isEn) {
                                        "Weekly net contract change. Reveals dynamic momentum: rapid inflows indicate institutional conviction, while contract reductions warn of a fading trend."
                                    } else {
                                        "Wöchentliche Veränderung der Positionen. Zeigt das dynamische Momentum: Starke Zukäufe belegen frische Kaufkraft, während Kontrakt-Abbau vor Trendermüdung warnt."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
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
                                        "Non-reportable small traders. Often acts as a contrarian indicator at market tops/bottoms when sentiment becomes extremely one-sided (>70%)."
                                    } else {
                                        "Privatanleger unterhalb der CFTC-Meldegrenze. Dient an Extrempunkten als Kontraindikator: Sind Privatanleger extrem einseitig bullisch (>70%), drohen oft Gegenbewegungen."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        // 4. Stichtag vs. Veröffentlichung
                        Row {
                            Text("📅 ", fontSize = 12.sp)
                            Column {
                                Text(
                                    text = if (isEn) "CFTC Reporting Cycle:" else "CFTC-Meldezyklus (Stichtag vs. Veröffentlichung):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccentGlow
                                )
                                Text(
                                    text = if (isEn) {
                                        "Cutoff date is Tuesday close (positions recorded). Official CFTC audit and release occurs on Friday at 15:30 EST (3 days regulatory verification lag)."
                                    } else {
                                        "Kauf-/Verkaufserfassung erfolgt zum Börsenschluss am Dienstag. Die offizielle behördliche Prüfung und Veröffentlichung durch die CFTC erfolgt freitags um 15:30 EST (3 Tage Meldefrist)."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        val categories = listOf(
            "all" to (if (isEn) "All Markets" else "Alle Märkte"),
            "indices" to "📈 US-Indizes",
            "commodities" to (if (isEn) "🛢️ Commodities" else "🛢️ Rohstoffe"),
            "rates" to (if (isEn) "💵 Bonds & Rates" else "💵 Zinsen & Anleihen"),
            "crypto" to "₿ Krypto"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        // Markets List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(markets, key = { it.id }) { market ->
                CotMarketCard(
                    market = market,
                    onClick = { onMarketClick(market) },
                    isEn = isEn
                )
            }
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

                val isBullish = market.signal.contains("BULLISCH", ignoreCase = true)
                val badgeColor = if (isBullish) GreenBuy else RedSell
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = market.signal,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row: Whale Net & Flow
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
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
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
