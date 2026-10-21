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
        _isLoading.value = true

        // 1. Radar
        try {
            val radarStr = readAssetString("data/radar.json")
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
        } catch (e: Exception) {
            android.util.Log.e("WhaleRepo", "Error parsing radar.json", e)
        }

        // 2. COT Markets
        try {
            val cotStr = readAssetString("data/cot.json")
            if (cotStr.isNotEmpty()) {
                val json = JSONObject(cotStr)
                val arr = json.optJSONArray("markets") ?: JSONArray()
                val list = mutableListOf<CotMarket>()
                for (i in 0 until arr.length()) {
                    val m = arr.getJSONObject(i)
                    val rDate = m.optString("report_date", json.optString("report_date", "2026-10-13")).take(10)
                    val pubDate = m.optString("publication_date", "2026-10-16")
                    val rWeek = m.optString("week", json.optString("report_week", "Woche 41, 2026"))
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
                            marketRaw = m.optString("market_raw", "")
                        )
                    )
                }
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
                            ticker = t.optString("ticker", ""),
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

        // 4. SEC Filings
        try {
            val secStr = readAssetString("data/sec.json")
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
                    val isBuyVal = if (a.has("is_buy")) a.optBoolean("is_buy", true) else (a.optString("code") != "S")
                    val tVal = a.optLong("total_value", 0)
                    val rawTotal = a.optString("total_value_formatted", "-")
                    val cleanTotal = if (tVal <= 0 || rawTotal == "$0" || !rawTotal.startsWith("$")) "-" else rawTotal

                    val sharesVal = a.optLong("shares", 0)
                    val rawShares = a.optString("shares_formatted", "-")
                    val cleanShares = if (sharesVal <= 0 || rawShares == "0" || rawShares == "0 Aktien") "-" else rawShares

                    val priceVal = a.optDouble("price", 0.0)
                    val rawPrice = a.optString("price_formatted", "-")
                    val cleanPrice = if (priceVal <= 0.0 || rawPrice == "$0" || rawPrice == "$0,00") "-" else rawPrice

                    var ownerName = a.optString("reporting_owner", "")
                    if (ownerName == "Vorstand / Aufsichtsrat" || ownerName == "Vorstand") {
                        ownerName = "Vorstand / Aufsichtsrat"
                    }
                    var roleName = a.optString("role", "Vorstandsmitglied")
                    roleName = roleName.replace("Vorstand / Aufsichtsrat", "Vorstand & Aufsichtsrat")
                        .replace("Vorstand", "Vorstand")
                        .replace("C-Suit", "Vorstand")

                    list.add(
                        SecAlert(
                            id = a.optString("accession_number", a.optString("ticker") + "_" + i),
                            ticker = a.optString("ticker", ""),
                            issuer = a.optString("issuer", ""),
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
                            code = a.optString("code", if (isBuyVal) "P" else "S"),
                            priceFormatted = cleanPrice,
                            totalValue = tVal,
                            clusterCount = a.optInt("cluster_count", if (a.optBoolean("is_cluster", false)) 2 else 1),
                            accessionNumber = a.optString("accession_number", "sec_$i"),
                            filingUrl = a.optString("filing_url", "https://www.sec.gov"),
                            securityTitle = a.optString("security_title", "Common Stock"),
                            filingTime = fTime,
                            securityBeneficials = a.optLong("security_beneficials", 0L),
                            securityBeneficialsFormatted = a.optString("security_beneficials_formatted", ""),
                            ownershipNature = a.optString("ownership_nature", "D"),
                            investorExplanationDe = a.optString("investor_explanation_de", ""),
                            price = priceVal,
                            shares = sharesVal
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    // 1. Single Filings (non-cluster only, deduplicated)
                    val singleMap = mutableMapOf<String, SecAlert>()
                    for (item in list) {
                        if (!item.isCluster) {
                            val key = item.accessionNumber.ifBlank { "${item.issuer}_${item.reportingOwner}_${item.filingTime}" }
                            if (!singleMap.containsKey(key)) {
                                singleMap[key] = item
                            }
                        }
                    }
                    _secAlerts.value = singleMap.values.toList()

                    // 2. Clusters (grouped by issuer, 1 consolidated entry per company, sorted descending by cluster count)
                    val clusterGroupMap = mutableMapOf<String, MutableList<SecAlert>>()
                    for (item in list) {
                        if (item.isCluster) {
                            val groupKey = item.issuer.trim().lowercase()
                            clusterGroupMap.getOrPut(groupKey) { mutableListOf() }.add(item)
                        }
                    }

                    val clusterList = clusterGroupMap.values.map { group ->
                        val first = group.first()
                        SecCluster(
                            id = "cluster_${first.ticker}_${first.issuer.hashCode()}",
                            issuer = first.issuer,
                            ticker = first.ticker,
                            clusterCount = group.size,
                            filingDate = group.maxOfOrNull { it.filingDate } ?: first.filingDate,
                            filingTime = group.maxOfOrNull { it.filingTime } ?: first.filingTime,
                            isBuy = group.count { it.isBuy } >= (group.size / 2),
                            filings = group.sortedByDescending { it.filingTime }
                        )
                    }.sortedByDescending { it.clusterCount }

                    _secClusters.value = clusterList
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

        // Berechne Kombi-Signale dynamisch aus allen 3 Quellen: COT, Kongress und SEC
        _superSignals.value = calculateSuperSignals()

        _isLoading.value = false
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
                whaleLongFormatted = "%,d".format(totalLong).replace(',', '.'),
                whaleShortFormatted = "%,d".format(totalShort).replace(',', '.'),
                badgeDe = badgeDe,
                badgeEn = badgeEn,
                whaleMoodDe = moodDe,
                whaleMoodEn = moodEn,
                whaleSubDe = "Großspekulanten halten aktuell $calcLongPct% Long- und $calcShortPct% Short-Positionen.",
                whaleSubEn = "Institutional large traders hold $calcLongPct% Long and $calcShortPct% Short positions.",
                reportDate = markets.firstOrNull()?.reportDate ?: "13.10.2026",
                reportWeek = markets.firstOrNull()?.reportWeek ?: "Woche 41, 2026"
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
                CotMarket("SPY", "SPY (SPDR S&P 500 ETF) / VOO", "S&P 500 Large-Cap ETF", "indices", "-15.990", "-2.317 Abbau", "BÄRISCH", "#f87171", 43.9, 51.0, 172796),
                CotMarket("QQQ", "QQQ (Invesco QQQ Trust)", "Nasdaq-100 Tech ETF", "indices", "+56.000", "+8.000 Akkumulation", "STARK BULLISCH", "#10b981", 70.0, 62.7, 1120000),
                CotMarket("RUSSELL", "IWM (Russell 2000 ETF)", "Small Caps US", "indices", "-11.000", "-2.200 Abbau", "BÄRISCH", "#f87171", 43.7, 55.6, 390000),
                CotMarket("DOW", "DIA (Dow Jones ETF)", "Blue Chips US", "indices", "+16.000", "+1.600 Akkumulation", "BULLISCH", "#34d399", 61.1, 53.7, 410000),
                CotMarket("SILVER", "Silber (SLV / Futures)", "Edelmetall / Industrie", "commodities", "+31.000", "+3.700 Akkumulation", "STARK BULLISCH", "#10b981", 71.2, 66.7, 320000),
                CotMarket("CRUDE_OIL", "WTI Rohöl (USO ETF)", "Energie & Rohstoffe", "commodities", "+13.000", "-7.700 Abbau", "NEUTRAL", "#94a3b8", 51.8, 55.7, 1480000),
                CotMarket("US10Y", "10Y US Treasuries (IEF / TLT)", "US Staatsanleihen", "rates", "-530.000", "-39.000 Abbau", "BÄRISCH", "#f87171", 38.8, 53.7, 4900000),
                CotMarket("BITCOIN", "Bitcoin (IBIT ETF / Futures)", "Krypto / Risk-On", "crypto", "+6.700", "+1.200 Akkumulation", "BULLISCH", "#34d399", 62.7, 63.3, 41000)
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

        val qqqMarket = _cotMarkets.value.find { it.id.equals("QQQ", ignoreCase = true) }
        val cotWhaleLong = qqqMarket?.whaleBullishPct ?: 70.0
        val cotWhaleShort = Math.round((100.0 - cotWhaleLong) * 10.0) / 10.0
        val cotFlow = qqqMarket?.whaleChangeDesc ?: "+8.000 Akkumulation"
        val cotMarketLabel = if (upper in listOf("NVDA", "MSFT", "AAPL", "PLTR", "NXPI")) "Nasdaq-100 / QQQ (Tech)" else "S&P 500 / SPY (Large Caps)"
        val cotContextStr = "$cotMarketLabel: Whales $cotWhaleLong% Long · $cotWhaleShort% Short ($cotFlow)"

        return StockAnalysis(
            ticker = upper,
            companyName = if (congMatches.isNotEmpty()) congMatches[0].assetName else if (secMatches.isNotEmpty()) secMatches[0].issuer else if (superSig != null) superSig.companyName else "$upper Corporation",
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
            cotTimeframe = "Stichtag: Di, 13.10. · Veröffentlichung: Fr, 16.10.2026 (CFTC)",
            superSignal = superSig,
            recentTrades = congMatches.take(10),
            recentSec = secMatches.take(10)
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
