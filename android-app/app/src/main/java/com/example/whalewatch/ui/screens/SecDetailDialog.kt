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
 * Helper to generate a clean English shareholder explanation for Form 4 filings
 */
private fun buildEnglishInvestorNote(alert: SecAlert, sharesText: String, benefText: String): String {
    val owner = alert.reportingOwner.ifBlank { "Corporate Insider" }
    val role = alert.role
        .replace(" / Aufsichtsrat", "")
        .replace("Vorstand & Aufsichtsrat", "Executive / Director")
        .replace("Vorstand / Aufsichtsrat", "Executive / Director")
        .ifBlank { "Executive" }
    val holdingSuffix = if (benefText.isNotBlank() && benefText != "-") " Following the transaction, $owner holds $benefText." else ""
    if (alert.transactions.size > 1) {
        val n = alert.transactions.size
        val buys = alert.transactions.count { it.isBuy }
        val sells = n - buys
        return "$owner ($role) reported $n transactions in one Form 4 filing ($buys acquisitions, $sells sales/withholdings). Awards or option exercises are often combined with a tax withholding or sale – see every transaction listed above.$holdingSuffix"
    }
    return when {
        alert.code == "M" -> "Option Exercise (Code M): $owner ($role) exercised vested stock options ($sharesText) and retained the shares directly.$holdingSuffix"
        alert.isBuy -> "Open Market Purchase (Code P): $owner ($role) acquired $sharesText on the open market using personal capital – a strong signal of operational conviction.$holdingSuffix"
        alert.code == "S" -> "Open Market Sale (Code S): $owner ($role) sold $sharesText on the open market (typically for tax obligations or personal portfolio diversification).$holdingSuffix"
        else -> "Corporate Filing (Code ${alert.code}): $owner ($role) reported a transaction of $sharesText per statutory Form 4 disclosure.$holdingSuffix"
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
    val txLabel = if (alert.transactions.size > 1) (if (isEn) "${alert.transactions.size} transactions in this filing" else "${alert.transactions.size} Transaktionen in diesem Formblatt") else if (isEn) alert.txTypeEn else alert.txTypeDe
    val scrollState = rememberScrollState()

    val rawSharesText = if (alert.sharesFormatted.isNotBlank() && alert.sharesFormatted != "-") {
        alert.sharesFormatted
    } else if (alert.shares > 0) {
        "${alert.shares} Aktien"
    } else {
        "-"
    }
    val sharesText = if (isEn) rawSharesText.replace("Aktien", "Shares") else rawSharesText

    val priceText = if (alert.price > 0.0) {
        String.format(if (isEn) java.util.Locale.US else java.util.Locale.GERMANY, "$%.2f", alert.price)
    } else if (alert.priceFormatted.isNotBlank() && alert.priceFormatted != "-") {
        alert.priceFormatted
    } else {
        "-"
    }

    val rawBenefText = if (alert.securityBeneficialsFormatted.isNotBlank()) {
        alert.securityBeneficialsFormatted
    } else if (alert.securityBeneficials > 0) {
        String.format(java.util.Locale.GERMANY, "%,d Aktien", alert.securityBeneficials)
    } else {
        "-"
    }
    val benefText = if (isEn) rawBenefText.replace("Aktien", "Shares") else rawBenefText

    val natureText = if (alert.ownershipNature == "I") {
        if (isEn) "Indirect (I) · Trust/Holding" else "Indirekt (I) · Trust/Holding"
    } else {
        if (isEn) "Direct (D) · Personal Custody" else "Direkt (D) · Privatdepot"
    }

    val roleDisplay = if (isEn) {
        alert.role
            .replace(" / Aufsichtsrat", "")
            .replace("Vorstand & Aufsichtsrat", "Officer / Director")
            .replace("Vorstand / Aufsichtsrat", "Officer / Director")
            .ifBlank { "Officer / Director" }
    } else {
        alert.role.ifBlank { "Vorstand / Aufsichtsrat" }
    }

    val secTitleDisplay = if (isEn) {
        alert.securityTitle
            .replace("Common Stock (Stammaktien)", "Common Stock")
            .replace("Stammaktien (Common Stock)", "Common Stock")
            .ifBlank { "Common Stock" }
    } else {
        alert.securityTitle.ifBlank { "Common Stock" }
    }

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
                    // Ticker & full company name first (always horizontal), badge underneath
                    SecCompanyTitle(ticker = alert.ticker.ifBlank { "" }, issuer = alert.issuer)
                    Spacer(modifier = Modifier.height(6.dp))
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
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
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
                                text = roleDisplay,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyanAccentGlow
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            Text(
                                text = if (isEn) "OWNERSHIP & 10B5-1 PLAN" else "EIGENTUM & 10B5-1 PLAN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (alert.is10b51Plan) (if (isEn) "10b5-1 plan: Yes" else "10b5-1-Plan: Ja") else (if (isEn) "10b5-1 plan: No" else "10b5-1-Plan: Nein"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SecTxDetailList(alert = alert, isEn = isEn)
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
                            text = if (isEn) {
                                "💡 Shareholder Note: ${buildEnglishInvestorNote(alert, sharesText, benefText)}"
                            } else {
                                if (alert.transactions.size > 1) "💡 Vermerk für Aktionäre: ${alert.reportingOwner} meldete ${alert.transactions.size} Transaktionen in einem Formblatt 4 (${alert.transactions.count { it.isBuy }} Erwerbe, ${alert.transactions.count { !it.isBuy }} Verkäufe/Einbehalte). Zuteilungen oder Optionsausübungen werden oft mit Steuereinbehalt oder Verkauf kombiniert – siehe alle Transaktionen oben."
                                else if (alert.investorExplanationDe.isNotBlank()) "💡 Vermerk für Aktionäre: ${alert.investorExplanationDe}"
                                else if (alert.isBuy) "💡 Vermerk für Aktionäre: Kauf aus persönlichem Barvermögen am offenen Markt. Führungskräfte investieren persönliches Kapital direkt an der Börse – ein direktes Signal für operatives Vertrauen."
                                else "💡 Vermerk für Aktionäre: Insider-Verkauf am Markt. Position verkleinert (oft Steuer- oder Vermögensdiversifikation)."
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
                                text = if (isOnWatchlist) (if (isEn) "⭐ On Watchlist (Remove)" else "⭐ Watchlist (Entfernen)") else "⭐ + Watchlist",
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
                    // Ticker & full company name (always horizontal), badge underneath
                    val clusterBadge = clusterExplanation(cluster, isEn)
                    SecCompanyTitle(ticker = cluster.ticker, issuer = cluster.issuer)
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = clusterBadge.color,
                        border = androidx.compose.foundation.BorderStroke(1.dp, clusterBadge.color)
                    ) {
                        Text(
                            text = "${clusterBadge.badge} · ${cluster.uniqueInsidersCount} " + (if (isEn) "INSIDERS" else "INSIDER"),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cluster Explanation Banner (adapts to buys / sales / mixed)
                    val clusterInfo = clusterExplanation(cluster, isEn)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = clusterInfo.color.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, clusterInfo.color.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = clusterInfo.title,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = clusterInfo.color
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = clusterInfo.body,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "A cluster requires at least 2 different people. Several filings by the same person are not a cluster."
                                       else "Ein Cluster setzt mindestens 2 verschiedene Personen voraus. Mehrere Meldungen derselben Person sind kein Cluster.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Section: ALL Contributors in the Cluster with XML Form 4 Details
                    Text(
                        text = if (isEn) "👥 ALL ${cluster.filings.size} FILINGS BY ${cluster.uniqueInsidersCount} DIFFERENT INSIDERS"
                               else "👥 ALLE ${cluster.filings.size} FILINGS VON ${cluster.uniqueInsidersCount} VERSCHIEDENEN INSIDERN",
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

                            val rawSharesText = if (filing.sharesFormatted.isNotBlank() && filing.sharesFormatted != "-") {
                                filing.sharesFormatted
                            } else if (filing.shares > 0) {
                                "${filing.shares} Aktien"
                            } else {
                                "-"
                            }
                            val sharesText = if (isEn) rawSharesText.replace("Aktien", "Shares") else rawSharesText

                            val priceText = if (filing.price > 0.0) {
                                String.format(if (isEn) java.util.Locale.US else java.util.Locale.GERMANY, "$%.2f", filing.price)
                            } else if (filing.priceFormatted.isNotBlank() && filing.priceFormatted != "-") {
                                filing.priceFormatted
                            } else {
                                "-"
                            }

                            val rawBenefText = if (filing.securityBeneficialsFormatted.isNotBlank()) {
                                filing.securityBeneficialsFormatted
                            } else if (filing.securityBeneficials > 0) {
                                String.format(java.util.Locale.GERMANY, "%,d Aktien", filing.securityBeneficials)
                            } else {
                                "-"
                            }
                            val benefText = if (isEn) rawBenefText.replace("Aktien", "Shares") else rawBenefText

                            val natureText = if (filing.ownershipNature == "I") (if (isEn) "Indirect (I)" else "Indirekt (I)") else (if (isEn) "Direct (D)" else "Direkt (D)")
                            val dateText = if (filing.filingTime.isNotBlank()) {
                                filing.filingTime.take(16).replace("T", ", ") + (if (isEn) " EST" else " Uhr")
                            } else {
                                filing.filingDate.ifBlank { "-" }
                            }
                            val roleText = if (isEn) {
                                filing.role
                                    .replace(" / Aufsichtsrat", "")
                                    .replace("Vorstand & Aufsichtsrat", "Officer / Director")
                                    .replace("Vorstand / Aufsichtsrat", "Officer / Director")
                                    .ifBlank { "Officer / Director" }
                            } else {
                                filing.role.ifBlank { "Vorstand / Aufsichtsrat" }
                            }
                            val secTitleDisplay = if (isEn) {
                                filing.securityTitle
                                    .replace("Common Stock (Stammaktien)", "Common Stock")
                                    .replace("Stammaktien (Common Stock)", "Common Stock")
                                    .ifBlank { "Common Stock" }
                            } else {
                                filing.securityTitle.ifBlank { "Common Stock" }
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
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                text = "${index + 1}. ${filing.reportingOwner.ifBlank { "Reporting Person" }}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "$roleText · $dateText",
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
                                                softWrap = false,
                                                maxLines = 1,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    SecTxDetailList(alert = filing, isEn = isEn)

                                    // Explanatory Vermerk for this filing
                                    if (filing.investorExplanationDe.isNotBlank() || isEn) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isEn) "💡 Note: ${buildEnglishInvestorNote(filing, sharesText, benefText)}" else if (filing.transactions.size > 1) "💡 Vermerk: ${filing.transactions.size} Transaktionen in einem Formblatt 4 – siehe Liste oben." else "💡 Vermerk: ${filing.investorExplanationDe}",
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
                                                text = if (isEn) "📄 Open Form 4 (sec.gov) ↗" else "📄 Zum Formblatt 4 (sec.gov) ↗",
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
                                text = if (isOnWatchlist) (if (isEn) "⭐ On Watchlist (Remove)" else "⭐ Watchlist (Entfernen)") else "⭐ + Watchlist",
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
