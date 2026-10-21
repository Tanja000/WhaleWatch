package com.example.whalewatch.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whalewatch.data.model.FilterTab
import com.example.whalewatch.data.model.SecFiling
import com.example.whalewatch.data.model.SecStats
import com.example.whalewatch.data.model.TradeType
import java.util.Locale

import androidx.navigation3.runtime.NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel() }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0B0F19),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🐋", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "InsiderWhales",
                                color = Color(0xFFF8FAFC),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    state.apiMode,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF38BDF8),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Aktualisieren",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.onToggleFilter(true) }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (state.minAmountFilter > 0 || state.onlyDiscretionary) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                        )
                    }
                    IconButton(onClick = { viewModel.onToggleSettings(true) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Einstellungen",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            StatsSummaryBanner(stats = state.stats)

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                placeholder = {
                    Text("Ticker, Firma oder Insider suchen...", color = Color(0xFF64748B), fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Löschen", tint = Color(0xFF94A3B8))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF151E2E),
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color(0xFFF8FAFC),
                    unfocusedTextColor = Color(0xFFF8FAFC)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
            )

            TabsRow(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) },
                watchlistCount = state.displayedFilings.count { it.isBookmarked }
            )

            state.errorMessage?.let { msg ->
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(msg, color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${state.displayedFilings.size} Form 4 Trades",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (state.onlyDiscretionary || state.minAmountFilter > 0) {
                    Text(
                        "Filter aktiv",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp
                    )
                }
            }

            if (state.displayedFilings.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Keine Form 4 Insider-Trades gefunden",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.displayedFilings, key = { it.accessionNumber }) { filing ->
                        FilingCard(
                            filing = filing,
                            onClick = { viewModel.onSelectFiling(filing) },
                            onToggleBookmark = { viewModel.onToggleBookmark(filing) },
                            onOpenFilingUrl = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(filing.filingUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    state.selectedFiling?.let { filing ->
        FilingDetailDialog(
            filing = filing,
            onDismiss = { viewModel.onSelectFiling(null) },
            onToggleBookmark = { viewModel.onToggleBookmark(filing) },
            onOpenFilingUrl = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(filing.filingUrl))
                context.startActivity(intent)
            }
        )
    }

    if (state.showFilterDialog) {
        FilterDialog(
            minAmount = state.minAmountFilter,
            onlyDiscretionary = state.onlyDiscretionary,
            onMinAmountChange = { viewModel.onSetMinAmount(it) },
            onDiscretionaryToggle = { viewModel.onToggleOnlyDiscretionary(it) },
            onDismiss = { viewModel.onToggleFilter(false) }
        )
    }

    if (state.showSettingsDialog) {
        SettingsDialog(
            currentApiKey = state.apiKey,
            onSaveApiKey = { viewModel.onSetApiKey(it) },
            onDismiss = { viewModel.onToggleSettings(false) }
        )
    }
}
@Composable
fun StatsSummaryBanner(stats: SecStats) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151E2E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "MARKET INSIDER RADAR",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    "Bull/Bear: ${stats.buyRatioPercent}% Bullisch",
                    color = if (stats.buyRatioPercent >= 50) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF334155))
            ) {
                val buyWeight = stats.buyRatioPercent.coerceIn(5, 95) / 100f
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(buyWeight)
                        .background(Color(0xFF10B981))
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f - buyWeight)
                        .background(Color(0xFFEF4444))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Käufe (Buy)", value = "${stats.buyCount}", subValue = formatVolume(stats.totalBuyVolume), color = Color(0xFF10B981))
                StatItem(label = "Verkäufe (Sell)", value = "${stats.sellCount}", subValue = formatVolume(stats.totalSellVolume), color = Color(0xFFEF4444))
                StatItem(label = "Whales (> $1M)", value = "${stats.whaleTradesCount}", subValue = "Großorders", color = Color(0xFFF59E0B))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, subValue: String, color: Color) {
    Column {
        Text(label, color = Color(0xFF64748B), fontSize = 11.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(subValue, color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
    }
}

@Composable
fun TabsRow(
    selectedTab: FilterTab,
    onTabSelected: (FilterTab) -> Unit,
    watchlistCount: Int
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedTab == FilterTab.ALL,
                onClick = { onTabSelected(FilterTab.ALL) },
                label = { Text("Alle Trades") },
                colors = chipColors()
            )
        }
        item {
            FilterChip(
                selected = selectedTab == FilterTab.BUYS,
                onClick = { onTabSelected(FilterTab.BUYS) },
                label = { Text("🟢 Käufe") },
                colors = chipColors()
            )
        }
        item {
            FilterChip(
                selected = selectedTab == FilterTab.SELLS,
                onClick = { onTabSelected(FilterTab.SELLS) },
                label = { Text("🔴 Verkäufe") },
                colors = chipColors()
            )
        }
        item {
            FilterChip(
                selected = selectedTab == FilterTab.WHALES,
                onClick = { onTabSelected(FilterTab.WHALES) },
                label = { Text("🐳 Whales (> $1M)") },
                colors = chipColors()
            )
        }
        item {
            FilterChip(
                selected = selectedTab == FilterTab.WATCHLIST,
                onClick = { onTabSelected(FilterTab.WATCHLIST) },
                label = { Text("⭐ Watchlist ($watchlistCount)") },
                colors = chipColors()
            )
        }
    }
}

