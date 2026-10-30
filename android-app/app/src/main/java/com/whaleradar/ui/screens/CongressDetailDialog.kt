package com.whaleradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.whaleradar.data.model.CongressTrade
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccentGlow
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.RedSell

@Composable
fun CongressDetailDialog(
    trade: CongressTrade,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onDismiss: () -> Unit,
    isEn: Boolean = false
) {
    val uriHandler = LocalUriHandler.current
    val isDem = trade.party.uppercase() == "D" || trade.party.contains("Dem", ignoreCase = true)
    val isRep = trade.party.uppercase() == "R" || trade.party.contains("Rep", ignoreCase = true)
    val partyColor = if (isDem) Color(0xFF3B82F6) else if (isRep) Color(0xFFEF4444) else Color(0xFF94A3B8)
    val partyName = if (isEn) {
        if (isDem) "Democratic Party (Democrat)" else if (isRep) "Republican Party (Republican)" else "Independent"
    } else {
        if (isDem) "Demokratische Partei (Demokrat)" else if (isRep) "Republikanische Partei (Republikaner)" else "Unabhängig"
    }

    val chamberName = if (isEn) "US House of Representatives" else "US-Repräsentantenhaus (House of Representatives)"
    val isPurchase = trade.transactionType.lowercase().let { it.contains("purchase") || it.contains("kauf") }
    val typeColor = if (isPurchase) GreenBuy else RedSell
    val typeLabel = if (isPurchase) (if (isEn) "🟢 BUY (Purchase)" else "🟢 KAUF (Purchase)") else (if (isEn) "🔴 SALE" else "🔴 VERKAUF (Sale)")

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
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(partyColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = trade.filerName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$chamberName · $partyName",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = typeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, typeColor.copy(alpha = 0.35f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = typeLabel,
                            color = typeColor,
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

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Asset & Transaktions-Box mit SCHÖNER, HOCH-KONTRASTIERTER WERTESPANNE
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "📈 TRADED STOCK & AMOUNT" else "📈 GEHANDELTE AKTIE & BETRAG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Ticker & Firmenname
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = trade.ticker.ifEmpty { "STOCK" },
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = trade.assetName.ifEmpty { if (isEn) "Unknown Security" else "Unbekanntes Wertpapier" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // WERTESPANNE - Groß, kontrastreich, schön zentriert und perfekt lesbar
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.background,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenBuy.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEn) "💰 STATUTORY AMOUNT RANGE:" else "💰 GESETZLICHE WERTE-SPANNE:",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GreenBuy
                                        )
                                        Surface(
                                            color = GreenBuy.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isEn) "STOCK Act Mandatory" else "STOCK Act Pflichtangabe",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GreenBuy,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = trade.amountRangeLabel.ifBlank { "-" },
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 0.5.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isEn)
                                            "Note: US lawmakers are required by law to disclose transactions in statutory value ranges rather than exact share counts or dollar amounts."
                                        else
                                            "Hinweis: US-Politiker sind gesetzlich verpflichtet, Transaktionen in Wertspannen statt exakter Stückzahlen/Beträge offenzulegen.",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 13.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. STOCK Act Fristen- & Datums-Zeitplan
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "📅 TRADE & DISCLOSURE DATES (STOCK ACT SCHEDULE)" else "📅 STICHTAG & MELDUNG (STOCK ACT ZEITPLAN)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Handelstag
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEn) "Transaction Date (Execution):" else "Handelstag (Börsen-Ausführung):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEn) "The day the politician actually bought or sold the security." else "Der Tag, an dem der Politiker die Aktie real kaufte bzw. verkaufte.",
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
                                        text = trade.transactionDate.ifEmpty { "-" },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            // Meldedatum
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEn) "Official Disclosure & Filing Date:" else "Offizielles Melde- & Eintragungsdatum:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEn) "When the Periodic Transaction Report (PTR) was submitted to Congress." else "Wann der Transaktionsbericht (PTR) beim Kongress eingereicht wurde.",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 13.sp
                                    )
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = trade.filingDate.ifEmpty { "-" },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            // Meldeverzögerung & 45-Tage-Frist
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isEn) "Filing Delay (45-Day STOCK Act Deadline):" else "Meldeverzögerung (45-Tage STOCK Act Frist):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEn) {
                                            if (trade.isLate) "⚠️ Late: Statutory 45-day deadline exceeded!"
                                            else "✅ Timely filing (within 45 days)."
                                        } else {
                                            if (trade.isLate) "⚠️ Verspätet: Gesetzliche 45-Tage Frist überschritten!"
                                            else "✅ Fristgerecht eingereicht (innerhalb der 45 Tage)."
                                        },
                                        fontSize = 10.sp,
                                        color = if (trade.isLate) AmberWhale else GreenBuy
                                    )
                                }
                                Surface(
                                    color = if (trade.isLate) AmberWhale.copy(alpha = 0.2f) else GreenBuy.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isEn) "${trade.daysToFile} Days" else "${trade.daysToFile} Tage",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (trade.isLate) AmberWhale else GreenBuy,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (trade.hasCommitteeConflict) {
                    // 3. Ausschuss-Konflikt-Radar (Committee Watch)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (trade.hasCommitteeConflict) AmberWhale.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (trade.hasCommitteeConflict) AmberWhale.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (trade.hasCommitteeConflict) "⚠️ " else "🛡️ ",
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isEn) "COMMITTEE CONFLICT RADAR (COMMITTEE WATCH)" else "AUSSCHUSS-KONFLIKT-RADAR (COMMITTEE WATCH)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (trade.hasCommitteeConflict) AmberWhale else GreenBuy
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (trade.hasCommitteeConflict) {
                                Text(
                                    text = if (isEn) "Committee Conflict of Interest Detected:" else "Ausschuss-Interessenskonflikt ermittelt:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEn) trade.conflictDescEn.ifEmpty { "Politician serves on a committee directly overseeing or awarding contracts in this sector (${trade.conflictSector})." }
                                           else trade.conflictDescDe.ifEmpty { "Politiker ist Mitglied in einem Ausschuss, der direkt für die Regulierung oder Auftragsvergabe dieses Sektors (${trade.conflictSector}) zuständig ist." },
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                if (trade.conflictCommittee.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isEn) "Assigned Committee: ${trade.conflictCommittee}" else "Zuständiger Ausschuss: ${trade.conflictCommittee}",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberWhale
                                    )
                                }
                            } else {
                                Text(
                                    text = if (isEn) "No direct committee conflict detected for this stock's sector." else "Kein direkter Ausschuss-Konflikt mit dem Sektor dieser Aktie ermittelt.",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 4. Erklärung zu Kammer & Partei (U.S. Repräsentantenhaus)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "🏛️ BACKGROUND: CHAMBER & PARTY" else "🏛️ HINTERGRUND: KAMMER & PARTEI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = if (isEn) {
                                    "The US House of Representatives consists of 435 members. Lawmakers originate all federal appropriations, revenue bills, and tax incentive programs."
                                } else {
                                    "Das US-Repräsentantenhaus (House of Representatives) besteht aus 435 Abgeordneten. Sie initiieren alle Bundes-Finanzgesetze, Haushaltsbudgets und steuerlichen Förderprogramme über spezialisierte Fachausschüsse."
                                },
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 14.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Watchlist Button
                    if (trade.ticker.isNotBlank()) {
                        Button(
                            onClick = onToggleWatchlist,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOnWatchlist) AmberWhale else MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (isOnWatchlist) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Watchlist",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) {
                                    if (isOnWatchlist) "⭐ On Watchlist (Tap to remove)" else "⭐ Add $${trade.ticker} to Watchlist"
                                } else {
                                    if (isOnWatchlist) "⭐ Auf Watchlist (Tippen zum Entfernen)" else "⭐ $${trade.ticker} zur Watchlist hinzufügen"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isEn) "Close" else "Schließen",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
