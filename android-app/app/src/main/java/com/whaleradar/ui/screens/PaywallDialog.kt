package com.whaleradar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.whaleradar.data.model.DataTimeframe
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.CyanAccent
import com.whaleradar.theme.GreenBuy

@Composable
fun TimeframeSelectorBar(
    selectedTimeframe: DataTimeframe,
    isProUnlocked: Boolean,
    isLoading: Boolean,
    onSelectTimeframe: (DataTimeframe) -> Unit,
    onOpenPaywall: (DataTimeframe?) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isEn) "📅 Period:" else "📅 Zeitraum:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    softWrap = false,
                    maxLines = 1
                )

                DataTimeframe.values().forEach { tf ->
                    val isSelected = selectedTimeframe == tf
                    val isLocked = tf.isProOnly && !isProUnlocked
                    val label = if (isEn) tf.labelEn else tf.labelDe

                    val bgColor = when {
                        isSelected && tf.isProOnly -> AmberWhale
                        isSelected -> MaterialTheme.colorScheme.primary
                        isLocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                    }
                    val textColor = when {
                        isSelected -> Color.Black
                        isLocked -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                    val borderColor = when {
                        isSelected && tf.isProOnly -> AmberWhale
                        isSelected -> MaterialTheme.colorScheme.primary
                        tf.isProOnly -> AmberWhale.copy(alpha = 0.45f)
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    Surface(
                        modifier = Modifier.clickable {
                            if (isLocked) {
                                onOpenPaywall(tf)
                            } else if (!isSelected) {
                                onSelectTimeframe(tf)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = bgColor,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLocked) {
                                Text(
                                    text = "🔒 ",
                                    fontSize = 9.5.sp,
                                    softWrap = false,
                                    maxLines = 1
                                )
                            } else if (tf.isProOnly) {
                                Text(
                                    text = "👑 ",
                                    fontSize = 9.5.sp,
                                    softWrap = false,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = textColor,
                                softWrap = false,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = if (selectedTimeframe.isProOnly) AmberWhale else MaterialTheme.colorScheme.primary
                )
            } else {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun PaywallDialog(
    isProUnlocked: Boolean,
    currentTimeframe: DataTimeframe,
    requestedTimeframe: DataTimeframe?,
    onUnlockPro: (planId: String, targetTimeframe: DataTimeframe) -> Unit,
    onResetProForTesting: () -> Unit,
    onDismiss: () -> Unit,
    isEn: Boolean
) {
    var selectedPlan by remember { mutableStateOf("yearly") }
    var targetTf by remember {
        mutableStateOf(
            requestedTimeframe?.takeIf { it.isProOnly }
                ?: currentTimeframe.takeIf { it.isProOnly }
                ?: DataTimeframe.MONTHS_6
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .heightIn(min = 320.dp, max = 720.dp),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 10.dp,
            border = BorderStroke(1.5.dp, AmberWhale.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AmberWhale,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "👑 WHALERADAR PRO",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        if (isProUnlocked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = GreenBuy.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, GreenBuy)
                            ) {
                                Text(
                                    text = if (isEn) "ACTIVE" else "AKTIV",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GreenBuy,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (isEn) "Unlock Extended Update Timeframes" else "Größere Zeiträume für das Datenupdate freischalten",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isEn) {
                            "Instead of only the last 4 weeks (1 month), analyze and update real institutional & insider trades across 3 Months, 6 Months, 1 Year, or 2 Years."
                        } else {
                            "Analysiere und aktualisiere echte Politiker- und Insider-Käufe nicht nur für die letzten 4 Wochen (1 Monat), sondern wähle flexibel zwischen 1 Monat, 3 Monaten, 6 Monaten, 1 Jahr und 2 Jahren."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Timeframe Preview / Picker inside Paywall
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AmberWhale.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, AmberWhale.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isEn) "📅 CHOOSE YOUR UPDATE TIMEFRAME:" else "📅 GEWÜNSCHTEN ZEITRAUM FÜR DAS UPDATE WÄHLEN:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberWhale
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                DataTimeframe.values().forEach { tf ->
                                    val isChosen = targetTf == tf
                                    val lbl = if (isEn) tf.labelEn else tf.labelDe
                                    val sub = if (!tf.isProOnly) (if (isEn) "Free" else "Basis") else "PRO"
                                    Surface(
                                        modifier = Modifier.clickable { targetTf = tf },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isChosen) AmberWhale else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isChosen) AmberWhale else MaterialTheme.colorScheme.outlineVariant
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = lbl,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isChosen) Color.Black else MaterialTheme.colorScheme.onSurface,
                                                softWrap = false,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = sub,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChosen) Color.Black.copy(alpha = 0.75f) else AmberWhale,
                                                softWrap = false,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Benefits Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isEn) "WHAT YOU GET WITH WHALERADAR PRO" else "DEINE VORTEILE MIT WHALERADAR PRO",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyanAccent
                            )

                            val features = if (isEn) listOf(
                                "📅 5 Update Timeframes: 1 Month, 3 Months, 6 Months, 1 Year & 2 Years (up to 730 days lookback).",
                                "🏛️ Deep US Congress History: Track multi-month & multi-year stock accumulation by Democrats & Republicans.",
                                "🏢 Extended SEC Form 4 Clusters: Detect multi-quarter insider buying waves & executive cluster alerts.",
                                "🎯 Long-Term Focus Radar: Ranking & combination signals dynamically calculated over your chosen period."
                            ) else listOf(
                                "📅 5 Zeiträume zur Auswahl: 1 Monat, 3 Monate, 6 Monate, 1 Jahr & 2 Jahre (bis zu 730 Tage Rückblick).",
                                "🏛️ Tiefe US-Kongress-Historie: Langfristige Käufe von Demokraten & Republikanern sowie überparteiliche Favoriten.",
                                "🏢 Mehrjährige SEC-Insider-Cluster: Erkenne, wenn Vorstände über mehrere Quartale hinweg eigene Aktien akkumulieren.",
                                "🎯 Dynamisches Fokus-Radar: Top-Aktien, Partei-Sentiments und Kombi-Signale passen sich sofort dem Zeitraum an."
                            )

                            features.forEach { feat ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GreenBuy,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = feat,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pricing Plans
                    Text(
                        text = if (isEn) "CHOOSE YOUR PLAN" else "TARIF WÄHLEN",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    data class PlanOption(
                        val id: String,
                        val title: String,
                        val price: String,
                        val sub: String,
                        val badge: String? = null
                    )

                    val plans = if (isEn) listOf(
                        PlanOption("monthly", "Monthly PRO", "€4.99 / month", "Flexible · Cancel anytime"),
                        PlanOption("yearly", "Yearly PRO", "€29.99 / year", "Only €2.50 / month · Save 50%", "BEST VALUE"),
                        PlanOption("lifetime", "Lifetime Access", "€59.99 one-time", "Pay once · Unlimited PRO forever", "NO SUBSCRIPTION")
                    ) else listOf(
                        PlanOption("monthly", "PRO Monatlich", "4,99 € / Monat", "Flexibel · Monatlich kündbar"),
                        PlanOption("yearly", "PRO Jährlich", "29,99 € / Jahr", "Nur 2,50 € / Monat · 50 % sparen", "EMPFOHLEN"),
                        PlanOption("lifetime", "PRO Lifetime (Einmalig)", "59,99 € einmalig", "Einmal zahlen · Dauerhaft freigeschaltet", "KEIN ABO")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        plans.forEach { p ->
                            val isSelected = selectedPlan == p.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPlan = p.id },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AmberWhale.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) AmberWhale else MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = p.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (p.badge != null) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = AmberWhale,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = p.badge,
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.Black,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = p.sub,
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = p.price,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) AmberWhale else MaterialTheme.colorScheme.onSurface,
                                        softWrap = false,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Bottom Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val tfLabel = if (isEn) targetTf.labelEn else targetTf.labelDe
                    Button(
                        onClick = { onUnlockPro(selectedPlan, targetTf) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberWhale,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (isProUnlocked) {
                                if (isEn) "✅ Apply Timeframe ($tfLabel) & Update Data" else "✅ Zeitraum ($tfLabel) übernehmen & Daten laden"
                            } else {
                                if (isEn) "🚀 Unlock PRO & Load $tfLabel" else "🚀 Jetzt PRO freischalten & $tfLabel laden"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onUnlockPro("restore", targetTf) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isEn) "Restore Purchase" else "Kauf wiederherstellen",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (isProUnlocked) {
                            TextButton(
                                onClick = onResetProForTesting,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isEn) "Lock PRO (Test Paywall)" else "PRO sperren (Paywall testen)",
                                    fontSize = 11.sp,
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
