package com.example.whalewatch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whalewatch.data.SecRepository
import com.example.whalewatch.data.model.FilterTab
import com.example.whalewatch.data.model.SecFiling
import com.example.whalewatch.data.model.SecStats
import com.example.whalewatch.data.model.TradeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val displayedFilings: List<SecFiling> = emptyList(),
    val stats: SecStats = SecStats(),
    val selectedTab: FilterTab = FilterTab.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedFiling: SecFiling? = null,
    val showSettingsDialog: Boolean = false,
    val showFilterDialog: Boolean = false,
    val minAmountFilter: Double = 0.0,
    val onlyDiscretionary: Boolean = false,
    val apiKey: String = "",
    val apiMode: String = "SEC EDGAR Direct Feed (Aktiv)"
)

class MainScreenViewModel(
    private val repository: SecRepository = SecRepository()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(FilterTab.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFiling = MutableStateFlow<SecFiling?>(null)
    private val _showSettingsDialog = MutableStateFlow(false)
    private val _showFilterDialog = MutableStateFlow(false)
    private val _minAmountFilter = MutableStateFlow(0.0)
    private val _onlyDiscretionary = MutableStateFlow(false)
    private val _apiKey = MutableStateFlow("")

    val uiState: StateFlow<MainUiState> = combine(
        repository.filings,
        repository.stats,
        repository.isLoading,
        repository.errorMessage,
        _selectedTab,
        _searchQuery,
        _selectedFiling,
        _showSettingsDialog,
        _showFilterDialog,
        _minAmountFilter,
        _onlyDiscretionary,
        _apiKey
    ) { params ->
        val rawList = params[0] as List<SecFiling>
        val stats = params[1] as SecStats
        val isLoading = params[2] as Boolean
        val errorMsg = params[3] as String?
        val tab = params[4] as FilterTab
        val query = params[5] as String
        val selected = params[6] as SecFiling?
        val showSettings = params[7] as Boolean
        val showFilter = params[8] as Boolean
        val minAmt = params[9] as Double
        val onlyDiscretionary = params[10] as Boolean
        val apiKey = params[11] as String

        val filtered = rawList.filter { filing ->
            // Tab filter
            val tabMatch = when (tab) {
                FilterTab.ALL -> true
                FilterTab.BUYS -> filing.tradeType == TradeType.BUY
                FilterTab.SELLS -> filing.tradeType == TradeType.SELL
                FilterTab.WHALES -> filing.isWhale
                FilterTab.WATCHLIST -> filing.isBookmarked
            }

            // Search query filter
            val queryMatch = if (query.isBlank()) true else {
                filing.ticker.contains(query, ignoreCase = true) ||
                filing.companyName.contains(query, ignoreCase = true) ||
                filing.reportingOwner.contains(query, ignoreCase = true)
            }

            // Min amount filter
            val amountMatch = filing.totalValue >= minAmt

            // Discretionary filter (exclude 10b5-1)
            val discretionaryMatch = if (onlyDiscretionary) !filing.isRule10b51 else true

            tabMatch && queryMatch && amountMatch && discretionaryMatch
        }

        MainUiState(
            displayedFilings = filtered,
            stats = stats,
            selectedTab = tab,
            searchQuery = query,
            isLoading = isLoading,
            errorMessage = errorMsg,
            selectedFiling = selected,
            showSettingsDialog = showSettings,
            showFilterDialog = showFilter,
            minAmountFilter = minAmt,
            onlyDiscretionary = onlyDiscretionary,
            apiKey = apiKey,
            apiMode = if (apiKey.isNotBlank()) "RapidAPI / Form 4 Custom Key" else "SEC EDGAR Direct Feed (Aktiv)"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(isLoading = false)
    )

    fun onTabSelected(tab: FilterTab) {
        _selectedTab.value = tab
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onToggleBookmark(filing: SecFiling) {
        repository.toggleBookmark(filing.accessionNumber)
    }

    fun onSelectFiling(filing: SecFiling?) {
        _selectedFiling.value = filing
    }

    fun onToggleSettings(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun onToggleFilter(show: Boolean) {
        _showFilterDialog.value = show
    }

    fun onSetMinAmount(min: Double) {
        _minAmountFilter.value = min
    }

    fun onToggleOnlyDiscretionary(enabled: Boolean) {
        _onlyDiscretionary.value = enabled
    }

    fun onSetApiKey(key: String) {
        _apiKey.value = key
        repository.rapidApiKey = key
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
        }
    }
}