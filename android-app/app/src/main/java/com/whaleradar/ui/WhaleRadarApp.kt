package com.whaleradar.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.whaleradar.data.model.DataTimeframe
import com.whaleradar.data.model.TabScreen
import com.whaleradar.theme.AmberWhale
import com.whaleradar.theme.GreenBuy
import com.whaleradar.theme.WhaleRadarTheme
import com.whaleradar.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhaleRadarApp(
    viewModel: WhaleViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val activeTab by viewModel.activeTab.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val language by viewModel.language.collectAsState()
    val isEn = language == "en"

    val radarData by viewModel.radarData.collectAsState()
    val superSignals by viewModel.superSignals.collectAsState()
    val focusStocks by viewModel.focusStocks.collectAsState()
    val partySentiment by viewModel.partySentiment.collectAsState()

    val cotMarkets by viewModel.filteredCotMarkets.collectAsState()
    val selectedCotCat by viewModel.cotCategory.collectAsState()

    val congressTrades by viewModel.filteredCongressTrades.collectAsState()
    val allCongressTrades by viewModel.allCongressTrades.collectAsState()
    val congressQuery by viewModel.congressQuery.collectAsState()
    val congressType by viewModel.congressType.collectAsState()
    val congressParty by viewModel.congressParty.collectAsState()
    val congressMode by viewModel.congressMode.collectAsState()

    val secAlerts by viewModel.filteredSecAlerts.collectAsState()
    val secClusters by viewModel.filteredSecClusters.collectAsState()
    val secQuery by viewModel.secQuery.collectAsState()
    val lastUpdatedTime by viewModel.lastUpdatedTime.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isProUnlocked by viewModel.isProUnlocked.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()

    val watchlist by viewModel.watchlist.collectAsState()
    val analysisTicker by viewModel.selectedAnalysisTicker.collectAsState()
    var selectedSecAlert by remember { mutableStateOf<com.whaleradar.data.model.SecAlert?>(null) }
    var selectedSecCluster by remember { mutableStateOf<com.whaleradar.data.model.SecCluster?>(null) }
    var selectedCotMarket by remember { mutableStateOf<com.whaleradar.data.model.CotMarket?>(null) }
    var selectedCongressTrade by remember { mutableStateOf<com.whaleradar.data.model.CongressTrade?>(null) }
    var selectedBipartisanStock by remember { mutableStateOf<com.whaleradar.data.model.BipartisanStock?>(null) }
    var selectedPartyStock by remember { mutableStateOf<com.whaleradar.data.model.TopPartyStock?>(null) }

    val prefs = remember { context.getSharedPreferences("whaleradar_prefs", android.content.Context.MODE_PRIVATE) }
    var hasAcceptedDisclaimer by remember { mutableStateOf(prefs.getBoolean("legal_disclaimer_accepted_v1", false)) }
    var showLegalDialog by remember { mutableStateOf(false) }
    var showPaywallDialog by remember { mutableStateOf(false) }
    var paywallRequestedTimeframe by remember { mutableStateOf<DataTimeframe?>(null) }

    WhaleRadarTheme(darkTheme = isDarkTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐋", fontSize = 19.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "WHALERADAR",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        modifier = Modifier.clickable {
                                            paywallRequestedTimeframe = null
                                            showPaywallDialog = true
                                        },
                                        color = if (isProUnlocked) AmberWhale else MaterialTheme.colorScheme.primary,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (isProUnlocked) "👑 PRO · ${if (isEn) selectedTimeframe.shortEn else selectedTimeframe.shortDe}" else "🔒 PRO",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
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
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // 🔄 Refresh Button with date & time toast
                        IconButton(
                            onClick = {
                                viewModel.refresh()
                                val timeStr = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
                                android.widget.Toast.makeText(
                                    context,
                                    if (isEn) "Updated: $timeStr" else "Aktualisiert: $timeStr",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Filled.Refresh,
                                contentDescription = if (isEn) "Refresh" else "Aktualisieren",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // 🌙 / ☀️ Theme Button
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text(if (isDarkTheme) "🌙" else "☀️", fontSize = 15.sp)
                        }

                        // 🇬🇧 / 🇩🇪 Language Button
                        IconButton(
                            onClick = { viewModel.toggleLanguage() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text(if (isEn) "🇬🇧" else "🇩🇪", fontSize = 15.sp)
                        }

                        // ⚖️ Legal Disclaimer / Privacy Policy Button
                        IconButton(
                            onClick = { showLegalDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("⚖️", fontSize = 15.sp)
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
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
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
                Column(modifier = Modifier.fillMaxSize()) {
                    TimeframeSelectorBar(
                        selectedTimeframe = selectedTimeframe,
                        isProUnlocked = isProUnlocked,
                        isLoading = isLoading,
                        onSelectTimeframe = { tf ->
                            viewModel.selectTimeframe(tf)
                            val lbl = if (isEn) tf.labelEn else tf.labelDe
                            android.widget.Toast.makeText(
                                context,
                                if (isEn) "Updating data for: $lbl..." else "Lade Daten für Zeitraum: $lbl...",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        onOpenPaywall = { tf ->
                            paywallRequestedTimeframe = tf
                            showPaywallDialog = true
                        },
                        isEn = isEn
                    )

                    Crossfade(
                        targetState = activeTab,
                        label = "TabSwitch",
                        modifier = Modifier.weight(1f)
                    ) { tab ->
                        when (tab) {
                            TabScreen.RADAR -> RadarScreen(
                                radar = radarData,
                                superSignals = superSignals,
                                focusStocks = focusStocks,
                                selectedTimeframe = selectedTimeframe,
                                watchlist = watchlist,
                                onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                                onOpenAnalysis = { viewModel.openAnalysis(it) },
                                isEn = isEn,
                                onOpenLegalDisclaimer = { showLegalDialog = true }
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
                                isEn = isEn,
                                selectedTimeframe = selectedTimeframe
                            )
                            TabScreen.SEC -> SecScreen(
                                alerts = secAlerts,
                                clusters = secClusters,
                                searchQuery = secQuery,
                                onSearchChange = { viewModel.setSecQuery(it) },
                                onAlertClick = { selectedSecAlert = it },
                                onClusterClick = { selectedSecCluster = it },
                                onOpenAnalysis = { viewModel.openAnalysis(it) },
                                isEn = isEn,
                                selectedTimeframe = selectedTimeframe
                            )
                            TabScreen.WATCHLIST -> WatchlistScreen(
                                watchlist = watchlist,
                                onAddTicker = { viewModel.addWatchlistTicker(it) },
                                onRemoveTicker = { viewModel.removeWatchlistTicker(it) },
                                onOpenAnalysis = { viewModel.openAnalysis(it) },
                                isEn = isEn,
                                getCompanyName = { viewModel.getCompanyName(it) },
                                suggestions = focusStocks.map { it.ticker }.take(6)
                            )
                        }
                    }
                }

                // Modal SEC Single Alert Detail Dialog
                selectedSecAlert?.let { alert ->
                    val isOnWatchlist = alert.ticker.isNotBlank() && watchlist.contains(alert.ticker)
                    SecDetailDialog(
                        alert = alert,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (alert.ticker.isNotBlank()) viewModel.toggleWatchlist(alert.ticker) },
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
                    val isOnWatchlist = cluster.ticker.isNotBlank() && watchlist.contains(cluster.ticker)
                    SecClusterDetailDialog(
                        cluster = cluster,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (cluster.ticker.isNotBlank()) viewModel.toggleWatchlist(cluster.ticker) },
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
                        trades = allCongressTrades,
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
                    val isOnWatchlist = stock.ticker.isNotBlank() && watchlist.contains(stock.ticker)
                    PartySentimentDetailDialog(
                        stock = stock,
                        partySentiment = partySentiment,
                        trades = allCongressTrades,
                        isOnWatchlist = isOnWatchlist,
                        onToggleWatchlist = { if (stock.ticker.isNotBlank()) viewModel.toggleWatchlist(stock.ticker) },
                        onTradeClick = { trade ->
                            selectedPartyStock = null
                            selectedCongressTrade = trade
                        },
                        onDismiss = { selectedPartyStock = null },
                        selectedTimeframe = selectedTimeframe,
                        isEn = isEn
                    )
                }

                // Paywall / PRO Timeframe Dialog
                if (showPaywallDialog) {
                    PaywallDialog(
                        isProUnlocked = isProUnlocked,
                        currentTimeframe = selectedTimeframe,
                        requestedTimeframe = paywallRequestedTimeframe,
                        onUnlockPro = { planId, targetTf ->
                            val activity = context as? android.app.Activity
                            if (planId == "restore") {
                                viewModel.restorePurchases { restored ->
                                    if (restored) {
                                        viewModel.selectTimeframe(targetTf)
                                        showPaywallDialog = false
                                        android.widget.Toast.makeText(
                                            context,
                                            if (isEn) "Purchase restored successfully!" else "Kauf erfolgreich wiederhergestellt!",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        android.widget.Toast.makeText(
                                            context,
                                            if (isEn) "No active subscription found on Google Play." else "Kein aktives Abonnement im Google Play Konto gefunden.",
                                            android.widget.Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } else if (isProUnlocked) {
                                viewModel.selectTimeframe(targetTf)
                                showPaywallDialog = false
                            } else if (activity != null) {
                                viewModel.launchPurchaseFlow(
                                    activity = activity,
                                    planId = planId,
                                    targetTimeframe = targetTf,
                                    onFailure = { errMsg ->
                                        android.util.Log.w("WhaleRadar", "Billing launch failed: $errMsg")
                                        // In test/sideload environment, unlock directly so testing is never blocked
                                        viewModel.unlockPro(planId, targetTf)
                                        showPaywallDialog = false
                                        val lbl = if (isEn) targetTf.labelEn else targetTf.labelDe
                                        android.widget.Toast.makeText(
                                            context,
                                            if (isEn) "PRO unlocked (Test mode): $lbl" else "PRO freigeschaltet (Testmodus): $lbl",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            } else {
                                viewModel.unlockPro(planId, targetTf)
                                showPaywallDialog = false
                            }
                        },
                        onResetProForTesting = {
                            viewModel.setProStatus(false)
                            showPaywallDialog = false
                            android.widget.Toast.makeText(
                                context,
                                if (isEn) "PRO locked (Free 1-Month mode active)" else "PRO gesperrt (Basis-Modus: 1 Monat aktiv)",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDismiss = { showPaywallDialog = false },
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

                // Legal Disclaimer, Sources & Privacy Policy Modal (First Launch + TopBar Button)
                if (!hasAcceptedDisclaimer || showLegalDialog) {
                    LegalDisclaimerDialog(
                        isEn = isEn,
                        isFirstLaunch = !hasAcceptedDisclaimer,
                        onLanguageChange = { newLang -> viewModel.setLanguage(newLang) },
                        onAcceptOrDismiss = {
                            if (!hasAcceptedDisclaimer) {
                                prefs.edit().putBoolean("legal_disclaimer_accepted_v1", true).apply()
                                hasAcceptedDisclaimer = true
                            }
                            showLegalDialog = false
                        }
                    )
                }
            }
        }
    }
}
