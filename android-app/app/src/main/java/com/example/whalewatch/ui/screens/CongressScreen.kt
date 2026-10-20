package com.example.whalewatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whalewatch.data.model.BipartisanStock
import com.example.whalewatch.data.model.CongressTrade
import com.example.whalewatch.data.model.PartySentimentData
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.GreenBuy
import com.example.whalewatch.theme.RedSell

@Composable
fun CongressScreen(
    trades: List<CongressTrade>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedType: String,
    onSelectType: (String) -> Unit,
    selectedParty: String,
    onSelectParty: (String) -> Unit,
    congressMode: String, // "all", "conflicts", "bipartisan", "sentiment"
    onSelectMode: (String) -> Unit,
    partySentiment: PartySentimentData,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = if (isEn) "US Congress STOCK Act Radar" else "US-Kongress STOCK Act Radar",
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isEn) "Track politician portfolios, committee conflicts, and bipartisan sentiment."
                   else "Offenlegungen nach dem STOCK Act. Ausschuss-Konflikte & Partei-Sentiment.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Intelligence Mode Selector Tabs (Feature 1 & Feature 2)
        val modes = listOf(
            "all" to (if (isEn) "All Trades" else "Alle Trades"),
            "conflicts" to (if (isEn) "⚠️ Committee Watch" else "⚠️ Ausschuss-Konflikte"),
            "bipartisan" to (if (isEn) "🤝 Bipartisan Buys" else "🤝 Überparteiliche Käufe"),
            "sentiment" to (if (isEn) "⚖️ Party Sentiment" else "⚖️ Partei-Sentiment")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            modes.forEach { (m, label) ->
                val isSelected = congressMode == m
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectMode(m) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (m == "conflicts") AmberWhale else MaterialTheme.colorScheme.primary,
                        selectedLabelColor = if (m == "conflicts") Color.Black else Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (congressMode) {
            "bipartisan" -> {
                // Bipartisan Confluence View
                BipartisanConfluenceView(
                    bipartisanStocks = partySentiment.bipartisanBuys,
                    onOpenAnalysis = onOpenAnalysis,
                    isEn = isEn
                )
            }
            "sentiment" -> {
                // Party Sentiment Drift View
                PartySentimentView(
                    partySentiment = partySentiment,
                    onOpenAnalysis = onOpenAnalysis,
                    isEn = isEn
                )
            }
            else -> {
                // Trades View (All or Filtered by Conflicts)
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(if (isEn) "Search politician, ticker, committee..." else "Politiker, Ticker, Ausschuss suchen...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Filter Pills: Type & Party
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val typeFilters = listOf(
                        "all" to (if (isEn) "All" else "Alle"),
                        "buy" to (if (isEn) "🟢 Buys" else "🟢 Käufe"),
                        "sale" to (if (isEn) "🔴 Sales" else "🔴 Verkäufe")
                    )
                    typeFilters.forEach { (t, label) ->
                        FilterChip(
                            selected = selectedType == t,
                            onClick = { onSelectType(t) },
                            label = { Text(label, fontSize = 10.5.sp) }
                        )
                    }

                    val partyFilters = listOf(
                        "all" to (if (isEn) "All Parties" else "Alle Parteien"),
                        "D" to (if (isEn) "🔵 Democrats" else "🔵 Demokraten"),
                        "R" to (if (isEn) "🔴 Republicans" else "🔴 Republikaner")
                    )
                    partyFilters.forEach { (p, label) ->
                        FilterChip(
                            selected = selectedParty == p,
                            onClick = { onSelectParty(p) },
                            label = { Text(label, fontSize = 10.5.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Trades List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(trades, key = { it.id }) { trade ->
                        CongressTradeCard(
                            trade = trade,
                            onOpenAnalysis = { onOpenAnalysis(trade.ticker) },
                            isEn = isEn
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CongressTradeCard(
    trade: CongressTrade,
    onOpenAnalysis: () -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trade.hasCommitteeConflict) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = if (trade.hasCommitteeConflict) androidx.compose.foundation.BorderStroke(1.5.dp, AmberWhale) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Committee Conflict Banner (Feature 1: Ausschuss-Konflikt-Radar)
            if (trade.hasCommitteeConflict) {
                Surface(
                    color = AmberWhale.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberWhale),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⚠️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEn) "COMMITTEE CONFLICT DETECTED" else "AUSSCHUSS-KONFLIKT ERKANNT",
                                color = AmberWhale,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isEn) trade.conflictDescEn else trade.conflictDescDe,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Politician Name & Chamber / Party
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val partyColor = when (trade.party.uppercase()) {
                        "D" -> Color(0xFF3B82F6)
                        "R" -> Color(0xFFEF4444)
                        else -> Color(0xFF94A3B8)
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(partyColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trade.filerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${trade.chamber.uppercase()} · ${trade.party}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ticker & Asset Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = trade.ticker.ifEmpty { "STOCK" },
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trade.assetName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                if (trade.ticker.isNotEmpty()) {
                    TextButton(
                        onClick = onOpenAnalysis,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text("📊 Analyse", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Transaction Type & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isPurchase = trade.transactionType.lowercase().contains("purchase")
                val typeColor = if (isPurchase) GreenBuy else RedSell
                val typeLabel = if (isPurchase) (if (isEn) "🟢 BUY" else "🟢 KAUF") else (if (isEn) "🔴 SALE" else "🔴 VERKAUF")

                Surface(
                    color = typeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, typeColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = typeLabel,
                        color = typeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = trade.amountRangeLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Late warning badge if > 45 days
            if (trade.isLate) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = AmberWhale.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isEn) "⚠️ Late disclosure (${trade.daysToFile} days delay)"
                               else "⚠️ Verspätete Offenlegung (${trade.daysToFile} Tage Verzug)",
                        color = AmberWhale,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BipartisanConfluenceView(
    bipartisanStocks: List<BipartisanStock>,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isEn) "🤝 Bipartisan Confluence (Dem + Rep Buys)" else "🤝 Überparteiliche Confluence (Dem + Rep Käufe)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isEn) "Stocks that BOTH Democrats and Republicans are accumulating. Highest political conviction."
                               else "Aktien, bei denen sowohl Demokraten als auch Republikaner zeitgleich als Käufer auftreten.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(bipartisanStocks, key = { it.ticker }) { stock ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stock.ticker,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            color = GreenBuy.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${stock.totalBuys} Käufe gesamt",
                                color = GreenBuy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stock.sector, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🔵 Demokraten: ${stock.demBuys} Käufe",
                            color = Color(0xFF3B82F6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🔴 Republikaner: ${stock.repBuys} Käufe",
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onOpenAnalysis(stock.ticker) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("📊 Vollanalyse & Trades ansehen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PartySentimentView(
    partySentiment: PartySentimentData,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Sentiment Drift Explanation Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isEn) "⚖️ Party Sentiment & Sector Drift" else "⚖️ Partei-Sentiment & Sektoren-Drift",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isEn) partySentiment.sentimentDriftEn else partySentiment.sentimentDriftDe,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            // Dem vs Rep Top Buys Columns
            Text(
                text = "🔵 Top 10 Käufe der Demokraten",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF3B82F6)
            )
        }

        items(partySentiment.demTopBuys) { stock ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(stock.ticker, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(stock.category, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onOpenAnalysis(stock.ticker) },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6).copy(alpha = 0.15f), contentColor = Color(0xFF3B82F6)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("${stock.count} Käufe · Analyse", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "🔴 Top 10 Käufe der Republikaner",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFEF4444)
            )
        }

        items(partySentiment.repTopBuys) { stock ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(stock.ticker, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(stock.category, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = { onOpenAnalysis(stock.ticker) },
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f), contentColor = Color(0xFFEF4444)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("${stock.count} Käufe · Analyse", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
