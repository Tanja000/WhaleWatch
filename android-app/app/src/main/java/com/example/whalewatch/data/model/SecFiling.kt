package com.example.whalewatch.data.model

import java.io.Serializable

enum class TradeType {
    BUY,
    SELL,
    OPTION_EXERCISE,
    AWARD
}

data class SecFiling(
    val accessionNumber: String,
    val ticker: String,
    val companyName: String,
    val companyCik: String,
    val reportingOwner: String,
    val ownerCik: String,
    val ownerRole: String,
    val filingDate: String,
    val transactionDate: String,
    val tradeType: TradeType,
    val sharesTraded: Long,
    val pricePerShare: Double,
    val totalValue: Double,
    val sharesOwnedFollowing: Long,
    val isRule10b51: Boolean,
    val filingUrl: String,
    val isBookmarked: Boolean = false
) : Serializable {
    val isWhale: Boolean
        get() = totalValue >= 1_000_000.0

    val formattedValue: String
        get() {
            return when {
                totalValue >= 1_000_000_000.0 -> String.format("$%.2fB", totalValue / 1_000_000_000.0)
                totalValue >= 1_000_000.0 -> String.format("$%.2fM", totalValue / 1_000_000.0)
                totalValue >= 1_000.0 -> String.format("$%.1fK", totalValue / 1_000.0)
                else -> String.format("$%.0f", totalValue)
            }
        }

    val formattedShares: String
        get() = String.format("%,d", sharesTraded)
}

data class SecStats(
    val totalFilings: Int = 0,
    val buyCount: Int = 0,
    val sellCount: Int = 0,
    val totalBuyVolume: Double = 0.0,
    val totalSellVolume: Double = 0.0,
    val whaleTradesCount: Int = 0
) {
    val buyRatioPercent: Int
        get() {
            val total = buyCount + sellCount
            return if (total > 0) ((buyCount.toDouble() / total) * 100).toInt() else 50
        }
}

enum class FilterTab {
    ALL,
    BUYS,
    SELLS,
    WHALES,
    WATCHLIST
}