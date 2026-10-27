package com.whaleradar.ui.screens

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
import com.whaleradar.data.model.CotMarket
import com.whaleradar.data.model.signalEn
import com.whaleradar.data.model.signalLevel
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccentGlow
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.RedSell
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

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = localizedName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$localizedAssetClass · ${market.category.uppercase()}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val sigLevel = market.signalLevel()
                    val signalColor = when { sigLevel > 0 -> GreenBuy; sigLevel < 0 -> RedSell; else -> Color(0xFF94A3B8) }
                    val localizedSignal = if (isEn) market.signalEn() else market.signal
                    Surface(
                        color = signalColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, signalColor.copy(alpha = 0.35f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = localizedSignal,
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
                                    text = if (isEn) market.reportWeek.replace("Woche", "Week") else market.reportWeek,
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
                                        text = if (isEn) "Whale Net:" else "Wale Netto:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenBuy
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isEn) "${market.whaleNetFormatted} Contracts" else "${market.whaleNetFormatted} Kontrakte",
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
                                val localizedFlowDesc = if (isEn) {
                                    market.whaleChangeDesc
                                        .replace("Akkumulation", "Accumulation")
                                        .replace("Abbau", "Reduction")
                                } else market.whaleChangeDesc
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "🌊 ",
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = if (isEn) "Weekly Flow:" else "Wochen-Fluss:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${if (market.whaleChangeNet >= 0) "+" else ""}${numFormat.format(market.whaleChangeNet)} ($localizedFlowDesc)",
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

                    // 3. Ausführliche Positions-Statistik (1:1 FuturesBench & CFTC Legacy Futures-Only)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "📊 FUTURESBENCH & CFTC LEGACY FUTURES-ONLY BREAKDOWN" else "📊 FUTURESBENCH & CFTC LEGACY FUTURES-ONLY DATEN",
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
                                    text = if (isEn) "Open Interest (Total Contracts):" else "Open Interest (Offene Kontrakte):",
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

                            // Large Speculators (Whales) Net & Weekly Change
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Large Speculators (Whales) Net:" else "Wale Netto (Large Speculators Net):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${market.whaleNetFormatted} (${if (market.whaleChangeNet >= 0) "+" else ""}${numFormat.format(market.whaleChangeNet)} w/w)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (market.whaleNet >= 0) GreenBuy else RedSell
                                )
                            }

                            // Wale Long / Short
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Whales Long vs. Short:" else "Wale Long vs. Short Kontrakte:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${numFormat.format(market.whaleLong)} (${market.whaleBullishPct}%) / ${numFormat.format(market.whaleShort)} (${Math.round((100.0 - market.whaleBullishPct) * 10.0) / 10.0}%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                            // Commercials Net & Long/Short
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Commercials (Hedgers) Net:" else "Commercials (Hedger) Netto:",
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
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Commercials Long vs. Short:" else "Commercials Long vs. Short:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${numFormat.format(market.commercialLong)} / ${numFormat.format(market.commercialShort)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                            // Small Traders (Retail) Net & Long/Short
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Small Speculators (Retail) Net:" else "Kleinanleger (Small Traders) Netto:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${if (market.retailNet >= 0) "+" else ""}${numFormat.format(market.retailNet)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWhale
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isEn) "Retail Long vs. Short:" else "Kleinanleger Long vs. Short:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${numFormat.format(market.retailLong)} / ${numFormat.format(market.retailShort)} (${market.retailBullishPct}% Bull)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWhale
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                            // COT Index 26W / 52W / 3Y & Z-Score
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "COT Index (26W / 52W / 3Y):",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${market.cotIndex26w}% / ${market.cotIndex52w}% / ${market.cotIndex3y}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanAccentGlow
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "3Y Z-Score & CFTC Code:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Z = ${market.zScore3y} · #${market.cftcCode}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
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

                // Bottom Action Links: FuturesBench + Official CFTC Source
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val url = if (market.futuresBenchUrl.isNotBlank()) market.futuresBenchUrl else "https://futuresbench.com/api/"
                            try { uriHandler.openUri(url) } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isEn) "🌐 Verify on FuturesBench.com ↗" else "🌐 Auf FuturesBench.com prüfen ↗",
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
