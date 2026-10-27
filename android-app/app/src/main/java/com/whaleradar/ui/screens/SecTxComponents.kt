package com.whaleradar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whaleradar.data.model.SecAlert
import com.whaleradar.data.model.SecCluster
import com.whaleradar.data.model.SecTransactionItem
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccentGlow
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.RedSell

/** Returns all transactions of a filing (falls back to one item built from the filing header). */
fun SecAlert.allTransactions(): List<SecTransactionItem> = transactions.ifEmpty {
    listOf(
        SecTransactionItem(
            code = code, isBuy = isBuy, txTypeDe = txTypeDe, txTypeEn = txTypeEn,
            securityTitle = securityTitle, transactionDate = filingDate, shares = shares,
            sharesFormatted = sharesFormatted, price = price, priceFormatted = priceFormatted,
            totalValue = totalValue, totalValueFormatted = totalValueFormatted,
            securityBeneficials = securityBeneficials,
            securityBeneficialsFormatted = securityBeneficialsFormatted.ifBlank { "-" },
            ownershipNature = ownershipNature
        )
    )
}

private fun txColor(tx: SecTransactionItem): Color =
    if (tx.isBuy) GreenBuy else if (tx.code == "S" || tx.code == "F" || tx.code == "D") RedSell else Color(0xFF94A3B8)

private fun tr(text: String, isEn: Boolean): String =
    if (isEn) text.replace("Aktien", "Shares") else text

/**
 * Company name block: the ticker and the FULL company name are always written horizontally,
 * one below the other, using the full available width (no truncation, no side-by-side squeezing).
 */
