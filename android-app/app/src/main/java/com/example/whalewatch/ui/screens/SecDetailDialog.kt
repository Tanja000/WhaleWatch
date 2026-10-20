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
import com.example.whalewatch.data.model.SecAlert
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell

@Composable
fun SecDetailDialog(
    alert: SecAlert,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onOpenAnalysis: (String) -> Unit,
    onDismiss: () -> Unit,
    isEn: Boolean
) {
    val uriHandler = LocalUriHandler.current
    val tagColor = if (alert.isBuy) GreenBuy else if (alert.code == "S") RedSell else Color(0xFF94A3B8)
    val txLabel = if (isEn) alert.txTypeEn else alert.txTypeDe
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header: Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEn) "SEC FORM 4 FILING DETAILS" else "SEC FORM 4 FILING DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Ticker & Company Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (alert.ticker.isNotBlank()) "$${alert.ticker}" else alert.issuer,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow
                            )
                            Text(
                                text = alert.issuer,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = tagColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = txLabel,
                                color = tagColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cluster Alert Banner if applicable
                    if (alert.isCluster) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AmberWhale.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWhale.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isEn) "🔥 CLUSTER PURCHASE DETECTED (${alert.clusterCount}x Filings)"
                                           else "🔥 CLUSTER-KAUF ERKANNT (${alert.clusterCount}x Filings)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AmberWhale
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isEn) "Multiple corporate insiders concurrently accumulated shares at ${alert.issuer}. Cluster filings historically exhibit a high probability of outperformance over subsequent quarters."
                                           else "Mehrere Vorstandsmitglieder kauften synchron Aktien bei ${alert.issuer}. Cluster-Käufe erzielen historisch eine überdurchschnittliche Trefferquote bei anstehenden Kursbewegungen.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Key Insider Metrics Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEn) "REPORTING INSIDER" else "MELDENDER INSIDER",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = alert.reportingOwner,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = alert.role,
                                        fontSize = 11.sp,
                                        color = CyanAccentGlow
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isEn) "CODE & TYPE" else "CODE & TYP",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Code ${alert.code.ifBlank { "P" }}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = tagColor
                                    )
                                    Text(
                                        text = txLabel,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

                            // 3 KPI columns
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (isEn) "SHARES" else "STÜCKZAHL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = alert.sharesFormatted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isEn) "PRICE" else "KURS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = alert.priceFormatted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isEn) "TOTAL VALUE" else "GESAMTWERT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = alert.totalValueFormatted,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = tagColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shareholder Deep Dive Assessment
                    Text(
                        text = if (isEn) "💡 SHAREHOLDER ASSESSMENT & VALUE" else "💡 AKTIONÄRS-BEWERTUNG & MEHRWERT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (alert.isBuy) {
                                if (alert.code == "P") {
                                    if (isEn) "High Conviction Open-Market Purchase: ${alert.reportingOwner} (${alert.role}) acquired shares using personal capital on the public exchange. Academic studies indicate corporate insiders outperform broad indices by +6% to +10% annually following direct market purchases."
                                    else "Kauf aus persönlichem Vermögen (Code P): ${alert.reportingOwner} (${alert.role}) erwarb Aktien direkt an der Börse. Studien belegen, dass direkte Insider-Käufe am Markt in den folgenden 6 bis 12 Monaten eine signifikante Überrendite erzielen, da Führungskräfte Quartalstrends als Erste wahrnehmen."
                                } else {
                                    if (isEn) "Grant or Option Execution: This transaction corresponds to contractually allocated equity or exercise of options without direct out-of-pocket investment."
                                    else "Zuteilung / Option: Diese Transaktion ist ein vertraglich vereinbarter Vergütungsbestandteil. Kein frisches Eigenkapital geflossen."
                                }
                            } else {
                                if (isEn) "Open Market Disposition (Sale): Insider trimmed position. Often conducted under predefined SEC Rule 10b5-1 trading plans for tax or portfolio rebalancing purposes."
                                else "Insider-Verkauf am Markt: Position verkleinert. Häufig im Rahmen geplanter SEC Rule 10b5-1 Verkaufspläne zur Steueroptimierung oder Vermögensstreuung. Erst bei mehreren Vorstandsverkäufen zeitgleich bärisch zu werten."
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filing Metadata
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "SEC Accession No.: ${alert.accessionNumber}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Filing Time: ${alert.filingTime}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Security: ${alert.securityTitle.ifBlank { "Common Stock" }}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action Buttons Bottom Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Open SEC EDGAR link button
                    Button(
                        onClick = {
                            val url = if (alert.filingUrl.isNotBlank()) alert.filingUrl else "https://www.sec.gov"
                            try { uriHandler.openUri(url) } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isEn) "📄 Open Original Form 4 on sec.gov ↗" else "📄 Original Form 4 Filing auf sec.gov ansehen ↗",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (alert.ticker.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onToggleWatchlist,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isOnWatchlist) "⭐ Watchlist (Entfernen)" else "⭐ + Watchlist",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    onDismiss()
                                    onOpenAnalysis(alert.ticker)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "📊 $${alert.ticker} Analyse ↗",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
