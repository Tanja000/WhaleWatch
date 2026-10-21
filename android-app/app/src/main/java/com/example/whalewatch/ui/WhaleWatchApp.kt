package com.example.whalewatch.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whalewatch.data.model.TabScreen
import com.example.whalewatch.data.model.isValidStockTicker
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.WhaleWatchTheme
import com.example.whalewatch.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhaleWatchApp(
    viewModel: WhaleViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val activeTab by viewModel.activeTab.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val language by viewModel.language.collectAsState()
    val isEn = language == "en"

    val radarData by viewModel.radarData.collectAsState()
    val superSignals by viewModel.superSignals.collectAsState()
    val partySentiment by viewModel.partySentiment.collectAsState()

    val cotMarkets by viewModel.filteredCotMarkets.collectAsState()
    val selectedCotCat by viewModel.cotCategory.collectAsState()

    val congressTrades by viewModel.filteredCongressTrades.collectAsState()
    val congressQuery by viewModel.congressQuery.collectAsState()
    val congressType by viewModel.congressType.collectAsState()
    val congressParty by viewModel.congressParty.collectAsState()
    val congressMode by viewModel.congressMode.collectAsState()

    val secAlerts by viewModel.filteredSecAlerts.collectAsState()
    val secClusters by viewModel.filteredSecClusters.collectAsState()
    val secQuery by viewModel.secQuery.collectAsState()
    val lastUpdatedTime by viewModel.lastUpdatedTime.collectAsState()

    val watchlist by viewModel.watchlist.collectAsState()
    val analysisTicker by viewModel.selectedAnalysisTicker.collectAsState()
    var selectedSecAlert by remember { mutableStateOf<com.example.whalewatch.data.model.SecAlert?>(null) }
    var selectedSecCluster by remember { mutableStateOf<com.example.whalewatch.data.model.SecCluster?>(null) }
    var selectedCotMarket by remember { mutableStateOf<com.example.whalewatch.data.model.CotMarket?>(null) }
    var selectedCongressTrade by remember { mutableStateOf<com.example.whalewatch.data.model.CongressTrade?>(null) }
    var selectedBipartisanStock by remember { mutableStateOf<com.example.whalewatch.data.model.BipartisanStock?>(null) }
    var selectedPartyStock by remember { mutableStateOf<com.example.whalewatch.data.model.TopPartyStock?>(null) }

    WhaleWatchTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐋", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "INSIDERWHALES",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "PRO",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(GreenBuy)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEn) "Updated: $lastUpdatedTime" else "Aktualisiert: $lastUpdatedTime",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // 🔄 Refresh Button with date & time toast
                        IconButton(onClick = {
                            viewModel.refresh()
                            val timeStr = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
                            android.widget.Toast.makeText(
                                context,
                                if (isEn) "Updated: $timeStr" else "Aktualisiert: $timeStr",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text("🔄", fontSize = 16.sp)
                        }

                        // 🌙 / ☀️ Theme Button
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Text(if (isDarkTheme) "🌙" else "☀️", fontSize = 16.sp)
                        }

                        // 🇬🇧 / 🇩🇪 Language Button
                        IconButton(onClick = { viewModel.toggleLanguage() }) {
                            Text(if (isEn) "🇬🇧" else "🇩🇪", fontSize = 16.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    TabScreen.values().forEach { tab ->
                        val isSelected = activeTab == tab
                        val label = if (isEn) tab.labelEn else tab.labelDe
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = { Text(tab.icon, fontSize = 18.sp) },
                            label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = activeTab, label = "TabSwitch") { tab ->
                    when (tab) {
                        TabScreen.RADAR -> RadarScreen(
                            radar = radarData,
                            superSignals = superSignals,
                            watchlist = watchlist,
                            onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                            onOpenAnalysis = { viewModel.openAnalysis(it) },
                            isEn = isEn
                        )
                        TabScreen.COT -> CotScreen(
                            markets = cotMarkets,
                            selectedCategory = selectedCotCat,
                            onSelectCategory = { viewModel.setCotCategory(it) },
                            onMarketClick = { selectedCotMarket = it },
                            isEn = isEn,
                            radar = radarData
                        )
                        TabScreen.CONGRESS -> CongressScreen(
                            trades = congressTrades,
                            searchQuery = congressQuery,
                            onSearchChange = { viewModel.setCongressQuery(it) },
                            selectedType = congressType,
                            onSelectType = { viewModel.setCongressType(it) },
                            selectedParty = congressParty,
                            onSelectParty = { viewModel.setCongressParty(it) },
                            congressMode = congressMode,
                            onSelectMode = { viewModel.setCongressMode(it) },
                            partySentiment = partySentiment,
                            onTradeClick = { selectedCongressTrade = it },
                            onBipartisanClick = { selectedBipartisanStock = it },
                            onPartyStockClick = { selectedPartyStock = it },
                            onOpenAnalysis = { viewModel.openAnalysis(it) },
                            isEn = isEn
                        )
                        TabScreen.SEC -> SecScreen(
                            alerts = secAlerts,
                            clusters = secClusters,
                            searchQuery = secQuery,
                            onSearchChange = { viewModel.setSecQuery(it) },
                            onAlertClick = { selectedSecAlert = it },
                            onClusterClick = { selectedSecCluster = it },
                            onOpenAnalysis = { viewModel.openAnalysis(it) },
                            isEn = isEn
                        )
                        TabScreen.WATCHLIST -> WatchlistScreen(
                            watchlist = watchlist,
                            onAddTicker = { viewModel.addWatchlistTicker(it) },
                            onRemoveTicker = { viewModel.removeWatchlistTicker(it) },
                            onOpenAnalysis = { viewModel.openAnalysis(it) },
                            isEn = isEn,
                            getCompanyName = { viewModel.getCompanyName(it) }
                        )
                    }
                }

                // Modal SEC Single Alert Detail Dialog
                selectedSecAlert?.let { alert ->
                    val isOnWatchlist = isValidStockTicker(alert.ticker) && watchlist.contains(alert.ticker)
                    SecDetailDialog(
                        alert = alert,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (isValidStockTicker(alert.ticker)) viewModel.toggleWatchlist(alert.ticker) },
                        onOpenAnalysis = { ticker ->
                            selectedSecAlert = null
                            viewModel.openAnalysis(ticker)
                        },
                        onDismiss = { selectedSecAlert = null },
                        isEn = isEn
                    )
                }

                // Modal SEC Cluster Detail Dialog (All Contributors Listed)
                selectedSecCluster?.let { cluster ->
                    val isOnWatchlist = isValidStockTicker(cluster.ticker) && watchlist.contains(cluster.ticker)
                    SecClusterDetailDialog(
                        cluster = cluster,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (isValidStockTicker(cluster.ticker)) viewModel.toggleWatchlist(cluster.ticker) },
                        onOpenAnalysis = { ticker ->
                            selectedSecCluster = null
                            viewModel.openAnalysis(ticker)
                        },
                        onDismiss = { selectedSecCluster = null },
                        isEn = isEn
                    )
                }

                // Modal COT Market Detail Dialog
                selectedCotMarket?.let { market ->
                    CotDetailDialog(
                        market = market,
                        onDismiss = { selectedCotMarket = null },
                        isEn = isEn
                    )
                }

                // Modal Congress Trade Detail Dialog
                selectedCongressTrade?.let { trade ->
                    val isOnWatchlist = trade.ticker.isNotBlank() && watchlist.contains(trade.ticker)
                    CongressDetailDialog(
                        trade = trade,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (trade.ticker.isNotBlank()) viewModel.toggleWatchlist(trade.ticker) },
                        onDismiss = { selectedCongressTrade = null },
                        isEn = isEn
                    )
                }

                // Modal Bipartisan Buy Detail Dialog
                selectedBipartisanStock?.let { stock ->
                    val isOnWatchlist = stock.ticker.isNotBlank() && watchlist.contains(stock.ticker)
                    BipartisanDetailDialog(
                        stock = stock,
                        trades = congressTrades,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (stock.ticker.isNotBlank()) viewModel.toggleWatchlist(stock.ticker) },
                        onTradeClick = { trade ->
                            selectedBipartisanStock = null
                            selectedCongressTrade = trade
                        },
                        onDismiss = { selectedBipartisanStock = null },
                        isEn = isEn
                    )
                }

                // Modal Party Sentiment Detail Dialog
                selectedPartyStock?.let { stock ->
                    val isOnWatchlist = isValidStockTicker(stock.ticker) && watchlist.contains(stock.ticker)
                    PartySentimentDetailDialog(
                        stock = stock,
                        partySentiment = partySentiment,
                        trades = congressTrades,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (isValidStockTicker(stock.ticker)) viewModel.toggleWatchlist(stock.ticker) },
                        onTradeClick = { trade ->
                            selectedPartyStock = null
                            selectedCongressTrade = trade
                        },
                        onDismiss = { selectedPartyStock = null },
                        isEn = isEn
                    )
                }

                // Modal Full Analysis Dialog
                analysisTicker?.let { ticker ->
                    val analysis = viewModel.getAnalysis(ticker)
                    val isOnWatchlist = watchlist.contains(ticker)
                    AnalysisDialog(
                        analysis = analysis,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { viewModel.toggleWatchlist(ticker) },
                        onDismiss = { viewModel.closeAnalysis() },
                        isEn = isEn
                    )
                }
            }
        }
    }
}
