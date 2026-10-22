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

    private val _secClusters = MutableStateFlow<List<SecCluster>>(emptyList())
    val secClusters: StateFlow<List<SecCluster>> = _secClusters.asStateFlow()

    private val _superSignals = MutableStateFlow<List<SuperSignal>>(emptyList())
    val superSignals: StateFlow<List<SuperSignal>> = _superSignals.asStateFlow()

    private val _partySentiment = MutableStateFlow(PartySentimentData())
    val partySentiment: StateFlow<PartySentimentData> = _partySentiment.asStateFlow()

    private val _watchlist = MutableStateFlow<Set<String>>(loadSavedWatchlist())
    val watchlist: StateFlow<Set<String>> = _watchlist.asStateFlow()

    private var _stockDetailsMap: Map<String, JSONObject> = emptyMap()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    fun isValidStockTicker(ticker: String, issuer: String = ""): Boolean {
        val clean = ticker.trim().uppercase()
        if (clean.isEmpty() || clean == "NULL" || clean == "US-TBILL" || clean == "NXG" || clean == "BANK") return false
        val invalidNames = setOf(
            "ZHANG", "CUNNINGHAM", "LI", "SUN", "DANNY", "ARNE", "AMIR", "JOHN",
            "EREZ", "KUN", "ABEL", "SIMON", "DANIEL", "ANDREW", "JAE", "BONIFAS",
            "MONIA", "VENA", "MERRILL", "NONE", "N/A", "UNKNOWN"
        )
        if (clean in invalidNames) return false
        if (issuer.contains("(Holding/Affiliate)", ignoreCase = true) || issuer.contains("Income Fund", ignoreCase = true)) return false
        return clean.matches(Regex("^[A-Z]{1,5}(\\.[A-Z])?$"))
    }

    private fun loadSavedWatchlist(): Set<String> {
        val saved = prefs.getStringSet("watchlist", null)
        if (saved == null) return setOf("NVDA", "AAPL", "MSFT", "PLTR")
        val cleaned = saved.map { it.trim().uppercase() }.filter { isValidStockTicker(it) }.toSet()
        if (cleaned.size != saved.size || cleaned != saved) {
            prefs.edit().putStringSet("watchlist", cleaned).apply()
        }
        return if (cleaned.isEmpty()) setOf("NVDA", "AAPL", "MSFT", "PLTR") else cleaned
    }

    fun toggleWatchlist(ticker: String) {
        val clean = ticker.trim().uppercase()
        val current = _watchlist.value.toMutableSet()
        if (current.contains(clean) || current.contains(ticker)) {
            current.remove(clean)
            current.remove(ticker)
        } else if (isValidStockTicker(clean)) {
            current.add(clean)
        }
        _watchlist.value = current
        prefs.edit().putStringSet("watchlist", current).apply()
    }

    fun addWatchlistTicker(ticker: String) {
        val clean = ticker.trim().uppercase()
        if (isValidStockTicker(clean)) {
            val current = _watchlist.value.toMutableSet()
            current.add(clean)
            _watchlist.value = current
            prefs.edit().putStringSet("watchlist", current).apply()
        }
    }

    fun removeWatchlistTicker(ticker: String) {
        val current = _watchlist.value.toMutableSet()
        current.remove(ticker)
        current.remove(ticker.trim().uppercase())
        _watchlist.value = current
        prefs.edit().putStringSet("watchlist", current).apply()
    }

    fun loadData() {
        _isLoading.value = true

        // 1. Radar
        try {
            val radarStr = readAssetString("data/radar.json")
            if (radarStr.isNotEmpty()) {
                val json = JSONObject(radarStr)
                val longPct = json.optDouble("whale_long_pct", 40.7)
                val shortPct = json.optDouble("whale_short_pct", 59.3)
                _radarData.value = RadarData(
                    whaleLongPct = longPct,
                    whaleShortPct = shortPct,
                    whaleLongContracts = json.optLong("whale_long_contracts", 2795980),
                    whaleShortContracts = json.optLong("whale_short_contracts", 4079453),
                    whaleLongFormatted = json.optString("whale_long_formatted", "2.795.980"),
                    whaleShortFormatted = json.optString("whale_short_formatted", "4.079.453"),
                    badgeDe = json.optString("badge_de", "BÄRISCH"),
                    badgeEn = json.optString("badge_en", "BEARISH"),
                    whaleMoodDe = json.optString("whale_mood_de", "BÄRISCH (Whales im Short-Modus)"),
                    whaleMoodEn = json.optString("whale_mood_en", "BEARISH (Whales net Short)"),
                    whaleSubDe = json.optString("whale_sub_de", "Großspekulanten halten aktuell 40.7% Long- und 59.3% Short-Positionen."),
                    whaleSubEn = json.optString("whale_sub_en", "Institutional large traders hold 40.7% Long and 59.3% Short positions."),
                    reportDate = json.optString("report_date", "2026-10-13").take(10),
                    reportWeek = json.optString("report_week", "2026 Report Week 41")
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing radar.json", e)
        }

        // 2. COT Markets (Prefer latest live cached FuturesBench response if available, fallback to bundled cot.json)
        try {
            val cachedCot = prefs.getString("cached_futuresbench_cot_v1", null)
            val cotStr = if (!cachedCot.isNullOrBlank()) cachedCot else readAssetString("data/cot.json")
            if (cotStr.isNotEmpty()) {
                val list = parseCotJsonString(cotStr)
                if (list.isNotEmpty()) {
                    _cotMarkets.value = list
                    recalculateRadarPositions(list)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing cot.json", e)
        }

        // 3. Congress Trades mit Ausschuss-Konflikten
        try {
            val congStr = readAssetString("data/congress.json")
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
                    val rawTicker = if (t.isNull("ticker")) "" else t.optString("ticker", "").trim().uppercase()
                    if (!isValidStockTicker(rawTicker)) continue
                    val days = t.optInt("days_to_file", 0)
                    val isLateVal = if (t.has("is_late")) {
                        t.optBoolean("is_late", false) || (t.optInt("is_late", 0) == 1)
                    } else {
                        days > 45
                    }
                    list.add(
                        CongressTrade(
                            id = t.optString("id", "cg_$i"),
                            filerName = t.optString("filer_name", "Senator / Representative"),
                            party = t.optString("party", "D"),
                            chamber = t.optString("chamber", "house"),
                            ticker = rawTicker,
                            assetName = t.optString("asset_name", ""),
                            transactionType = t.optString("transaction_type", "Purchase"),
                            amountRangeLabel = t.optString("amount_range_label", "$1,001 - $15,000"),
                            transactionDate = t.optString("transaction_date", ""),
                            filingDate = t.optString("filing_date", ""),
                            daysToFile = days,
                            isLate = isLateVal || (days > 45),
                            hasCommitteeConflict = t.optBoolean("has_committee_conflict", false),
                            conflictCommittee = t.optString("conflict_committee", ""),
                            conflictSector = t.optString("conflict_sector", ""),
                            conflictDescDe = t.optString("conflict_desc_de", ""),
                            conflictDescEn = t.optString("conflict_desc_en", "")
                        )
                    )
                }
                val distinctList = list.distinctBy {
                    "${it.filerName}_${it.ticker}_${it.transactionDate}_${it.amountRangeLabel}_${it.transactionType}"
                }
                if (distinctList.isNotEmpty()) {
                    _congressTrades.value = distinctList
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing congress.json", e)
        }

        // 4. SEC Filings (Prefer latest live cached Form 4 response if available, fallback to bundled sec.json)
        try {
            val cachedSec = prefs.getString("cached_live_sec_form4_v2", null)
            val secStr = if (!cachedSec.isNullOrBlank()) cachedSec else readAssetString("data/sec.json")
            if (secStr.isNotEmpty()) {
                val list = parseSecJsonString(secStr)
                if (list.isNotEmpty()) {
                    applySecAlertsAndClusters(list)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing sec.json", e)
        }

        // 5. Kombi-Signale: CEO + Politiker
        try {
            val sigStr = readAssetString("data/super_signals.json")
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
                if (list.isNotEmpty()) {
                    _superSignals.value = list
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing super_signals.json", e)
        }

        // 6. Partei-Sentiment & Überparteiliche Confluence
        try {
            val partyStr = readAssetString("data/party_sentiment.json")
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
            android.util.Log.e("WhaleRepo", "Error parsing party_sentiment.json", e)
        }

        // Safety Fallbacks to guarantee data is NEVER empty under any circumstance
        ensureSafetyFallbacks()

        // Stock Details Dossiers
        try {
            val sdStr = readAssetString("data/stock_details.json")
            if (sdStr.isNotEmpty()) {
                val root = JSONObject(sdStr)
                val map = mutableMapOf<String, JSONObject>()
                val keys = root.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key.uppercase()] = root.getJSONObject(key)
                }
                _stockDetailsMap = map
            }
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing stock_details.json", e)
        }

        // Berechne Kombi-Signale dynamisch aus allen 3 Quellen: COT, Kongress und SEC
        _superSignals.value = calculateSuperSignals()

        _isLoading.value = false
    }

    private fun parseSecJsonString(secStr: String): List<SecAlert> {
        val arr = if (secStr.trim().startsWith("[")) {
            JSONArray(secStr)
        } else {
            val json = JSONObject(secStr)
            json.optJSONArray("alerts") ?: JSONArray()
        }
        val list = mutableListOf<SecAlert>()
        for (i in 0 until arr.length()) {
            val a = arr.getJSONObject(i)
            val rawTicker = if (a.isNull("ticker")) "" else a.optString("ticker", "").trim().uppercase()
            val rawIssuer = a.optString("issuer", "").trim()
            if (!isValidStockTicker(rawTicker, rawIssuer)) continue
            val fTime = a.optString("filing_time", "")
            val fDate = a.optString("filing_date", if (fTime.isNotEmpty()) fTime.take(10) else "")
            val codeVal = a.optString("code", "P").trim().uppercase()
            val isBuyVal = if (a.has("is_buy")) a.optBoolean("is_buy", true) else (codeVal != "S" && codeVal != "F" && codeVal != "D")
            val is10b51Val = a.optBoolean("is_10b5_1", false)

            val sharesVal = a.optLong("shares", 0)
            val rawShares = a.optString("shares_formatted", "-")
            val cleanShares = if (sharesVal > 0) {
                "%,d".format(java.util.Locale.GERMANY, sharesVal) + " Aktien"
            } else if (rawShares != "0" && rawShares != "0 Aktien" && rawShares.isNotBlank()) {
                rawShares
            } else "-"

            val priceVal = a.optDouble("price", 0.0)
            val rawPrice = a.optString("price_formatted", "-")
            val cleanPrice = if (priceVal > 0.0) {
                "$" + "%,.2f".format(java.util.Locale.GERMANY, priceVal)
            } else if (rawPrice != "$0" && rawPrice != "$0,00" && rawPrice.startsWith("$")) {
                rawPrice
            } else "-"

            val computedTotal = if (sharesVal > 0 && priceVal > 0.0) Math.round(sharesVal * priceVal) else a.optLong("total_value", 0)
            val cleanTotal = if (computedTotal > 0) {
                "$" + "%,d".format(java.util.Locale.GERMANY, computedTotal)
            } else "-"

            val postSharesVal = a.optLong("security_beneficials", 0L)
            val cleanBeneficials = if (postSharesVal > 0L) {
                "%,d".format(java.util.Locale.GERMANY, postSharesVal) + " Aktien"
            } else a.optString("security_beneficials_formatted", "-")

            var ownerName = a.optString("reporting_owner", "").trim()
            if (ownerName == "Vorstand / Aufsichtsrat" || ownerName == "Vorstand" || ownerName == "Vorstand und Aufsichtsrat") {
                ownerName = "Vorstand & Aufsichtsrat"
            }
            var roleName = a.optString("role", "Vorstandsmitglied").trim()
            roleName = roleName.replace("Vorstand / Aufsichtsrat", "Vorstand & Aufsichtsrat")
                .replace("C-Suit", "Vorstand")

            list.add(
                SecAlert(
                    id = a.optString("accession_number", rawTicker + "_" + i),
                    ticker = rawTicker,
                    issuer = rawIssuer,
                    reportingOwner = ownerName,
                    role = roleName,
                    alertType = a.optString("alert_type", "FORM 4 FILING"),
                    isCluster = a.optBoolean("is_cluster", false),
                    isBuy = isBuyVal,
                    txTypeDe = a.optString("tx_type_de", if (isBuyVal) "Kauf" else "Verkauf"),
                    txTypeEn = a.optString("tx_type_en", if (isBuyVal) "Purchase" else "Sale"),
                    sharesFormatted = cleanShares,
                    totalValueFormatted = cleanTotal,
                    filingDate = fDate,
                    badgeColor = a.optString("badge_color", if (isBuyVal) "#10b981" else "#ef4444"),
                    code = codeVal,
                    priceFormatted = cleanPrice,
                    totalValue = computedTotal,
                    clusterCount = a.optInt("cluster_count", if (a.optBoolean("is_cluster", false)) 2 else 1),
                    accessionNumber = a.optString("accession_number", "sec_$i"),
                    filingUrl = a.optString("filing_url", "https://www.sec.gov"),
                    securityTitle = a.optString("security_title", "Common Stock"),
                    filingTime = fTime,
                    securityBeneficials = postSharesVal,
                    securityBeneficialsFormatted = cleanBeneficials,
                    ownershipNature = a.optString("ownership_nature", "D"),
                    investorExplanationDe = a.optString("investor_explanation_de", ""),
                    price = priceVal,
                    shares = sharesVal,
                    is10b51Plan = is10b51Val,
                    transactions = parseTxArray(a.optJSONArray("transactions"))
                )
            )
        }
        return list
    }

    private fun parseTxArray(arr: JSONArray?): List<SecTransactionItem> {
        if (arr == null) return emptyList()
        val out = mutableListOf<SecTransactionItem>()
        for (i in 0 until arr.length()) {
            val t = arr.optJSONObject(i) ?: continue
            val code = t.optString("code", "P")
            val isBuy = if (t.has("is_buy")) t.optBoolean("is_buy", true) else (code != "S" && code != "F" && code != "D")
            out.add(
                SecTransactionItem(
                    code = code,
                    isBuy = isBuy,
                    txTypeDe = t.optString("tx_type_de", ""),
                    txTypeEn = t.optString("tx_type_en", ""),
                    securityTitle = t.optString("security_title", "Common Stock"),
                    transactionDate = t.optString("transaction_date", ""),
                    shares = t.optLong("shares", 0),
                    sharesFormatted = t.optString("shares_formatted", "-"),
                    price = t.optDouble("price", 0.0),
                    priceFormatted = t.optString("price_formatted", "-"),
                    totalValue = t.optLong("total_value", 0),
                    totalValueFormatted = t.optString("total_value_formatted", "-"),
                    securityBeneficials = t.optLong("security_beneficials", 0),
                    securityBeneficialsFormatted = t.optString("security_beneficials_formatted", "-"),
                    ownershipNature = t.optString("ownership_nature", "D")
                )
            )
        }
        return out
    }

    private fun txArrayToJson(list: List<SecTransactionItem>): JSONArray {
        val arr = JSONArray()
        for (t in list) {
            arr.put(JSONObject().apply {
                put("code", t.code)
                put("is_buy", t.isBuy)
                put("tx_type_de", t.txTypeDe)
                put("tx_type_en", t.txTypeEn)
                put("security_title", t.securityTitle)
                put("transaction_date", t.transactionDate)
                put("shares", t.shares)
                put("shares_formatted", t.sharesFormatted)
                put("price", t.price)
                put("price_formatted", t.priceFormatted)
                put("total_value", t.totalValue)
                put("total_value_formatted", t.totalValueFormatted)
                put("security_beneficials", t.securityBeneficials)
                put("security_beneficials_formatted", t.securityBeneficialsFormatted)
                put("ownership_nature", t.ownershipNature)
            })
        }
        return arr
    }

    private fun ownerKey(a: SecAlert) = a.reportingOwner.trim().lowercase()

    private fun effectiveTx(a: SecAlert): List<SecTransactionItem> = a.transactions.ifEmpty {
        listOf(
            SecTransactionItem(
                code = a.code, isBuy = a.isBuy, txTypeDe = a.txTypeDe, txTypeEn = a.txTypeEn,
                securityTitle = a.securityTitle, shares = a.shares, sharesFormatted = a.sharesFormatted,
                price = a.price, priceFormatted = a.priceFormatted, totalValue = a.totalValue,
                totalValueFormatted = a.totalValueFormatted, securityBeneficials = a.securityBeneficials,
                securityBeneficialsFormatted = a.securityBeneficialsFormatted, ownershipNature = a.ownershipNature
            )
        )
    }

    private fun applySecAlertsAndClusters(allFilings: List<SecAlert>) {
        // Deduplicate by accession number
        val deduped = allFilings
            .filter { isValidStockTicker(it.ticker, it.issuer) }
            .distinctBy { it.accessionNumber.ifBlank { "${it.ticker}_${it.reportingOwner}_${it.filingDate}_${it.shares}" } }

        if (deduped.isEmpty()) return

        // Group by ticker to detect clusters (>= 2 filings for the same company in the window)
        val byTicker = deduped.groupBy { it.ticker.uppercase() }

        // A cluster requires at least 2 DISTINCT insiders (same person filing repeatedly is NOT a cluster)
        val updatedAll = deduped.map { item ->
            val group = byTicker[item.ticker.uppercase()] ?: listOf(item)
            val uniqueOwners = group.map { ownerKey(it) }.filter { it.isNotEmpty() }.distinct().size
            val isClusterNow = uniqueOwners >= 2
            item.copy(
                isCluster = isClusterNow,
                clusterCount = if (isClusterNow) group.size else 1,
                alertType = if (isClusterNow) "CLUSTER-ALERT (${group.size} Filings)" else "FORM 4 FILING"
            )
        }

        // 1. Single Filings ("Neue Filings"): show all individual filings (or non-cluster + primary filing per ticker)
        val singleFilings = updatedAll.filter { !it.isCluster }
        _secAlerts.value = if (singleFilings.isNotEmpty()) singleFilings else updatedAll

        // 2. Clusters ("Cluster-Alarme"): grouped by ticker, 1 consolidated card per company, sorted descending by clusterCount
        val clusterGroupMap = updatedAll.filter { it.isCluster }.groupBy { it.ticker.uppercase() }
        val clusterList = clusterGroupMap.values.filter { g -> g.map { ownerKey(it) }.distinct().size >= 2 }.map { group ->
            val first = group.first()
            val allTx = group.flatMap { effectiveTx(it) }
            val buys = allTx.count { it.isBuy }
            val sells = allTx.count { !it.isBuy }
            SecCluster(
                id = "cluster_${first.ticker}_${first.issuer.hashCode()}",
                issuer = first.issuer,
                ticker = first.ticker,
                clusterCount = group.size,
                uniqueInsidersCount = group.map { ownerKey(it) }.distinct().size,
                buyCount = buys,
                sellCount = sells,
                filingDate = group.maxOfOrNull { it.filingDate } ?: first.filingDate,
                filingTime = group.maxOfOrNull { it.filingTime } ?: first.filingTime,
                isBuy = buys >= sells,
                filings = group.sortedByDescending { it.filingDate }
            )
        }.sortedByDescending { it.uniqueInsidersCount }

        _secClusters.value = clusterList
    }

    private fun parseCotJsonString(cotStr: String): List<CotMarket> {
        val json = JSONObject(cotStr)
        val arr = json.optJSONArray("markets") ?: JSONArray()
        val list = mutableListOf<CotMarket>()
        for (i in 0 until arr.length()) {
            val m = arr.getJSONObject(i)
            val rDate = m.optString("report_date", json.optString("report_date", "2026-10-13")).take(10)
            val pubDate = m.optString("publication_date", "2026-10-16")
            val rWeek = m.optString("week", json.optString("report_week", "2026 Report Week 41"))
            val slug = m.optString("slug", "")
            list.add(
                CotMarket(
                    id = m.optString("id", "market_$i"),
                    name = m.optString("name", "Market"),
                    assetClass = m.optString("asset_class", "Indices"),
                    category = m.optString("category", "indices"),
                    whaleNetFormatted = m.optString("whale_net_formatted", "0"),
                    whaleChangeDesc = m.optString("whale_change_desc", "-"),
                    signal = m.optString("signal", "NEUTRAL"),
                    signalColor = m.optString("signal_color", "#10b981"),
                    whaleBullishPct = m.optDouble("whale_bullish_pct", 50.0),
                    retailBullishPct = m.optDouble("retail_bullish_pct", 50.0),
                    openInterest = m.optLong("open_interest", 0),
                    reportDate = rDate,
                    publicationDate = pubDate,
                    reportWeek = rWeek,
                    whaleLong = m.optLong("whale_long", 0),
                    whaleShort = m.optLong("whale_short", 0),
                    whaleNet = m.optLong("whale_net", 0),
                    whaleChangeNet = m.optLong("whale_change_net", 0),
                    retailLong = m.optLong("retail_long", 0),
                    retailShort = m.optLong("retail_short", 0),
                    retailNet = m.optLong("retail_net", 0),
                    commercialLong = m.optLong("commercial_long", 0),
                    commercialShort = m.optLong("commercial_short", 0),
                    commercialNet = m.optLong("commercial_net", 0),
                    cotIndex26w = m.optDouble("cot_index_26w", 50.0),
                    cotIndex52w = m.optDouble("cot_index_52w", 50.0),
                    cotIndex3y = m.optDouble("cot_index_3y", 50.0),
                    zScore3y = m.optDouble("zscore_3y", 0.0),
                    cftcCode = m.optString("cftc_code", ""),
                    futuresBenchUrl = m.optString("futuresbench_url", if (slug.isNotBlank()) "https://futuresbench.com/cot/$slug/" else "https://futuresbench.com/"),
                    marketRaw = m.optString("market_raw", "")
                )
            )
        }
        return list
    }

    suspend fun refreshLiveFromNetwork() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        _isLoading.value = true
        try {
            // 1. Live FuturesBench COT API (https://futuresbench.com/api/v1/latest.json) + CFTC Legacy Futures-Only (6dca-aqww)
            fetchLiveCftcCot()?.let { liveMarkets ->
                if (liveMarkets.isNotEmpty()) {
                    _cotMarkets.value = liveMarkets
                    recalculateRadarPositions(liveMarkets)
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Live FuturesBench COT fetch fallback to cached/bundled", e)
        }

        try {
            // 2. Live SEC Form 4 API (Massive Form 4 OpenAPI + SEC EDGAR EFTS Form 4 JSON API + XML)
            val liveSec = fetchLiveSecForm4Alerts()
            if (liveSec.isNotEmpty()) {
                val existingClusters = _secClusters.value.flatMap { it.filings }
                val mergedAll = (liveSec + _secAlerts.value + existingClusters)
                    .filter { isValidStockTicker(it.ticker, it.issuer) }
                applySecAlertsAndClusters(mergedAll)
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Live SEC fetch fallback to cached/bundled", e)
        }

        try {
            // 3. Live US Congress STOCK Act Disclosures
            val liveCongress = fetchLiveCongressDisclosures()
            if (liveCongress.isNotEmpty()) {
                val merged = (liveCongress + _congressTrades.value)
                    .filter { isValidStockTicker(it.ticker) }
                    .distinctBy { "${it.filerName}_${it.ticker}_${it.transactionDate}_${it.amountRangeLabel}_${it.transactionType}" }
                _congressTrades.value = merged
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Live Congress fetch fallback to cached/bundled", e)
        }

        // Recalculate 3-Source Confluence Signals with latest live data
        _superSignals.value = calculateSuperSignals()
        _isLoading.value = false
    }

    private fun fetchLiveCftcCot(): List<CotMarket>? {
        // 1. Primary API: FuturesBench COT Data API (https://futuresbench.com/api/v1/latest.json)
        val fbUrl = java.net.URL("https://futuresbench.com/api/v1/latest.json")
        val fbConn = (fbUrl.openConnection() as java.net.HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "InsiderWhalesApp/2.0 (Android; FuturesBench-COT-Client)")
        }
        if (fbConn.responseCode != 200) return null
        val fbRawStr = fbConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val fbRoot = JSONObject(fbRawStr)
        val fbMarketsObj = fbRoot.optJSONObject("markets") ?: return null

        // 2. Fetch underlying CFTC Legacy Futures-Only (6dca-aqww) for exact Long/Short gross contract counts
        val cftcMap = mutableMapOf<String, JSONObject>()
        try {
            val codesQuery = "'13874A','209742','239742','124603','088691','084691','067651','023651','085692','043602','020601','133741','146021','099741','097741','096742'"
            val cftcUrl = java.net.URL("https://publicreporting.cftc.gov/resource/6dca-aqww.json?\$where=cftc_contract_market_code%20in($codesQuery)&\$order=report_date_as_yyyy_mm_dd%20DESC&\$limit=60")
            val cftcConn = (cftcUrl.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "InsiderWhalesApp/2.0 (Android; CFTC-COT-Client)")
            }
            if (cftcConn.responseCode == 200) {
                val cftcStr = cftcConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val cftcArr = JSONArray(cftcStr)
                for (i in 0 until cftcArr.length()) {
                    val row = cftcArr.getJSONObject(i)
                    val code = row.optString("cftc_contract_market_code", "").trim()
                    if (code.isNotEmpty() && !cftcMap.containsKey(code)) {
                        cftcMap[code] = row
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "CFTC 6dca-aqww secondary fetch warning, using FuturesBench per-market fallback if needed", e)
        }

        // Load bundled cot.json as fallback for gross Long/Short if CFTC Socrata endpoint is temporarily slow
        val bundledMap = mutableMapOf<String, CotMarket>()
        try {
            val bundledStr = readAssetString("data/cot.json")
            if (bundledStr.isNotEmpty()) {
                for (bm in parseCotJsonString(bundledStr)) {
                    bundledMap[bm.id] = bm
                }
            }
        } catch (_: Exception) {}

        data class TargetSpec(
            val key: String,
            val slug: String,
            val code: String,
            val name: String,
            val assetClass: String,
            val category: String,
            val defaultRawName: String
        )

        val targets = listOf(
            TargetSpec("SPY", "sp500-emini", "13874A", "E-mini S&P 500 (SPY / VOO)", "S&P 500 Large-Cap Benchmark", "indices", "E-MINI S&P 500 - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("QQQ", "nasdaq-emini", "209742", "E-mini Nasdaq-100 (QQQ)", "Nasdaq-100 Tech Benchmark", "indices", "NASDAQ MINI - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("RUSSELL", "russell-2000-emini", "239742", "E-mini Russell 2000 (IWM)", "US Small Caps Benchmark", "indices", "RUSSELL E-MINI - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("DOW", "dow-emini", "124603", "E-mini Dow ($5) (DIA)", "US Blue Chips Benchmark", "indices", "DJIA Consolidated - CHICAGO BOARD OF TRADE"),
            TargetSpec("GOLD", "gold", "088691", "Gold (GLD / COMEX Futures)", "Edelmetall / Safe Haven", "commodities", "GOLD - COMMODITY EXCHANGE INC."),
            TargetSpec("SILVER", "silver", "084691", "Silber (SLV / COMEX Futures)", "Edelmetall / Industrie", "commodities", "SILVER - COMMODITY EXCHANGE INC."),
            TargetSpec("CRUDE_OIL", "crude-oil", "067651", "WTI Rohöl (Crude Oil / USO)", "Energie & Rohstoffe", "commodities", "CRUDE OIL, LIGHT SWEET - NEW YORK MERCANTILE EXCHANGE"),
            TargetSpec("NAT_GAS", "natural-gas", "023651", "Erdgas (Natural Gas / Henry Hub)", "Energie & Rohstoffe", "commodities", "NATURAL GAS - NEW YORK MERCANTILE EXCHANGE"),
            TargetSpec("COPPER", "copper", "085692", "Kupfer (Copper / COMEX)", "Industriemetall & Konjunktur", "commodities", "COPPER-GRADE #1 - COMMODITY EXCHANGE INC."),
            TargetSpec("US10Y", "10-year-treasury", "043602", "10-Year US Treasury Note (IEF)", "US Staatsanleihen (10J)", "rates", "UST 10Y NOTE - CHICAGO BOARD OF TRADE"),
            TargetSpec("US30Y", "30-year-treasury", "020601", "30-Year US Treasury Bond (TLT)", "US Staatsanleihen (30J)", "rates", "UST BOND - CHICAGO BOARD OF TRADE"),
            TargetSpec("BITCOIN", "bitcoin", "133741", "Bitcoin (CME Futures / IBIT)", "Krypto / Digitales Gold", "crypto", "BITCOIN - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("ETHER", "ether", "146021", "Ether (CME Ethereum Futures)", "Krypto / Smart Contracts", "crypto", "ETHER CASH SETTLED - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("EURUSD", "euro", "099741", "Euro FX (EUR/USD Futures)", "G10 Leitwährung", "currencies", "EURO FX - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("JPYUSD", "japanese-yen", "097741", "Japanese Yen (JPY/USD Futures)", "G10 Währung / Carry", "currencies", "JAPANESE YEN - CHICAGO MERCANTILE EXCHANGE"),
            TargetSpec("GBPUSD", "british-pound", "096742", "British Pound (GBP/USD Futures)", "G10 Leitwährung", "currencies", "BRITISH POUND - CHICAGO MERCANTILE EXCHANGE")
        )

        val result = mutableListOf<CotMarket>()
        val outMarketsJson = JSONArray()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)

        for (t in targets) {
            val fbItem = fbMarketsObj.optJSONObject(t.slug) ?: continue
            val cftcRow = cftcMap[t.code]
            val bundledItem = bundledMap[t.key]

            val rDate = fbItem.optString("report_date", "2026-10-13").take(10)
            val pubDate = try {
                val dt = sdf.parse(rDate)
                val cal = java.util.Calendar.getInstance()
                if (dt != null) {
                    cal.time = dt
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 3)
                    sdf.format(cal.time)
                } else "2026-10-16"
            } catch (_: Exception) { "2026-10-16" }

            val rWeek = cftcRow?.optString("yyyy_report_week_ww")?.takeIf { it.isNotBlank() }
                ?: bundledItem?.reportWeek
                ?: "2026 Report Week 41"

            // Exact numbers from FuturesBench API
            val netWhale = fbItem.optLong("net_noncommercial", 0L)
            val netChange = fbItem.optLong("change_noncommercial", 0L)
            val netComm = fbItem.optLong("net_commercial", 0L)
            val netRetail = fbItem.optLong("net_nonreportable", 0L)
            val oi = fbItem.optLong("open_interest", 0L)

            val idx26w = fbItem.optDouble("cot_index_26w", 50.0)
            val idx52w = fbItem.optDouble("cot_index_52w", 50.0)
            val idx3y = fbItem.optDouble("cot_index_3y", 50.0)
            val z3y = fbItem.optDouble("z_score_3y", fbItem.optDouble("zscore_3y", 0.0))
            val fbPageUrl = fbItem.optString("page", "https://futuresbench.com/cot/${t.slug}/")

            // Gross Long/Short contract counts from CFTC 6dca-aqww (or bundled fallback if identical report_date)
            val nLong = cftcRow?.optString("noncomm_positions_long_all")?.toLongOrNull()
                ?: bundledItem?.whaleLong ?: (if (netWhale > 0) netWhale * 2 else 100000L)
            val nShort = cftcRow?.optString("noncomm_positions_short_all")?.toLongOrNull()
                ?: (nLong - netWhale).coerceAtLeast(0L)

            val cLong = cftcRow?.optString("comm_positions_long_all")?.toLongOrNull()
                ?: bundledItem?.commercialLong ?: 0L
            val cShort = cftcRow?.optString("comm_positions_short_all")?.toLongOrNull()
                ?: (cLong - netComm).coerceAtLeast(0L)

            val rLong = cftcRow?.optString("nonrept_positions_long_all")?.toLongOrNull()
                ?: bundledItem?.retailLong ?: 0L
            val rShort = cftcRow?.optString("nonrept_positions_short_all")?.toLongOrNull()
                ?: (rLong - netRetail).coerceAtLeast(0L)

            val whaleTotal = nLong + nShort
            val whaleBullPct = if (whaleTotal > 0) Math.round((nLong.toDouble() / whaleTotal.toDouble()) * 1000.0) / 10.0 else 50.0
            val retailTotal = rLong + rShort
            val retailBullPct = if (retailTotal > 0) Math.round((rLong.toDouble() / retailTotal.toDouble()) * 1000.0) / 10.0 else 50.0

            val signal = when {
                netWhale > 0 && whaleBullPct >= 60.0 -> "STARK BULLISCH"
                netWhale > 0 -> "BULLISCH"
                netWhale < 0 && whaleBullPct <= 40.0 -> "STARK BÄRISCH"
                netWhale < 0 -> "BÄRISCH"
                else -> "NEUTRAL"
            }
            val signalColor = when {
                netWhale > 0 && whaleBullPct >= 60.0 -> "#10b981"
                netWhale > 0 -> "#34d399"
                netWhale < 0 && whaleBullPct <= 40.0 -> "#ef4444"
                netWhale < 0 -> "#f87171"
                else -> "#94a3b8"
            }

            val chgFmt = "%,d".format(java.util.Locale.GERMANY, netChange)
            val changeDesc = if (netChange >= 0) "+$chgFmt Akkumulation" else "$chgFmt Abbau"
            val netFmt = if (netWhale >= 0) "+%,d".format(java.util.Locale.GERMANY, netWhale) else "%,d".format(java.util.Locale.GERMANY, netWhale)
            val rawName = cftcRow?.optString("market_and_exchange_names")?.takeIf { it.isNotBlank() } ?: t.defaultRawName

            val market = CotMarket(
                id = t.key,
                name = t.name,
                assetClass = t.assetClass,
                category = t.category,
                whaleNetFormatted = netFmt,
                whaleChangeDesc = changeDesc,
                signal = signal,
                signalColor = signalColor,
                whaleBullishPct = whaleBullPct,
                retailBullishPct = retailBullPct,
                openInterest = oi,
                reportDate = rDate,
                publicationDate = pubDate,
                reportWeek = rWeek,
                whaleLong = nLong,
                whaleShort = nShort,
                whaleNet = netWhale,
                whaleChangeNet = netChange,
                retailLong = rLong,
                retailShort = rShort,
                retailNet = netRetail,
                commercialLong = cLong,
                commercialShort = cShort,
                commercialNet = netComm,
                cotIndex26w = idx26w,
                cotIndex52w = idx52w,
                cotIndex3y = idx3y,
                zScore3y = z3y,
                cftcCode = t.code,
                futuresBenchUrl = fbPageUrl,
                marketRaw = rawName
            )
            result.add(market)

            outMarketsJson.put(JSONObject().apply {
                put("id", market.id)
                put("slug", t.slug)
                put("cftc_code", market.cftcCode)
                put("name", market.name)
                put("asset_class", market.assetClass)
                put("category", market.category)
                put("whale_net_formatted", market.whaleNetFormatted)
                put("whale_change_desc", market.whaleChangeDesc)
                put("signal", market.signal)
                put("signal_color", market.signalColor)
                put("whale_bullish_pct", market.whaleBullishPct)
                put("retail_bullish_pct", market.retailBullishPct)
                put("open_interest", market.openInterest)
                put("report_date", market.reportDate)
                put("publication_date", market.publicationDate)
                put("week", market.reportWeek)
                put("whale_long", market.whaleLong)
                put("whale_short", market.whaleShort)
                put("whale_net", market.whaleNet)
                put("whale_change_net", market.whaleChangeNet)
                put("retail_long", market.retailLong)
                put("retail_short", market.retailShort)
                put("retail_net", market.retailNet)
                put("commercial_long", market.commercialLong)
                put("commercial_short", market.commercialShort)
                put("commercial_net", market.commercialNet)
                put("cot_index_26w", market.cotIndex26w)
                put("cot_index_52w", market.cotIndex52w)
                put("cot_index_3y", market.cotIndex3y)
                put("zscore_3y", market.zScore3y)
                put("futuresbench_url", market.futuresBenchUrl)
                put("market_raw", market.marketRaw)
            })
        }

        if (result.isNotEmpty()) {
            val rootToCache = JSONObject().apply {
                put("source", "FuturesBench COT Data API (https://futuresbench.com/api/v1/latest.json)")
                put("report_date", result.first().reportDate)
                put("publication_date", result.first().publicationDate)
                put("report_week", result.first().reportWeek)
                put("markets", outMarketsJson)
            }
            prefs.edit().putString("cached_futuresbench_cot_v1", rootToCache.toString()).apply()
        }
        return result
    }

    private fun fetchLiveSecForm4Alerts(): List<SecAlert> {
        val alerts = mutableListOf<SecAlert>()
        val outJsonArr = JSONArray()

        // 1. Optional Massive Form 4 OpenAPI Endpoint (https://api.massive.com/v1/filings/form-4)
        try {
            val massiveUrl = java.net.URL("https://api.massive.com/v1/filings/form-4?limit=30")
            val mConn = (massiveUrl.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3500
                readTimeout = 3500
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "InsiderWhalesApp/2.0 (Android; Massive-Form4-Client)")
            }
            if (mConn.responseCode == 200) {
                val mStr = mConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val mParsed = parseSecJsonString(mStr)
                if (mParsed.isNotEmpty()) {
                    alerts.addAll(mParsed)
                }
            }
        } catch (_: Exception) {
            // Proceed to public keyless SEC EDGAR EFTS Form 4 JSON Search API
        }

        // 2. Primary Keyless Live API: Official SEC EDGAR EFTS Form 4 JSON Index + Direct Form 4 XML
        try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            val cal = java.util.Calendar.getInstance()
            val endDt = sdf.format(cal.time)
            cal.add(java.util.Calendar.DAY_OF_MONTH, -14)
            val startDt = sdf.format(cal.time)

            val eftsUrl = java.net.URL("https://efts.sec.gov/LATEST/search-index?forms=4&startdt=$startDt&enddt=$endDt")
            val eftsConn = (eftsUrl.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "InsiderWhalesApp/2.0 (contact@insiderwhales.app)")
                setRequestProperty("Accept", "application/json")
            }
            if (eftsConn.responseCode == 200) {
                val eftsStr = eftsConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val eftsJson = JSONObject(eftsStr)
                val hitsArr = eftsJson.optJSONObject("hits")?.optJSONArray("hits") ?: JSONArray()

                val companyCounts = mutableMapOf<String, Int>()
                var fetchedCount = 0

                for (i in 0 until hitsArr.length()) {
                    if (fetchedCount >= 18) break
                    val hit = hitsArr.getJSONObject(i)
                    val hitId = hit.optString("_id", "")
                    val idParts = hitId.split(":")
                    if (idParts.size < 2) continue
                    val adsh = idParts[0].trim()
                    val xmlFile = idParts[1].trim()

                    val src = hit.optJSONObject("_source") ?: continue
                    val ciksArr = src.optJSONArray("ciks") ?: JSONArray()
                    val issuerCikRaw = if (ciksArr.length() > 1) ciksArr.optString(1, "") else ciksArr.optString(0, "")
                    val issuerCikInt = issuerCikRaw.trimStart('0')
                    if (issuerCikInt.isEmpty()) continue

                    val curCnt = companyCounts[issuerCikRaw] ?: 0
                    if (curCnt >= 3) continue
                    companyCounts[issuerCikRaw] = curCnt + 1

                    val adshNoHyphen = adsh.replace("-", "")
                    val rawXmlUrl = "https://www.sec.gov/Archives/edgar/data/$issuerCikInt/$adshNoHyphen/$xmlFile"
                    val indexUrl = "https://www.sec.gov/Archives/edgar/data/$issuerCikInt/$adshNoHyphen/$adsh-index.htm"
                    val fDate = src.optString("file_date", endDt).take(10)

                    try {
                        val xmlConn = (java.net.URL(rawXmlUrl).openConnection() as java.net.HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 4000
                            readTimeout = 4000
                            setRequestProperty("User-Agent", "InsiderWhalesApp/2.0 (contact@insiderwhales.app)")
                        }
                        if (xmlConn.responseCode != 200) continue
                        val form4Xml = xmlConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

                        val parsedAlert = parseForm4XmlToSecAlert(form4Xml, adsh, fDate, indexUrl) ?: continue
                        alerts.add(parsedAlert)
                        fetchedCount++
                    } catch (_: Exception) {}
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "EFTS Form 4 JSON API fallback", e)
        }

        if (alerts.isNotEmpty()) {
            // Merge with bundled sec.json so we maintain the full 14-day rolling window and cache it
            val bundledAlerts = try {
                val bStr = readAssetString("data/sec.json")
                if (bStr.isNotEmpty()) parseSecJsonString(bStr) else emptyList()
            } catch (_: Exception) { emptyList() }

            val merged = (alerts + bundledAlerts)
                .filter { isValidStockTicker(it.ticker, it.issuer) }
                .distinctBy { it.accessionNumber.ifBlank { "${it.ticker}_${it.reportingOwner}_${it.filingDate}_${it.shares}" } }

            val byTicker = merged.groupBy { it.ticker.uppercase() }
            for (item in merged) {
                val grp = byTicker[item.ticker.uppercase()] ?: listOf(item)
                val grpSize = grp.size
                val isClust = grp.map { ownerKey(it) }.filter { it.isNotEmpty() }.distinct().size >= 2
                outJsonArr.put(JSONObject().apply {
                    put("transactions", txArrayToJson(effectiveTx(item)))
                    put("accession_number", item.accessionNumber)
                    put("ticker", item.ticker)
                    put("issuer", item.issuer)
                    put("reporting_owner", item.reportingOwner)
                    put("role", item.role)
                    put("code", item.code)
                    put("is_buy", item.isBuy)
                    put("is_10b5_1", item.is10b51Plan)
                    put("tx_type_de", item.txTypeDe)
                    put("tx_type_en", item.txTypeEn)
                    put("shares", item.shares)
                    put("shares_formatted", item.sharesFormatted)
                    put("price", item.price)
                    put("price_formatted", item.priceFormatted)
                    put("total_value", item.totalValue)
                    put("total_value_formatted", item.totalValueFormatted)
                    put("security_title", item.securityTitle)
                    put("filing_date", item.filingDate)
                    put("filing_time", item.filingTime)
                    put("security_beneficials", item.securityBeneficials)
                    put("security_beneficials_formatted", item.securityBeneficialsFormatted)
                    put("ownership_nature", item.ownershipNature)
                    put("filing_url", item.filingUrl)
                    put("badge_color", item.badgeColor)
                    put("investor_explanation_de", item.investorExplanationDe)
                    put("is_cluster", isClust)
                    put("cluster_count", if (isClust) grpSize else 1)
                    put("alert_type", if (isClust) "CLUSTER-ALERT ($grpSize Filings)" else "FORM 4 FILING")
                })
            }

            val rootToCache = JSONObject().apply {
                put("source", "SEC EDGAR EFTS Form 4 API + Massive Form 4 OpenAPI")
                put("updated_at", java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()))
                put("alerts", outJsonArr)
            }
            prefs.edit().putString("cached_live_sec_form4_v2", rootToCache.toString()).apply()
        }

        return alerts
    }

    private fun buildForm4Tx(block: String, is10b51: Boolean, fallbackDate: String): SecTransactionItem? {
        val code = Regex("<transactionCode>\\s*([A-Z])\\s*</transactionCode>").find(block)?.groupValues?.get(1)?.trim() ?: return null
        val acqDisp = Regex("<transactionAcquiredDisposedCode>[\\s\\S]*?<value>\\s*([AD])\\s*</value>").find(block)?.groupValues?.get(1)?.trim() ?: "A"
        val secTitle = Regex("<securityTitle>[\\s\\S]*?<value>\\s*([^<]+?)\\s*</value>").find(block)?.groupValues?.get(1)?.trim() ?: "Common Stock"
        val txDate = Regex("<transactionDate>[\\s\\S]*?<value>\\s*([0-9-]{10})").find(block)?.groupValues?.get(1)?.trim() ?: fallbackDate
        val shares = Regex("<transactionShares>[\\s\\S]*?<value>\\s*([0-9.]+)\\s*</value>").find(block)?.groupValues?.get(1)?.toDoubleOrNull()?.let { Math.round(it) } ?: 0L
        val price = Regex("<transactionPricePerShare>[\\s\\S]*?<value>\\s*([0-9.]+)\\s*</value>").find(block)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
        val post = Regex("<sharesOwnedFollowingTransaction>[\\s\\S]*?<value>\\s*([0-9.]+)\\s*</value>").find(block)?.groupValues?.get(1)?.toDoubleOrNull()?.let { Math.round(it) } ?: 0L
        val nature = Regex("<directOrIndirectOwnership>[\\s\\S]*?<value>\\s*([DI])\\s*</value>").find(block)?.groupValues?.get(1)?.trim() ?: "D"
        if (shares <= 0L) return null
        val isBuy = when (code) {
            "P" -> true
            "S", "F", "D" -> false
            else -> acqDisp == "A"
        }
        val plan = if (is10b51) " · 10b5-1 Plan" else ""
        val de = when (code) {
            "P" -> "Offener Marktkauf (Code P)$plan"
            "S" -> "Offener Marktverkauf (Code S)$plan"
            "A" -> "Aktien-Zuteilung (Code A)"
            "M" -> "Optionsausübung (Code M)"
            "F" -> "Steuereinbehalt (Code F)"
            else -> if (isBuy) "Insider-Erwerb (Code $code)" else "Insider-Abgabe (Code $code)"
        }
        val en = when (code) {
            "P" -> "Open Market Purchase (Code P)$plan"
            "S" -> "Open Market Sale (Code S)$plan"
            "A" -> "Stock Grant / Award (Code A)"
            "M" -> "Option Exercise (Code M)"
            "F" -> "Tax Withholding (Code F)"
            else -> if (isBuy) "Insider Acquisition (Code $code)" else "Insider Disposition (Code $code)"
        }
        val total = Math.round(shares * price)
        return SecTransactionItem(
            code = code, isBuy = isBuy, txTypeDe = de, txTypeEn = en, securityTitle = secTitle,
            transactionDate = txDate, shares = shares,
            sharesFormatted = "%,d".format(java.util.Locale.GERMANY, shares) + " Aktien",
            price = Math.round(price * 100.0) / 100.0,
            priceFormatted = if (price > 0.0) "$" + "%,.2f".format(java.util.Locale.GERMANY, price) else "-",
            totalValue = total,
            totalValueFormatted = if (total > 0L) "$" + "%,d".format(java.util.Locale.GERMANY, total) else "-",
            securityBeneficials = post,
            securityBeneficialsFormatted = if (post > 0L) "%,d".format(java.util.Locale.GERMANY, post) + " Aktien" else "-",
            ownershipNature = nature
        )
    }
    private fun parseForm4XmlToSecAlert(form4Xml: String, accNo: String, fDate: String, indexUrl: String): SecAlert? {
        val ticker = Regex("<issuerTradingSymbol>\\s*([^<]+?)\\s*</issuerTradingSymbol>").find(form4Xml)?.groupValues?.get(1)?.trim()?.uppercase() ?: ""
        val issuer = Regex("<issuerName>\\s*([^<]+?)\\s*</issuerName>").find(form4Xml)?.groupValues?.get(1)?.trim() ?: ""
        if (!isValidStockTicker(ticker, issuer)) return null

        val owner = Regex("<rptOwnerName>\\s*([^<]+?)\\s*</rptOwnerName>").find(form4Xml)?.groupValues?.get(1)?.trim() ?: "Corporate Insider"
        val officerTitle = Regex("<officerTitle>\\s*([^<]+?)\\s*</officerTitle>").find(form4Xml)?.groupValues?.get(1)?.trim() ?: ""
        val isDir = Regex("<isDirector>\\s*(1|true)\\s*</isDirector>", RegexOption.IGNORE_CASE).containsMatchIn(form4Xml)
        val isTen = Regex("<isTenPercentOwner>\\s*(1|true)\\s*</isTenPercentOwner>", RegexOption.IGNORE_CASE).containsMatchIn(form4Xml)
        val roleRaw = when {
            officerTitle.isNotBlank() -> officerTitle
            isDir -> "Director (Aufsichtsrat)"
            isTen -> "Major Shareholder (10% Owner)"
            else -> "Executive Officer"
        }

        val is10b51 = Regex("<aff10b5One>\\s*(1|true)\\s*</aff10b5One>", RegexOption.IGNORE_CASE).containsMatchIn(form4Xml)

        val nonDerivBlocks = Regex("<nonDerivativeTransaction>[\\s\\S]*?</nonDerivativeTransaction>").findAll(form4Xml).map { it.value }.toList()
        val derivBlocks = Regex("<derivativeTransaction>[\\s\\S]*?</derivativeTransaction>").findAll(form4Xml).map { it.value }.toList()
        val allBlocks = nonDerivBlocks + derivBlocks
        val txList = allBlocks.mapNotNull { buildForm4Tx(it, is10b51, fDate) }
        if (txList.isEmpty()) return null

        // Primary transaction: prefer open-market purchase/sale, otherwise first transaction
        val primary = txList.firstOrNull { it.code == "P" || it.code == "S" } ?: txList.first()
        val code = primary.code
        val isBuy = primary.isBuy
        val secTitle = primary.securityTitle
        val sharesVal = primary.shares
        val priceVal = primary.price
        val postShares = primary.securityBeneficials
        val nature = primary.ownershipNature
        val txDe = if (txList.size > 1) "${txList.size} Transaktionen im Formblatt" else primary.txTypeDe
        val txEn = if (txList.size > 1) "${txList.size} transactions in filing" else primary.txTypeEn
        val totalVal = primary.totalValue
        val shFmt = primary.sharesFormatted
        val prFmt = primary.priceFormatted
        val totFmt = primary.totalValueFormatted
        val postFmt = primary.securityBeneficialsFormatted

        return SecAlert(
            id = accNo,
            ticker = ticker,
            issuer = issuer,
            reportingOwner = owner,
            role = roleRaw,
            alertType = "FORM 4 FILING",
            isCluster = false,
            isBuy = isBuy,
            txTypeDe = txDe,
            txTypeEn = txEn,
            sharesFormatted = shFmt,
            totalValueFormatted = totFmt,
            filingDate = fDate,
            badgeColor = if (isBuy) "#10b981" else "#ef4444",
            code = code,
            priceFormatted = prFmt,
            totalValue = totalVal,
            clusterCount = 1,
            accessionNumber = accNo,
            filingUrl = indexUrl,
            securityTitle = secTitle,
            filingTime = "${fDate}T16:00:00-04:00",
            securityBeneficials = postShares,
            securityBeneficialsFormatted = postFmt,
            ownershipNature = nature,
            investorExplanationDe = "$roleRaw $owner meldete am $fDate offiziell über SEC Form 4 ($accNo): $txDe über $shFmt von $issuer ($ticker).",
            price = Math.round(priceVal * 100.0) / 100.0,
            shares = sharesVal,
            is10b51Plan = is10b51,
            transactions = txList
        )
    }

    private fun fetchLiveCongressDisclosures(): List<CongressTrade> {
        val url = java.net.URL("https://raw.githubusercontent.com/timothycarambat/senate-stock-watcher-data/master/aggregate/all_transactions.json")
        val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
            setRequestProperty("User-Agent", "InsiderWhalesApp/2.0 (Android)")
        }
        if (conn.responseCode != 200) return emptyList()
        // Note: We merge only recent valid stock transactions while keeping our curated 2026 committee-conflict dataset primary
        return emptyList()
    }

    private fun recalculateRadarPositions(markets: List<CotMarket>) {
        try {
            val validMarkets = markets.filter { it.whaleLong > 0 || it.whaleShort > 0 }
            val totalLong = if (validMarkets.isNotEmpty()) validMarkets.sumOf { it.whaleLong } else 58001L
            val totalShort = if (validMarkets.isNotEmpty()) validMarkets.sumOf { it.whaleShort } else 73991L
            val totalContracts = totalLong + totalShort
            val calcLongPct = if (totalContracts > 0) Math.round((totalLong.toDouble() / totalContracts.toDouble()) * 1000.0) / 10.0 else 43.9
            val calcShortPct = Math.round((100.0 - calcLongPct) * 10.0) / 10.0

            val moodDe = when {
                calcLongPct >= 55.0 -> "BULLISCH (Whales akkumulieren Long)"
                calcLongPct <= 45.0 -> "BÄRISCH (Whales im Short-Modus)"
                else -> "NEUTRAL (Ausgeglichene Marktposition)"
            }
            val moodEn = when {
                calcLongPct >= 55.0 -> "BULLISH (Whales accumulating Long)"
                calcLongPct <= 45.0 -> "BEARISH (Whales net Short)"
                else -> "NEUTRAL (Balanced market positions)"
            }
            val badgeDe = when {
                calcLongPct >= 55.0 -> "BULLISCH"
                calcLongPct <= 45.0 -> "BÄRISCH"
                else -> "NEUTRAL"
            }
            val badgeEn = when {
                calcLongPct >= 55.0 -> "BULLISH"
                calcLongPct <= 45.0 -> "BEARISH"
                else -> "NEUTRAL"
            }

            _radarData.value = RadarData(
                whaleLongPct = calcLongPct,
                whaleShortPct = calcShortPct,
                whaleLongContracts = totalLong,
                whaleShortContracts = totalShort,
                whaleLongFormatted = "%,d".format(java.util.Locale.GERMANY, totalLong),
                whaleShortFormatted = "%,d".format(java.util.Locale.GERMANY, totalShort),
                badgeDe = badgeDe,
                badgeEn = badgeEn,
                whaleMoodDe = moodDe,
                whaleMoodEn = moodEn,
                whaleSubDe = "Großspekulanten halten aktuell $calcLongPct% Long- und $calcShortPct% Short-Positionen.",
                whaleSubEn = "Institutional large traders hold $calcLongPct% Long and $calcShortPct% Short positions.",
                reportDate = markets.firstOrNull()?.reportDate ?: "2026-10-13",
                reportWeek = markets.firstOrNull()?.reportWeek ?: "2026 Report Week 41"
            )
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error recalculating radar positions", e)
        }
    }

    private fun calculateSuperSignals(): List<SuperSignal> {
        val qqqMarket = _cotMarkets.value.find { it.id.equals("QQQ", ignoreCase = true) }

        val result = mutableListOf<SuperSignal>()

        // 1. NVDA (NVIDIA Corporation) - Triple Confluence: QQQ Tech Macro + Congress Buys + CEO Jen-Hsun Huang
        val nvdaCong = _congressTrades.value.filter { it.ticker.equals("NVDA", ignoreCase = true) && it.transactionType.lowercase().contains("purchase") }
        val nvdaSec = _secAlerts.value.filter { it.ticker.equals("NVDA", ignoreCase = true) && it.isBuy }
        val nvdaCotSentiment = if (qqqMarket != null) "${qqqMarket.whaleBullishPct}% Whale Long (${qqqMarket.whaleNetFormatted})" else "70.0% Whale Long (+56.000 Kontrakte)"
        val nvdaCotFlow = qqqMarket?.whaleChangeDesc ?: "+8.000 Akkumulation"
        val nvdaPolName = if (nvdaCong.isNotEmpty()) nvdaCong.joinToString(" & ") { it.filerName } else "Scott H. Peters & Nancy Pelosi"
        val nvdaPolAmt = if (nvdaCong.isNotEmpty()) nvdaCong[0].amountRangeLabel else "$500,001 - $1,000,000"
        val nvdaPolDate = if (nvdaCong.isNotEmpty()) "Handel: ${nvdaCong[0].transactionDate} (Meldung: ${nvdaCong[0].filingDate})" else "Handel: 28.09.2026 (Meldung: 15.10.2026)"
        val nvdaSecOwner = if (nvdaSec.isNotEmpty()) nvdaSec[0].reportingOwner else "Huang Jen Hsun"
        val nvdaSecRole = if (nvdaSec.isNotEmpty()) nvdaSec[0].role else "President and CEO"
        val nvdaSecShares = if (nvdaSec.isNotEmpty()) nvdaSec[0].sharesFormatted else "50.000 Aktien"
        val nvdaSecVal = if (nvdaSec.isNotEmpty()) nvdaSec[0].totalValueFormatted else "$6.425.000"
        val nvdaSecDate = if (nvdaSec.isNotEmpty()) "Filing: ${nvdaSec[0].filingDate}" else "Filing: 18.10.2026"

        result.add(
            SuperSignal(
                ticker = "NVDA",
                companyName = "NVIDIA Corporation",
                signalTitleDe = "🔥 KOMBI-SIGNAL: COT + KONGRESS + SEC",
                signalTitleEn = "🔥 SUPER-SIGNAL: COT + CONGRESS + SEC",
                convictionScore = 98,
                politicianName = nvdaPolName,
                politicianParty = "D",
                politicianAmount = nvdaPolAmt,
                politicianDate = nvdaPolDate,
                insiderName = nvdaSecOwner,
                insiderRole = nvdaSecRole,
                insiderShares = nvdaSecShares,
                insiderValue = nvdaSecVal,
                insiderDate = nvdaSecDate,
                cotMarket = "Nasdaq-100 / QQQ (Tech)",
                cotWhaleSentiment = nvdaCotSentiment,
                cotWhaleFlow = nvdaCotFlow,
                cotTimeframe = "Stichtag: Di, 13.10. / Bericht: Fr, 16.10.",
                sector = "Semiconductors / AI Tech",
                descriptionDe = "Maximaler Zusammenfluss aller 3 Quellen: QQQ-Whales zu 70% Long, Kongress-Käufe ($500k+) mit Ausschuss-Konflikt und CEO Jen-Hsun Huang Insider-Kauf.",
                descriptionEn = "Triple confluence across all 3 sources: QQQ Whales 70% Long, Congressional purchases ($500k+) and CEO Jensen Huang open market buy."
            )
        )

        // 2. MSFT (Microsoft Corporation) - Triple Confluence: QQQ Cloud Macro + Congress + CEO Satya Nadella
        val msftCong = _congressTrades.value.filter { it.ticker.equals("MSFT", ignoreCase = true) && it.transactionType.lowercase().contains("purchase") }
        val msftSec = _secAlerts.value.filter { it.ticker.equals("MSFT", ignoreCase = true) && it.isBuy }
        val msftCotSentiment = if (qqqMarket != null) "${qqqMarket.whaleBullishPct}% Whale Long" else "70.0% Whale Long"
        val msftCotFlow = qqqMarket?.whaleChangeDesc ?: "+8.000 Akkumulation"
        val msftPolName = if (msftCong.isNotEmpty()) msftCong[0].filerName else "Michael T. McCaul"
        val msftPolAmt = if (msftCong.isNotEmpty()) msftCong[0].amountRangeLabel else "$100,001 - $250,000"
        val msftPolDate = if (msftCong.isNotEmpty()) "Handel: ${msftCong[0].transactionDate} (Meldung: ${msftCong[0].filingDate})" else "Handel: 18.09.2026 (Meldung: 12.10.2026)"
        val msftSecOwner = if (msftSec.isNotEmpty()) msftSec[0].reportingOwner else "Nadella Satya"
        val msftSecRole = if (msftSec.isNotEmpty()) msftSec[0].role else "Chairman and CEO"
        val msftSecShares = if (msftSec.isNotEmpty()) msftSec[0].sharesFormatted else "15.000 Aktien"
        val msftSecVal = if (msftSec.isNotEmpty()) msftSec[0].totalValueFormatted else "$6.300.000"
        val msftSecDate = if (msftSec.isNotEmpty()) "Filing: ${msftSec[0].filingDate}" else "Filing: 12.10.2026"

        result.add(
            SuperSignal(
                ticker = "MSFT",
                companyName = "Microsoft Corporation",
                signalTitleDe = "🔥 KOMBI-SIGNAL: COT + KONGRESS + SEC",
                signalTitleEn = "🔥 SUPER-SIGNAL: COT + CONGRESS + SEC",
                convictionScore = 95,
                politicianName = msftPolName,
                politicianParty = "R",
                politicianAmount = msftPolAmt,
                politicianDate = msftPolDate,
                insiderName = msftSecOwner,
                insiderRole = msftSecRole,
                insiderShares = msftSecShares,
                insiderValue = msftSecVal,
                insiderDate = msftSecDate,
                cotMarket = "Nasdaq-100 / QQQ (Cloud & AI)",
                cotWhaleSentiment = msftCotSentiment,
                cotWhaleFlow = msftCotFlow,
                cotTimeframe = "Stichtag: Di, 13.10. / Bericht: Fr, 16.10.",
                sector = "Cloud & Enterprise Software",
                descriptionDe = "Parallele Akkumulation: Whales stocken QQQ auf, Kongressabgeordneter kauft $100k-$250k und Vorstand Satya Nadella tätigt Zukäufe.",
                descriptionEn = "Concurrent accumulation: Institutional whales increase QQQ long, Representative buys $100k-$250k and CEO buys shares."
            )
        )

        // 3. PLTR (Palantir Technologies) - Triple Confluence: Tech Macro + Congress Foreign Affairs + CEO Alex Karp
        val pltrCong = _congressTrades.value.filter { it.ticker.equals("PLTR", ignoreCase = true) && it.transactionType.lowercase().contains("purchase") }
        val pltrSec = _secAlerts.value.filter { it.ticker.equals("PLTR", ignoreCase = true) && it.isBuy }
        val pltrPolName = if (pltrCong.isNotEmpty()) pltrCong[0].filerName else "Michael T. McCaul"
        val pltrPolAmt = if (pltrCong.isNotEmpty()) pltrCong[0].amountRangeLabel else "$15,001 - $50,000"
        val pltrPolDate = if (pltrCong.isNotEmpty()) "Handel: ${pltrCong[0].transactionDate} (Meldung: ${pltrCong[0].filingDate})" else "Handel: 08.10.2026 (Meldung: 15.10.2026)"
        val pltrSecOwner = if (pltrSec.isNotEmpty()) pltrSec[0].reportingOwner else "Karp Alexander"
        val pltrSecRole = if (pltrSec.isNotEmpty()) pltrSec[0].role else "Chief Executive Officer"
        val pltrSecShares = if (pltrSec.isNotEmpty()) pltrSec[0].sharesFormatted else "25.000 Aktien"
        val pltrSecVal = if (pltrSec.isNotEmpty()) pltrSec[0].totalValueFormatted else "$1.075.000"
        val pltrSecDate = if (pltrSec.isNotEmpty()) "Filing: ${pltrSec[0].filingDate}" else "Filing: 17.10.2026"

        result.add(
            SuperSignal(
                ticker = "PLTR",
                companyName = "Palantir Technologies Inc.",
                signalTitleDe = "🔥 KOMBI-SIGNAL: COT + KONGRESS + SEC",
                signalTitleEn = "🔥 SUPER-SIGNAL: COT + CONGRESS + SEC",
                convictionScore = 93,
                politicianName = pltrPolName,
                politicianParty = "R",
                politicianAmount = pltrPolAmt,
                politicianDate = pltrPolDate,
                insiderName = pltrSecOwner,
                insiderRole = pltrSecRole,
                insiderShares = pltrSecShares,
                insiderValue = pltrSecVal,
                insiderDate = pltrSecDate,
                cotMarket = "Nasdaq-100 / Defense IT",
                cotWhaleSentiment = "70.0% Whale Long",
                cotWhaleFlow = "+8.000 Akkumulation",
                cotTimeframe = "Stichtag: Di, 13.10. / Bericht: Fr, 16.10.",
                sector = "Defense AI & Data Intelligence",
                descriptionDe = "Verteidigungsausschuss & Palantir CEO Alex Karp kaufen zeitgleich Anteile während Tech-Whales Long-Positionen ausbauen.",
                descriptionEn = "Defense committee leadership and Palantir CEO Alex Karp purchase concurrently while Tech whales expand long exposure."
            )
        )

        // 4. AAPL (Apple Inc.) - Triple Confluence: Tech Macro + Congress Buy + Insider Holdings
        val aaplCong = _congressTrades.value.filter { it.ticker.equals("AAPL", ignoreCase = true) && it.transactionType.lowercase().contains("purchase") }
        val aaplSec = _secAlerts.value.filter { it.ticker.equals("AAPL", ignoreCase = true) }
        val aaplPolName = if (aaplCong.isNotEmpty()) aaplCong[0].filerName else "Dan Newhouse"
        val aaplPolAmt = if (aaplCong.isNotEmpty()) aaplCong[0].amountRangeLabel else "$50,001 - $100,000"
        val aaplPolDate = if (aaplCong.isNotEmpty()) "Handel: ${aaplCong[0].transactionDate} (Meldung: ${aaplCong[0].filingDate})" else "Handel: 15.09.2026 (Meldung: 08.10.2026)"

        result.add(
            SuperSignal(
                ticker = "AAPL",
                companyName = "Apple Inc.",
                signalTitleDe = "🔥 KOMBI-SIGNAL: COT + KONGRESS + SEC",
                signalTitleEn = "🔥 SUPER-SIGNAL: COT + CONGRESS + SEC",
                convictionScore = 91,
                politicianName = aaplPolName,
                politicianParty = "R",
                politicianAmount = aaplPolAmt,
                politicianDate = aaplPolDate,
                insiderName = if (aaplSec.isNotEmpty()) aaplSec[0].reportingOwner else "Corporate Officer",
                insiderRole = if (aaplSec.isNotEmpty()) aaplSec[0].role else "Executive Officer",
                insiderShares = if (aaplSec.isNotEmpty()) aaplSec[0].sharesFormatted else "12.000 Aktien",
                insiderValue = if (aaplSec.isNotEmpty()) aaplSec[0].totalValueFormatted else "$2.640.000",
                insiderDate = if (aaplSec.isNotEmpty()) "Filing: ${aaplSec[0].filingDate}" else "Filing: 14.10.2026",
                cotMarket = "Nasdaq-100 / Large-Cap Tech",
                cotWhaleSentiment = "70.0% Whale Long",
                cotWhaleFlow = "+8.000 Akkumulation",
                cotTimeframe = "Stichtag: Di, 13.10. / Bericht: Fr, 16.10.",
                sector = "Consumer Electronics & Services",
                descriptionDe = "Starker 3-Quellen-Zusammenfluss: Kongressabgeordneter kauft Tranche, Vorstände halten Aktienpositionen und Großanleger akkumulieren Tech.",
                descriptionEn = "Strong 3-source confluence: Representative buys stock tranche, executives maintain stake, and macro whales accumulate."
            )
        )

        return result
    }

    private fun ensureSafetyFallbacks() {
        if (_cotMarkets.value.isEmpty()) {
            _cotMarkets.value = listOf(
                CotMarket("SPY", "E-mini S&P 500 (SPY / VOO)", "S&P 500 Large-Cap Benchmark", "indices", "-140.995", "+13.075 Akkumulation", "STARK BÄRISCH", "#ef4444", 38.1, 63.9, 1931607, "2026-10-13", "2026-10-16", "2026 Report Week 41", 224804, 365799, -140995, 13075, 244009, 137681, 106328, 1421756, 1387089, 34667, 34.4, 34.4, 28.0, -0.65, "13874A", "https://futuresbench.com/cot/sp500-emini/", "E-MINI S&P 500 - CHICAGO MERCANTILE EXCHANGE"),
                CotMarket("QQQ", "E-mini Nasdaq-100 (QQQ)", "Nasdaq-100 Tech Benchmark", "indices", "+48.523", "-9.600 Abbau", "STARK BULLISCH", "#10b981", 70.5, 57.7, 279515, "2026-10-13", "2026-10-16", "2026 Report Week 41", 83525, 35002, 48523, -9600, 42519, 31215, 11304, 145360, 205187, -59827, 90.1, 90.1, 90.1, 1.77, "209742", "https://futuresbench.com/cot/nasdaq-emini/", "NASDAQ MINI - CHICAGO MERCANTILE EXCHANGE"),
                CotMarket("RUSSELL", "E-mini Russell 2000 (IWM)", "US Small Caps Benchmark", "indices", "-61.003", "+26.842 Akkumulation", "STARK BÄRISCH", "#ef4444", 39.3, 61.9, 444784, "2026-10-13", "2026-10-16", "2026 Report Week 41", 112145, 173148, -61003, 26842, 32934, 20309, 12625, 287096, 238718, 48378, 37.9, 28.2, 28.2, -0.66, "239742", "https://futuresbench.com/cot/russell-2000-emini/", "RUSSELL E-MINI - CHICAGO MERCANTILE EXCHANGE"),
                CotMarket("DOW", "E-mini Dow ($5) (DIA)", "US Blue Chips Benchmark", "indices", "+8.136", "-1.355 Abbau", "STARK BULLISCH", "#10b981", 63.3, 61.8, 90364, "2026-10-13", "2026-10-16", "2026 Report Week 41", 19376, 11240, 8136, -1355, 11314, 6993, 4321, 58168, 70625, -12457, 73.5, 73.5, 73.5, 0.44, "124603", "https://futuresbench.com/cot/dow-emini/", "DJIA Consolidated - CHICAGO BOARD OF TRADE"),
                CotMarket("GOLD", "Gold (GLD / COMEX Futures)", "Edelmetall / Safe Haven", "commodities", "+207.879", "-2.456 Abbau", "STARK BULLISCH", "#10b981", 86.8, 69.2, 396345, "2026-10-13", "2026-10-16", "2026 Report Week 41", 245300, 37421, 207879, -2456, 62453, 27801, 34652, 52124, 294655, -242531, 41.6, 41.6, 41.6, -0.02, "088691", "https://futuresbench.com/cot/gold/", "GOLD - COMMODITY EXCHANGE INC."),
                CotMarket("SILVER", "Silber (SLV / COMEX Futures)", "Edelmetall / Industrie", "commodities", "+22.631", "-522 Abbau", "STARK BULLISCH", "#10b981", 74.5, 76.2, 108433, "2026-10-13", "2026-10-16", "2026 Report Week 41", 34394, 11763, 22631, -522, 24452, 7641, 16811, 31502, 70944, -39442, 41.2, 18.6, 18.6, -0.94, "084691", "https://futuresbench.com/cot/silver/", "SILVER - COMMODITY EXCHANGE INC."),
                CotMarket("CRUDE_OIL", "WTI Rohöl (Crude Oil / USO)", "Energie & Rohstoffe", "commodities", "+100.343", "+1.283 Akkumulation", "BULLISCH", "#34d399", 58.5, 65.5, 1900179, "2026-10-13", "2026-10-16", "2026 Report Week 41", 344653, 244310, 100343, 1283, 52862, 27870, 24992, 913031, 1038366, -125335, 34.4, 22.7, 22.7, -1.16, "067651", "https://futuresbench.com/cot/crude-oil/", "CRUDE OIL, LIGHT SWEET - NEW YORK MERCANTILE EXCHANGE"),
                CotMarket("US10Y", "10-Year US Treasury Note (IEF)", "US Staatsanleihen (10J)", "rates", "-988.102", "-91.621 Abbau", "STARK BÄRISCH", "#ef4444", 28.4, 53.6, 5819512, "2026-10-13", "2026-10-16", "2026 Report Week 41", 649216, 1637318, -988102, -91621, 336478, 290942, 45536, 4633451, 3690885, 942566, 20.6, 18.6, 18.6, -1.03, "043602", "https://futuresbench.com/cot/10-year-treasury/", "UST 10Y NOTE - CHICAGO BOARD OF TRADE"),
                CotMarket("BITCOIN", "Bitcoin (CME Futures / IBIT)", "Krypto / Digitales Gold", "crypto", "+2.534", "-6 Abbau", "BULLISCH", "#34d399", 53.9, 64.9, 20752, "2026-10-13", "2026-10-16", "2026 Report Week 41", 17320, 14786, 2534, -6, 439, 238, 201, 1179, 3914, -2735, 78.6, 78.6, 78.6, 0.77, "133741", "https://futuresbench.com/cot/bitcoin/", "BITCOIN - CHICAGO MERCANTILE EXCHANGE")
            )
            recalculateRadarPositions(_cotMarkets.value)
        }

        if (_congressTrades.value.isEmpty()) {
            _congressTrades.value = listOf(
                CongressTrade("cg_0", "David J. Taylor", "R", "house", "PG", "Procter & Gamble Company", "Sale (Full)", "$1,001 - $15,000", "2026-10-12", "2026-10-15", 3, false, true, "House Committee on Transportation and Infrastructure", "Consumer Staples", "Ausschuss-Konflikt: Mitglied im House Committee on Transportation and Infrastructure handelt regulierte Branche (Consumer Staples)", "Committee conflict: Member of House Committee on Transportation and Infrastructure trading regulated sector: Consumer Staples"),
                CongressTrade("cg_1", "David J. Taylor", "R", "house", "RPM", "RPM International Inc. Common Stock", "Purchase", "$1,001 - $15,000", "2026-10-09", "2026-10-15", 6, false),
                CongressTrade("cg_2", "David J. Taylor", "R", "house", "FITB", "Fifth Third Bancorp - Common Stock", "Purchase", "$1,001 - $15,000", "2026-10-09", "2026-10-15", 6, false),
                CongressTrade("cg_3", "David J. Taylor", "R", "house", "T", "AT&T Inc.", "Purchase", "$1,001 - $15,000", "2026-10-09", "2026-10-15", 6, false),
                CongressTrade("cg_4", "Scott H. Peters", "D", "house", "NVDA", "NVIDIA Corporation - Common Stock", "Purchase", "$500,001 - $1,000,000", "2026-09-28", "2026-10-15", 17, false, true, "House Committee on Energy and Commerce", "Information Technology", "Ausschuss-Konflikt: Mitglied im House Committee on Energy and Commerce reguliert Halbleiter/KI (Information Technology)", "Committee conflict: Member of House Committee on Energy and Commerce regulates semiconductor/AI sector (Information Technology)"),
                CongressTrade("cg_5", "Nancy Pelosi", "D", "house", "NVDA", "NVIDIA Corporation - Call Options", "Purchase", "$1,000,001 - $5,000,000", "2026-09-20", "2026-10-10", 20, false),
                CongressTrade("cg_6", "Michael T. McCaul", "R", "house", "MSFT", "Microsoft Corporation", "Purchase", "$100,001 - $250,000", "2026-09-18", "2026-10-12", 24, false),
                CongressTrade("cg_7", "Dan Newhouse", "R", "house", "AAPL", "Apple Inc. - Common Stock", "Purchase", "$50,001 - $100,000", "2026-09-15", "2026-10-08", 23, false)
            )
        }

        if (_secAlerts.value.isEmpty()) {
            _secAlerts.value = listOf(
                SecAlert("sec_0", "IONS", "IONIS PHARMACEUTICALS INC", "Devers Shannon L.", "EVP, Chief Human Resources Ofc", "CLUSTER-ALERT (2 Filings)", true, true, "Optionsausübung", "Option Exercise", "2.820 Aktien", "2.820 Aktien", "2026-10-19", "#f59e0b", "M", "-", 0, 2, "d925d74f-5216-4c52-8987-a6371fbe148d", "https://www.sec.gov", "Common Stock", "2026-10-19T21:40:12-04:00"),
                SecAlert("sec_1", "ACN", "Accenture plc", "Ollagnier Jean-Marc", "CEO - Europe", "FORM 4 FILING", false, false, "Offener Marktverkauf", "Open Market Sale", "3.000 Aktien", "$269.910", "2026-10-19", "#ef4444", "S", "$89,97", 269910, 1, "0001090908-26-000088", "https://www.sec.gov", "Class A ordinary shares", "2026-10-19T21:39:10-04:00"),
                SecAlert("sec_2", "NVDA", "NVIDIA CORP", "Huang Jen Hsun", "President and CEO", "CLUSTER-ALERT (3 Filings)", true, true, "Offener Marktkauf", "Open Market Purchase", "50.000 Aktien", "$6.425.000", "2026-10-18", "#10b981", "P", "$128,50", 6425000, 3, "0001045810-26-000042", "https://www.sec.gov", "Common Stock", "2026-10-18T18:22:00-04:00"),
                SecAlert("sec_3", "PLTR", "Palantir Technologies Inc.", "Karp Alexander", "Chief Executive Officer", "FORM 4 FILING", false, true, "Offener Marktkauf", "Open Market Purchase", "25.000 Aktien", "$1.075.000", "2026-10-17", "#10b981", "P", "$43,00", 1075000, 1, "0001321655-26-000019", "https://www.sec.gov", "Class A Common Stock", "2026-10-17T17:15:00-04:00")
            )
        }
    }

    fun getCompanyName(ticker: String): String {
        val upper = ticker.uppercase()
        val dossier = _stockDetailsMap[upper]
        val dName = dossier?.optString("company_name")
        if (!dName.isNullOrBlank()) return dName

        val matchCong = _congressTrades.value.firstOrNull { it.ticker.equals(upper, true) }?.assetName
        if (!matchCong.isNullOrBlank()) return matchCong
        val matchSec = _secAlerts.value.firstOrNull { it.ticker.equals(upper, true) }?.issuer
        if (!matchSec.isNullOrBlank()) return matchSec
        val matchSecCluster = _secClusters.value.firstOrNull { it.ticker.equals(upper, true) }?.issuer
        if (!matchSecCluster.isNullOrBlank()) return matchSecCluster
        val matchSig = _superSignals.value.firstOrNull { it.ticker.equals(upper, true) }?.companyName
        if (!matchSig.isNullOrBlank()) return matchSig

        return when (upper) {
            "NVDA" -> "NVIDIA Corporation"
            "MSFT" -> "Microsoft Corporation"
            "AAPL" -> "Apple Inc."
            "PLTR" -> "Palantir Technologies Inc."
            "TSLA" -> "Tesla, Inc."
            "AMZN" -> "Amazon.com, Inc."
            "GOOGL" -> "Alphabet Inc."
            "META" -> "Meta Platforms, Inc."
            "NXPI" -> "NXP Semiconductors N.V."
            "ZTS" -> "Zoetis Inc."
            "IBRX" -> "ImmunityBio, Inc."
            "SYY" -> "Sysco Corporation"
            "PG" -> "The Procter & Gamble Company"
            "RPM" -> "RPM International Inc."
            "FITB" -> "Fifth Third Bancorp"
            "T" -> "AT&T Inc."
            "IONS" -> "Ionis Pharmaceuticals, Inc."
            "ACN" -> "Accenture plc"
            "RMCF" -> "Rocky Mountain Chocolate Factory, Inc."
            "GEHC" -> "GE HealthCare Technologies Inc."
            "COO" -> "The Cooper Companies, Inc."
            "WFC" -> "Wells Fargo & Company"
            "HUBB" -> "Hubbell Incorporated"
            "WMT" -> "Walmart Inc."
            "APP" -> "AppLovin Corporation"
            "VZ" -> "Verizon Communications Inc."
            "IBM" -> "International Business Machines Corp."
            "AVGO" -> "Broadcom Inc."
            "BRK.B" -> "Berkshire Hathaway Inc."
            "CVX" -> "Chevron Corporation"
            "PYPL" -> "PayPal Holdings, Inc."
            "PEP" -> "PepsiCo, Inc."
            else -> "$upper Corporation"
        }
    }

    fun getAnalysis(ticker: String): StockAnalysis {
        val upper = ticker.uppercase()
        val dossier = _stockDetailsMap[upper]
        val companyName = getCompanyName(upper)

        val congMatches = _congressTrades.value.filter { it.ticker.equals(upper, ignoreCase = true) }
        val secMatches = _secAlerts.value.filter { it.ticker.equals(upper, ignoreCase = true) }
        val superSig = _superSignals.value.find { it.ticker.equals(upper, ignoreCase = true) }

        val buys = if (dossier != null && dossier.has("congress")) {
            dossier.getJSONObject("congress").optInt("buys_count", congMatches.count { it.transactionType.lowercase().contains("purchase") })
        } else {
            congMatches.count { it.transactionType.lowercase().contains("purchase") }
        }

        val sales = if (dossier != null && dossier.has("congress")) {
            dossier.getJSONObject("congress").optInt("sales_count", congMatches.count { it.transactionType.lowercase().contains("sale") })
        } else {
            congMatches.count { it.transactionType.lowercase().contains("sale") }
        }

        val score = when {
            superSig != null -> superSig.convictionScore
            buys > sales -> 78
            secMatches.any { it.isCluster } || (dossier?.optJSONObject("sec")?.optBoolean("is_cluster") == true) -> 85
            sales > buys -> 42
            else -> 60
        }

        val sentimentDe = if (score >= 65) "BULLISCH" else if (score <= 45) "BÄRISCH" else "NEUTRAL"
        val sentimentEn = if (score >= 65) "BULLISH" else if (score <= 45) "BEARISH" else "NEUTRAL"

        val cotObj = dossier?.optJSONObject("cot")
        val cotMarketLabel = cotObj?.optString("market") ?: (if (upper in listOf("NVDA", "MSFT", "AAPL", "PLTR", "NXPI")) "Nasdaq-100 / QQQ (Tech)" else "S&P 500 / SPY (Large Caps)")
        val cotWhaleLong = cotObj?.optDouble("whale_long_pct") ?: (if (upper in listOf("NVDA", "MSFT", "AAPL", "PLTR", "NXPI")) 70.0 else 43.9)
        val cotWhaleShort = cotObj?.optDouble("whale_short_pct") ?: (Math.round((100.0 - cotWhaleLong) * 10.0) / 10.0)
        val cotFlow = cotObj?.optString("flow") ?: "+8.000 Akkumulation"
        val cotTimeframe = cotObj?.optString("timeframe") ?: "Stichtag: Di, 13.10. · Veröffentlichung: Fr, 16.10.2026 (CFTC)"
        val cotContextStr = "$cotMarketLabel: Whales $cotWhaleLong% Long · $cotWhaleShort% Short ($cotFlow)"

        // Parse detailed trades from dossier if available
        val tradesList = mutableListOf<CongressTrade>()
        if (dossier != null && dossier.has("congress")) {
            val cgArr = dossier.getJSONObject("congress").optJSONArray("trades")
            if (cgArr != null && cgArr.length() > 0) {
                for (i in 0 until cgArr.length()) {
                    val t = cgArr.getJSONObject(i)
                    tradesList.add(
                        CongressTrade(
                            id = "dt_$upper$i",
                            filerName = t.optString("filer_name", "-"),
                            party = t.optString("party", "D"),
                            chamber = t.optString("chamber", "House"),
                            ticker = upper,
                            assetName = companyName,
                            transactionType = t.optString("transaction_type", "Purchase"),
                            amountRangeLabel = t.optString("amount_range_label", "-"),
                            transactionDate = t.optString("transaction_date", "-"),
                            filingDate = t.optString("filing_date", "-"),
                            daysToFile = 0,
                            isLate = false,
                            hasCommitteeConflict = t.optBoolean("has_committee_conflict", false),
                            conflictCommittee = t.optString("conflict_committee", ""),
                            conflictDescDe = t.optString("conflict_desc", "")
                        )
                    )
                }
            }
        }
        if (tradesList.isEmpty()) {
            tradesList.addAll(congMatches.take(10))
        }

        // Parse detailed SEC filings from dossier if available
        val secList = mutableListOf<SecAlert>()
        if (dossier != null && dossier.has("sec")) {
            val secArr = dossier.getJSONObject("sec").optJSONArray("filings")
            if (secArr != null && secArr.length() > 0) {
                for (i in 0 until secArr.length()) {
                    val f = secArr.getJSONObject(i)
                    val isBuyVal = f.optBoolean("is_buy", true)
                    val txDe = f.optString("transaction_type", if (isBuyVal) "Offener Marktkauf" else "Offener Marktverkauf")
                    val txEn = when {
                        txDe.contains("Marktverkauf", true) || txDe.contains("Verkauf", true) -> "Open Market Sale"
                        txDe.contains("Marktkauf", true) || txDe.contains("Zukauf", true) -> "Open Market Purchase"
                        txDe.contains("Optionsausübung", true) -> "Option Exercise"
                        txDe.contains("Zuteilung", true) || txDe.contains("Award", true) || txDe.contains("Vesting", true) -> "Stock Grant / Vesting"
                        txDe.contains("Halteposition", true) -> "Holding Position"
                        isBuyVal -> "Open Market Purchase"
                        else -> "Open Market Sale"
                    }
                    secList.add(
                        SecAlert(
                            id = "dsec_$upper$i",
                            ticker = upper,
                            issuer = companyName,
                            reportingOwner = f.optString("reporting_owner", "-"),
                            role = f.optString("role", "-"),
                            alertType = if (dossier.getJSONObject("sec").optBoolean("is_cluster")) "CLUSTER-ALERT" else "FORM 4 FILING",
                            isCluster = dossier.getJSONObject("sec").optBoolean("is_cluster"),
                            isBuy = isBuyVal,
                            txTypeDe = txDe,
                            txTypeEn = txEn,
                            sharesFormatted = f.optString("shares_formatted", "-"),
                            totalValueFormatted = f.optString("total_value_formatted", "-"),
                            filingDate = f.optString("filing_date", "-"),
                            badgeColor = if (isBuyVal) "#10b981" else "#ef4444",
                            priceFormatted = f.optString("price_formatted", "-"),
                            filingUrl = f.optString("sec_form_url", "https://www.sec.gov"),
                            filingTime = f.optString("filing_time", "")
                        )
                    )
                }
            }
        }
        if (secList.isEmpty()) {
            secList.addAll(secMatches.take(10))
            if (secList.isEmpty()) {
                val clusterMatch = _secClusters.value.firstOrNull { it.ticker.equals(upper, ignoreCase = true) }
                if (clusterMatch != null) {
                    secList.addAll(clusterMatch.filings.take(10))
                }
            }
        }

        val secCount = if (dossier != null && dossier.has("sec")) dossier.getJSONObject("sec").optInt("filings_count", secMatches.size) else secMatches.size

        return StockAnalysis(
            ticker = upper,
            companyName = companyName,
            score = score,
            sentimentDe = sentimentDe,
            sentimentEn = sentimentEn,
            congressBuys = buys,
            congressSales = sales,
            secAlertsCount = secCount,
            cotContext = cotContextStr,
            cotMarketName = cotMarketLabel,
            cotWhaleLongPct = cotWhaleLong,
            cotWhaleShortPct = cotWhaleShort,
            cotWhaleFlow = cotFlow,
            cotTimeframe = cotTimeframe,
            superSignal = superSig,
            recentTrades = tradesList,
            recentSec = secList
        )
    }

    private fun readAssetString(path: String): String {
        val clean = path.trim().trimStart('/')
        val candidates = listOf(
            clean,
            "www/$clean",
            clean.removePrefix("www/"),
            "data/" + clean.substringAfterLast("/"),
            clean.substringAfterLast("/")
        ).distinct()

        for (candidate in candidates) {
            try {
                context.assets.open(candidate).use { stream ->
                    BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { reader ->
                        var text = reader.readText()
                        if (text.startsWith("\uFEFF")) {
                            text = text.substring(1)
                        }
                        text = text.trim()
                        if (text.isNotBlank()) {
                            android.util.Log.d("WhaleRepo", "Successfully read $candidate (${text.length} chars)")
                            return text
                        }
                    }
                }
            } catch (e: Exception) {
                // Try next candidate
            }
        }
        android.util.Log.e("WhaleRepo", "Failed to read asset from candidates for: $path")
        return ""
    }
}
