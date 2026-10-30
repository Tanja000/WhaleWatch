package com.whaleradar.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccent
import com.whaleradar.theme.GreenBuy

@Composable
fun LegalDisclaimerDialog(
    isEn: Boolean,
    isFirstLaunch: Boolean = false,
    onLanguageChange: ((String) -> Unit)? = null,
    onAcceptOrDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentIsEn by remember(isEn) { mutableStateOf(isEn) }

    fun openUrl(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {}
    }

    Dialog(
        onDismissRequest = { if (!isFirstLaunch) onAcceptOrDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isFirstLaunch,
            dismissOnClickOutside = !isFirstLaunch
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                    Row(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚖️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (currentIsEn) "Legal Notice & Disclaimer" else "Rechtliche Hinweise & Haftungsausschluss",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (currentIsEn) "No Financial Advice · Sources · Privacy" else "Keine Anlageberatung · Quellen · Datenschutz",
                                fontSize = 11.sp,
                                color = AmberWhale,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (!isFirstLaunch) {
                        IconButton(onClick = onAcceptOrDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Prominent Language Selector Segment (🇩🇪 Deutsch | 🇬🇧 English)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    currentIsEn = false
                                    onLanguageChange?.invoke("de")
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (!currentIsEn) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🇩🇪", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Deutsch",
                                    fontSize = 12.sp,
                                    fontWeight = if (!currentIsEn) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (!currentIsEn) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    currentIsEn = true
                                    onLanguageChange?.invoke("en")
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentIsEn) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 7.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🇬🇧", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "English",
                                    fontSize = 12.sp,
                                    fontWeight = if (currentIsEn) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (currentIsEn) Color.Black else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Scrollable Legal Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Keine Anlageberatung / No Investment Advice
                    LegalSectionCard(
                        icon = "⚠️",
                        title = if (currentIsEn) "1. Legal Disclaimer & No Investment Advice" else "1. Rechtlicher Hinweis & Keine Anlageberatung",
                        borderColor = AmberWhale.copy(alpha = 0.55f),
                        body = if (currentIsEn) {
                            "LEGAL DISCLAIMER: WhaleRadar aggregates publicly available primary data solely for informational and market research purposes. WhaleRadar does not provide investment advice, financial analysis within the meaning of the German Securities Trading Act (WpHG), or any solicitation to buy or sell securities. All investment decisions are made at your own responsibility.\n\nAll contents, signals, confluence indicators, CFTC COT market data, congressional disclosures, and SEC Form 4 insider filings provided in the 'WhaleRadar' app are strictly for general informational, educational, and journalistic research purposes only. Nothing in this app constitutes financial, investment, legal, or tax advice, nor does it represent a solicitation, recommendation, or endorsement to buy, hold, or sell any securities, futures, or financial instruments."
                        } else {
                            "RECHTLICHER HINWEIS: WhaleRadar aggregiert öffentlich zugängliche Primärdaten ausschließlich zu Informations- und Marktforschungszwecken. WhaleRadar ist keine Anlageberatung, Finanzanalyse im Sinne des WpHG oder Aufforderung zum Kauf oder Verkauf von Wertpapieren. Alle Anlageentscheidungen treffen Sie eigenverantwortlich.\n\nAlle in der App „WhaleRadar“ bereitgestellten Inhalte, Auswertungen, Kombi-Signale, CFTC-COT-Marktdaten, Kongress-Transaktionen und SEC-Form-4-Meldungen dienen ausschließlich allgemeinen Informations-, Bildungs- und Recherchezwecken. Sie stellen ausdrücklich keine Anlageberatung, keine Finanzanalyse, keine Rechts- oder Steuerberatung und keine Aufforderung oder Empfehlung zum Kauf, Halten oder Verkauf von Aktien, Futures oder sonstigen Finanzinstrumenten (z. B. im Sinne des WpHG oder § 34f GewO) dar."
                        }
                    )

                    // 2. Risikohinweis / Risk Disclosure
                    LegalSectionCard(
                        icon = "📉",
                        title = if (currentIsEn) "2. Investment Risk Warning" else "2. Risikohinweis zum Wertpapierhandel",
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        body = if (currentIsEn) {
                            "Trading stocks, ETFs, futures, and other financial instruments involves substantial risk of loss and is not suitable for every investor. Past performance and historical transactions by institutional traders, politicians, or corporate insiders are not reliable indicators of future results. You are solely responsible for your own investment decisions."
                        } else {
                            "Der Handel mit Aktien, ETFs, Terminkontrakten (Futures) und anderen Finanzinstrumenten ist mit erheblichen finanziellen Risiken bis hin zum Totalverlust des eingesetzten Kapitals verbunden. Vergangene Kursentwicklungen sowie vergangene Käufe oder Verkäufe von Großspekulanten, Politikern oder Unternehmens-Insidern sind kein verlässlicher Indikator für zukünftige Wertentwicklungen. Jede Investitionsentscheidung erfolgt ausschließlich auf eigenes Risiko und in eigener Verantwortung des Nutzers."
                        }
                    )

                    // 3. Keine Regierungszugehörigkeit & Offizielle Quellen (Google Play Policy!)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (currentIsEn) "🏛️ 3. Government Non-Affiliation & Official Public Sources"
                                       else "🏛️ 3. Keine Behörden-Zugehörigkeit & Offizielle Quellen",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (currentIsEn) {
                                    "WhaleRadar is an independent private financial information application. It is NOT affiliated with, endorsed by, sponsored by, or operated by the United States Government, the Commodity Futures Trading Commission (CFTC), the U.S. Securities and Exchange Commission (SEC), the U.S. House of Representatives, or any other government agency. All regulatory data displayed in this app is retrieved from publicly accessible official US government disclosure portals:"
                                } else {
                                    "WhaleRadar ist eine unabhängige, privat betriebene Informations-App. Sie steht in keinerlei offizieller Verbindung zur US-Regierung, der Commodity Futures Trading Commission (CFTC), der US-Börsenaufsicht SEC, dem US-Repräsentantenhaus oder sonstigen staatlichen Behörden und wird von diesen weder unterstützt noch betrieben. Alle dargestellten Melderegister-Daten stammen aus öffentlich zugänglichen Originalquellen der US-Behörden:"
                                },
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val sources = listOf(
                                "Insider Transactions (SEC Form 4): U.S. Securities and Exchange Commission (EDGAR)" to "https://www.sec.gov/edgar/search/",
                                "U.S. House of Representatives (STOCK Act PTR): Office of the Clerk, U.S. House of Representatives" to "https://disclosures-clerk.house.gov",
                                "Futures Market Positioning (COT Report): U.S. Commodity Futures Trading Commission (CFTC)" to "https://www.cftc.gov/MarketReports/CommitmentsofTraders/index.htm"
                            )
                            sources.forEach { (label, url) ->
                                Text(
                                    text = "🔗 $label",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                    modifier = Modifier
                                        .padding(vertical = 3.dp)
                                        .clickable { openUrl(url) }
                                )
                            }
                        }
                    }

                    // 4. Haftungsausschluss & Meldeverzug / Accuracy & Reporting Delays
                    LegalSectionCard(
                        icon = "⏱️",
                        title = if (currentIsEn) "4. Data Accuracy & Statutory Reporting Delays" else "4. Haftungsausschluss für Daten & Meldefristen",
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        body = if (currentIsEn) {
                            "While every effort is made to process public filings accurately, no warranty or liability is assumed for the completeness, accuracy, or timeliness of the information provided. Public disclosures are subject to statutory reporting lags: CFTC COT data is recorded on Tuesday and published on Friday; SEC Form 4 filings have a 2-business-day deadline; US Congress STOCK Act disclosures allow up to 45 days after a transaction."
                        } else {
                            "Trotz sorgfältiger Aufbereitung der öffentlichen Registerdaten wird keine Gewähr oder Haftung für die Richtigkeit, Vollständigkeit, Genauigkeit oder jederzeitige Aktualität der Daten übernommen. Gesetzliche Pflicht-Offenlegungen unterliegen behördlichen Meldefristen: CFTC-COT-Daten werden dienstags erhoben und freitags veröffentlicht; SEC-Form-4-Meldungen haben eine Frist von 2 Werktagen; Kongress-Meldungen nach dem STOCK Act dürfen bis zu 45 Tage nach einer Transaktion eingereicht werden."
                        }
                    )

                    // 5. Datenschutz / Privacy Policy (DSGVO / GDPR)
                    LegalSectionCard(
                        icon = "🔒",
                        title = if (currentIsEn) "5. Privacy Policy & Local Data Storage (GDPR)" else "5. Datenschutzerklärung & Lokale Speicherung (DSGVO)",
                        borderColor = GreenBuy.copy(alpha = 0.4f),
                        body = if (currentIsEn) {
                            "WhaleRadar respects your privacy: The app requires no user registration, collects no personal data, uses no advertising SDKs or analytics trackers, and does not share any user data with third parties. Your personal watchlist, theme preference, and language setting are stored exclusively locally on your device. When refreshing market data, your device connects directly via encrypted HTTPS to the public APIs of the respective data providers."
                        } else {
                            "WhaleRadar schützt deine Privatsphäre: Die App erfordert keine Registrierung, erhebt oder speichert keinerlei personenbezogene Daten, nutzt keine Werbe- oder Tracking-Dienste und gibt keine Nutzerdaten an Dritte weiter. Deine persönliche Watchlist sowie Design- und Spracheinstellungen werden ausschließlich lokal auf deinem eigenen Smartphone gespeichert. Beim Aktualisieren von Marktdaten stellt dein Gerät eine direkte, verschlüsselte HTTPS-Verbindung zu den öffentlichen Datenquellen her."
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Accept / Close Button
                Button(
                    onClick = onAcceptOrDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isFirstLaunch) {
                            if (currentIsEn) "I Understand & Accept (No Financial Advice)" else "Verstanden & Akzeptieren (Keine Anlageberatung)"
                        } else {
                            if (currentIsEn) "Close" else "Schließen"
                        },
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalSectionCard(
    icon: String,
    title: String,
    borderColor: Color,
    body: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
