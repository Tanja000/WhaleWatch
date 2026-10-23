package com.whaleradar.app.data.model

enum class TabScreen(val icon: String, val labelDe: String, val labelEn: String) {
    RADAR("📡", "Radar", "Radar"),
    COT("🐋", "COT Wale", "COT Whales"),
    CONGRESS("🏛️", "Kongress", "Congress"),
    SEC("🏢", "SEC Insider", "SEC Insiders"),
    WATCHLIST("⭐", "Watchlist", "Watchlist")
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