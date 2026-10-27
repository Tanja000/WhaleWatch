package com.whaleradar.data.model

enum class TabScreen(val icon: String, val labelDe: String, val labelEn: String) {
    RADAR("📡", "Radar", "Radar"),
    COT("🐋", "COT Wale", "COT Whales"),
    CONGRESS("🏛️", "Kongress", "Congress"),
    SEC("🏢", "SEC Insider", "SEC Insiders"),
    WATCHLIST("⭐", "Watchlist", "Watchlist")
}

enum class DataTimeframe(
    val id: String,
    val days: Int,
    val isProOnly: Boolean,
    val labelDe: String,
    val labelEn: String,
    val periodDe: String,
    val periodEn: String,
    val shortDe: String,
    val shortEn: String
) {
    MONTH_1(
        id = "1m",
        days = 30,
        isProOnly = false,
        labelDe = "1 Monat",
        labelEn = "1 Month",
        periodDe = "letzter 1 Monat (4 Wochen)",
        periodEn = "last 1 month (4 weeks)",
        shortDe = "1M",
        shortEn = "1M"
    ),
    MONTHS_3(
        id = "3m",
        days = 90,
        isProOnly = true,
        labelDe = "3 Monate",
        labelEn = "3 Months",
        periodDe = "letzte 3 Monate",
        periodEn = "last 3 months",
        shortDe = "3M",
        shortEn = "3M"
    ),
    MONTHS_6(
        id = "6m",
        days = 180,
        isProOnly = true,
        labelDe = "6 Monate",
        labelEn = "6 Months",
        periodDe = "letzte 6 Monate",
        periodEn = "last 6 months",
        shortDe = "6M",
        shortEn = "6M"
    ),
    YEAR_1(
        id = "1y",
        days = 365,
        isProOnly = true,
        labelDe = "1 Jahr",
        labelEn = "1 Year",
        periodDe = "letztes 1 Jahr (12 Monate)",
        periodEn = "last 1 year (12 months)",
        shortDe = "1J",
        shortEn = "1Y"
    ),
    YEARS_2(
        id = "2y",
        days = 730,
        isProOnly = true,
        labelDe = "2 Jahre",
        labelEn = "2 Years",
        periodDe = "letzte 2 Jahre (24 Monate)",
        periodEn = "last 2 years (24 months)",
        shortDe = "2J",
        shortEn = "2Y"
    );

    companion object {
        fun fromId(id: String?): DataTimeframe =
            values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MONTH_1
    }
}

data class RadarData(
    val whaleLongPct: Double = 0.0,
    val whaleShortPct: Double = 0.0,
    val whaleLongContracts: Long = 0,
    val whaleShortContracts: Long = 0,
    val whaleLongFormatted: String = "-",
    val whaleShortFormatted: String = "-",
    val badgeDe: String = "-",
    val badgeEn: String = "-",
    val whaleMoodDe: String = "Keine Daten",
    val whaleMoodEn: String = "No data",
    val whaleSubDe: String = "",
    val whaleSubEn: String = "",
    val reportDate: String = "",
    val reportWeek: String = ""
)

data class CotMarket(
    val id: String,
    val name: String,
    val assetClass: String,
    val category: String,
    val whaleNetFormatted: String,
    val whaleChangeDesc: String,
    val signal: String,
    val signalColor: String,
    val whaleBullishPct: Double,
    val retailBullishPct: Double,
    val openInterest: Long = 0,
    val reportDate: String = "",
    val publicationDate: String = "",
    val reportWeek: String = "",
    val whaleLong: Long = 0,
    val whaleShort: Long = 0,
    val whaleNet: Long = 0,
    val whaleChangeNet: Long = 0,
    val retailLong: Long = 0,
    val retailShort: Long = 0,
    val retailNet: Long = 0,
    val commercialLong: Long = 0,
    val commercialShort: Long = 0,
    val commercialNet: Long = 0,
    val cotIndex26w: Double = 50.0,
    val cotIndex52w: Double = 50.0,
    val cotIndex3y: Double = 50.0,
    val zScore3y: Double = 0.0,
    val cftcCode: String = "",
    val futuresBenchUrl: String = "https://futuresbench.com/",
    val marketRaw: String = "",
    val cftcUrl: String = "https://www.cftc.gov/MarketReports/CommitmentsofTraders/index.htm"
)

data class CongressTrade(
    val id: String,
    val filerName: String,
    val party: String,
    val chamber: String,
    val ticker: String,
    val assetName: String,
    val transactionType: String,
    val amountRangeLabel: String,
    val transactionDate: String,
    val filingDate: String,
    val daysToFile: Int,
    val isLate: Boolean,
    val hasCommitteeConflict: Boolean = false,
    val conflictCommittee: String = "",
    val conflictSector: String = "",
    val conflictDescDe: String = "",
    val conflictDescEn: String = ""
)

data class SecTransactionItem(
    val code: String = "P",
    val isBuy: Boolean = true,
    val txTypeDe: String = "",
    val txTypeEn: String = "",
    val securityTitle: String = "Common Stock",
    val transactionDate: String = "",
    val shares: Long = 0,
    val sharesFormatted: String = "-",
    val price: Double = 0.0,
    val priceFormatted: String = "-",
    val totalValue: Long = 0,
    val totalValueFormatted: String = "-",
    val securityBeneficials: Long = 0,
    val securityBeneficialsFormatted: String = "-",
    val ownershipNature: String = "D"
)

