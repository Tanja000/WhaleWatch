package com.whaleradar.app.data

import android.content.Context
import com.whaleradar.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class WhaleRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("whaleradar_prefs", Context.MODE_PRIVATE)

    private val congressLive = CongressLive(context, prefs)

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

    private val _focusStocks = MutableStateFlow<List<FocusStock>>(emptyList())
    val focusStocks: StateFlow<List<FocusStock>> = _focusStocks.asStateFlow()

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
        if (saved == null) return emptySet()
        val cleaned = saved.map { it.trim().uppercase() }.filter { isValidStockTicker(it) }.toSet()
        if (cleaned.size != saved.size || cleaned != saved) {
            prefs.edit().putStringSet("watchlist", cleaned).apply()
        }
        return if (cleaned.isEmpty()) emptySet() else cleaned
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

    /** Maximum age of any trade / filing / report that may be shown in the app. */
    private fun maxAgeCutoffIso(): String = congressCutoffIso()

    /**
     * Nothing is bundled with the app. On start only the last cached LIVE responses are shown
     * (always pruned to the 28-day window) until the live refresh has finished.
     */
    fun loadData() {
        _isLoading.value = true

        // 1. COT (cache of the last FuturesBench / CFTC response)
        try {
            val cachedCot = prefs.getString("cached_futuresbench_cot_v1", null)
            if (!cachedCot.isNullOrBlank()) applyCot(parseCotJsonString(cachedCot))
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Cached COT data unreadable", e)
        }

        // 2. Congress (cache of the last official House / Senate response)
        try {
            val cachedCongress = congressLive.loadCached()
            if (cachedCongress.isNotEmpty()) applyCongress(cachedCongress)
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Cached congress data unreadable", e)
        }

        // 3. SEC Form 4 (cache of the last EDGAR response)
        try {
            val cachedSec = prefs.getString("cached_live_sec_form4_v2", null)
            if (!cachedSec.isNullOrBlank()) applySecAlertsAndClusters(parseSecJsonString(cachedSec))
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Cached SEC data unreadable", e)
        }

        _superSignals.value = calculateSuperSignals()
        _focusStocks.value = calculateFocusStocks()
        _isLoading.value = false
    }

    /** Applies COT markets; reports older than 28 days are dropped. */
    private fun applyCot(list: List<CotMarket>) {
        val cutoff = maxAgeCutoffIso()
        val fresh = list.filter { it.reportDate.take(10) >= cutoff }
        _cotMarkets.value = fresh
        if (fresh.isNotEmpty()) recalculateRadarPositions(fresh) else _radarData.value = RadarData()
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
        val cutoff = maxAgeCutoffIso()
        val deduped = allFilings
            .filter { isValidStockTicker(it.ticker, it.issuer) && it.filingDate.take(10) >= cutoff }
            .mapNotNull { a ->
                if (a.transactions.isEmpty()) a else {
                    val kept = a.transactions.filter { it.transactionDate.isBlank() || it.transactionDate >= cutoff }
                    if (kept.isEmpty()) null else if (kept.size == a.transactions.size) a else a.copy(transactions = kept)
                }
            }
            .distinctBy { it.accessionNumber.ifBlank { "${it.ticker}_${it.reportingOwner}_${it.filingDate}_${it.shares}" } }

        if (deduped.isEmpty()) {
            _secAlerts.value = emptyList()
            _secClusters.value = emptyList()
            return
        }

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
            val rDate = m.optString("report_date", json.optString("report_date", "")).take(10)
            val pubDate = m.optString("publication_date", "")
            val rWeek = m.optString("week", json.optString("report_week", ""))
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

    /** Only the last 4 weeks (by transaction date) are kept. */
    private fun congressCutoffIso(): String {
        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        cal.add(java.util.Calendar.DAY_OF_YEAR, -28)
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.format(cal.time)
    }

    private fun applyCongress(list: List<CongressTrade>) {
        val cutoff = congressCutoffIso()
        val result = list
            .filter { isValidStockTicker(it.ticker) && it.transactionDate >= cutoff }
            .distinctBy { "${it.chamber}|${it.filerName}|${it.ticker}|${it.transactionDate}|${it.amountRangeLabel}|${it.transactionType}|${it.filingDate}" }
            .sortedWith(compareByDescending<CongressTrade> { it.transactionDate }.thenByDescending { it.filingDate })
        _congressTrades.value = result
        recomputePartySentiment(result)
    }

    private fun recomputePartySentiment(trades: List<CongressTrade>) {
        val buys = trades.filter { it.transactionType.lowercase().contains("purchase") }
        val demBuys = buys.filter { it.party.equals("D", true) }
        val repBuys = buys.filter { it.party.equals("R", true) }

        fun shortName(n: String) = n.replace(Regex("\\s+-\\s+.*$"), "").replace(Regex("\\s+(Common Stock|Class [A-C] Common Stock|Ordinary Shares).*$", RegexOption.IGNORE_CASE), "").trim()

        // Estimated value of a disclosed range: midpoint of "$a - $b", lower bound for "Over $x"
        fun amountMid(label: String): Long {
            val nums = Regex("\\$([\\d,]+)").findAll(label).mapNotNull { it.groupValues[1].replace(",", "").toLongOrNull() }.toList()
            return when {
                nums.isEmpty() -> 0L
                label.contains("Over", ignoreCase = true) -> nums.first()
                nums.size >= 2 -> (nums[0] + nums[1]) / 2
                else -> nums[0]
            }
        }

        // Sorted by the highest total sum (sum of the range midpoints of all purchases) first
        fun top(list: List<CongressTrade>, partyLabel: String): List<TopPartyStock> =
            list.groupBy { it.ticker.uppercase() }
                .map { (t, l) ->
                    TopPartyStock(
                        ticker = t, count = l.size, party = partyLabel,
                        category = shortName(l.first().assetName),
                        totalValue = l.sumOf { amountMid(it.amountRangeLabel) }
                    )
                }
                .sortedWith(compareByDescending<TopPartyStock> { it.totalValue }.thenByDescending { it.count }.thenBy { it.ticker })
                .take(10)

        val demTop = top(demBuys, "Demokraten")
        val repTop = top(repBuys, "Republikaner")

        val demByT = demBuys.groupBy { it.ticker.uppercase() }
        val repByT = repBuys.groupBy { it.ticker.uppercase() }
        val bipartisan = demByT.keys.intersect(repByT.keys).map { t ->
            val d = demByT[t]!!.size
            val r = repByT[t]!!.size
            BipartisanStock(
                ticker = t, demBuys = d, repBuys = r, totalBuys = d + r,
                sector = shortName(demByT[t]!!.first().assetName), signal = "BIPARTISAN"
            )
        }.sortedWith(compareByDescending<BipartisanStock> { it.totalBuys }.thenBy { it.ticker })

        fun listTop(l: List<TopPartyStock>) = l.take(3).joinToString(", ") { "${it.ticker} (${it.count})" }
        val driftDe = buildString {
            append(if (demTop.isEmpty()) "Demokraten: keine gemeldeten Käufe. " else "Demokraten kauften am häufigsten: ${listTop(demTop)}. ")
            append(if (repTop.isEmpty()) "Republikaner: keine gemeldeten Käufe." else "Republikaner kauften am häufigsten: ${listTop(repTop)}.")
        }
        val driftEn = buildString {
            append(if (demTop.isEmpty()) "Democrats: no reported purchases. " else "Democrats bought most often: ${listTop(demTop)}. ")
            append(if (repTop.isEmpty()) "Republicans: no reported purchases." else "Republicans bought most often: ${listTop(repTop)}.")
        }
        _partySentiment.value = PartySentimentData(
            demBuyCount = demBuys.size,
            repBuyCount = repBuys.size,
            demTopBuys = demTop,
            repTopBuys = repTop,
            bipartisanBuys = bipartisan,
            sentimentDriftDe = driftDe,
            sentimentDriftEn = driftEn
        )
    }
    suspend fun refreshLiveFromNetwork() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        _isLoading.value = true
        try {
            // 1. Live FuturesBench COT API (https://futuresbench.com/api/v1/latest.json) + CFTC Legacy Futures-Only (6dca-aqww)
            fetchLiveCftcCot()?.let { liveMarkets ->
                if (liveMarkets.isNotEmpty()) applyCot(liveMarkets)
            }
            // always prune data that became older than 28 days
            applyCot(_cotMarkets.value)
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Live FuturesBench COT fetch failed, keeping cached data", e)
        }

        try {
            // 2. Live SEC Form 4 API (Massive Form 4 OpenAPI + SEC EDGAR EFTS Form 4 JSON API + XML)
            val liveSec = fetchLiveSecForm4Alerts()
            val existingClusters = _secClusters.value.flatMap { it.filings }
            val mergedAll = (liveSec + _secAlerts.value + existingClusters)
                .filter { isValidStockTicker(it.ticker, it.issuer) }
            applySecAlertsAndClusters(mergedAll)
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Live SEC fetch failed, keeping cached data", e)
        }

        try {
            // 3. Official U.S. House (Clerk) + U.S. Senate (eFD) Periodic Transaction Reports
            val liveCongress = congressLive.fetchAll()
            if (liveCongress != null) {
                applyCongress(liveCongress)
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "Live Congress fetch fallback to cached data", e)
        }
        // Recalculate 3-Source Confluence Signals with latest live data
        _superSignals.value = calculateSuperSignals()
        _focusStocks.value = calculateFocusStocks()
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
            setRequestProperty("User-Agent", "WhaleRadarApp/2.0 (Android; FuturesBench-COT-Client)")
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
                setRequestProperty("User-Agent", "WhaleRadarApp/2.0 (Android; CFTC-COT-Client)")
            }
            if (cftcConn.responseCode == 200) {
                val cftcStr = cftcConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val cftcArr = JSONArray(cftcStr)
                for (i in 0 until cftcArr.length()) {
                    val row = cftcArr.getJSONObject(i)
                    val code = row.optString("cftc_contract_market_code", "").trim()
                    val rowDate = row.optString("report_date_as_yyyy_mm_dd", "").take(10)
                    if (code.isNotEmpty() && rowDate.isNotEmpty() && !cftcMap.containsKey("$code|$rowDate")) {
                        cftcMap["$code|$rowDate"] = row
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("WhaleRepo", "CFTC 6dca-aqww secondary fetch warning, using FuturesBench per-market fallback if needed", e)
        }

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
            val cftcRow = cftcMap[t.code + "|" + fbItem.optString("report_date", "").take(10)]


            val rDate = fbItem.optString("report_date", "").take(10)
            if (rDate.isEmpty()) continue
            // The CFTC gross numbers must belong to exactly the same report date as the FuturesBench figures
            if (cftcRow == null || cftcRow.optString("report_date_as_yyyy_mm_dd", "").take(10) != rDate) continue
            val pubDate = try {
                val dt = sdf.parse(rDate)
                val cal = java.util.Calendar.getInstance()
                if (dt != null) {
                    cal.time = dt
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 3)
                    sdf.format(cal.time)
                } else ""
            } catch (_: Exception) { "" }

            val rWeek = cftcRow.optString("yyyy_report_week_ww").takeIf { it.isNotBlank() } ?: rDate

            // Exact numbers from FuturesBench API
            val netWhale = fbItem.optLong("net_noncommercial", 0L)
            val netChange = fbItem.optLong("change_noncommercial", 0L)
            val netComm = fbItem.optLong("net_commercial", 0L)
            val netRetail = fbItem.optLong("net_nonreportable", 0L)
            val oi = fbItem.optLong("open_interest", 0L)

            val idx26w = fbItem.optDouble("cot_index_26w", 50.0)
            val idx52w = fbItem.optDouble("cot_index_52w", 50.0)
            val hasIdx52 = fbItem.has("cot_index_52w") && !fbItem.isNull("cot_index_52w")
            val idx3y = fbItem.optDouble("cot_index_3y", 50.0)
            val z3y = fbItem.optDouble("z_score_3y", fbItem.optDouble("zscore_3y", 0.0))
            val fbPageUrl = fbItem.optString("page", "https://futuresbench.com/cot/${t.slug}/")

            // Gross Long/Short contract counts from CFTC 6dca-aqww (or bundled fallback if identical report_date)
            val nLong = cftcRow.optString("noncomm_positions_long_all").toLongOrNull() ?: continue
            val nShort = cftcRow.optString("noncomm_positions_short_all").toLongOrNull() ?: continue

            val cLong = cftcRow?.optString("comm_positions_long_all")?.toLongOrNull()
                ?: 0L
            val cShort = cftcRow?.optString("comm_positions_short_all")?.toLongOrNull()
                ?: (cLong - netComm).coerceAtLeast(0L)

            val rLong = cftcRow?.optString("nonrept_positions_long_all")?.toLongOrNull()
                ?: 0L
            val rShort = cftcRow?.optString("nonrept_positions_short_all")?.toLongOrNull()
                ?: (rLong - netRetail).coerceAtLeast(0L)

            val whaleTotal = nLong + nShort
            val whaleBullPct = if (whaleTotal > 0) Math.round((nLong.toDouble() / whaleTotal.toDouble()) * 1000.0) / 10.0 else 50.0
            val retailTotal = rLong + rShort
            val retailBullPct = if (retailTotal > 0) Math.round((rLong.toDouble() / retailTotal.toDouble()) * 1000.0) / 10.0 else 50.0

            val (signal, signalColor) = cotSignal(if (hasIdx52) idx52w else null, whaleBullPct, netWhale)

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
                setRequestProperty("User-Agent", "WhaleRadarApp/2.0 (Android; Massive-Form4-Client)")
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
            cal.add(java.util.Calendar.DAY_OF_MONTH, -28)
            val startDt = sdf.format(cal.time)

            val eftsUrl = java.net.URL("https://efts.sec.gov/LATEST/search-index?forms=4&startdt=$startDt&enddt=$endDt")
            val eftsConn = (eftsUrl.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", "WhaleRadarApp/2.0 (contact@whaleradar.app)")
                setRequestProperty("Accept", "application/json")
            }
            if (eftsConn.responseCode == 200) {
                val eftsStr = eftsConn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val eftsJson = JSONObject(eftsStr)
                val hitsArr = eftsJson.optJSONObject("hits")?.optJSONArray("hits") ?: JSONArray()

                val companyCounts = mutableMapOf<String, Int>()
                var fetchedCount = 0

                for (i in 0 until hitsArr.length()) {
                    if (fetchedCount >= 40) break
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
                            setRequestProperty("User-Agent", "WhaleRadarApp/2.0 (contact@whaleradar.app)")
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
            // Merge with the previously cached live filings (rolling 28-day window) and cache the result
            val cutoffSec = maxAgeCutoffIso()
            val previousAlerts = try {
                val prev = prefs.getString("cached_live_sec_form4_v2", null)
                if (!prev.isNullOrBlank()) parseSecJsonString(prev) else emptyList()
            } catch (_: Exception) { emptyList() }

            val merged = (alerts + previousAlerts)
                .filter { isValidStockTicker(it.ticker, it.issuer) && it.filingDate.take(10) >= cutoffSec }
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

    /**
     * Overall whale picture = market breadth: in how many of the tracked markets are the large speculators
     * net long vs. net short (contract numbers of different markets are NOT added up - that would be meaningless).
     */
    private fun recalculateRadarPositions(markets: List<CotMarket>) {
        try {
            if (markets.isEmpty()) { _radarData.value = RadarData(); return }
            val n = markets.size
            val netLong = markets.count { it.whaleNet > 0 }
            val netShort = markets.count { it.whaleNet < 0 }
            val longPct = Math.round(netLong.toDouble() / n * 1000.0) / 10.0
            val shortPct = Math.round((100.0 - longPct) * 10.0) / 10.0

            val badgeDe = when { longPct >= 60.0 -> "BULLISCH"; longPct <= 40.0 -> "B\u00C4RISCH"; else -> "NEUTRAL" }
            val badgeEn = when { longPct >= 60.0 -> "BULLISH"; longPct <= 40.0 -> "BEARISH"; else -> "NEUTRAL" }
            val moodDe = when {
                longPct >= 60.0 -> "BULLISCH (Mehrheit der M\u00E4rkte netto Long)"
                longPct <= 40.0 -> "B\u00C4RISCH (Mehrheit der M\u00E4rkte netto Short)"
                else -> "NEUTRAL (ausgeglichene Marktbreite)"
            }
            val moodEn = when {
                longPct >= 60.0 -> "BULLISH (majority of markets net long)"
                longPct <= 40.0 -> "BEARISH (majority of markets net short)"
                else -> "NEUTRAL (balanced market breadth)"
            }
            val first = markets.first()
            _radarData.value = RadarData(
                whaleLongPct = longPct,
                whaleShortPct = shortPct,
                whaleLongContracts = netLong.toLong(),
                whaleShortContracts = (n - netLong).toLong(),
                whaleLongFormatted = "$netLong / $n",
                whaleShortFormatted = "${n - netLong} / $n",
                badgeDe = badgeDe,
                badgeEn = badgeEn,
                whaleMoodDe = moodDe,
                whaleMoodEn = moodEn,
                whaleSubDe = "Bei $netLong von $n M\u00E4rkten sind die Gro\u00DFspekulanten netto Long, bei $netShort netto Short.",
                whaleSubEn = "Large speculators are net long in $netLong of $n markets and net short in $netShort.",
                reportDate = first.reportDate,
                reportWeek = first.reportWeek
            )
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error recalculating radar positions", e)
        }
    }

    /**
     * COT signal. Basis is the COT index (position of the large speculators' net position within its own
     * 52-week range, 0-100). Confirmation: bullish only with a net-long position, bearish only with a net-short
     * position - otherwise the signal is NEUTRAL. Without an index the share of long contracts is used instead.
     */
    private fun cotSignal(index52: Double?, longShare: Double, net: Long): Pair<String, String> {
        val score = index52 ?: longShare
        var level = when {
            score >= 80.0 -> 2
            score >= 60.0 -> 1
            score > 40.0 -> 0
            score > 20.0 -> -1
            else -> -2
        }
        if (level > 0 && net <= 0) level = 0
        if (level < 0 && net >= 0) level = 0
        return when (level) {
            2 -> "STARK BULLISCH" to "#10b981"
            1 -> "BULLISCH" to "#34d399"
            -1 -> "B\u00C4RISCH" to "#f87171"
            -2 -> "STARK B\u00C4RISCH" to "#ef4444"
            else -> "NEUTRAL" to "#94a3b8"
        }
    }

    /** Estimated value of a disclosed range: midpoint of "$a - $b", lower bound for "Over $x". */
    private fun amountMidValue(label: String): Long {
        val nums = Regex("\\$([\\d,]+)").findAll(label).mapNotNull { it.groupValues[1].replace(",", "").toLongOrNull() }.toList()
        return when {
            nums.isEmpty() -> 0L
            label.contains("Over", ignoreCase = true) -> nums.first()
            nums.size >= 2 -> (nums[0] + nums[1]) / 2
            else -> nums[0]
        }
    }

    /**
     * Radar ranking (live data of the last 28 days only). Points:
     *  +2 per politician who bought (max 6), +2 if estimated congress volume >= $100k, +3 if bipartisan,
     *  +3 per insider with an open-market purchase (Form 4 code P, max 9), +3 insider cluster (>= 2 insiders),
     *  +1 / +2 for insider volume >= $250k / >= $1M, +4 combination (politicians AND insiders),
     *  -1 per politician / insider who sold (max -4).
     */
    private fun calculateFocusStocks(): List<FocusStock> {
        val congByT = _congressTrades.value.groupBy { it.ticker.uppercase() }
        val secByT = (_secAlerts.value + _secClusters.value.flatMap { it.filings })
            .distinctBy { it.accessionNumber.ifBlank { it.id } }
            .groupBy { it.ticker.uppercase() }

        val result = (congByT.keys + secByT.keys).mapNotNull { t ->
            val c = congByT[t].orEmpty()
            val cBuys = c.filter { it.transactionType.lowercase().contains("purchase") }
            val cSellers = c.filter { it.transactionType.lowercase().contains("sale") }.map { it.filerName }.distinct().size
            val buyers = cBuys.map { it.filerName }.distinct().size
            val dem = cBuys.filter { it.party.equals("D", true) }.map { it.filerName }.distinct().size
            val rep = cBuys.filter { it.party.equals("R", true) }.map { it.filerName }.distinct().size
            val cValue = cBuys.sumOf { amountMidValue(it.amountRangeLabel) }

            val s = secByT[t].orEmpty()
            val insBuyTx = s.flatMap { a -> effectiveTx(a).filter { it.code == "P" }.map { a.reportingOwner.trim().lowercase() to it } }
            val insBuyers = insBuyTx.map { it.first }.distinct().size
            val insValue = insBuyTx.sumOf { it.second.totalValue }
            val insSellers = s.flatMap { a -> effectiveTx(a).filter { it.code == "S" }.map { a.reportingOwner.trim().lowercase() } }.distinct().size

            if (buyers == 0 && insBuyers == 0) return@mapNotNull null

            var points = 0
            points += minOf(buyers, 3) * 2
            if (cValue >= 100_000) points += 2
            if (dem > 0 && rep > 0) points += 3
            points += minOf(insBuyers, 3) * 3
            if (insBuyers >= 2) points += 3
            if (insValue >= 1_000_000) points += 2 else if (insValue >= 250_000) points += 1
            if (buyers > 0 && insBuyers > 0) points += 4
            points -= minOf(cSellers + insSellers, 4)

            val lastDates = cBuys.map { it.transactionDate } + s.filter { a -> effectiveTx(a).any { it.code == "P" } }.map { it.filingDate.take(10) }
            FocusStock(
                ticker = t,
                companyName = getCompanyName(t),
                points = points,
                congressBuyers = buyers,
                congressDem = dem,
                congressRep = rep,
                congressSellers = cSellers,
                congressValue = cValue,
                insiderBuyers = insBuyers,
                insiderBuyCount = insBuyTx.size,
                insiderValue = insValue,
                insiderSellers = insSellers,
                bipartisan = dem > 0 && rep > 0,
                lastActivity = lastDates.maxOrNull() ?: ""
            )
        }
        return result
            .sortedWith(compareByDescending<FocusStock> { it.points }.thenByDescending { it.insiderValue + it.congressValue }.thenBy { it.ticker })
            .take(15)
    }

    /**
     * Combination signals are computed ONLY from live data: a stock that was bought by members of Congress
     * AND by company insiders (open-market purchase, Form 4 code P) within the 28-day window.
     * The COT context (equity index whales) is added when available.
     */
    private fun calculateSuperSignals(): List<SuperSignal> {
        val cot = _cotMarkets.value
        val ref = cot.find { it.id.equals("QQQ", ignoreCase = true) } ?: cot.find { it.id.equals("SPY", ignoreCase = true) }

        val congBuys = _congressTrades.value
            .filter { it.transactionType.lowercase().contains("purchase") }
            .groupBy { it.ticker.uppercase() }
        val secBuys = (_secAlerts.value + _secClusters.value.flatMap { it.filings })
            .filter { it.isBuy && it.code == "P" }
            .distinctBy { it.accessionNumber.ifBlank { it.id } }
            .groupBy { it.ticker.uppercase() }

        val fire = "\uD83D\uDD25"
        val titleDe = if (ref != null) "$fire KOMBI-SIGNAL: COT + KONGRESS + SEC" else "$fire KOMBI-SIGNAL: KONGRESS + SEC"
        val titleEn = if (ref != null) "$fire SUPER-SIGNAL: COT + CONGRESS + SEC" else "$fire SUPER-SIGNAL: CONGRESS + SEC"
        val timeframe = if (ref != null) "Stichtag: ${ref.reportDate} / Bericht: ${ref.publicationDate}" else "-"

        return congBuys.keys.intersect(secBuys.keys).map { t ->
            val cTrades = congBuys[t]!!.sortedByDescending { it.transactionDate }
            val c = cTrades.first()
            val s = secBuys[t]!!.sortedByDescending { it.filingDate }.first()
            val names = cTrades.map { it.filerName }.distinct()
            SuperSignal(
                ticker = t,
                companyName = s.issuer,
                signalTitleDe = titleDe,
                signalTitleEn = titleEn,
                convictionScore = 0,
                politicianName = names.joinToString(" & "),
                politicianParty = c.party,
                politicianAmount = c.amountRangeLabel,
                politicianDate = "Handel: ${c.transactionDate} (Meldung: ${c.filingDate})",
                insiderName = s.reportingOwner,
                insiderRole = s.role,
                insiderShares = s.sharesFormatted,
                insiderValue = s.totalValueFormatted,
                insiderDate = "Filing: ${s.filingDate}",
                cotMarket = ref?.name ?: "-",
                cotWhaleSentiment = if (ref != null) "${ref.whaleBullishPct}% Whale Long (${ref.whaleNetFormatted})" else "-",
                cotWhaleFlow = ref?.whaleChangeDesc ?: "-",
                cotTimeframe = timeframe,
                sector = "",
                descriptionDe = "${names.size} Kongressmitglied(er) kauften $t (${c.amountRangeLabel}) und ${s.reportingOwner} (${s.role}) kaufte am offenen Markt: Kauf von Politikern und Insidern innerhalb von 28 Tagen.",
                descriptionEn = "${names.size} member(s) of Congress bought $t (${c.amountRangeLabel}) and ${s.reportingOwner} (${s.role}) bought on the open market: purchases by politicians and insiders within 28 days."
            )
        }.sortedBy { it.ticker }
    }

    fun getCompanyName(ticker: String): String {
        val upper = ticker.uppercase()
        val matchCong = _congressTrades.value.firstOrNull { it.ticker.equals(upper, true) }?.assetName
        if (!matchCong.isNullOrBlank()) return matchCong
        val matchSec = _secAlerts.value.firstOrNull { it.ticker.equals(upper, true) }?.issuer
        if (!matchSec.isNullOrBlank()) return matchSec
        val matchSecCluster = _secClusters.value.firstOrNull { it.ticker.equals(upper, true) }?.issuer
        if (!matchSecCluster.isNullOrBlank()) return matchSecCluster
        val matchSig = _superSignals.value.firstOrNull { it.ticker.equals(upper, true) }?.companyName
        if (!matchSig.isNullOrBlank()) return matchSig
        return upper
    }

    fun getAnalysis(ticker: String): StockAnalysis {
        val upper = ticker.uppercase()
        val companyName = getCompanyName(upper)

        val congMatches = _congressTrades.value.filter { it.ticker.equals(upper, ignoreCase = true) }
        val secMatches = _secAlerts.value.filter { it.ticker.equals(upper, ignoreCase = true) }
        val superSig = _superSignals.value.find { it.ticker.equals(upper, ignoreCase = true) }

        val buys = congMatches.count { it.transactionType.lowercase().contains("purchase") }
        val sales = congMatches.count { it.transactionType.lowercase().contains("sale") }

        val score = when {
            buys > sales -> 78
            secMatches.any { it.isCluster } -> 85
            sales > buys -> 42
            else -> 60
        }

        val sentimentDe = if (score >= 65) "BULLISCH" else if (score <= 45) "BÄRISCH" else "NEUTRAL"
        val sentimentEn = if (score >= 65) "BULLISH" else if (score <= 45) "BEARISH" else "NEUTRAL"

        // COT context: broad US equity index positioning of the large speculators (live data only)
        val cot = _cotMarkets.value
        val ref = cot.find { it.id.equals("SPY", ignoreCase = true) } ?: cot.find { it.id.equals("QQQ", ignoreCase = true) }
        val cotMarketLabel = ref?.name ?: "-"
        val cotWhaleLong = ref?.whaleBullishPct ?: 0.0
        val cotWhaleShort = if (ref != null) Math.round((100.0 - cotWhaleLong) * 10.0) / 10.0 else 0.0
        val cotFlow = ref?.whaleChangeDesc ?: "-"
        val cotTimeframe = if (ref != null) "Stichtag: ${ref.reportDate} · Veröffentlichung: ${ref.publicationDate} (CFTC)" else "-"
        val cotContextStr = if (ref != null) "$cotMarketLabel: Whales $cotWhaleLong% Long · $cotWhaleShort% Short ($cotFlow)" else "-"

        val tradesList = congMatches.take(10)

        val secList = mutableListOf<SecAlert>()
        secList.addAll(secMatches.take(10))
        if (secList.isEmpty()) {
            val clusterMatch = _secClusters.value.firstOrNull { it.ticker.equals(upper, ignoreCase = true) }
            if (clusterMatch != null) secList.addAll(clusterMatch.filings.take(10))
        }

        return StockAnalysis(
            ticker = upper,
            companyName = companyName,
            score = score,
            sentimentDe = sentimentDe,
            sentimentEn = sentimentEn,
            congressBuys = buys,
            congressSales = sales,
            secAlertsCount = secMatches.size,
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
}