@Composable
fun SecCompanyTitle(ticker: String, issuer: String, tickerSize: TextUnit = 22.sp, issuerSize: TextUnit = 14.sp) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (ticker.isNotBlank()) {
            Text(
                text = "$$ticker",
                fontSize = tickerSize,
                fontWeight = FontWeight.ExtraBold,
                color = CyanAccentGlow
            )
        }
        Text(
            text = issuer,
            fontSize = issuerSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Compact list of ALL transactions of a filing – used on cards in the SEC tab. */
@Composable
fun SecTxCompactList(alert: SecAlert, isEn: Boolean) {
    val txs = alert.allTransactions()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
        if (txs.size > 1) {
            Text(
                text = if (isEn) "${txs.size} transactions in this Form 4" else "${txs.size} Transaktionen in diesem Formblatt 4",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        txs.forEachIndexed { i, tx ->
            val c = txColor(tx)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = c.copy(alpha = 0.10f),
                border = androidx.compose.foundation.BorderStroke(1.dp, c.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                    Text(
                        text = (if (txs.size > 1) "${i + 1}. " else "") + (if (tx.isBuy) "🟢 " else "🔴 ") +
                            (if (isEn) tx.txTypeEn else tx.txTypeDe).ifBlank { "Code ${tx.code}" },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = c
                    )
                    val price = if (tx.priceFormatted.isNotBlank() && tx.priceFormatted != "-")
                        (if (isEn) " @ ${tx.priceFormatted}" else " à ${tx.priceFormatted}") else ""
                    val total = if (tx.totalValueFormatted.isNotBlank() && tx.totalValueFormatted != "-")
                        " (${tx.totalValueFormatted})" else ""
                    Text(
                        text = "📊 " + tr(tx.sharesFormatted, isEn) + price + total,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun TxField(label: String, value: String, valueColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Text(text = label, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

/**
 * Detailed list of ALL transactions of a filing – used in the SEC detail dialogs.
 * Fields are stacked vertically so text is never squeezed or written vertically.
 */
@Composable
fun SecTxDetailList(alert: SecAlert, isEn: Boolean, labelPadding: Dp = 0.dp) {
    val txs = alert.allTransactions()
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(start = labelPadding)) {
        Text(
            text = if (isEn) "TRANSACTIONS IN THIS FORM 4 (${txs.size})" else "TRANSAKTIONEN IN DIESEM FORMBLATT 4 (${txs.size})",
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CyanAccentGlow,
            letterSpacing = 0.4.sp
        )
        txs.forEachIndexed { i, tx ->
            val c = txColor(tx)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, c.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "${i + 1}. " + (if (tx.isBuy) "🟢 " else "🔴 ") +
                            (if (isEn) tx.txTypeEn else tx.txTypeDe).ifBlank { "Code ${tx.code}" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = c
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val secTitle = if (isEn) tx.securityTitle
                        .replace("Common Stock (Stammaktien)", "Common Stock")
                        .replace("Stammaktien (Common Stock)", "Common Stock")
                        .ifBlank { "Common Stock" } else tx.securityTitle.ifBlank { "Common Stock" }
                    TxField("TITLE OF SECURITY", secTitle, MaterialTheme.colorScheme.onSurface)
                    if (tx.transactionDate.isNotBlank()) {
                        TxField(if (isEn) "TRANSACTION DATE" else "TRANSAKTIONSDATUM", tx.transactionDate, MaterialTheme.colorScheme.onSurface)
                    }
                    val price = if (tx.priceFormatted.isNotBlank() && tx.priceFormatted != "-")
                        (if (isEn) " @ ${tx.priceFormatted}" else " à ${tx.priceFormatted}") else ""
                    TxField(if (isEn) "AMOUNT OF SECURITIES" else "STÜCKZAHL (AMOUNT)", tr(tx.sharesFormatted, isEn) + price, CyanAccentGlow)
                    if (tx.totalValueFormatted.isNotBlank() && tx.totalValueFormatted != "-") {
                        TxField(if (isEn) "TOTAL VALUE" else "GESAMTWERT", tx.totalValueFormatted, MaterialTheme.colorScheme.onSurface)
                    }
                    TxField(
                        if (isEn) "BENEFICIALLY OWNED AFTER" else "BESTAND DANACH (BENEFICIALS)",
                        tr(tx.securityBeneficialsFormatted.ifBlank { "-" }, isEn),
                        GreenBuy
                    )
                    TxField(
                        if (isEn) "OWNERSHIP NATURE" else "EIGENTUMSART (NATURE)",
                        if (tx.ownershipNature == "I") (if (isEn) "Indirect (I) · Trust/Holding" else "Indirekt (I) · Trust/Holding")
                        else (if (isEn) "Direct (D) · Personal Custody" else "Direkt (D) · Privatdepot"),
                        MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/** Title + explanation of a cluster depending on its mix of buys and sales. */
data class ClusterExplanation(val badge: String, val title: String, val body: String, val color: Color)

fun clusterExplanation(cluster: SecCluster, isEn: Boolean): ClusterExplanation {
    val n = cluster.uniqueInsidersCount
    val name = cluster.issuer
    val buys = cluster.buyCount
    val sells = cluster.sellCount
    return when {
        sells == 0 -> ClusterExplanation(
            badge = if (isEn) "🟢 BUY CLUSTER" else "🟢 KAUF-CLUSTER",
            title = if (isEn) "🟢 BUY CLUSTER: $n DIFFERENT INSIDERS ACQUIRED SHARES"
                    else "🟢 KAUF-CLUSTER: $n VERSCHIEDENE INSIDER ERWARBEN AKTIEN",
            body = if (isEn)
                "$n different insiders reported acquisitions at $name in the selected period ($buys transactions). Several people acting in the same direction is a stronger signal than a single trade. Note: Open-market purchases (Code P) use private money; awards and option exercises (Codes A/M) are part of compensation and are less meaningful."
            else
                "$n verschiedene Insider meldeten im gewählten Zeitraum Erwerbe bei $name ($buys Transaktionen). Mehrere Personen in dieselbe Richtung sind ein stärkeres Signal als ein einzelner Handel. Hinweis: Marktkäufe (Code P) erfolgen mit privatem Geld; Zuteilungen und Optionsausübungen (Code A/M) gehören zur Vergütung und sind weniger aussagekräftig.",
            color = GreenBuy
        )
        buys == 0 -> ClusterExplanation(
            badge = if (isEn) "🔴 SELL CLUSTER" else "🔴 VERKAUFS-CLUSTER",
            title = if (isEn) "🔴 SELL CLUSTER: $n DIFFERENT INSIDERS SOLD / DISPOSED SHARES"
                    else "🔴 VERKAUFS-CLUSTER: $n VERSCHIEDENE INSIDER VERKAUFTEN AKTIEN",
            body = if (isEn)
                "$n different insiders reported sales or disposals at $name in the selected period ($sells transactions). Sales happen for many reasons (taxes, diversification, pre-planned 10b5-1 plans) and are not automatically negative – but several sellers at once deserve attention."
            else
                "$n verschiedene Insider meldeten im gewählten Zeitraum Verkäufe oder Abgaben bei $name ($sells Transaktionen). Verkäufe haben viele Gründe (Steuern, Streuung, vorab geplante 10b5-1-Pläne) und sind nicht automatisch negativ – mehrere Verkäufer gleichzeitig verdienen aber Aufmerksamkeit.",
            color = RedSell
        )
        else -> ClusterExplanation(
            badge = if (isEn) "🟡 MIXED CLUSTER" else "🟡 GEMISCHTES CLUSTER",
            title = if (isEn) "🟡 MIXED CLUSTER: $buys ACQUISITIONS AND $sells SALES BY $n INSIDERS"
                    else "🟡 GEMISCHTES CLUSTER: $buys ERWERBE UND $sells VERKÄUFE VON $n INSIDERN",
            body = if (isEn)
                "$n different insiders at $name traded in both directions: $buys acquisitions (purchases, awards or option exercises) and $sells sales or tax withholdings. Often an award or option exercise is paired with a sale or tax withholding in the same filing – this is routine compensation handling and NOT a clear bullish or bearish signal. Check each transaction below: only open-market purchases (Code P) and open-market sales (Code S) reflect a real investment decision."
            else
                "$n verschiedene Insider bei $name handelten in beide Richtungen: $buys Erwerbe (Käufe, Zuteilungen oder Optionsausübungen) und $sells Verkäufe bzw. Steuereinbehalte. Oft wird eine Zuteilung oder Optionsausübung im selben Formblatt mit einem Verkauf oder Steuereinbehalt kombiniert – das ist übliche Vergütungsabwicklung und KEIN eindeutiges Kauf- oder Verkaufssignal. Prüfe jede Transaktion unten: Nur Marktkäufe (Code P) und Marktverkäufe (Code S) spiegeln eine echte Anlageentscheidung wider.",
            color = AmberWhale
        )
    }
}
