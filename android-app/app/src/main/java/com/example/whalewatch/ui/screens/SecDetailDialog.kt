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
import com.example.whalewatch.data.model.SecCluster
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccentGlow
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell

/**
 * Explains Form 4 notations and codes so that retail investors understand what they mean
 */
@Composable
fun SecForm4GlossaryBox(isEn: Boolean) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = if (isEn) "📋 Form 4 XML Notation Guide & Glossary" else "📋 Was bedeuten die Form-4-Vermerke? (Legende)",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyanAccentGlow,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = if (isEn) {
                    "• Code P (Purchase): Open market purchase using private out-of-pocket capital. Statistically the strongest bullish conviction signal."
                } else {
                    "• Code P (Purchase): Kauf am offenen Markt mit eigenem privaten Barvermögen. Historisch das stärkste Kaufsignal für Aktionäre."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (isEn) {
                    "• Code S (Sale): Open market sale of shares (for profit taking, tax liabilities, or estate diversification)."
                } else {
                    "• Code S (Sale): Offener Verkauf über die Börse (zur Gewinnmitnahme, Steuer- oder Nachlassplanung)."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (isEn) {
                    "• Code A (Award): Corporate equity grant / award under compensation plan without private money invested (market neutral)."
                } else {
                    "• Code A (Award): Zuteilung als vertragliche Gehaltsvergütung ohne privaten Geldeinsatz (kursneutral)."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (isEn) {
                    "• Amount (Shares): Exact number of shares transacted in this specific filing event."
                } else {
                    "• Amount (Stückzahl): Exakte Anzahl der bei dieser Transaktion gehandelten Aktien."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (isEn) {
                    "• Security Beneficially Owned: Remaining total shares owned by the insider following this transaction (confirms long-term skin in the game)."
                } else {
                    "• Security Beneficially Owned: Der verbleibende Gesamtbestand an Aktien, die der Insider nach dieser Meldung noch hält (belegt Skin in the Game)."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (isEn) {
                    "• Ownership Nature (D vs. I): 'D' = Direct personal custody; 'I' = Indirect ownership via Family Trust, Holding, or 401(k)."
                } else {
                    "• Ownership Nature (D vs. I): 'D' = Direktes persönliches Depot; 'I' = Indirekter Besitz über Familientrust, Holding oder Vorsorgeplan."
                },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * Dialog for a single Form 4 filing (Neue Filings) with XML attributes
 */
@Composable
fun SecDetailDialog(
    alert: SecAlert,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onOpenAnalysis: (String) -> Unit = {},
    onDismiss: () -> Unit,
    isEn: Boolean
) {
    val uriHandler = LocalUriHandler.current
    val tagColor = if (alert.isBuy) GreenBuy else if (alert.code == "S") RedSell else Color(0xFF94A3B8)
    val txLabel = if (isEn) alert.txTypeEn else alert.txTypeDe
    val scrollState = rememberScrollState()

    val sharesText = if (alert.sharesFormatted.isNotBlank() && alert.sharesFormatted != "-") {
        alert.sharesFormatted
    } else if (alert.shares > 0) {
        "${alert.shares} Aktien"
    } else {
        "-"
    }

    val priceText = if (alert.price > 0.0) {
        String.format(java.util.Locale.GERMANY, "$%.2f", alert.price)
    } else if (alert.priceFormatted.isNotBlank() && alert.priceFormatted != "-") {
        alert.priceFormatted
    } else {
        "-"
    }

    val benefText = if (alert.securityBeneficialsFormatted.isNotBlank()) {
        alert.securityBeneficialsFormatted
    } else if (alert.securityBeneficials > 0) {
        String.format(java.util.Locale.GERMANY, "%,d Aktien", alert.securityBeneficials)
    } else {
        "-"
    }

    val natureText = if (alert.ownershipNature == "I") "Indirekt (I) · Trust/Holding" else "Direkt (D) · Privatdepot"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .heightIn(min = 280.dp, max = 680.dp),
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
                        text = if (isEn) "SEC FORM 4 FILING DETAILS (XML)" else "SEC FORM 4 DETAILS (XML-DATEN)",
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (alert.ticker.isNotBlank()) "$${alert.ticker}" else alert.issuer,
                                fontSize = 22.sp,
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

                    // Reporting Person & Relationship Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "REPORTING PERSON & RELATIONSHIP" else "MELDENDE PERSON & FUNKTION (RELATIONSHIP)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = alert.reportingOwner.ifBlank { "Reporting Person" },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = alert.role.ifBlank { "Vorstand / Aufsichtsrat" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccentGlow
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            // 2x2 Grid of Form 4 XML attributes
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TITLE OF SECURITY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = alert.securityTitle.ifBlank { "Common Stock" },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "STÜCKZAHL (AMOUNT)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = if (priceText != "-") "$sharesText à $priceText" else sharesText,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccentGlow
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "BESTAND DANACH (BENEFICIALS)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = benefText,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenBuy
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "EIGENTUMSART (NATURE)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = natureText,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Investor Vermerk / Interpretation
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (alert.investorExplanationDe.isNotBlank()) {
                                "💡 Vermerk für Aktionäre: ${alert.investorExplanationDe}"
                            } else if (alert.isBuy) {
                                "💡 Vermerk für Aktionäre: Kauf aus persönlichem Barvermögen am offenen Markt. Führungskräfte investieren persönliches Kapital direkt an der Börse – ein direktes Signal für operatives Vertrauen."
                            } else {
                                "💡 Vermerk für Aktionäre: Insider-Verkauf am Markt. Position verkleinert (oft Steuer- oder Vermögensdiversifikation)."
                            },
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Form 4 XML Glossary / Legend
                    SecForm4GlossaryBox(isEn = isEn)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filing Metadata
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        if (alert.accessionNumber.isNotBlank()) {
                            Text(
                                text = "SEC Accession No.: ${alert.accessionNumber}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (isEn) "Filing Date: ${alert.filingDate}" else "Einreichungsdatum: ${alert.filingDate}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action Buttons Bottom Bar (Keeps link to official form)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                            text = if (isEn) "📄 Open Original Form 4 on sec.gov ↗" else "📄 Original Formblatt 4 auf sec.gov ansehen ↗",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    if (alert.ticker.isNotBlank()) {
                        OutlinedButton(
                            onClick = onToggleWatchlist,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isOnWatchlist) "⭐ Watchlist (Entfernen)" else "⭐ + Watchlist",
                                fontSize = 12.sp,
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

/**
 * Dialog for a Consolidated Cluster Event (Cluster-Alarme)
 * Lists ALL individual contributors with rich XML data (Title of Security, Amount, Beneficially Owned, Ownership Nature, Form 4 link)
 */
@Composable
fun SecClusterDetailDialog(
    cluster: SecCluster,
    isOnWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onOpenAnalysis: (String) -> Unit = {},
    onDismiss: () -> Unit,
    isEn: Boolean
) {
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .heightIn(min = 280.dp, max = 680.dp),
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
                        text = if (isEn) "SEC CLUSTER ALERT DETAILS (FORM 4 XML)" else "SEC CLUSTER-ALARM DETAILS (FORM 4 XML)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWhale,
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
                    // Ticker & Issuer Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = if (cluster.ticker.isNotBlank()) "$${cluster.ticker}" else cluster.issuer,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccentGlow
                            )
                            Text(
                                text = cluster.issuer,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // High-visibility yellow cluster badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberWhale,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWhale)
                        ) {
                            Text(
                                text = "🔥 ${cluster.clusterCount}x CLUSTER-ALARM",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cluster Explanation Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberWhale.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWhale.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "🔥 CLUSTER PURCHASE ACCUMULATION (${cluster.clusterCount} SYNCHRONIZED FILINGS)"
                                       else "🔥 CLUSTER-KAUF ERKANNT (${cluster.clusterCount} SYNCHRONE FILINGS)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberWhale
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) {
                                    "${cluster.clusterCount} corporate insiders concurrently purchased shares at ${cluster.issuer}. Cluster filings historically exhibit a high win-rate for subsequent stock rallies."
                                } else {
                                    "${cluster.clusterCount} Vorstandsmitglieder & Aufsichtsräte haben synchron Anteile bei ${cluster.issuer} erworben. Ein massives Indiz für operative Stärke und Unterbewertung."
                                },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Section: ALL Contributors in the Cluster with XML Form 4 Details
                    Text(
                        text = if (isEn) "👥 ALL ${cluster.clusterCount} CONTRIBUTORS (FORM 4 XML DETAILS)"
                               else "👥 ALLE ${cluster.clusterCount} BETEILIGTEN IM CLUSTER (FORM 4 XML DETAILS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccentGlow,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // List of all individual contributors
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        cluster.filings.forEachIndexed { index, filing ->
                            val sigColor = if (filing.isBuy) GreenBuy else if (filing.code == "S") RedSell else Color(0xFF94A3B8)
                            val sigLabel = if (filing.isBuy) (if (isEn) "🟢 BUY (Code ${filing.code})" else "🟢 Kauf (Code ${filing.code})")
                                           else if (filing.code == "S" || !filing.isBuy) (if (isEn) "🔴 SALE (Code ${filing.code})" else "🔴 Verkauf (Code ${filing.code})")
                                           else "⚪ Award (Code ${filing.code})"

                            val sharesText = if (filing.sharesFormatted.isNotBlank() && filing.sharesFormatted != "-") {
                                filing.sharesFormatted
                            } else if (filing.shares > 0) {
                                "${filing.shares} Aktien"
                            } else {
                                "-"
                            }

                            val priceText = if (filing.price > 0.0) {
                                String.format(java.util.Locale.GERMANY, "$%.2f", filing.price)
                            } else if (filing.priceFormatted.isNotBlank() && filing.priceFormatted != "-") {
                                filing.priceFormatted
                            } else {
                                "-"
                            }

                            val benefText = if (filing.securityBeneficialsFormatted.isNotBlank()) {
                                filing.securityBeneficialsFormatted
                            } else if (filing.securityBeneficials > 0) {
                                String.format(java.util.Locale.GERMANY, "%,d Aktien", filing.securityBeneficials)
                            } else {
                                "-"
                            }

                            val natureText = if (filing.ownershipNature == "I") "Indirekt (I)" else "Direkt (D)"
                            val dateText = if (filing.filingTime.isNotBlank()) {
                                filing.filingTime.take(16).replace("T", ", ") + " Uhr"
                            } else {
                                filing.filingDate.ifBlank { "-" }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    // Header: Index & Name + Signal
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${index + 1}. ${filing.reportingOwner.ifBlank { "Reporting Person" }}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${filing.role.ifBlank { "Vorstand / Aufsichtsrat" }} · $dateText",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = CyanAccentGlow
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = sigColor.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, sigColor.copy(alpha = 0.35f))
                                        ) {
                                            Text(
                                                text = sigLabel,
                                                color = sigColor,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // 2x2 XML Grid for Contributor
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = "TITLE OF SECURITY", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(text = filing.securityTitle.ifBlank { "Common Stock" }, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = "STÜCKZAHL (AMOUNT)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(text = if (priceText != "-") "$sharesText à $priceText" else sharesText, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = CyanAccentGlow)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = "BESTAND DANACH (BENEFICIALS)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(text = benefText, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = GreenBuy)
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(text = "EIGENTUMSART (NATURE)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    Text(text = natureText, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                }
                                            }
                                        }
                                    }

                                    // Explanatory Vermerk for this filing
                                    if (filing.investorExplanationDe.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "💡 Vermerk: ${filing.investorExplanationDe}",
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Direct link to Form 4 on sec.gov
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        FilledTonalButton(
                                            onClick = {
                                                val url = if (filing.filingUrl.isNotBlank()) filing.filingUrl else "https://www.sec.gov"
                                                try { uriHandler.openUri(url) } catch (_: Exception) {}
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(26.dp)
                                        ) {
                                            Text(
                                                text = "📄 Zum Formblatt 4 (sec.gov) ↗",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyanAccentGlow
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Form 4 XML Glossary / Legend
                    SecForm4GlossaryBox(isEn = isEn)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cluster Observation Window
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = if (isEn) "Observation Window: 14-day rolling cluster detection window"
                                   else "Cluster-Zeitspanne: 14-Tage-Clusterfenster (rollierend)",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isEn) "Latest Filing: ${cluster.filingTime.ifBlank { cluster.filingDate }}"
                                   else "Letzte Meldung: ${cluster.filingTime.ifBlank { cluster.filingDate }}",
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
                    if (cluster.ticker.isNotBlank()) {
                        OutlinedButton(
                            onClick = onToggleWatchlist,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isOnWatchlist) "⭐ Watchlist (Entfernen)" else "⭐ + Watchlist",
                                fontSize = 12.sp,
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
