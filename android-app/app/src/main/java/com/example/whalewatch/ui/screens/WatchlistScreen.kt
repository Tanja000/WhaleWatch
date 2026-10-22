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
    val suggestions = listOf("NVDA", "MSFT", "AAPL", "TSLA", "PLTR", "AMZN")

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
                text = if (isEn) "Personal Watchlist" else "Persönliche Watchlist",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEn) {
                    "With your personal Watchlist, you can monitor your favorite stocks permanently and centrally in one place. For every saved stock, the app automatically aggregates all available data across the three major institutional sources: the broader market positioning of large speculators (CFTC COT), disclosed stock purchases and sales by US lawmakers (Congress STOCK Act), and transactions by corporate executives and major shareholders (SEC Form 4). This means you no longer have to search across separate tabs individually – instead, you immediately see whether institutional whales, politicians, or corporate insiders are active in your tracked stocks. You can add any US stock at any time via its ticker symbol (e.g., NVDA, AAPL, or MSFT) or save stocks directly from the other tabs. Tapping 'Details' opens the complete breakdown for each stock, including all timeframes, names, volumes, and official source links."
                } else {
                    "Mit deiner persönlichen Watchlist behältst du deine wichtigsten Favoriten-Aktien dauerhaft und zentral an einem Ort im Blick. Für jede gespeicherte Aktie führt die App im Hintergrund automatisch alle verfügbaren Informationen aus den drei großen Datenquellen zusammen: das übergeordnete Marktsentiment der Großspekulanten (CFTC COT), die gemeldeten Aktienkäufe und -verkäufe von US-Politikern (Kongress STOCK Act) sowie die Transaktionen von Vorständen und Großaktionären (SEC Form 4). So musst du nicht mehr einzeln in verschiedenen Registern suchen, sondern erkennst sofort, ob bei deinen Wunschaktien gerade institutionelle Wale, Politiker oder Unternehmens-Insider aktiv sind. Du kannst jederzeit beliebige US-Aktien über ihr Börsenkürzel (Ticker, z. B. NVDA, AAPL oder MSFT) hinzufügen oder direkt aus den anderen Tabs heraus auf deine Watchlist setzen. Ein Tipp auf „Details“ öffnet für jede Aktie die vollständige Übersicht mit allen Zeiträumen, Namen, Volumina und Quellenlinks."
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )
        }

        // Add Ticker Row
        item {
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
                        if (newTickerInput.isNotBlank()) {
                            onAddTicker(newTickerInput)
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
                        fontSize = 12.sp,
                        softWrap = false,
                        maxLines = 1
                    )
                }
            }
        }

        // Quick Suggestion Chips
        item {
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
        }

        // Watchlist Items
        if (watchlist.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isEn) "Your watchlist is empty. Add a ticker above!"
                               else "Deine Watchlist ist leer. Füge oben eine Aktie hinzu!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
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
                        text = if (isEn) "🏛️ Congress" else "🏛️ Kongress",
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
