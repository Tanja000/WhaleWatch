package com.example.whalewatch.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
    onToggleWatchlist: ((String) -> Unit)? = null,
    onOpenLegalDisclaimer: (() -> Unit)? = null
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
                        text = if (isEn) "CONFLUENCE RADAR (3-SOURCE SIGNALS)" else "KOMBI-SIGNALE (3-QUELLEN-RADAR)",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWhale
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isEn)
                        "A Confluence Signal is triggered when three completely independent institutional and regulatory disclosure streams align simultaneously for a single stock:"
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
                            "CFTC COT Macro-Sentiment: Institutional hedge funds and large speculators build decisive net-long exposure in the broader sector or index (Cutoff: every Tuesday · Release: every Friday at 15:30 EST)."
                        else
                            "CFTC COT Makro-Sentiment: Institutionelle Großanleger und Hedgefonds bauen im übergeordneten Sektor oder Index eindeutige Netto-Long-Positionen auf (Stichtag: jeden Dienstag · Veröffentlichung: jeden Freitag 15:30 EST / 21:30 MEZ).",
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
                            "US Congress (STOCK Act): US Senators or House Representatives purchase shares with mandatory statutory disclosure (Continuous daily releases Mon–Fri · Statutory filing deadline: max. 45 days after trade)."
                        else
                            "US-Kongress (STOCK Act): US-Senatoren oder Abgeordnete des Repräsentantenhauses kaufen Aktien dieses Unternehmens mit gesetzlicher Offenlegungspflicht (Tägliche Meldungen Mo–Fr · Gesetzliche Meldefrist: max. 45 Tage nach Handel).",
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
                            "SEC Form 4 Insiders: Corporate executives (such as Chief Executive Officers, Chief Financial Officers) and Board Directors invest their own private capital directly on the open market (Real-time releases Mon–Fri · Mandatory filing within 2 business days / T+2)."
                        else
                            "SEC Form 4 Insider: Vorstände (wie Vorstandsvorsitzende, Finanzvorstände) und Aufsichtsräte investieren eigenes privates Kapital direkt über die Börse in das eigene Unternehmen (Börsentäglich in Echtzeit Mo–Fr · Pflicht-Meldung binnen 2 Werktagen / T+2).",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Update & Disclosure Schedule Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isEn) "📅 DATA UPDATE & DISCLOSURE SCHEDULE" else "📅 UPDATE- & BEKANNTGABE-ZEITPLAN",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isEn)
                                "• Tue & Fri (CFTC COT): Positions recorded every Tuesday (market close), published every Friday at 15:30 EST.\n• Mon–Fri (SEC Form 4): Continuous real-time EDGAR disclosures within 2 business days (T+2) after trade.\n• Mon–Fri (US Congress): Daily House & Senate disclosures within the 45-day STOCK Act deadline."
                            else
                                "• Di & Fr (CFTC COT): Positions-Stichtag jeden Dienstag (Börsenschluss), Bekanntgabe jeden Freitag um 15:30 EST (21:30 MEZ).\n• Mo–Fr (SEC Form 4): Börsentägliche Echtzeit-Veröffentlichung binnen max. 2 Werktagen (T+2) nach Kauf/Verkauf.\n• Mo–Fr (US-Kongress): Laufende tägliche Bekanntgabe eingereichter Transaktionen (45-Tage-Frist nach STOCK Act).",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
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

            val cotSentimentDisplay = if (isEn) {
                sig.cotWhaleSentiment.replace("Kontrakte", "Contracts")
            } else sig.cotWhaleSentiment

            val cotFlowDisplay = if (isEn) {
                sig.cotWhaleFlow.replace("Akkumulation", "Accumulation").replace("Abbau", "Reduction")
            } else sig.cotWhaleFlow

            val cotTimeframeDisplay = if (isEn) {
                sig.cotTimeframe
                    .replace("Stichtag: Di,", "Cutoff: Tue,")
                    .replace("Bericht: Fr,", "Release: Fri,")
                    .replace("Veröffentlichung: Fr,", "Release: Fri,")
            } else sig.cotTimeframe

            val polDateDisplay = if (isEn) {
                sig.politicianDate
                    .replace("Handel:", "Trade:")
                    .replace("Meldung:", "Filed:")
            } else sig.politicianDate

            val insiderSharesDisplay = if (isEn) {
                sig.insiderShares.replace("Aktien", "Shares")
            } else sig.insiderShares

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
                                    text = cotSentimentDisplay,
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
                                    text = cotFlowDisplay,
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
                                    text = "⏱️ $cotTimeframeDisplay",
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
                                    text = "📅 $polDateDisplay",
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
                                    text = "📝 $insiderSharesDisplay · ${sig.insiderDate}",
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

        // ================= RECHTLICHER HINWEIS / KEINE ANLAGEBERATUNG FOOTER =================
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenLegalDisclaimer?.invoke() },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEn) "⚖️ No Financial Advice · Legal Notice, Sources & Privacy"
                           else "⚖️ Keine Anlageberatung · Rechtliche Hinweise, Quellen & Datenschutz",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEn) {
                        "All data (CFTC, US Congress, SEC) is for informational purposes only and does not constitute investment advice. Independent app – not affiliated with any US government agency. Tap for full details."
                    } else {
                        "Alle Angaben (CFTC, US-Kongress, SEC) dienen ausschließlich zu Informationszwecken und stellen keine Anlageberatung oder Kaufempfehlung dar. Unabhängige App ohne Behörden-Zugehörigkeit. Für Details hier tippen."
                    },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
