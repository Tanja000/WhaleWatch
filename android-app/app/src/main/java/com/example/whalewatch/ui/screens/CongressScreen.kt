package com.example.whalewatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.whalewatch.data.model.TopPartyStock
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
    onTradeClick: (CongressTrade) -> Unit,
    onBipartisanClick: (BipartisanStock) -> Unit,
    onPartyStockClick: (TopPartyStock) -> Unit,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier
) {
    val modes = listOf(
        "all" to (if (isEn) "All Trades" else "Alle Trades"),
        "conflicts" to (if (isEn) "⚠️ Committee Watch" else "⚠️ Ausschuss-Konflikte"),
        "bipartisan" to (if (isEn) "🤝 Bipartisan Buys" else "🤝 Überparteiliche Käufe"),
        "sentiment" to (if (isEn) "⚖️ Party Sentiment" else "⚖️ Partei-Sentiment")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Title & Unified 5-Sentence Layman-Friendly Intro
        item {
            Text(
                text = if (isEn) "US Congress STOCK Act Radar" else "US-Kongress STOCK Act Radar",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEn) {
                    "In the United States, all members of the Senate and the House of Representatives are legally required under the 'STOCK Act' to publicly disclose their personal stock purchases and sales within 45 days. Because many lawmakers sit on powerful congressional committees – covering defense, healthcare, energy, or technology – they often directly shape multi-billion-dollar budgets, government contracts, and regulations. When politicians buy stocks in the exact industries overseen by their own committee, it highlights a potential committee conflict of interest. Bipartisan purchases, where both Democrats and Republicans invest in the same stock simultaneously despite political rivalries, are also considered a particularly strong signal. Here you can transparently track all disclosed transactions, committee overlaps, and the top stock picks of both parties."
                } else {
                    "In den USA sind alle Mitglieder des Senats und des Repräsentantenhauses durch das sogenannte „STOCK Act“-Gesetz verpflichtet, ihre privaten Aktienkäufe und -verkäufe spätestens innerhalb von 45 Tagen öffentlich zu melden. Da viele Politiker in mächtigen Fachausschüssen sitzen – etwa für Verteidigung, Gesundheit, Energie oder Technologie –, entscheiden sie oft direkt über Milliarden-Budgets, Staatsaufträge und neue Gesetze mit. Wenn Abgeordnete genau in den Branchen Aktien kaufen, die ihr eigener Ausschuss reguliert, spricht man von einem möglichen Ausschuss-Konflikt. Besonders aussagekräftig sind zudem überparteiliche Käufe, bei denen Demokraten und Republikaner trotz politischer Gegensätze gleichzeitig in dieselbe Aktie investieren. Hier kannst du alle gemeldeten Transaktionen, Ausschuss-Überschneidungen und die bevorzugten Aktien beider Parteien transparent nachverfolgen."
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }

        // Intelligence Mode Selector Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                modes.forEach { (mId, label) ->
                    val isSelected = congressMode == mId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectMode(mId) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (mId == "conflicts") AmberWhale else MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }
        }

        when (congressMode) {
            "bipartisan" -> {
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Stocks where BOTH Democrats and Republicans are heavily accumulating. One of the strongest predictive signals."
                                       else "Aktien, bei denen SOWOHL Demokraten als auch Republikaner zeitgleich kaufen. Extrem starkes Signal für bevorstehende parteiübergreifende Gesetze oder Budgets.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
                items(partySentiment.bipartisanBuys, key = { "bip_${it.ticker}" }) { stock ->
                    BipartisanStockCard(
                        stock = stock,
                        onBipartisanClick = onBipartisanClick,
                        isEn = isEn
                    )
                }
            }
            "sentiment" -> {
                item {
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
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
                item {
                    Text(
                        text = if (isEn) "🔵 Top 10 Democrat Buys" else "🔵 Top 10 Käufe der Demokraten",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF3B82F6)
                    )
                }
                items(partySentiment.demTopBuys, key = { "dem_${it.ticker}" }) { stock ->
                    PartyStockRowCard(
                        stock = stock,
                        partyColor = Color(0xFF3B82F6),
                        onPartyStockClick = onPartyStockClick,
                        isEn = isEn
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isEn) "🔴 Top 10 Republican Buys" else "🔴 Top 10 Käufe der Republikaner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFEF4444)
                    )
                }
                items(partySentiment.repTopBuys, key = { "rep_${it.ticker}" }) { stock ->
                    PartyStockRowCard(
                        stock = stock,
                        partyColor = Color(0xFFEF4444),
                        onPartyStockClick = onPartyStockClick,
                        isEn = isEn
                    )
                }
            }
            else -> {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(if (isEn) "Search politician or ticker (e.g. Pelosi, NVDA)..." else "Politiker oder Ticker suchen (z. B. Pelosi, NVDA)...", fontSize = 11.5.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
                item {
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
                }
                items(trades, key = { it.id }) { trade ->
                    CongressTradeCard(
                        trade = trade,
                        onClick = { onTradeClick(trade) },
                        isEn = isEn
                    )
                }
            }
        }
    }
}

@Composable
fun CongressTradeCard(
    trade: CongressTrade,
    onClick: () -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                val chamberLabel = if (trade.chamber.lowercase() == "senate") (if (isEn) "Senate" else "Senat") else (if (isEn) "House" else "Repräsentantenhaus")
                val partyLabel = if (trade.party.uppercase() == "D") (if (isEn) "Democrat" else "Demokrat") else if (trade.party.uppercase() == "R") (if (isEn) "Republican" else "Republikaner") else trade.party
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$chamberLabel · $partyLabel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        softWrap = false,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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

                TextButton(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text("Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                        softWrap = false,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
fun BipartisanStockCard(
    stock: BipartisanStock,
    onBipartisanClick: (BipartisanStock) -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBipartisanClick(stock) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = stock.ticker,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stock.sector,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = GreenBuy.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isEn) "${stock.totalBuys} BUYS" else "${stock.totalBuys} KÄUFE",
                        color = GreenBuy,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        softWrap = false,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isEn) "🔵 Democrats: ${stock.demBuys} Buys" else "🔵 Demokraten: ${stock.demBuys} Käufe",
                    color = Color(0xFF3B82F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isEn) "🔴 Republicans: ${stock.repBuys} Buys" else "🔴 Republikaner: ${stock.repBuys} Käufe",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onBipartisanClick(stock) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PartyStockRowCard(
    stock: TopPartyStock,
    partyColor: Color,
    onPartyStockClick: (TopPartyStock) -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPartyStockClick(stock) },
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
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(stock.ticker, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(stock.category, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = { onPartyStockClick(stock) },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = partyColor.copy(alpha = 0.15f), contentColor = partyColor),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(if (isEn) "${stock.count} Buys · Details" else "${stock.count} Käufe · Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, softWrap = false, maxLines = 1)
            }
        }
    }
}
