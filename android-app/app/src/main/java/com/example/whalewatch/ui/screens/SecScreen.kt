package com.example.whalewatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    val listState = rememberLazyListState()

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

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Title & Unified 5-Sentence Layman-Friendly Intro
        item {
            Text(
                text = if (isEn) "SEC Form 4 Insider Radar" else "SEC Form 4 Insider-Radar",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEn) {
                    "Corporate executives (CEOs, CFOs), board directors, and major shareholders owning more than 10% of a publicly traded US company know their firm's internal business outlook better than any outsider. Whenever these corporate insiders buy or sell shares of their own company on the open market, they are legally required to report the transaction to the US Securities and Exchange Commission (SEC) within just two business days via an official 'Form 4' filing. While insider sales often occur for personal reasons such as tax obligations, Rule 10b5-1 trading plans, or portfolio diversification, open-market purchases made with personal capital carry strong weight as a direct sign of confidence in the company's future. The signal is even stronger during 'Cluster Buys', when multiple executives of the same company purchase shares simultaneously within a few days. Here you can track all recent individual filings as well as bundled cluster alerts from the rolling 14-day window at a glance."
                } else {
                    "Vorstände, Geschäftsführer (CEOs), Finanzchefs (CFOs), Aufsichtsräte und Großaktionäre, die mehr als 10 % an einem börsennotierten US-Unternehmen halten, kennen die interne Geschäftslage ihrer Firma besser als jeder Außenstehende. Wenn diese Unternehmens-Insider eigene Aktien am offenen Markt kaufen oder verkaufen, müssen sie dies der US-Börsenaufsicht SEC innerhalb von nur zwei Werktagen über das offizielle „Form 4“-Dokument melden. Während Verkäufe häufig aus privaten Gründen wie Steuerzahlungen, automatisierten 10b5-1-Handelsplänen oder zur Diversifikation erfolgen, haben direkte Aktienkäufe mit eigenem Privatvermögen ein besonders starkes Gewicht als Zeichen des Vertrauens in die eigene Firma. Noch stärker ist das Signal bei sogenannten „Cluster-Käufen“, wenn mehrere Führungskräfte derselben Firma innerhalb weniger Tage gleichzeitig zugreifen. Hier siehst du alle aktuellen Einzelmeldungen sowie gebündelte Cluster-Käufe der letzten 14 Tage auf einen Blick."
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }

        // Search Input
        item {
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
        }

        // Interactive Clickable Stat Buttons
        item {
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
                            color = if (isAllActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            softWrap = false,
                            maxLines = 1
                        )
                    }
                }

                val isClusterActive = secFilterMode == "cluster"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            secFilterMode = if (isClusterActive) "all" else "cluster"
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
                            color = AmberWhale,
                            softWrap = false,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Observation Period Banner
        item {
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
        }

        // List Rendering
        if (secFilterMode == "cluster") {
            if (filteredClusters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = if (isEn) "No cluster alerts currently active." else "Aktuell keine Cluster-Alarme aktiv.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredClusters, key = { it.id }) { cluster ->
                    SecClusterCard(
                        cluster = cluster,
                        onClusterClick = { onClusterClick(cluster) },
                        isEn = isEn
                    )
                }
            }
        } else {
            if (filteredSingles.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = if (isEn) "No matching SEC filings found." else "Keine passenden SEC Filings gefunden.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
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
            // Header: full company name (always horizontal), badge underneath
            val headInfo = clusterExplanation(cluster, isEn)
            SecCompanyTitle(ticker = cluster.ticker, issuer = cluster.issuer, tickerSize = 15.sp, issuerSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = headInfo.color,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${headInfo.badge} (${cluster.uniqueInsidersCount}x)",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Time span indicator
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isEn) "⏱️ Period: 14-day cluster window (until ${cluster.filingDate.ifBlank { "-" }})"
                           else "⏱️ Zeitraum: 14-Tage-Clusterfenster (bis ${cluster.filingDate.ifBlank { "-" }})",
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
                    text = if (isEn) "👥 ${cluster.uniqueInsidersCount} different insiders · ${cluster.clusterCount} filings"
                           else "👥 ${cluster.uniqueInsidersCount} verschiedene Insider · ${cluster.clusterCount} Filings",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccentGlow,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shareholder Takeaway Box (adapts to buys / sales / mixed)
            val clusterInfo = clusterExplanation(cluster, isEn)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = clusterInfo.color.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, clusterInfo.color.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = clusterInfo.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
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
            // Header: full company name (always horizontal), badge underneath
            SecCompanyTitle(ticker = alert.ticker, issuer = alert.issuer, tickerSize = 15.sp, issuerSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            val badgeText = if (alert.allTransactions().size > 1) {
                if (isEn) "📑 ${alert.allTransactions().size} TRANSACTIONS" else "📑 ${alert.allTransactions().size} TRANSAKTIONEN"
            } else when (alert.code) {
                "P" -> if (isEn) "🟢 BUY (Code P)" else "🟢 KAUF (Code P)"
                "S" -> if (isEn) "🔴 SALE (Code S)" else "🔴 VERKAUF (Code S)"
                "A" -> if (isEn) "🟢 AWARD (Code A)" else "🟢 ZUTEILUNG (Code A)"
                "M" -> "🟢 OPTION (Code M)"
                "F" -> if (isEn) "🔴 TAX (Code F)" else "🔴 STEUER (Code F)"
                else -> if (alert.isBuy) (if (isEn) "🟢 BUY (Code ${alert.code})" else "🟢 ERWERB (Code ${alert.code})")
                        else (if (isEn) "🔴 SALE (Code ${alert.code})" else "🔴 ABGABE (Code ${alert.code})")
            }
            Surface(
                color = tagColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.3f))
            ) {
                Text(
                    text = badgeText,
                    color = tagColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Time Span & 10b5-1 Plan Detail on Card
            val planNote = if (alert.is10b51Plan) " · 10b5-1 Plan" else ""
            val timeSpanText = if (isEn) "⏱️ Filed: ${alert.filingDate.ifBlank { "-" }} · SEC #${alert.accessionNumber}$planNote"
                               else "⏱️ Meldung: ${alert.filingDate.ifBlank { "-" }} · SEC #${alert.accessionNumber}$planNote"
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = timeSpanText,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Owner & Role Pill (Always horizontal; Role text box automatically expands across full width if long)
            val roleDisplay = if (isEn) {
                alert.role
                    .replace(" (Aufsichtsrat)", "")
                    .replace(" / Aufsichtsrat", "")
                    .replace("Vorstand & Aufsichtsrat", "Executive & Board")
                    .replace("Vorstand / Aufsichtsrat", "Executive & Board")
            } else alert.role
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = alert.reportingOwner,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isCeo) GreenBuy.copy(alpha = 0.15f) else if (isCfo) CyanAccentGlow.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = "${if (isCeo) "👔" else if (isCfo) "💼" else "🏛️"} $roleDisplay",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCeo) GreenBuy else if (isCfo) CyanAccentGlow else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ALL transactions of this Form 4 (exact numbers)
            SecTxCompactList(alert = alert, isEn = isEn)

            Spacer(modifier = Modifier.height(8.dp))

            // Shareholder Takeaway Box
            val takeawayColor = if (alert.isBuy) GreenBuy else if (alert.code == "S") RedSell else Color(0xFF94A3B8)
            val multiTx = alert.allTransactions().size > 1
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = takeawayColor.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, takeawayColor.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (multiTx) {
                        if (isEn) "ℹ️ ${alert.allTransactions().size} transactions in one filing by ${alert.reportingOwner}. Awards or option exercises are often combined with a tax withholding or sale (routine compensation handling). See details for every transaction."
                        else "ℹ️ ${alert.allTransactions().size} Transaktionen in einem Formblatt von ${alert.reportingOwner}. Zuteilungen oder Optionsausübungen werden oft mit Steuereinbehalt oder Verkauf kombiniert (übliche Vergütungsabwicklung). Details zu jeder Transaktion siehe Detailansicht."
                    } else if (isEn) {
                        if (alert.isBuy) "🟢 INSIDER PURCHASE: Open-market acquisition by ${alert.reportingOwner}. Signal of operational executive confidence."
                        else "🔴 SALE: Profit-taking or personal liquidity/tax diversification by ${alert.reportingOwner}."
                    } else {
                        if (alert.isBuy) "🟢 INSIDER-KAUF: Offener Markterwerb durch ${alert.reportingOwner}. Signal für operatives Vertrauen der Führungsebene."
                        else "🔴 VERKAUF: Teilgewinnmitnahme durch ${alert.reportingOwner}. Oft private Vermögensstreuung oder Steuerfälligkeit."
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
