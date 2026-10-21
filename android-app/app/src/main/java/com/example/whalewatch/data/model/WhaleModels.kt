package com.example.whalewatch.data.model

enum class TabScreen(val icon: String, val labelDe: String, val labelEn: String) {
    RADAR("📡", "Radar", "Radar"),
    COT("🐋", "COT Wale", "COT Whales"),
    CONGRESS("🏛️", "Kongress", "Congress"),
    SEC("🏢", "SEC Insider", "SEC Insiders"),
    WATCHLIST("⭐", "Watchlist", "Watchlist")
}

data class RadarData(
    val whaleLongPct: Double = 43.9,
    val whaleShortPct: Double = 56.1,
    val whaleLongContracts: Long = 58001,
    val whaleShortContracts: Long = 73991,
    val whaleLongFormatted: String = "58.001",
    val whaleShortFormatted: String = "73.991",
    val badgeDe: String = "BÄRISCH",
    val badgeEn: String = "BEARISH",
    val whaleMoodDe: String = "BÄRISCH (Whales im Short-Modus)",
    val whaleMoodEn: String = "BEARISH (Whales net Short)",
    val whaleSubDe: String = "Großspekulanten halten 43.9% Long- und 56.1% Short-Positionen.",
    val whaleSubEn: String = "Institutional large traders hold 43.9% Long and 56.1% Short positions.",
    val reportDate: String = "13.10.2026",
    val reportWeek: String = "Woche 41, 2026"
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
    val reportDate: String = "2026-10-13",
    val publicationDate: String = "2026-10-16",
    val reportWeek: String = "Woche 41, 2026",
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
    val shares: Long = 0
)

data class SecCluster(
    val id: String,
    val issuer: String,
    val ticker: String,
    val clusterCount: Int,
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
    val convictionScore: Int = 96,
    val politicianName: String,
    val politicianParty: String,
    val politicianAmount: String,
    val politicianDate: String,
    val insiderName: String,
    val insiderRole: String,
    val insiderShares: String,
    val insiderValue: String,
    val insiderDate: String,
    val cotMarket: String = "Nasdaq-100 / QQQ",
    val cotWhaleSentiment: String = "70.0% Whale Long",
    val cotWhaleFlow: String = "+8.000 Zukauf",
    val cotTimeframe: String = "Stichtag Di, 13.10. / Bericht Fr, 16.10.",
    val sector: String,
    val descriptionDe: String,
    val descriptionEn: String
)

data class TopPartyStock(
    val ticker: String,
    val count: Int,
    val party: String,
    val category: String
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
    val demBuyCount: Int = 473,
    val repBuyCount: Int = 135,
    val demTopBuys: List<TopPartyStock> = emptyList(),
    val repTopBuys: List<TopPartyStock> = emptyList(),
    val bipartisanBuys: List<BipartisanStock> = emptyList(),
    val sentimentDriftDe: String = "Demokraten fokussieren überwiegend Big Tech & AI (MSFT, NVDA, IBRX), während Republikaner Industrie, Spezialchemie & Grundstoffe akkumulieren.",
    val sentimentDriftEn: String = "Democrats heavily focus on Big Tech & AI (MSFT, NVDA, IBRX), while Republicans accumulate Industrials, Specialty Chemicals & Energy."
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
    val cotMarketName: String = "US-Equity Benchmark / QQQ",
    val cotWhaleLongPct: Double = 70.0,
    val cotWhaleShortPct: Double = 30.0,
    val cotWhaleFlow: String = "+8.000 Kontrakte Zufluss",
    val cotTimeframe: String = "Stichtag: Di, 13.10. · Veröffentlichung: Fr, 16.10.2026 (CFTC)",
    val superSignal: SuperSignal? = null,
    val recentTrades: List<CongressTrade> = emptyList(),
    val recentSec: List<SecAlert> = emptyList()
)
