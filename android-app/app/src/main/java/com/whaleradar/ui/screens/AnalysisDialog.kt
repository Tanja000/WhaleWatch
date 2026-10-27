package com.whaleradar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.whaleradar.data.model.CongressTrade
import com.whaleradar.data.model.SecAlert
import com.whaleradar.data.model.StockAnalysis
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccent
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.RedSell

@Composable
fun AnalysisDialog(
    analysis: StockAnalysis,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onDismiss: () -> Unit,
    isEn: Boolean
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // ================= DIALOG HEADER =================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "$${analysis.ticker}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DETAILS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = analysis.companyName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                // ================= SCROLLABLE CONTENT =================
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // ================= 1. SÄULE: 🐋 COT MAKRO- & WAL-SENTIMENT =================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header with high-contrast Pill Badge (Always wide & horizontal, never vertical!)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "🐋 1. CFTC COT WHALE SENTIMENT" else "🐋 1. CFTC COT MAKRO- & WAL-SENTIMENT",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                )

                                val isLong = analysis.cotWhaleLongPct >= 50.0
                                val cotColor = if (isLong) GreenBuy else RedSell
                                val cotLabel = if (isLong) "${analysis.cotWhaleLongPct.toInt()}% LONG" else "${(100 - analysis.cotWhaleLongPct).toInt()}% SHORT"
                                Surface(
                                    color = cotColor.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, cotColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = cotLabel,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = cotColor,
                                        softWrap = false,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val cotFlowDisplay = if (isEn) {
                                analysis.cotWhaleFlow.replace("Akkumulation", "Accumulation").replace("Abbau", "Reduction")
                            } else analysis.cotWhaleFlow

                            val cotTimeframeDisplay = if (isEn) {
                                analysis.cotTimeframe
                                    .replace("Stichtag: Di,", "Cutoff: Tue,")
                                    .replace("Veröffentlichung: Fr,", "Release: Fri,")
                                    .replace("Bericht: Fr,", "Release: Fri,")
                            } else analysis.cotTimeframe

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (isEn) "Market & Sector:" else "Markt & Sektor:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = analysis.cotMarketName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (isEn) "Weekly Flow:" else "Wochen-Fluss (Flow):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cotFlowDisplay,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenBuy,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(text = if (isEn) "Period & Cutoff:" else "Zeitspanne & Stichtag:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cotTimeframeDisplay,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ================= 2. SÄULE: 🏛️ KONGRESS STOCK ACT =================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header with separate, crisp Buy/Sale Pills (Always horizontal!)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "🏛️ 2. CONGRESS STOCK ACT" else "🏛️ 2. KONGRESS STOCK ACT",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanAccent,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (analysis.congressBuys > 0) {
                                        Surface(
                                            color = GreenBuy.copy(alpha = 0.18f),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, GreenBuy.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = if (isEn) "${analysis.congressBuys}x BUY" else "${analysis.congressBuys}x KAUF",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = GreenBuy,
                                                softWrap = false,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                            )
                                        }
                                    }
                                    if (analysis.congressSales > 0) {
                                        Surface(
                                            color = RedSell.copy(alpha = 0.18f),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, RedSell.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = if (isEn) "${analysis.congressSales}x SALE" else "${analysis.congressSales}x VERKAUF",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = RedSell,
                                                softWrap = false,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                            )
                                        }
                                    }
                                    if (analysis.congressBuys == 0 && analysis.congressSales == 0) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "0 TRADES",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                softWrap = false,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Gesamt-Spanne banner when more than 1 trade
                            if (analysis.recentTrades.size > 1) {
                                val totalRange = calculateCongressTotalRange(analysis.recentTrades)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = CyanAccent.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                                        ) {
                                            Text("📊", fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isEn) "Total Range (${analysis.recentTrades.size} traders):" else "Gesamt-Spanne (${analysis.recentTrades.size} Käufer/Verkäufer):",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = totalRange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CyanAccent,
                                            softWrap = false,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (analysis.recentTrades.isEmpty()) {
                                Text(
                                    text = if (isEn) "No recorded politician trades for this stock." else "Keine Politiker-Trades für diese Aktie im aktuellen Zeitraum.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                analysis.recentTrades.forEach { trade ->
                                    val isBuy = trade.transactionType.lowercase().contains("purchase") || trade.transactionType.lowercase().contains("kauf")
                                    val isSen = trade.chamber.lowercase().contains("senat")
                                    val chamberClean = if (isEn) (if (isSen) "Senate" else "House") else (if (isSen) "Senat" else "Repräsentantenhaus")
                                    val isDem = trade.party == "D" || trade.party.contains("Dem", ignoreCase = true)
                                    val isRep = trade.party == "R" || trade.party.contains("Rep", ignoreCase = true)
                                    val partyLabel = when {
                                        isDem -> if (isEn) "Democrat" else "Demokrat"
                                        isRep -> if (isEn) "Republican" else "Republikaner"
                                        else -> trade.party
                                    }
                                    val partyBg = if (isDem) CyanAccent.copy(alpha = 0.18f) else if (isRep) RedSell.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant
                                    val partyColor = if (isDem) CyanAccent else if (isRep) RedSell else MaterialTheme.colorScheme.onSurface

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.5.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            // Row 1: Name + Party/Chamber Badge + Action Pill
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(end = 8.dp)
                                                ) {
                                                    Text(
                                                        text = trade.filerName,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(3.dp))
                                                    Surface(
                                                        color = partyBg,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = "$partyLabel · $chamberClean",
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = partyColor,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Surface(
                                                    color = if (isBuy) GreenBuy.copy(alpha = 0.18f) else RedSell.copy(alpha = 0.18f),
                                                    shape = RoundedCornerShape(5.dp),
                                                    border = BorderStroke(1.dp, if (isBuy) GreenBuy.copy(alpha = 0.5f) else RedSell.copy(alpha = 0.5f))
                                                ) {
                                                    Text(
                                                        text = if (isBuy) (if (isEn) "🟢 BUY" else "🟢 KAUF") else (if (isEn) "🔴 SALE" else "🔴 VERKAUF"),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (isBuy) GreenBuy else RedSell,
                                                        softWrap = false,
                                                        maxLines = 1,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Row 2: Spanne + Daten
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = if (isEn) "Range: ${trade.amountRangeLabel}" else "Spanne: ${trade.amountRangeLabel}",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = CyanAccent
                                                )
                                                Text(
                                                    text = if (isEn) "Trade: ${trade.transactionDate} · Filed: ${trade.filingDate}" else "Handel: ${trade.transactionDate} · Meldung: ${trade.filingDate}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            if (trade.hasCommitteeConflict) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Surface(
                                                    color = AmberWhale.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.35f))
                                                ) {
                                                    Text(
                                                        text = if (isEn) "⚠️ Committee Conflict: ${trade.conflictCommittee}" else "⚠️ Ausschuss-Konflikt: ${trade.conflictCommittee}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = AmberWhale,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ================= 3. SÄULE: 🏢 SEC FORM 4 INSIDER =================
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Header with crisp Filing Count Pill (Always horizontal!)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "🏢 3. SEC FORM 4 INSIDER ALERTS" else "🏢 3. SEC FORM 4 INSIDER-ALARME",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberWhale,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                                )
                                Surface(
                                    color = AmberWhale.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "${analysis.secAlertsCount} FILINGS",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberWhale,
                                        softWrap = false,
                                        maxLines = 1,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.5.dp)
                                    )
                                }
                            }

                            // Gesamt-Spanne / Volumen banner when more than 1 SEC filing
                            if (analysis.recentSec.size > 1) {
                                val totalSecFormatted = calculateSecTotalValue(analysis.recentSec)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = AmberWhale.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                                        ) {
                                            Text("📊", fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isEn) "Total Volume (${analysis.recentSec.size} Insiders):" else "Gesamt-Volumen (${analysis.recentSec.size} Insider):",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = totalSecFormatted,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AmberWhale,
                                            softWrap = false,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (analysis.recentSec.isEmpty()) {
                                Text(
                                    text = if (isEn) "No Form 4 insider filings recorded." else "Keine aktuellen Form 4 Insider-Filings verzeichnet.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                analysis.recentSec.forEach { sec ->
                                    val dateStr = if (sec.filingTime.isNotBlank()) sec.filingTime.take(16).replace('T', ' ') + (if (isEn) " EST" else " Uhr") else sec.filingDate
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.5.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            // Row 1: Name + Role + Buy/Sell Pill
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(end = 8.dp)
                                                ) {
                                                    Text(
                                                        text = sec.reportingOwner,
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = sec.role,
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Surface(
                                                    color = if (sec.isBuy) GreenBuy.copy(alpha = 0.18f) else RedSell.copy(alpha = 0.18f),
                                                    shape = RoundedCornerShape(5.dp),
                                                    border = BorderStroke(1.dp, if (sec.isBuy) GreenBuy.copy(alpha = 0.5f) else RedSell.copy(alpha = 0.5f))
                                                ) {
                                                    Text(
                                                        text = if (sec.isBuy) (if (isEn) "🟢 BUY" else "🟢 KAUF") else (if (isEn) "🔴 SALE" else "🔴 VERKAUF"),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (sec.isBuy) GreenBuy else RedSell,
                                                        softWrap = false,
                                                        maxLines = 1,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Row 2: Shares, Total Value & Date
                                            val sharesDisplay = if (isEn) sec.sharesFormatted.replace("Aktien", "Shares") else sec.sharesFormatted
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "$sharesDisplay · ${sec.totalValueFormatted}",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = GreenBuy
                                                )
                                                Text(
                                                    text = "Filing: $dateStr",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ================= WATCHLIST ACTION BUTTON =================
                Button(
                    onClick = onToggleWatchlist,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOnWatchlist) RedSell.copy(alpha = 0.15f) else AmberWhale,
                        contentColor = if (isOnWatchlist) RedSell else Color.Black
                    ),
                    border = if (isOnWatchlist) BorderStroke(1.dp, RedSell.copy(alpha = 0.4f)) else null
                ) {
                    Icon(
                        imageVector = if (isOnWatchlist) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Watchlist",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOnWatchlist) {
                            if (isEn) "Remove from Watchlist" else "Von Watchlist entfernen"
                        } else {
                            if (isEn) "Add to Watchlist" else "Zur Watchlist hinzufügen"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun calculateCongressTotalRange(trades: List<CongressTrade>): String {
    if (trades.isEmpty()) return "-"
    var minSum = 0L
    var maxSum = 0L
    var parsedCount = 0

    for (t in trades) {
        val raw = t.amountRangeLabel.replace("$", "").replace(",", "").trim()
        val parts = raw.split("-").map { it.trim() }
        if (parts.size == 2) {
            val min = parts[0].toLongOrNull() ?: 0L
            val max = parts[1].toLongOrNull() ?: 0L
            if (max > 0) {
                minSum += min
                maxSum += max
                parsedCount++
            }
        }
    }

    if (parsedCount > 1 && maxSum > 0) {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
        format.maximumFractionDigits = 0
        return "${format.format(minSum)} - ${format.format(maxSum)}"
    }
    return trades.firstOrNull()?.amountRangeLabel ?: "-"
}

fun calculateSecTotalValue(alerts: List<SecAlert>): String {
    val total = alerts.sumOf { it.totalValue }
    if (total > 0L) {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.US)
        format.maximumFractionDigits = 0
        return format.format(total)
    }
    return alerts.firstOrNull()?.totalValueFormatted ?: "-"
}
