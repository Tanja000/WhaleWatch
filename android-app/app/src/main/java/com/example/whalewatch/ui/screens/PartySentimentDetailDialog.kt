package com.example.whalewatch.ui.screens

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
import com.example.whalewatch.data.model.CongressTrade
import com.example.whalewatch.data.model.PartySentimentData
import com.example.whalewatch.data.model.TopPartyStock
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy

@Composable
fun PartySentimentDetailDialog(
    stock: TopPartyStock,
    partySentiment: PartySentimentData,
    trades: List<CongressTrade>,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onTradeClick: (CongressTrade) -> Unit,
    onDismiss: () -> Unit,
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
                            Text(
                                text = "$${stock.ticker} · ${stock.party}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${stock.category} · ${stock.count} Käufe registriert",
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
                            text = "${stock.count} KÄUFE",
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
                    // 1. Sektor & Partei-Drift Analyse
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = partyColor.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, partyColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "⚖️ PARTEI-SENTIMENT & SEKTOR-FOKUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = if (isDem) {
                                    "Demokratische Abgeordnete fokussieren sich überwiegend auf Large-Cap Technologie, Software, KI und erneuerbare Energien. Käufe wie $${stock.ticker} spiegeln den klaren parteipolitischen Innovations- und Tech-Fokus wider."
                                } else {
                                    "Republikanische Abgeordnete allokieren traditionell bevorzugt in Industrie, Spezialchemie, Verteidigung und traditionelle Rohstoffe & Energie. $${stock.ticker} zählt zu den Kernwerten des republikanischen Portfolios."
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Gesamt-Statistik der Partei
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "📊 GESAMT-VERGLEICH IM KONGRESS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Demokraten Gesamtkäufe:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${partySentiment.demBuyCount} Trades",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3B82F6)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Republikaner Gesamtkäufe:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${partySentiment.repBuyCount} Trades",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Einzel-Trades der Partei (MIT ALLEN VERFÜGBAREN DATEN)
                    Text(
                        text = "📋 Alle erfassten ${stock.party}-Trades für $${stock.ticker} (${distinctTrades.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (distinctTrades.isEmpty()) {
                        Text(
                            text = "Keine Einzeltrades im aktuellen Filterzeitraum gefunden.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            distinctTrades.forEach { trade ->
                                val chamberText = if (trade.chamber.lowercase() == "senate") "Senat" else "Repräsentantenhaus"
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
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(partyColor)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = trade.filerName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
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
                                                    text = "Gesetzliche Spanne:",
                                                    fontSize = 9.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = trade.amountRangeLabel.ifBlank { "$1,001 - $15,000" },
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "Handel: ${trade.transactionDate} · Meldung: ${trade.filingDate}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "${trade.daysToFile} Tage Frist (${if (trade.isLate) "⚠️ Verspätet" else "✅ Fristgerecht"})",
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
                                                    text = "⚠️ Ausschuss-Konflikt: ${trade.conflictCommittee} (${trade.conflictSector})",
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
                                                Text("Details zu diesem Trade ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                            text = "🏛️ Offizielle Offenlegungsdaten (Congress Disclosures) ↗",
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
                            text = if (isOnWatchlist) "⭐ Auf Watchlist (Tippen zum Entfernen)" else "⭐ $${stock.ticker} zur Watchlist hinzufügen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
