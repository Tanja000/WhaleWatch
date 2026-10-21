package com.example.whalewatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.whalewatch.data.model.CotMarket
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CotDetailDialog(
    market: CotMarket,
    onDismiss: () -> Unit,
    isEn: Boolean = false
) {
    val uriHandler = LocalUriHandler.current
    val numFormat = NumberFormat.getNumberInstance(Locale.GERMANY)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = market.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${market.assetClass} · ${market.category.uppercase()}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val isBullish = market.signal.contains("BULLISCH", ignoreCase = true)
                    val signalColor = if (isBullish) GreenBuy else RedSell
                    Surface(
                        color = signalColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, signalColor.copy(alpha = 0.35f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = market.signal,
                            color = signalColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Stichtag & Eintragungsdaten (CFTC Veröffentlichungszyklus)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "📅 CFTC REPORTING SCHEDULE & TIMING" else "📅 STICHTAG & EINTRAGUNG (CFTC-MELDEZYKLUS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Kauf-/Verkaufsdatum (Stichtag)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEn) "Trade & Cutoff Date (Positions Recorded):" else "Stichtag / Kauf- & Verkaufszeitpunkt:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEn) "Brokers & clearing houses record actual open contracts up to Tuesday close."
                                               else "Handelsschluss am Dienstag: Exakter Erfassungszeitpunkt aller Käufe & Verkäufe.",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 13.sp
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = market.reportDate,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Divider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            // Eintragungs- & Veröffentlichungsdatum
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEn) "Official Registration & Release Date:" else "Offizielles Eintragungs- & Veröffentlichungsdatum:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEn) "CFTC audits, enters and officially releases data every Friday at 15:30 EST (3 days regulatory audit lag)."
                                               else "Eintragung und Freigabe durch die CFTC freitags um 15:30 EST (3 Tage behördliche Prüffrist).",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 13.sp
                                    )
                                }
                                Surface(
                                    color = AmberWhale.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${market.publicationDate}, 15:30 EST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWhale,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Report Period:" else "Berichtswoche:",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = market.reportWeek,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Erklärung: Was bedeuten Wale Netto, Wochen-Fluss & Retail?
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "💡 METRIC EXPLANATIONS & INVESTOR SIGNIFICANCE" else "💡 WAS BEDEUTEN DIE KENNZAHLEN FÜR AKTIONÄRE?",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Wale Netto Erklärung
                            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🐋 ",
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (isEn) "Wale Netto (Whale Net):" else "Wale Netto:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenBuy
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${market.whaleNetFormatted} Kontrakte",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (market.whaleNetFormatted.startsWith("+")) GreenBuy else RedSell
                                    )
                                }
                                Text(
                                    text = if (isEn) {
                                        "Difference between total Long (buy) and Short (sell) contracts held by institutional giants (commercial hedgers and large speculative funds). A positive value shows whales are net bullish on rising prices; negative indicates short positions or institutional hedging."
                                    } else {
                                        "Die Netto-Differenz zwischen Long- (Kauf) und Short- (Verkauf) Kontrakten institutioneller Großanleger (Commercials & Großspekulanten). Ein positiver Wert bedeutet, dass die Profis netto auf steigende Kurse setzen; ein negativer Wert signalisiert Short-Positionierung oder Absicherung."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(start = 18.dp, top = 2.dp)
                                )
                            }

                            // Wochen-Fluss Erklärung
                            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🌊 ",
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (isEn) "Wochen-Fluss (Weekly Flow):" else "Wochen-Fluss:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${if (market.whaleChangeNet >= 0) "+" else ""}${numFormat.format(market.whaleChangeNet)} (${market.whaleChangeDesc})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = if (isEn) {
                                        "Weekly net contract change compared to previous report cutoff. Indicates dynamic capital momentum: heavy weekly buying reflects aggressive liquidity injection, while contract unwinding during a rally signals weakening bullish momentum."
                                    } else {
                                        "Wöchentliche Veränderung der Netto-Positionen im Vergleich zur Vorwoche. Zeigt das dynamische Kapital-Momentum: Aggressive Zukäufe belegen frischen Kapitalzufluss, während Kontrakt-Abbau trotz steigender Kurse auf eine nachlassende Rallye hindeutet."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(start = 18.dp, top = 2.dp)
                                )
                            }

                            // Retail Erklärung
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "👥 ",
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (isEn) "Retail (Non-Reportable Traders):" else "Retail (Kleinanleger):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWhale
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${market.retailBullishPct.toInt()}% Bullish",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWhale
                                    )
                                }
                                Text(
                                    text = if (isEn) {
                                        "Small retail speculators whose positions fall below CFTC regulatory reporting thresholds. Historically acts as a contrarian indicator at market turning points: when retail sentiment is overwhelmingly one-sided (>70%+ Bullish), the market frequently reverses."
                                    } else {
                                        "Kleinspekulanten und Privatanleger mit Kontraktgrößen unterhalb der CFTC-Meldeschwelle. Gilt an Wendepunkten als Kontraindikator: Sind Privatanleger extrem einseitig bullisch (>70%), neigt der Markt oft dazu, die Masse auf dem falschen Fuß zu erwischen."
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(start = 18.dp, top = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Ausführliche Positions-Statistik
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "📊 DETAILED CFTC POSITION BREAKDOWN" else "📊 DETAILLIERTE CFTC POSITIONSAUFSCHLÜSSELUNG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Open Interest
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Open Interest (Total Active Contracts):" else "Gesamtes Open Interest (Offene Kontrakte):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = numFormat.format(market.openInterest),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                            // Wale Long / Short
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Whales Long vs. Short:" else "Wale Long vs. Short:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${numFormat.format(market.whaleLong)} (${market.whaleBullishPct.toInt()}%) / ${numFormat.format(market.whaleShort)} (${100 - market.whaleBullishPct.toInt()}%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Commercials
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Commercials (Hedgers & Producers) Net:" else "Commercials (Hedger & Produzenten) Netto:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${if (market.commercialNet >= 0) "+" else ""}${numFormat.format(market.commercialNet)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (market.commercialNet >= 0) GreenBuy else RedSell
                                )
                            }

                            // Retail Long / Short
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Retail Long vs. Short:" else "Retail Long vs. Short:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${numFormat.format(market.retailLong)} / ${numFormat.format(market.retailShort)} (${market.retailBullishPct.toInt()}% Bull)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWhale
                                )
                            }

                            if (market.marketRaw.isNotBlank()) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                                Text(
                                    text = if (isEn) "Official Contract Symbol / Exchange:" else "Offizieller Börsenkontrakt / Handelsplatz:",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = market.marketRaw,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Action Link: Official Source
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val url = if (market.cftcUrl.isNotBlank()) market.cftcUrl else "https://www.cftc.gov/MarketReports/CommitmentsofTraders/index.htm"
                            try { uriHandler.openUri(url) } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isEn) "🌐 Official CFTC COT Portal ↗" else "🌐 Offizieller CFTC COT Report (cftc.gov) ↗",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
