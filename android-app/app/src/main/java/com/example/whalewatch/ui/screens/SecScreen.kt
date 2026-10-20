package com.example.whalewatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whalewatch.data.model.SecAlert
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell

@Composable
fun SecScreen(
    alerts: List<SecAlert>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAlertClick: (SecAlert) -> Unit,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier
) {
    var secFilterMode by remember { mutableStateOf("all") } // "all" | "cluster"
    var showGuide by remember { mutableStateOf(false) }

    val filteredAlerts = remember(alerts, searchQuery, secFilterMode) {
        var list = alerts
        if (secFilterMode == "cluster") {
            list = list.filter { it.isCluster }
        }
        val q = searchQuery.trim().lowercase()
        if (q.isNotBlank()) {
            list = list.filter {
                it.ticker.lowercase().contains(q) ||
                it.issuer.lowercase().contains(q) ||
                it.reportingOwner.lowercase().contains(q)
            }
        }
        list
    }

    val clusterCount = remember(alerts) { alerts.count { it.isCluster } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = if (isEn) "SEC Form 4 Insider Radar" else "SEC Form 4 Insider-Radar",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isEn) "Real-time disclosures from CEOs, CFOs, and 10%+ shareholders. Cluster purchases indicate strong conviction."
                   else "Echtzeit-Meldungen von CEOs, CFOs und Großaktionären (10%+). Cluster-Käufe signalisieren höchste Überzeugung.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(if (isEn) "Search company or insider..." else "Unternehmen oder Insider suchen...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Interactive Clickable Stat Buttons (Filter Badges)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isAllActive = secFilterMode == "all"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { secFilterMode = "all" },
                shape = RoundedCornerShape(10.dp),
                color = if (isAllActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    if (isAllActive) 1.5.dp else 1.dp,
                    if (isAllActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(GreenBuy, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEn) "${alerts.size} New Filings" else "${alerts.size} Neue Filings",
                        fontSize = 11.sp,
                        fontWeight = if (isAllActive) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (isAllActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            val isClusterActive = secFilterMode == "cluster"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { secFilterMode = if (isClusterActive) "all" else "cluster" },
                shape = RoundedCornerShape(10.dp),
                color = if (isClusterActive) AmberWhale.copy(alpha = 0.25f) else AmberWhale.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(
                    if (isClusterActive) 1.5.dp else 1.dp,
                    if (isClusterActive) AmberWhale else AmberWhale.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🔥", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEn) "$clusterCount Cluster Alerts" else "$clusterCount Cluster-Alarme",
                        fontSize = 11.sp,
                        fontWeight = if (isClusterActive) FontWeight.ExtraBold else FontWeight.Bold,
                        color = AmberWhale
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expandable Shareholder Knowledge Guide Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccentGlow.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGuide = !showGuide },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEn) "Shareholder Guide: Form 4 Codes Explained"
                                   else "Aktionärs-Kompass: Was bedeuten die Codes?",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanAccentGlow
                        )
                    }
                    Icon(
                        imageVector = if (showGuide) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Guide",
                        tint = CyanAccentGlow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = showGuide) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GuidePill(
                                title = "CODE P · KAUF",
                                desc = "Offener Marktkauf mit eigenem Geld. Starkes Indiz für Unterbewertung!",
                                color = GreenBuy,
                                modifier = Modifier.weight(1f)
                            )
                            GuidePill(
                                title = "🔥 CLUSTER",
                                desc = "Mehrere Vorstände kaufen synchron. Höchste statistische Trefferquote!",
                                color = AmberWhale,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GuidePill(
                                title = "CODE S · VERKAUF",
                                desc = "Marktverkauf. Oft persönliche Liquidität/Steuern – erst als Cluster bärisch.",
                                color = RedSell,
                                modifier = Modifier.weight(1f)
                            )
                            GuidePill(
                                title = "CODE A · AWARD",
                                desc = "Zuteilung lt. Gehaltsvertrag. Kein privates Geld investiert (kursneutral).",
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "👔 Vorstands-Hierarchie: Käufe von CEO & CFO haben statistisch 3x höhere Vorhersagekraft als von Aufsichtsräten.",
                            fontSize = 10.sp,
                            color = CyanAccentGlow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Alerts List
        if (filteredAlerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = if (isEn) "No matching SEC filings found." else "Keine passenden SEC Filings gefunden.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredAlerts, key = { it.id }) { alert ->
                    SecAlertCard(
                        alert = alert,
                        onAlertClick = { onAlertClick(alert) },
                        onOpenAnalysis = { onOpenAnalysis(alert.ticker) },
                        isEn = isEn
                    )
                }
            }
        }
    }
}

