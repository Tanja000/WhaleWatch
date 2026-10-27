package com.whaleradar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.whaleradar.data.WhaleRepository
import com.whaleradar.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WhaleViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("whaleradar_prefs", android.content.Context.MODE_PRIVATE)

    val repository = WhaleRepository(application)

    private val _activeTab = MutableStateFlow(TabScreen.RADAR)
    val activeTab: StateFlow<TabScreen> = _activeTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _language = MutableStateFlow(
        prefs.getString("app_language", null)
            ?: if (java.util.Locale.getDefault().language.equals("en", ignoreCase = true)) "en" else "de"
    )
    val language: StateFlow<String> = _language.asStateFlow()

    private val _cotCategory = MutableStateFlow("all")
    val cotCategory: StateFlow<String> = _cotCategory.asStateFlow()

    // Congress Filter State
    private val _congressQuery = MutableStateFlow("")
    val congressQuery: StateFlow<String> = _congressQuery.asStateFlow()

    private val _congressType = MutableStateFlow("all")
    val congressType: StateFlow<String> = _congressType.asStateFlow()

    private val _congressParty = MutableStateFlow("all")
    val congressParty: StateFlow<String> = _congressParty.asStateFlow()

    private val _congressMode = MutableStateFlow("all") // "all", "conflicts", "bipartisan", "sentiment"
    val congressMode: StateFlow<String> = _congressMode.asStateFlow()

    private val _secQuery = MutableStateFlow("")
    val secQuery: StateFlow<String> = _secQuery.asStateFlow()

    private val _lastUpdatedTime = MutableStateFlow(
        java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
    )
    val lastUpdatedTime: StateFlow<String> = _lastUpdatedTime.asStateFlow()

    private val _selectedAnalysisTicker = MutableStateFlow<String?>(null)
    val selectedAnalysisTicker: StateFlow<String?> = _selectedAnalysisTicker.asStateFlow()

    init {
        viewModelScope.launch {
            repository.refreshLiveFromNetwork()
            _lastUpdatedTime.value = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
        }
    }

    val radarData: StateFlow<RadarData> = repository.radarData
    val watchlist: StateFlow<Set<String>> = repository.watchlist
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val isProUnlocked: StateFlow<Boolean> = repository.isProUnlocked
    val selectedTimeframe: StateFlow<DataTimeframe> = repository.selectedTimeframe
    val superSignals: StateFlow<List<SuperSignal>> = repository.superSignals
    val focusStocks: StateFlow<List<FocusStock>> = repository.focusStocks
    val partySentiment: StateFlow<PartySentimentData> = repository.partySentiment
    val allCongressTrades: StateFlow<List<CongressTrade>> = repository.congressTrades

    // Filtered COT markets
    val filteredCotMarkets: StateFlow<List<CotMarket>> = combine(
        repository.cotMarkets,
        _cotCategory
    ) { list, cat ->
        if (cat == "all") list else list.filter {
            it.category.equals(cat, ignoreCase = true) ||
            (cat == "rates" && it.category.equals("treasuries", ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Congress trades (supports Search, Type, Party, and Committee Conflicts mode)
    val filteredCongressTrades: StateFlow<List<CongressTrade>> = combine(
        repository.congressTrades,
        _congressQuery,
        _congressType,
        _congressParty,
        _congressMode
    ) { trades, q, type, party, mode ->
        trades.filter { t ->
            val matchMode = when (mode) {
                "conflicts" -> t.hasCommitteeConflict
                else -> true
            }

            val matchQuery = q.isEmpty() ||
                    t.filerName.contains(q, ignoreCase = true) ||
                    t.ticker.contains(q, ignoreCase = true) ||
                    t.assetName.contains(q, ignoreCase = true) ||
                    t.conflictCommittee.contains(q, ignoreCase = true) ||
                    t.conflictSector.contains(q, ignoreCase = true)

            val matchType = when (type) {
                "buy" -> t.transactionType.lowercase().contains("purchase")
                "sale" -> t.transactionType.lowercase().contains("sale")
                else -> true
            }

            val matchParty = when (party) {
                "D" -> t.party.equals("D", ignoreCase = true)
                "R" -> t.party.equals("R", ignoreCase = true)
                else -> true
            }

            matchMode && matchQuery && matchType && matchParty
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered SEC Alerts (Single Filings only, no clusters, no duplicates)
    val filteredSecAlerts: StateFlow<List<SecAlert>> = combine(
        repository.secAlerts,
        _secQuery
    ) { alerts, q ->
        if (q.isEmpty()) alerts else alerts.filter {
            it.ticker.contains(q, ignoreCase = true) ||
            it.issuer.contains(q, ignoreCase = true) ||
            it.reportingOwner.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered SEC Clusters (Grouped by company, 1 card per company, sorted descending by cluster count)
    val filteredSecClusters: StateFlow<List<SecCluster>> = combine(
        repository.secClusters,
        _secQuery
    ) { clusters, q ->
        if (q.isEmpty()) clusters else clusters.filter { c ->
            c.ticker.contains(q, ignoreCase = true) ||
            c.issuer.contains(q, ignoreCase = true) ||
            c.filings.any { f -> f.reportingOwner.contains(q, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: TabScreen) {
        _activeTab.value = tab
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setLanguage(lang: String) {
        val clean = if (lang.equals("en", ignoreCase = true)) "en" else "de"
        _language.value = clean
        prefs.edit().putString("app_language", clean).apply()
    }

    fun toggleLanguage() {
        val next = if (_language.value == "de") "en" else "de"
        setLanguage(next)
    }

    fun setCotCategory(cat: String) {
        _cotCategory.value = cat
    }

    fun setCongressQuery(q: String) {
        _congressQuery.value = q
    }

    fun setCongressType(t: String) {
        _congressType.value = t
    }

    fun setCongressParty(p: String) {
        _congressParty.value = p
    }

    fun setCongressMode(m: String) {
        _congressMode.value = m
    }

    fun setSecQuery(q: String) {
        _secQuery.value = q
    }

    fun openAnalysis(ticker: String) {
        _selectedAnalysisTicker.value = ticker
    }

    fun closeAnalysis() {
        _selectedAnalysisTicker.value = null
    }

    fun getAnalysis(ticker: String): StockAnalysis {
        return repository.getAnalysis(ticker)
    }

    fun getCompanyName(ticker: String): String {
        return repository.getCompanyName(ticker)
    }

    fun addWatchlistTicker(ticker: String) {
        repository.addWatchlistTicker(ticker)
    }

    fun removeWatchlistTicker(ticker: String) {
        repository.removeWatchlistTicker(ticker)
    }

    fun toggleWatchlist(ticker: String) {
        repository.toggleWatchlist(ticker)
    }

    fun selectTimeframe(tf: DataTimeframe) {
        if (tf.isProOnly && !repository.isProUnlocked.value) return
        repository.selectTimeframe(tf)
        val now = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
        _lastUpdatedTime.value = now
        viewModelScope.launch {
            repository.refreshLiveFromNetwork()
            _lastUpdatedTime.value = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
        }
    }

    fun unlockPro(planId: String = "yearly", targetTimeframe: DataTimeframe? = null) {
        repository.unlockPro(planId)
        if (targetTimeframe != null) {
            selectTimeframe(targetTimeframe)
        }
    }

    fun setProStatus(unlocked: Boolean) {
        repository.setProStatus(unlocked)
    }

    fun refresh() {
        val now = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm 'Uhr'", java.util.Locale.GERMANY).format(java.util.Date())
        _lastUpdatedTime.value = now
        viewModelScope.launch {
            repository.loadData()
            repository.refreshLiveFromNetwork()
        }
    }
}