@Composable
fun chipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color(0xFF151E2E),
    labelColor = Color(0xFF94A3B8),
    selectedContainerColor = Color(0xFF1E293B),
    selectedLabelColor = Color(0xFF38BDF8)
)
@Composable
fun FilingCard(
    filing: SecFiling,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenFilingUrl: () -> Unit
) {
    val isBuy = filing.tradeType == TradeType.BUY
    val accentColor = if (isBuy) Color(0xFF10B981) else Color(0xFFEF4444)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151E2E)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(130.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Text(
                                filing.ticker,
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            filing.companyName,
                            color = Color(0xFFF8FAFC),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 180.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            if (filing.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Watchlist",
                            tint = if (filing.isBookmarked) Color(0xFFF59E0B) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        filing.reportingOwner,
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Text(
                            filing.ownerRole,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                if (isBuy) "KAUF (Buy)" else "VERKAUF (Sell)",
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "${filing.formattedShares} Stk. @ $${String.format(Locale.US, "%.2f", filing.pricePerShare)}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        filing.formattedValue,
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (filing.isWhale) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "🐳 WHALE",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        if (filing.isRule10b51) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF334155)
                            ) {
                                Text(
                                    "10b5-1 Plan",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        } else if (isBuy) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "🔥 Freier Kauf",
                                    color = Color(0xFF10B981),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        filing.filingDate,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
@Composable
fun FilingDetailDialog(
    filing: SecFiling,
    onDismiss: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenFilingUrl: () -> Unit
) {
    val isBuy = filing.tradeType == TradeType.BUY
    val accentColor = if (isBuy) Color(0xFF10B981) else Color(0xFFEF4444)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        filing.ticker,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Form 4 Details",
                        color = Color(0xFFF8FAFC),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        if (filing.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Watchlist",
                        tint = if (filing.isBookmarked) Color(0xFFF59E0B) else Color(0xFF64748B)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailRow("Unternehmen", filing.companyName)
                DetailRow("Company CIK", filing.companyCik)
                DetailRow("Insider", filing.reportingOwner)
                DetailRow("Owner CIK", filing.ownerCik)
                DetailRow("Funktion", filing.ownerRole)
                HorizontalDivider(color = Color(0xFF1E293B))

                DetailRow("Transaktion", if (isBuy) "🟢 Kauf (Purchase - P)" else "🔴 Verkauf (Sale - S)")
                DetailRow("Aktien", "${filing.formattedShares} Stück")
                DetailRow("Ausführungspreis", "$${String.format(Locale.US, "%.2f", filing.pricePerShare)}")
                DetailRow("Gesamtwert", filing.formattedValue, highlightColor = accentColor)
                DetailRow("Bestand danach", String.format(Locale.US, "%,d Aktien", filing.sharesOwnedFollowing))
                HorizontalDivider(color = Color(0xFF1E293B))

                DetailRow(
                    "Rule 10b5-1 Plan",
                    if (filing.isRule10b51) "Ja (Geplanter Verkauf)" else "Nein (Freie Marktentscheidung)",
                    highlightColor = if (filing.isRule10b51) Color(0xFF94A3B8) else Color(0xFF10B981)
                )
                DetailRow("SEC Einreichung", filing.filingDate)
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenFilingUrl,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
            ) {
                Text("Auf SEC EDGAR ansehen", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Schließen", color = Color(0xFF94A3B8))
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String, highlightColor: Color = Color(0xFFF8FAFC)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF64748B), fontSize = 12.sp)
        Text(
            value,
            color = highlightColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FilterDialog(
    minAmount: Double,
    onlyDiscretionary: Boolean,
    onMinAmountChange: (Double) -> Unit,
    onDiscretionaryToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Text("Filter & Whale-Alerts", color = Color(0xFFF8FAFC), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Minimale Transaktionsgröße: ${if (minAmount == 0.0) "Alle Größen" else "$${(minAmount/1000).toInt()}k"}",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )

                Slider(
                    value = (minAmount / 1_000_000.0).toFloat(),
                    onValueChange = { onMinAmountChange((it * 1_000_000.0)) },
                    valueRange = 0f..5f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF38BDF8),
                        inactiveTrackColor = Color(0xFF334155)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nur Freie Insider-Käufe", color = Color(0xFFF8FAFC), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Blendet automatische 10b5-1 Verkäufe aus", color = Color(0xFF64748B), fontSize = 11.sp)
                    }
                    Switch(
                        checked = onlyDiscretionary,
                        onCheckedChange = onDiscretionaryToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF38BDF8),
                            checkedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
            ) {
                Text("Fertig", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun SettingsDialog(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var apiKeyText by remember { mutableStateOf(currentApiKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Text("API & SEC Konfiguration", color = Color(0xFFF8FAFC), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Standard: Direkte SEC EDGAR Form 4 Live-Schnittstelle (100% kostenlos und aktiv).",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )

                Text(
                    "Optional: RapidAPI / Insider Trades API Key eingeben:",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = apiKeyText,
                    onValueChange = { apiKeyText = it },
                    placeholder = { Text("RapidAPI Key eingeben...", color = Color(0xFF64748B), fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF151E2E),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color(0xFFF8FAFC),
                        unfocusedTextColor = Color(0xFFF8FAFC)
                    )
                )

                Surface(
                    color = Color(0xFF151E2E),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("SEC EDGAR Compliance:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("• User-Agent: WhaleWatchInsider/1.0", color = Color(0xFF10B981), fontSize = 11.sp)
                        Text("• Endpunkt: /cgi-bin/browse-edgar (Form 4)", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveApiKey(apiKeyText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
            ) {
                Text("Speichern", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = Color(0xFF94A3B8))
            }
        }
    )
}

fun formatVolume(vol: Double): String {
    return when {
        vol >= 1_000_000_000.0 -> String.format(Locale.US, "$%.2fB", vol / 1_000_000_000.0)
        vol >= 1_000_000.0 -> String.format(Locale.US, "$%.1fM", vol / 1_000_000.0)
        vol >= 1_000.0 -> String.format(Locale.US, "$%.0fK", vol / 1_000.0)
        else -> String.format(Locale.US, "$%.0f", vol)
    }
}