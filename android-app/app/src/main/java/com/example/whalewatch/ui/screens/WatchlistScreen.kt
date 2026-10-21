package com.example.whalewatch.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whalewatch.data.model.isValidStockTicker
import com.example.whalewatch.theme.AmberWhale
import com.example.whalewatch.theme.CyanAccent
import com.example.whalewatch.theme.RedSell

@Composable
fun WatchlistScreen(
    watchlist: Set<String>,
    onAddTicker: (String) -> Unit,
    onRemoveTicker: (String) -> Unit,
    onOpenAnalysis: (String) -> Unit,
    isEn: Boolean,
    modifier: Modifier = Modifier,
    getCompanyName: (String) -> String = { it }
) {
    var newTickerInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = if (isEn) "Personal Watchlist" else "Persönliche Watchlist",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = if (isEn) "Monitor your stocks for Congress buys and SEC Form 4 insider filings."
                   else "Beobachte deine Aktien auf Käufe von Politikern und neue SEC Form 4 Filings.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Add Ticker Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTickerInput,
                onValueChange = { newTickerInput = it.uppercase() },
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (isEn) "Add ticker (e.g. NVDA, AAPL)..." else "Ticker hinzufügen (z.B. NVDA)...", fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Button(
                onClick = {
                    if (isValidStockTicker(newTickerInput)) {
                        onAddTicker(newTickerInput.trim().uppercase())
                        newTickerInput = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Text(
                    text = if (isEn) "+ Add" else "+ Hinzufügen",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestion Chips
        val suggestions = listOf("NVDA", "MSFT", "AAPL", "TSLA", "PLTR", "AMZN")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isEn) "Popular:" else "Beliebt:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            suggestions.forEach { ticker ->
                SuggestionChip(
                    onClick = { onAddTicker(ticker) },
                    label = { Text(ticker, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Watchlist Items
        if (watchlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEn) "Your watchlist is empty. Add a ticker above!"
                           else "Deine Watchlist ist leer. Füge oben eine Aktie hinzu!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Alphabetische Sortierung der Watchlist A-Z
                items(watchlist.toList().sorted(), key = { it }) { ticker ->
                    WatchlistCard(
                        ticker = ticker,
                        companyName = getCompanyName(ticker),
                        onRemove = { onRemoveTicker(ticker) },
                        onOpenAnalysis = { onOpenAnalysis(ticker) },
                        isEn = isEn
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistCard(
    ticker: String,
    companyName: String,
    onRemove: () -> Unit,
    onOpenAnalysis: () -> Unit,
    isEn: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Ticker & Vollständiger Unternehmensname & Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$$ticker",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = companyName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Multi-Source Tracking Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🐋 COT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    color = CyanAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🏛️ Kongress",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Surface(
                    color = AmberWhale.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🏢 SEC Insider",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberWhale,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Button (Einheitlich "Details" wie in allen anderen Tabs)
            Button(
                onClick = onOpenAnalysis,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Text(
                    text = if (isEn) "Details" else "Details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
