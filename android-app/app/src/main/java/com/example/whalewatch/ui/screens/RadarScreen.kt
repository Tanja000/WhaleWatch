package com.example.whalewatch.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whalewatch.data.model.RadarData
import com.example.whalewatch.data.model.SuperSignal
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccent
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell

@Composable
fun RadarScreen(
    radar: RadarData,
    superSignals: List<SuperSignal>,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier,
    watchlist: Set<String> = emptySet(),
    onToggleWatchlist: ((String) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ================= HEADER & ERKLÄRUNG DER KOMBI-SIGNALE =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEn) "CONFLUENCE RADAR (COT + CONGRESS + SEC)" else "KOMBI-SIGNALE (COT + KONGRESS + SEC)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWhale
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isEn)
                        "A Kombi-Signal is triggered when three completely independent institutional and regulatory disclosure streams align simultaneously for a single stock:"
                    else
                        "Ein Kombi-Signal entsteht, wenn drei voneinander völlig unabhängige Markt- und behördliche Melderegister zeitgleich dieselbe Aktie positiv bewerten:",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Pillar 1: COT
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("1️⃣ ", fontSize = 12.sp)
                    Text(
                        text = if (isEn)
                            "CFTC COT Macro-Sentiment: Institutional hedge funds and large speculators build decisive net-long exposure in the broader sector or index."
                        else
                            "CFTC COT Makro-Sentiment: Institutionelle Großanleger und Hedgefonds bauen im übergeordneten Sektor oder Index eindeutige Netto-Long-Positionen auf.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                // Pillar 2: Congress
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("2️⃣ ", fontSize = 12.sp)
                    Text(
                        text = if (isEn)
                            "US Congress (STOCK Act): US Senators or House Representatives purchase shares with mandatory statutory disclosure."
                        else
                            "US-Kongress (STOCK Act): US-Senatoren oder Abgeordnete des Repräsentantenhauses kaufen Aktien dieses Unternehmens mit gesetzlicher Offenlegungspflicht.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                // Pillar 3: SEC Form 4 (No "C-Level"!)
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("3️⃣ ", fontSize = 12.sp)
                    Text(
                        text = if (isEn)
                            "SEC Form 4 Insiders: Corporate executives (such as Chief Executive Officers, Chief Financial Officers) and Board Directors invest their own private capital directly on the open market."
                        else
                            "SEC Form 4 Insider: Vorstände (wie Vorstandsvorsitzende, Finanzvorstände) und Aufsichtsräte investieren eigenes privates Kapital direkt über die Börse in das eigene Unternehmen.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Update-Zeiten & Bekanntgabe (Di, Fr ...)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏱️", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEn) "REPORTING SCHEDULE & ANNOUNCEMENT DAYS:" else "UPDATE-ZEITEN & BEKANNTGABE-TAGE:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccentGlow
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Schedule details: CFTC COT
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("• ", fontSize = 11.sp, color = AmberWhale)
                    Text(
                        text = if (isEn)
                            "CFTC COT Whales: Position cutoff every Tuesday after market close. Official publication every Friday at 3:30 PM EST (21:30 CET) by the CFTC regulator."
                        else
                            "CFTC COT Wale: Stichtag / Datenerfassung jeden Dienstag nach US-Börsenschluss. Offizielle Veröffentlichung jeden Freitag um 15:30 EST (21:30 MEZ) durch die US-Aufsichtsbehörde CFTC.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.5.sp
                    )
                }

                // Schedule details: Congress
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("• ", fontSize = 11.sp, color = AmberWhale)
                    Text(
                        text = if (isEn)
                            "US Congress (STOCK Act): Statutory reporting window between 30 and 45 days after trade. Updated continuously on trading days upon filing of new Periodic Transaction Reports (PTR)."
                        else
                            "US-Kongress (STOCK Act): Gesetzliche Offenlegungsfrist von 30 bis maximal 45 Tagen nach dem Handelstag. Laufende Aktualisierung an jedem Börsentag, sobald Abgeordnete PTR-Protokolle einreichen.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.5.sp
                    )
                }

                // Schedule details: SEC Form 4
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("• ", fontSize = 11.sp, color = AmberWhale)
                    Text(
                        text = if (isEn)
                            "SEC Form 4 Insiders: Strict 2-business-day statutory deadline following execution. Continuously updated in real-time via the SEC EDGAR system during and after market hours."
                        else
                            "SEC Form 4 Insider: Strenge Frist von maximal 2 Bankarbeitstagen nach Ausführung. Kontinuierlich live über das SEC EDGAR-System während und nach den Handelszeiten der Wall Street.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= TITELZEILE DER AKTIVEN SIGNALE =================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEn) "ACTIVE SUPER-SIGNALS (${superSignals.size})" else "AKTIVE KOMBI-SIGNALE (${superSignals.size})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp,
                        color = AmberWhale
                    )
                }
                Text(
                    text = if (isEn) "Stocks with concurrent backing from COT Whales, Congress & Corporate Insiders."
                           else "Aktien mit simultanem Rückenwind aus COT-Walen, Kongress & Firmenvorständen.",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ================= KARTEN DER SUPER-SIGNALE =================
        superSignals.forEach { sig ->
            val isOnWatchlist = watchlist.contains(sig.ticker)

            // Voller Parteiname statt (R) und (D)
            val partyLabel = when (sig.politicianParty.uppercase().trim()) {
                "D", "DEM" -> if (isEn) "Democrat" else "Demokrat"
                "R", "REP" -> if (isEn) "Republican" else "Republikaner"
                else -> sig.politicianParty
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, AmberWhale.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Ticker, Name, Sektor & Watchlist Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = AmberWhale.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "$${sig.ticker}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = AmberWhale,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = sig.companyName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = sig.sector,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Watchlist Icon Button direkt auf der Karte (Kein Score)
                        if (onToggleWatchlist != null) {
                            IconButton(
                                onClick = { onToggleWatchlist(sig.ticker) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isOnWatchlist) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Watchlist",
                                    tint = if (isOnWatchlist) AmberWhale else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ================= 1. QUELLE: COT MAKRO-SENTIMENT (RECHTSBÜNDIGE ZEITANGABEN) =================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ) {
                        Column(modifier = Modifier.padding(9.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "🐋 COT MACRO-SENTIMENT" else "🐋 COT MAKRO-SENTIMENT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = sig.cotWhaleSentiment,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenBuy,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sig.cotMarket,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = sig.cotWhaleFlow,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "⏱️ ${sig.cotTimeframe}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // ================= 2. QUELLE: KONGRESS STOCK ACT (VOLLE PARTEIBEZEICHNUNG) =================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ) {
                        Column(modifier = Modifier.padding(9.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "🏛️ CONGRESS STOCK ACT" else "🏛️ KONGRESS STOCK ACT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanAccent
                                )
                                Text(
                                    text = sig.politicianAmount,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenBuy,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sig.politicianName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "($partyLabel)",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyanAccent,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "📅 ${sig.politicianDate}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // ================= 3. QUELLE: SEC FORM 4 INSIDER =================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ) {
                        Column(modifier = Modifier.padding(9.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "🏢 SEC FORM 4 INSIDER" else "🏢 SEC FORM 4 INSIDER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberWhale
                                )
                                Text(
                                    text = sig.insiderValue,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenBuy,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sig.insiderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "(${sig.insiderRole})",
                                    fontSize = 10.5.sp,
                                    color = AmberWhale,
                                    textAlign = TextAlign.End
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "📝 ${sig.insiderShares} · ${sig.insiderDate}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
