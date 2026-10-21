package com.example.whalewatch.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.whalewatch.data.model.SecCluster
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell
import kotlinx.coroutines.launch

@Composable
fun SecScreen(
    alerts: List<SecAlert>,
    clusters: List<SecCluster>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAlertClick: (SecAlert) -> Unit,
    onClusterClick: (SecCluster) -> Unit,
    onOpenAnalysis: (String) -> Unit = {},
    isEn: Boolean,
    modifier: Modifier = Modifier
) {
    var secFilterMode by remember { mutableStateOf("all") } // "all" | "cluster"
    var showGuide by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val filteredSingles = remember(alerts, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) alerts else alerts.filter {
            it.ticker.lowercase().contains(q) ||
            it.issuer.lowercase().contains(q) ||
            it.reportingOwner.lowercase().contains(q)
        }
    }

    val filteredClusters = remember(clusters, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isBlank()) clusters else clusters.filter { c ->
            c.ticker.lowercase().contains(q) ||
            c.issuer.lowercase().contains(q) ||
            c.filings.any { f -> f.reportingOwner.lowercase().contains(q) }
        }
    }

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

        // Interactive Clickable Stat Buttons (Dynamische Benennung & Zählung ohne Dublikate)
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
                    .clickable {
                        secFilterMode = if (isClusterActive) "all" else "cluster"
                        if (secFilterMode == "cluster") {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    },
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
                        text = if (isEn) "${clusters.size} Cluster Alerts" else "${clusters.size} Cluster-Alarme",
                        fontSize = 11.sp,
                        fontWeight = if (isClusterActive) FontWeight.ExtraBold else FontWeight.Bold,
                        color = AmberWhale
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expandable Shareholder Knowledge Guide Card with Vorstand explanation
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
                            text = if (isEn) "Shareholder Guide: Signals & Orderflow"
                                   else "Aktionärs-Kompass: Signale & Erfassung",
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
                                title = "🟢 KAUF",
                                desc = "Offener Marktkauf mit eigenem Geld. Starkes Indiz für Unterbewertung!",
                                color = GreenBuy,
                                modifier = Modifier.weight(1f)
                            )
                            GuidePill(
                                title = "🔥 CLUSTER",
                                desc = "Mehrere Vorstände kaufen synchron innerhalb von 14 Tagen. Höchste Trefferquote!",
                                color = AmberWhale,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            GuidePill(
                                title = "🔴 VERKAUF",
                                desc = "Marktverkauf. Oft persönliche Liquidität/Steuern – erst als Cluster bärisch.",
                                color = RedSell,
                                modifier = Modifier.weight(1f)
                            )
                            GuidePill(
                                title = "⚪ AWARD",
                                desc = "Zuteilung lt. Gehaltsvertrag. Kein privates Geld investiert (kursneutral).",
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isEn) "👔 Executive Hierarchy: Open-market purchases by the CEO & CFO historically exhibit the strongest predictive power for subsequent stock outperformance."
                                   else "👔 Vorstands-Hierarchie: Direkte Marktkäufe von Vorstandsvorsitzenden (CEO) und Finanzchefs (CFO) haben historisch die stärkste Signalwirkung.",
                            fontSize = 10.sp,
                            color = CyanAccentGlow,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Observation Period Banner (Details zur Zeitspanne)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⏱️", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isEn) "Observation Window: Rolling last 14 days (Filings & Clusters)"
                           else "Erfassungszeitraum: Letzte 14 Tage rollierend (Filings & Cluster)",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List Rendering
        if (secFilterMode == "cluster") {
            if (filteredClusters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = if (isEn) "No cluster alerts currently active." else "Aktuell keine Cluster-Alarme aktiv.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredClusters, key = { it.id }) { cluster ->
                        SecClusterCard(
                            cluster = cluster,
                            onClusterClick = { onClusterClick(cluster) },
                            isEn = isEn
                        )
                    }
                }
            }
        } else {
            if (filteredSingles.isEmpty()) {
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
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredSingles, key = { it.id }) { alert ->
                        SecAlertCard(
                            alert = alert,
                            onAlertClick = { onAlertClick(alert) },
                            isEn = isEn
                        )
                    }
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
fun SecClusterCard(
    cluster: SecCluster,
    onClusterClick: () -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClusterClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWhale.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (cluster.ticker.isNotBlank()) {
                        Surface(
                            color = CyanAccentGlow.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccentGlow.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "$${cluster.ticker}",
                                color = CyanAccentGlow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = cluster.issuer,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = AmberWhale,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🔥 CLUSTER (${cluster.clusterCount}x)",
                        color = Color.Black,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Time span indicator
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⏱️ Zeitraum: 14-Tage-Clusterfenster (bis ${cluster.filingDate.ifBlank { "-" }})",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Contributor count pill (No Stückzahl/Kurs/Gesamtwert 3-box grid!)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyanAccentGlow.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccentGlow.copy(alpha = 0.25f))
            ) {
                Text(
                    text = "👥 ${cluster.clusterCount} Vorstände & Insider beteiligt",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccentGlow,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shareholder Takeaway Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AmberWhale.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberWhale.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔥 AKTIONÄRS-VORTEIL: ${cluster.clusterCount} Führungskräfte kauften zeitgleich bei ${cluster.issuer}. Höchste statistische Wahrscheinlichkeit für Kurserholungen!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Actions: Details & Link Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${cluster.clusterCount} Filings · Form 4 Cluster",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FilledTonalButton(
                    onClick = onClusterClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AmberWhale.copy(alpha = 0.2f),
                        contentColor = AmberWhale
                    ),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "Details ↗",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SecAlertCard(
    alert: SecAlert,
    onAlertClick: () -> Unit,
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Ticker, Issuer & Signal Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                val badgeText = if (alert.isBuy) (if (isEn) "🟢 KAUF" else "🟢 KAUF")
                                else if (alert.code == "S" || !alert.isBuy) (if (isEn) "🔴 VERKAUF" else "🔴 VERKAUF")
                                else "⚪ AWARD"
                Surface(
                    color = tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = badgeText,
                        color = tagColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Time Span Detail on Card
            val timeSpanText = if (isEn) "⏱️ Period: Filed on ${alert.filingDate.ifBlank { "-" }} (T+2 window)"
                               else "⏱️ Zeitraum: Eingereicht am ${alert.filingDate.ifBlank { "-" }} (T+2 Frist)"
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = timeSpanText,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
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

            Spacer(modifier = Modifier.height(6.dp))

            // Shares pill: If known, display sharesFormatted, else '-' (No 3-column Stückzahl/Kurs/Gesamtwert grid!)
            val sharesDisplay = if (alert.sharesFormatted.isNotBlank() && alert.sharesFormatted != "0" && alert.sharesFormatted != "0 Aktien" && alert.sharesFormatted != "-") alert.sharesFormatted else "-"
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyanAccentGlow.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccentGlow.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "📊 Stückzahl: $sharesDisplay",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccentGlow,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shareholder Takeaway Box
            val takeawayColor = if (alert.isBuy) GreenBuy else if (alert.code == "S") RedSell else Color(0xFF94A3B8)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = takeawayColor.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, takeawayColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (alert.isBuy) {
                        "🟢 INSIDER-KAUF: Offener Markterwerb durch ${alert.reportingOwner}. Signal für operatives Vertrauen der Führungsebene."
                    } else if (alert.code == "S" || !alert.isBuy) {
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

            // Bottom Actions: Details & SEC link
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

                FilledTonalButton(
                    onClick = onAlertClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "Details ↗",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