data class SecAlert(
    val id: String,
    val ticker: String,
    val issuer: String,
    val reportingOwner: String,
    val role: String,
    val alertType: String,
    val isCluster: Boolean,
    val isBuy: Boolean,
    val txTypeDe: String,
    val txTypeEn: String,
    val sharesFormatted: String,
    val totalValueFormatted: String,
    val filingDate: String,
    val badgeColor: String,
    val code: String = "P",
    val priceFormatted: String = "",
    val totalValue: Long = 0,
    val clusterCount: Int = 1,
    val accessionNumber: String = "",
    val filingUrl: String = "",
    val securityTitle: String = "Common Stock",
    val filingTime: String = "",
    val securityBeneficials: Long = 0,
    val securityBeneficialsFormatted: String = "",
    val ownershipNature: String = "D",
    val investorExplanationDe: String = "",
    val price: Double = 0.0,
    val shares: Long = 0,
    val is10b51Plan: Boolean = false,
    val transactions: List<SecTransactionItem> = emptyList()
)

data class SecCluster(
    val id: String,
    val issuer: String,
    val ticker: String,
    val clusterCount: Int,
    val uniqueInsidersCount: Int = 2,
    val buyCount: Int = 0,
    val sellCount: Int = 0,
    val filingDate: String,
    val filingTime: String,
    val isBuy: Boolean,
    val filings: List<SecAlert> = emptyList()
)

data class SuperSignal(
    val ticker: String,
    val companyName: String,
    val signalTitleDe: String = "🔥 KOMBI-SIGNAL: COT + KONGRESS + SEC",
    val signalTitleEn: String = "🔥 SUPER-SIGNAL: COT + CONGRESS + SEC",
    val convictionScore: Int = 0,
    val politicianName: String,
    val politicianParty: String,
    val politicianAmount: String,
    val politicianDate: String,
    val insiderName: String,
    val insiderRole: String,
    val insiderShares: String,
    val insiderValue: String,
    val insiderDate: String,
    val cotMarket: String = "-",
    val cotWhaleSentiment: String = "-",
    val cotWhaleFlow: String = "-",
    val cotTimeframe: String = "-",
    val sector: String,
    val descriptionDe: String,
    val descriptionEn: String
)

data class TopPartyStock(
    val ticker: String,
    val count: Int,
    val party: String,
    val category: String,
    val totalValue: Long = 0
)

data class BipartisanStock(
    val ticker: String,
    val demBuys: Int,
    val repBuys: Int,
    val totalBuys: Int,
    val sector: String,
    val signal: String
)

data class PartySentimentData(
    val demBuyCount: Int = 0,
    val repBuyCount: Int = 0,
    val demTopBuys: List<TopPartyStock> = emptyList(),
    val repTopBuys: List<TopPartyStock> = emptyList(),
    val bipartisanBuys: List<BipartisanStock> = emptyList(),
    val sentimentDriftDe: String = "",
    val sentimentDriftEn: String = ""
)
data class StockAnalysis(
    val ticker: String,
    val companyName: String,
    val score: Int,
    val sentimentDe: String,
    val sentimentEn: String,
    val congressBuys: Int,
    val congressSales: Int,
    val secAlertsCount: Int,
    val cotContext: String,
    val cotMarketName: String = "-",
    val cotWhaleLongPct: Double = 0.0,
    val cotWhaleShortPct: Double = 0.0,
    val cotWhaleFlow: String = "-",
    val cotTimeframe: String = "-",
    val superSignal: SuperSignal? = null,
    val recentTrades: List<CongressTrade> = emptyList(),
    val recentSec: List<SecAlert> = emptyList()
)

data class FocusStock(
    val ticker: String,
    val companyName: String,
    val points: Int,
    val congressBuyers: Int = 0,
    val congressDem: Int = 0,
    val congressRep: Int = 0,
    val congressSellers: Int = 0,
    val congressValue: Long = 0,
    val insiderBuyers: Int = 0,
    val insiderBuyCount: Int = 0,
    val insiderValue: Long = 0,
    val insiderSellers: Int = 0,
    val bipartisan: Boolean = false,
    val lastActivity: String = ""
)
/** 2 = strongly bullish, 1 = bullish, 0 = neutral, -1 = bearish, -2 = strongly bearish */
fun CotMarket.signalLevel(): Int {
    val bull = signal.contains("BULL", ignoreCase = true)
    val bear = !bull && signal.contains("RISCH", ignoreCase = true)
    val strong = signal.contains("STARK", ignoreCase = true)
    return when {
        bull -> if (strong) 2 else 1
        bear -> if (strong) -2 else -1
        else -> 0
    }
}

fun CotMarket.signalEn(): String = when (signalLevel()) {
    2 -> "STRONGLY BULLISH"
    1 -> "BULLISH"
    -1 -> "BEARISH"
    -2 -> "STRONGLY BEARISH"
    else -> "NEUTRAL"
}
/**
 * Long/short picture of a group of markets (e.g. one category): in how many of the given markets are the large
 * speculators net long vs. net short. Only the markets passed in are used.
 */
fun buildMarketBreadth(markets: List<CotMarket>): RadarData {
    if (markets.isEmpty()) return RadarData()
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
    return RadarData(
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
}