@Composable
fun GuidePill(
    title: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(7.dp)) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 9.5.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SecAlertCard(
    alert: SecAlert,
    onAlertClick: () -> Unit,
    onOpenAnalysis: () -> Unit,
    isEn: Boolean
) {
    val tagColor = if (alert.isBuy) GreenBuy else if (alert.code == "S") RedSell else Color(0xFF94A3B8)
    val roleLower = alert.role.lowercase()
    val isCeo = roleLower.contains("ceo") || roleLower.contains("chief executive") || roleLower.contains("president")
    val isCfo = roleLower.contains("cfo") || roleLower.contains("financial") || roleLower.contains("treasurer")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAlertClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isCluster) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        border = if (alert.isCluster) androidx.compose.foundation.BorderStroke(1.dp, AmberWhale.copy(alpha = 0.5f))
                 else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Ticker, Issuer & Signal Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (alert.ticker.isNotBlank()) {
                        Surface(
                            color = CyanAccentGlow.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccentGlow.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "$${alert.ticker}",
                                color = CyanAccentGlow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = alert.issuer,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                if (alert.isCluster) {
                    Surface(
                        color = AmberWhale,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🔥 CLUSTER (${alert.clusterCount}x)",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = tagColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (alert.isBuy) (if (alert.totalValue >= 250000) "🟢 STARKER KAUF" else "🟢 KAUF (CODE P)") else if (alert.code == "S") "🔴 VERKAUF" else "⚪ ZUTEILUNG",
                            color = tagColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Owner & Role Pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = alert.reportingOwner,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isCeo) GreenBuy.copy(alpha = 0.15f) else if (isCfo) CyanAccentGlow.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${if (isCeo) "👔" else if (isCfo) "💼" else "🏛️"} ${alert.role}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCeo) GreenBuy else if (isCfo) CyanAccentGlow else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3-Metrics Grid: Stückzahl, Kurs, Gesamtwert
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(if (isEn) "SHARES" else "STÜCKZAHL", fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(alert.sharesFormatted, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text(if (isEn) "PRICE" else "KURS", fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(alert.priceFormatted, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (isEn) "VALUE" else "GESAMTWERT", fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(alert.totalValueFormatted, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = tagColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Aktionärs-Einschätzung (Shareholder Takeaway Box)
            val takeawayColor = if (alert.isCluster) AmberWhale else if (alert.isBuy) GreenBuy else if (alert.code == "S") RedSell else Color(0xFF94A3B8)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = takeawayColor.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, takeawayColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (alert.isCluster) {
                        "🔥 AKTIONÄRS-VORTEIL: ${alert.clusterCount} Führungskräfte kauften zeitgleich bei ${alert.issuer}. Höchste statistische Wahrscheinlichkeit für Kurserholung!"
                    } else if (alert.isBuy && alert.totalValue >= 300000) {
                        "🟢 HOHE ÜBERZEUGUNG: ${alert.reportingOwner} (${alert.role}) investiert ${alert.totalValueFormatted} privates Eigenkapital am Markt."
                    } else if (alert.isBuy) {
                        "🟢 INSIDER-KAUF: Offener Markterwerb (Code P). Signal für operatives Vertrauen der Führungsebene."
                    } else if (alert.code == "S") {
                        "🔴 VERKAUF: Teilgewinnmitnahme durch ${alert.reportingOwner}. Oft private Vermögensstreuung oder Steuerfälligkeit."
                    } else {
                        "⚪ VERGÜTUNG: Zuteilung von Aktienanteilen lt. Arbeitsvertrag (kursneutral)."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Actions: Details & Link Button + Analysis Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${alert.filingTime.take(10)} · Form 4",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = onAlertClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(
                            text = if (isEn) "Details & SEC ↗" else "Details & SEC-Quelle ↗",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (alert.ticker.isNotBlank()) {
                        TextButton(
                            onClick = onOpenAnalysis,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = "Vollanalyse ↗",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccentGlow
                            )
                        }
                    }
                }
            }
        }
    }
}
