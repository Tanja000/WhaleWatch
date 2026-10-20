package com.example.whalewatch.data

import android.content.Context
import com.example.whalewatch.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class WhaleRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("whalewatch_prefs", Context.MODE_PRIVATE)

    private val _radarData = MutableStateFlow(RadarData())
    val radarData: StateFlow<RadarData> = _radarData.asStateFlow()

    private val _cotMarkets = MutableStateFlow<List<CotMarket>>(emptyList())
    val cotMarkets: StateFlow<List<CotMarket>> = _cotMarkets.asStateFlow()

    private val _congressTrades = MutableStateFlow<List<CongressTrade>>(emptyList())
    val congressTrades: StateFlow<List<CongressTrade>> = _congressTrades.asStateFlow()

    private val _secAlerts = MutableStateFlow<List<SecAlert>>(emptyList())
    val secAlerts: StateFlow<List<SecAlert>> = _secAlerts.asStateFlow()

    private val _superSignals = MutableStateFlow<List<SuperSignal>>(emptyList())
    val superSignals: StateFlow<List<SuperSignal>> = _superSignals.asStateFlow()

    private val _partySentiment = MutableStateFlow(PartySentimentData())
    val partySentiment: StateFlow<PartySentimentData> = _partySentiment.asStateFlow()

    private val _watchlist = MutableStateFlow<Set<String>>(loadSavedWatchlist())
    val watchlist: StateFlow<Set<String>> = _watchlist.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    private fun loadSavedWatchlist(): Set<String> {
        val saved = prefs.getStringSet("watchlist", null)
        return saved ?: setOf("NVDA", "AAPL", "MSFT", "PLTR")
    }

    fun toggleWatchlist(ticker: String) {
        val current = _watchlist.value.toMutableSet()
        if (current.contains(ticker)) {
            current.remove(ticker)
        } else {
            current.add(ticker)
        }
        _watchlist.value = current
        prefs.edit().putStringSet("watchlist", current).apply()
    }

    fun addWatchlistTicker(ticker: String) {
        val clean = ticker.trim().uppercase()
        if (clean.isNotEmpty()) {
            val current = _watchlist.value.toMutableSet()
            current.add(clean)
            _watchlist.value = current
            prefs.edit().putStringSet("watchlist", current).apply()
        }
    }

    fun removeWatchlistTicker(ticker: String) {
        val current = _watchlist.value.toMutableSet()
        current.remove(ticker)
        _watchlist.value = current
        prefs.edit().putStringSet("watchlist", current).apply()
    }

    fun loadData() {
        try {
            _isLoading.value = true

            // 1. Radar
            val radarStr = readAssetString("www/data/radar.json")
            if (radarStr.isNotEmpty()) {
                val json = JSONObject(radarStr)
                val longPct = json.optDouble("whale_long_pct", 43.9)
                val shortPct = json.optDouble("whale_short_pct", 56.1)
                _radarData.value = RadarData(
                    whaleLongPct = longPct,
                    whaleShortPct = shortPct,
                    whaleLongContracts = json.optLong("whale_long_contracts", 58001),
                    whaleShortContracts = json.optLong("whale_short_contracts", 73991),
                    whaleLongFormatted = json.optString("whale_long_formatted", "58.001"),
                    whaleShortFormatted = json.optString("whale_short_formatted", "73.991"),
                    badgeDe = json.optString("badge_de", "BÄRISCH"),
                    badgeEn = json.optString("badge_en", "BEARISH"),
                    whaleMoodDe = json.optString("whale_mood_de", "BÄRISCH (Whales im Short-Modus)"),
                    whaleMoodEn = json.optString("whale_mood_en", "BEARISH (Whales net Short)"),
                    whaleSubDe = json.optString("whale_sub_de", "Großspekulanten halten 43.9% Long- und 56.1% Short-Positionen."),
                    whaleSubEn = json.optString("whale_sub_en", "Institutional large traders hold 43.9% Long and 56.1% Short positions."),
                    reportDate = json.optString("report_date", "2026-10-13").take(10),
                    reportWeek = json.optString("report_week", "2026 Report Week 41")
                )
            }

            // 2. COT Markets
            val cotStr = readAssetString("www/data/cot.json")
            if (cotStr.isNotEmpty()) {
                val json = JSONObject(cotStr)
                val arr = json.optJSONArray("markets") ?: JSONArray()
                val list = mutableListOf<CotMarket>()
                for (i in 0 until arr.length()) {
                    val m = arr.getJSONObject(i)
                    list.add(
                        CotMarket(
                            id = m.optString("id"),
                            name = m.optString("name"),
                            assetClass = m.optString("asset_class"),
                            category = m.optString("category", "indices"),
                            whaleNetFormatted = m.optString("whale_net_formatted", "0"),
                            whaleChangeDesc = m.optString("whale_change_desc", "-"),
                            signal = m.optString("signal", "NEUTRAL"),
                            signalColor = m.optString("signal_color", "#10b981"),
                            whaleBullishPct = m.optDouble("whale_bullish_pct", 50.0),
                            retailBullishPct = m.optDouble("retail_bullish_pct", 50.0),
                            openInterest = m.optLong("open_interest", 0)
                        )
                    )
                }
                _cotMarkets.value = list
            }

            // 3. Congress Trades mit Ausschuss-Konflikten
            val congStr = readAssetString("www/data/congress.json")
            if (congStr.isNotEmpty()) {
                val list = mutableListOf<CongressTrade>()
                val arr = if (congStr.trim().startsWith("[")) {
                    JSONArray(congStr)
                } else {
                    val root = JSONObject(congStr)
                    root.optJSONArray("trades") ?: JSONArray()
                }

                for (i in 0 until arr.length()) {
                    val t = arr.getJSONObject(i)
                    val days = t.optInt("days_to_file", 0)
                    list.add(
                        CongressTrade(
                            id = t.optString("id", "cg_$i"),
                            filerName = t.optString("filer_name", "Senator / Representative"),
                            party = t.optString("party", "D"),
                            chamber = t.optString("chamber", "house"),
                            ticker = t.optString("ticker", ""),
                            assetName = t.optString("asset_name", ""),
                            transactionType = t.optString("transaction_type", "Purchase"),
                            amountRangeLabel = t.optString("amount_range_label", "$1,001 - $15,000"),
                            transactionDate = t.optString("transaction_date", ""),
                            filingDate = t.optString("filing_date", ""),
                            daysToFile = days,
                            isLate = (t.optInt("is_late", 0) == 1) || (days > 45),
                            hasCommitteeConflict = t.optBoolean("has_committee_conflict", false),
                            conflictCommittee = t.optString("conflict_committee", ""),
                            conflictSector = t.optString("conflict_sector", ""),
                            conflictDescDe = t.optString("conflict_desc_de", ""),
                            conflictDescEn = t.optString("conflict_desc_en", "")
                        )
                    )
                }
                _congressTrades.value = list
            }

            // 4. SEC Filings
            val secStr = readAssetString("www/data/sec.json")
            if (secStr.isNotEmpty()) {
                val arr = if (secStr.trim().startsWith("[")) {
                    JSONArray(secStr)
                } else {
                    val json = JSONObject(secStr)
                    json.optJSONArray("alerts") ?: JSONArray()
                }
                val list = mutableListOf<SecAlert>()
                for (i in 0 until arr.length()) {
                    val a = arr.getJSONObject(i)
                    val fTime = a.optString("filing_time", "")
                    val fDate = a.optString("filing_date", if (fTime.isNotEmpty()) fTime.take(10) else "")
                    list.add(
                        SecAlert(
                            id = a.optString("accession_number", a.optString("ticker") + "_" + i),
                            ticker = a.optString("ticker", ""),
                            issuer = a.optString("issuer", ""),
                            reportingOwner = a.optString("reporting_owner", ""),
                            role = a.optString("role", "Executive Officer"),
                            alertType = a.optString("alert_type", "FORM 4 FILING"),
                            isCluster = a.optBoolean("is_cluster", false),
                            isBuy = a.optBoolean("is_buy", true),
                            txTypeDe = a.optString("tx_type_de", "Kauf"),
                            txTypeEn = a.optString("tx_type_en", "Purchase"),
                            sharesFormatted = a.optString("shares_formatted", ""),
                            totalValueFormatted = a.optString("total_value_formatted", "$0"),
                            filingDate = fDate,
                            badgeColor = a.optString("badge_color", "#10b981"),
                            code = a.optString("code", if (a.optBoolean("is_buy", true)) "P" else "S"),
                            priceFormatted = a.optString("price_formatted", "-"),
                            totalValue = a.optLong("total_value", 0),
                            clusterCount = a.optInt("cluster_count", if (a.optBoolean("is_cluster", false)) 2 else 1),
                            accessionNumber = a.optString("accession_number", ""),
                            filingUrl = a.optString("filing_url", "https://www.sec.gov"),
                            securityTitle = a.optString("security_title", "Common Stock"),
                            filingTime = fTime
                        )
                    )
                }
                _secAlerts.value = list
            }

            // 5. Kombi-Signale: CEO + Politiker
            val sigStr = readAssetString("www/data/super_signals.json")
            if (sigStr.isNotEmpty()) {
                val arr = JSONArray(sigStr)
                val list = mutableListOf<SuperSignal>()
                for (i in 0 until arr.length()) {
                    val s = arr.getJSONObject(i)
                    list.add(
                        SuperSignal(
                            ticker = s.optString("ticker"),
                            companyName = s.optString("company_name"),
                            signalTitleDe = s.optString("signal_title_de", "🔥 SUPER-SIGNAL: CEO + KONGRESS"),
                            signalTitleEn = s.optString("signal_title_en", "🔥 SUPER-SIGNAL: CEO + CONGRESS"),
                            convictionScore = s.optInt("conviction_score", 96),
                            politicianName = s.optString("politician_name"),
                            politicianParty = s.optString("politician_party"),
                            politicianAmount = s.optString("politician_amount"),
                            politicianDate = s.optString("politician_date"),
                            insiderName = s.optString("insider_name"),
                            insiderRole = s.optString("insider_role"),
                            insiderShares = s.optString("insider_shares"),
                            insiderValue = s.optString("insider_value"),
                            insiderDate = s.optString("insider_date"),
                            sector = s.optString("sector"),
                            descriptionDe = s.optString("description_de"),
                            descriptionEn = s.optString("description_en")
                        )
                    )
                }
                _superSignals.value = list
            }

            // 6. Partei-Sentiment & Überparteiliche Confluence
            val partyStr = readAssetString("www/data/party_sentiment.json")
            if (partyStr.isNotEmpty()) {
                val json = JSONObject(partyStr)
                val demArr = json.optJSONArray("dem_top_buys") ?: JSONArray()
                val demList = mutableListOf<TopPartyStock>()
                for (i in 0 until demArr.length()) {
                    val item = demArr.getJSONObject(i)
                    demList.add(TopPartyStock(
                        ticker = item.optString("ticker"),
                        count = item.optInt("count"),
                        party = item.optString("party"),
                        category = item.optString("category")
                    ))
                }

                val repArr = json.optJSONArray("rep_top_buys") ?: JSONArray()
                val repList = mutableListOf<TopPartyStock>()
                for (i in 0 until repArr.length()) {
                    val item = repArr.getJSONObject(i)
                    repList.add(TopPartyStock(
                        ticker = item.optString("ticker"),
                        count = item.optInt("count"),
                        party = item.optString("party"),
                        category = item.optString("category")
                    ))
                }

                val biArr = json.optJSONArray("bipartisan_buys") ?: JSONArray()
                val biList = mutableListOf<BipartisanStock>()
                for (i in 0 until biArr.length()) {
                    val item = biArr.getJSONObject(i)
                    biList.add(BipartisanStock(
                        ticker = item.optString("ticker"),
                        demBuys = item.optInt("dem_buys"),
                        repBuys = item.optInt("rep_buys"),
                        totalBuys = item.optInt("total_buys"),
                        sector = item.optString("sector"),
                        signal = item.optString("signal")
                    ))
                }

                _partySentiment.value = PartySentimentData(
                    demBuyCount = json.optInt("dem_buy_count", 473),
                    repBuyCount = json.optInt("rep_buy_count", 135),
                    demTopBuys = demList,
                    repTopBuys = repList,
                    bipartisanBuys = biList,
                    sentimentDriftDe = json.optString("sentiment_drift_summary_de"),
                    sentimentDriftEn = json.optString("sentiment_drift_summary_en")
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }

    fun getAnalysis(ticker: String): StockAnalysis {
        val upper = ticker.uppercase()
        val congMatches = _congressTrades.value.filter { it.ticker.equals(upper, ignoreCase = true) }
        val secMatches = _secAlerts.value.filter { it.ticker.equals(upper, ignoreCase = true) }
        val superSig = _superSignals.value.find { it.ticker.equals(upper, ignoreCase = true) }

        val buys = congMatches.count { it.transactionType.lowercase().contains("purchase") }
        val sales = congMatches.count { it.transactionType.lowercase().contains("sale") }

        val score = when {
            superSig != null -> superSig.convictionScore
            buys > sales -> 78
            secMatches.any { it.isCluster } -> 85
            sales > buys -> 42
            else -> 60
        }

        val sentimentDe = if (score >= 65) "BULLISCH" else if (score <= 45) "BÄRISCH" else "NEUTRAL"
        val sentimentEn = if (score >= 65) "BULLISH" else if (score <= 45) "BEARISH" else "NEUTRAL"

        return StockAnalysis(
            ticker = upper,
            companyName = if (congMatches.isNotEmpty()) congMatches[0].assetName else if (secMatches.isNotEmpty()) secMatches[0].issuer else "$upper Corporation",
            score = score,
            sentimentDe = sentimentDe,
            sentimentEn = sentimentEn,
            congressBuys = buys,
            congressSales = sales,
            secAlertsCount = secMatches.size,
            cotContext = "Whales 43.9% Long · 56.1% Short",
            superSignal = superSig,
            recentTrades = congMatches.take(5),
            recentSec = secMatches.take(5)
        )
    }

    private fun readAssetString(path: String): String {
        return try {
            context.assets.open(path).use { stream ->
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            ""
        }
    }
}
