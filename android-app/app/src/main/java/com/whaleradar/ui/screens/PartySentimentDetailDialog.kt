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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.whaleradar.data.model.CongressTrade
import com.whaleradar.data.model.DataTimeframe
import com.whaleradar.data.model.PartySentimentData
import com.whaleradar.data.model.TopPartyStock
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccentGlow
import com.whaleradar.theme.GreenBuy

@Composable
fun PartySentimentDetailDialog(
    stock: TopPartyStock,
    partySentiment: PartySentimentData,
    trades: List<CongressTrade>,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onTradeClick: (CongressTrade) -> Unit,
    onDismiss: () -> Unit,
    selectedTimeframe: DataTimeframe = DataTimeframe.MONTH_1,
    isEn: Boolean = false
) {
    val uriHandler = LocalUriHandler.current
    val isDem = stock.party.contains("Demokrat", ignoreCase = true)
    val partyColor = if (isDem) Color(0xFF3B82F6) else Color(0xFFEF4444)
    val partyCode = if (isDem) "D" else "R"

    // DEDUKTION: Keine Duplikate in den Ergebnissen anzeigen!
    val distinctTrades = trades
        .filter { it.ticker.equals(stock.ticker, ignoreCase = true) && it.party.uppercase() == partyCode }
        .distinctBy { "${it.filerName}_${it.transactionDate}_${it.amountRangeLabel}_${it.transactionType}" }

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
                // Header
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
                            val partyName = if (isEn) {
                                if (isDem) "Democrat" else "Republican"
                            } else {
                                stock.party
                            }
                            Text(
                                text = "$${stock.ticker} · $partyName",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (isEn) "${stock.category} · ${stock.count} buys registered" else "${stock.category} · ${stock.count} Käufe registriert",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = partyColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, partyColor.copy(alpha = 0.35f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isEn) "${stock.count} BUYS" else "${stock.count} KÄUFE",
                            color = partyColor,
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

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Summary computed from the real disclosed trades
                    val buyCountT = distinctTrades.count { it.transactionType.lowercase().contains("purchase") }
                    val saleCountT = distinctTrades.count { it.transactionType.lowercase().contains("sale") }
                    val politiciansCount = distinctTrades.map { it.filerName }.distinct().size
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = partyColor.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, partyColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "⚖️ PARTY SENTIMENT FOR $${stock.ticker}" else "⚖️ PARTEI-SENTIMENT FÜR $${stock.ticker}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = if (isEn)
                                    "In the selected period (${selectedTimeframe.labelEn}), $politiciansCount ${if (isDem) "Democratic" else "Republican"} politician(s) reported $buyCountT purchase(s) and $saleCountT sale(s) of $${stock.ticker}. Source: official disclosures of the US House and Senate."
                                else
                                    "Im gewählten Zeitraum (${selectedTimeframe.labelDe}) meldeten $politiciansCount ${if (isDem) "demokratische" else "republikanische"} Politiker $buyCountT Kauf/Käufe und $saleCountT Verkauf/Verkäufe von $${stock.ticker}. Quelle: offizielle Meldungen des US-Repräsentantenhauses und des Senats.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Einzel-Trades der Partei (MIT ALLEN VERFÜGBAREN DATEN)
                    val partyTitle = if (isEn) (if (isDem) "Democrat" else "Republican") else stock.party
                    Text(
                        text = if (isEn) "📋 All Recorded $partyTitle Trades for $${stock.ticker} (${distinctTrades.size})" else "📋 Alle erfassten ${stock.party}-Trades für $${stock.ticker} (${distinctTrades.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (distinctTrades.isEmpty()) {
                        Text(
                            text = if (isEn) "No individual trades found in the current period." else "Keine Einzeltrades im aktuellen Filterzeitraum gefunden.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            distinctTrades.forEach { trade ->
                                val chamberText = if (isEn) {
                                    if (trade.chamber.lowercase() == "senate") "Senate" else "House"
                                } else {
                                    if (trade.chamber.lowercase() == "senate") "Senat" else "Repräsentantenhaus"
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (trade.hasCommitteeConflict) AmberWhale.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        // Header: Name & Kammer
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(partyColor)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = trade.filerName + " · " + (if (trade.transactionType.lowercase().contains("purchase")) (if (isEn) "🟢 BUY" else "🟢 KAUF") else (if (isEn) "🔴 SALE" else "🔴 VERKAUF")),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Surface(
                                                color = partyColor.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = chamberText,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = partyColor,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Data Grid: Spanne & Daten
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (isEn) "Statutory Range:" else "Gesetzliche Spanne:",
                                                    fontSize = 9.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = trade.amountRangeLabel.ifBlank { "-" },
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = if (isEn) "Trade: ${trade.transactionDate} · Filed: ${trade.filingDate}" else "Handel: ${trade.transactionDate} · Meldung: ${trade.filingDate}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                val lateStatus = if (isEn) {
                                                    if (trade.isLate) "⚠️ Delayed" else "✅ On Time"
                                                } else {
                                                    if (trade.isLate) "⚠️ Verspätet" else "✅ Fristgerecht"
                                                }
                                                val windowLabel = if (isEn) "${trade.daysToFile}d window" else "${trade.daysToFile} Tage Frist"
                                                Text(
                                                    text = "$windowLabel ($lateStatus)",
                                                    fontSize = 9.5.sp,
                                                    color = if (trade.isLate) AmberWhale else GreenBuy
                                                )
                                            }
                                        }

                                        // Ausschuss-Konflikt falls vorhanden
                                        if (trade.hasCommitteeConflict) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                color = AmberWhale.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = if (isEn) "⚠️ Committee Conflict: ${trade.conflictCommittee} (${trade.conflictSector})" else "⚠️ Ausschuss-Konflikt: ${trade.conflictCommittee} (${trade.conflictSector})",
                                                    color = AmberWhale,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Details zu diesem Trade öffnen
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = { onTradeClick(trade) },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                            ) {
                                                Text(if (isEn) "Details for this trade ↗" else "Details zu diesem Trade ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Offizielle Quelle
                    Button(
                        onClick = {
                            try { uriHandler.openUri("https://disclosures-clerk.house.gov/FinancialDisclosure") } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isEn) "🏛️ Official Congress Disclosures ↗" else "🏛️ Offizielle Offenlegungsdaten (Congress Disclosures) ↗",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = Color.Black
                        )
                    }

                    // Watchlist Button
                    OutlinedButton(
                        onClick = onToggleWatchlist,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isOnWatchlist) AmberWhale else MaterialTheme.colorScheme.onSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isOnWatchlist) AmberWhale else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Watchlist",
                            tint = if (isOnWatchlist) AmberWhale else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) {
                                if (isOnWatchlist) "⭐ On Watchlist (Tap to remove)" else "⭐ Add $${stock.ticker} to Watchlist"
                            } else {
                                if (isOnWatchlist) "⭐ Auf Watchlist (Tippen zum Entfernen)" else "⭐ $${stock.ticker} zur Watchlist hinzufügen"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
