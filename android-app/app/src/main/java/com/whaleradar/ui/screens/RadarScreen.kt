package com.whaleradar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whaleradar.data.model.DataTimeframe
import com.whaleradar.data.model.FocusStock
import com.whaleradar.data.model.RadarData
import com.whaleradar.data.model.SuperSignal
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccent
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.RedSell

private fun usd(value: Long, isEn: Boolean): String =
    "$" + "%,d".format(if (isEn) java.util.Locale.US else java.util.Locale.GERMANY, value)

@Composable
fun RadarScreen(
    radar: RadarData,
    superSignals: List<SuperSignal>,
    focusStocks: List<FocusStock>,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier,
    selectedTimeframe: DataTimeframe = DataTimeframe.MONTH_1,
    watchlist: Set<String> = emptySet(),
    onToggleWatchlist: ((String) -> Unit)? = null,
    onOpenLegalDisclaimer: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ================= HEADER: WHAT THE RADAR SHOWS & HOW STOCKS ARE SELECTED =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.45f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEn) "STOCKS IN FOCUS" else "AKTIEN IM FOKUS",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AmberWhale
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanAccent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (isEn) "📅 Active data period: ${selectedTimeframe.periodEn} (CFTC, US Congress, SEC)."
                               else "📅 Aktiver Daten-Zeitraum: ${selectedTimeframe.periodDe} (CFTC, US-Kongress, SEC).",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isEn)
                        "The radar ranks stocks that were bought by politicians and/or company insiders. A stock rises in the list the more of these features it shows:"
                    else
                        "Das Radar sortiert Aktien, die von Politikern und/oder Firmen-Insidern gekauft wurden. Je mehr der folgenden Merkmale eine Aktie zeigt, desto weiter oben steht sie:",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val criteria = if (isEn) listOf(
                    "🏛️ Several different politicians (House & Senate) bought the stock; the higher the estimated purchase volume, the stronger.",
                    "🤝 Bipartisan: Democrats AND Republicans bought the same stock.",
                    "🏢 Company insiders (executives, directors, major shareholders) bought with their own money on the open market (SEC Form 4, code P).",
                    "🔥 Insider cluster: at least 2 different insiders of the same company bought; large purchase volumes count extra.",
                    "⭐ Combination signal: politicians AND insiders bought the same stock.",
                    "⚠️ Counter-signal: sales by politicians or insiders reduce the ranking."
                ) else listOf(
                    "🏛️ Mehrere verschiedene Politiker (Repräsentantenhaus & Senat) kauften die Aktie; je höher das geschätzte Kaufvolumen, desto stärker.",
                    "🤝 Überparteilich: Demokraten UND Republikaner kauften dieselbe Aktie.",
                    "🏢 Firmen-Insider (Vorstände, Aufsichtsräte, Großaktionäre) kauften mit eigenem Geld am offenen Markt (SEC Form 4, Code P).",
                    "🔥 Insider-Cluster: mindestens 2 verschiedene Insider derselben Firma kauften; hohe Kaufvolumen zählen zusätzlich.",
                    "⭐ Kombi-Signal: Politiker UND Insider kauften dieselbe Aktie.",
                    "⚠️ Gegenläufig: Verkäufe von Politikern oder Insidern senken die Platzierung."
                )
                criteria.forEach {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Update & Disclosure Schedule Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isEn) "📅 DATA UPDATE & DISCLOSURE SCHEDULE" else "📅 UPDATE- & BEKANNTGABE-ZEITPLAN",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isEn)
                                "• Tue & Fri (CFTC COT): Positions recorded every Tuesday, published every Friday at 15:30 EST.\n• Mon–Fri (SEC Form 4): Disclosures within 2 business days (T+2) after the trade.\n• Mon–Fri (US Congress): Disclosures within the 45-day STOCK Act deadline."
                            else
                                "• Di & Fr (CFTC COT): Positions-Stichtag jeden Dienstag, Bekanntgabe jeden Freitag um 15:30 EST (21:30 MEZ).\n• Mo–Fr (SEC Form 4): Meldung binnen max. 2 Werktagen (T+2) nach dem Handel.\n• Mo–Fr (US-Kongress): Meldung innerhalb der 45-Tage-Frist nach STOCK Act.",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // ================= MARKET CONTEXT (COT) =================
        if (radar.whaleLongContracts + radar.whaleShortContracts > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isEn) "🐋 MARKET CONTEXT (CFTC COT)" else "🐋 MARKTUMFELD (CFTC COT)",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isEn) radar.whaleSubEn else radar.whaleSubDe,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 15.sp
                    )
                    if (radar.reportDate.isNotBlank()) {
                        Text(
                            text = (if (isEn) "Report date: " else "Stichtag: ") + radar.reportDate,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================= RANKING =================
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("🔥", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isEn) "TOP STOCKS (${focusStocks.size})" else "TOP-AKTIEN (${focusStocks.size})",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.5.sp,
                color = AmberWhale
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        if (focusStocks.isEmpty()) {
            Text(
                text = if (isEn) "No stock has been bought by politicians or insiders in the selected period (${selectedTimeframe.labelEn}) – or data is still loading."
                       else "Im gewählten Zeitraum (${selectedTimeframe.labelDe}) wurde keine Aktie von Politikern oder Insidern gekauft (oder die Daten werden noch geladen).",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        focusStocks.forEachIndexed { index, f ->
            val isOnWatchlist = watchlist.contains(f.ticker)
            val kombi = f.congressBuyers > 0 && f.insiderBuyers > 0
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onOpenAnalysis(f.ticker) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(if (kombi) 1.5.dp else 1.dp, if (kombi) AmberWhale.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = AmberWhale.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        text = "$${f.ticker}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = AmberWhale,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                if (kombi) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isEn) "⭐ COMBO" else "⭐ KOMBI",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberWhale,
                                        softWrap = false
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = f.companyName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (onToggleWatchlist != null) {
                            IconButton(onClick = { onToggleWatchlist(f.ticker) }, modifier = Modifier.size(36.dp)) {
                                Icon(
                                    imageVector = if (isOnWatchlist) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Watchlist",
                                    tint = if (isOnWatchlist) AmberWhale else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isEn) "WHY THIS STOCK?" else "WARUM DIESE AKTIE?",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    @Composable
                    fun Feature(text: String, color: Color) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = text,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }

                    if (f.congressBuyers > 0) {
                        val parties = buildString {
                            val parts = mutableListOf<String>()
                            if (f.congressDem > 0) parts.add(if (isEn) "${f.congressDem} Democrat(s)" else "${f.congressDem} Demokrat(en)")
                            if (f.congressRep > 0) parts.add(if (isEn) "${f.congressRep} Republican(s)" else "${f.congressRep} Republikaner")
                            append(parts.joinToString(", "))
                        }
                        Feature(
                            text = if (isEn) "🏛️ ${f.congressBuyers} politician(s) bought ($parties) · approx. ${usd(f.congressValue, true)} in total"
                                   else "🏛️ ${f.congressBuyers} Politiker kauften ($parties) · ca. ${usd(f.congressValue, false)} gesamt",
                            color = CyanAccent
                        )
                    }
                    if (f.bipartisan) {
                        Feature(
                            text = if (isEn) "🤝 Bipartisan: both parties bought this stock" else "🤝 Überparteilich: beide Parteien kauften diese Aktie",
                            color = CyanAccent
                        )
                    }
                    if (f.insiderBuyers > 0) {
                        Feature(
                            text = if (isEn) "🏢 ${f.insiderBuyers} insider(s) bought on the open market (${f.insiderBuyCount} purchase(s)) · ${usd(f.insiderValue, true)}"
                                   else "🏢 ${f.insiderBuyers} Insider kauften am offenen Markt (${f.insiderBuyCount} Käufe) · ${usd(f.insiderValue, false)}",
                            color = GreenBuy
                        )
                    }
                    if (f.insiderBuyers >= 2) {
                        Feature(
                            text = if (isEn) "🔥 Insider cluster: ${f.insiderBuyers} different insiders bought" else "🔥 Insider-Cluster: ${f.insiderBuyers} verschiedene Insider kauften",
                            color = AmberWhale
                        )
                    }
                    if (kombi) {
                        Feature(
                            text = if (isEn) "⭐ Combination signal: politicians AND insiders bought" else "⭐ Kombi-Signal: Politiker UND Insider kauften",
                            color = AmberWhale
                        )
                    }
                    if (f.congressSellers > 0 || f.insiderSellers > 0) {
                        Feature(
                            text = if (isEn) "⚠️ Counter-signal: ${f.congressSellers} politician(s) and ${f.insiderSellers} insider(s) sold"
                                   else "⚠️ Gegenläufig: ${f.congressSellers} Politiker und ${f.insiderSellers} Insider verkauften",
                            color = RedSell
                        )
                    }

                    if (f.lastActivity.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (if (isEn) "Latest activity: " else "Letzte Aktivität: ") + f.lastActivity,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }

        // ================= LEGAL NOTICE FOOTER =================
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenLegalDisclaimer?.invoke() },
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEn) "⚖️ No Financial Advice · Legal Notice, Sources & Privacy"
                           else "⚖️ Keine Anlageberatung · Rechtliche Hinweise, Quellen & Datenschutz",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isEn) {
                        "The ranking is a purely mechanical evaluation of public disclosures and is not a buy recommendation. All data (CFTC, US Congress, SEC) is for informational purposes only and does not constitute investment advice. Independent app – not affiliated with any US government agency. Tap for full details."
                    } else {
                        "Die Reihenfolge ist eine rein rechnerische Auswertung öffentlicher Meldungen und keine Kaufempfehlung. Alle Angaben (CFTC, US-Kongress, SEC) dienen ausschließlich zu Informationszwecken und stellen keine Anlageberatung dar. Unabhängige App ohne Behörden-Zugehörigkeit. Für Details hier tippen."
                    },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
