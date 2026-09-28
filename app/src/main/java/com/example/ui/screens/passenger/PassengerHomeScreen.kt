package com.example.ui.screens.passenger

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.TripCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.SearchCriteria

@Composable
fun PassengerHomeScreen(
    searchCriteria: SearchCriteria,
    availableTrips: List<TripEntity>,
    isKazakh: Boolean,
    onUpdateRoute: (String) -> Unit,
    onSwapRoute: () -> Unit,
    onUpdateDate: (String) -> Unit,
    onUpdateTime: (String) -> Unit,
    onUpdatePassengersCount: (Int) -> Unit,
    onUpdateSortBy: (String) -> Unit,
    onPerformSearch: () -> Unit,
    onBookTrip: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Title & Subtitle
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = Strings.appTitle(isKazakh),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyDark
                )
                Text(
                    text = Strings.appSubtitle(isKazakh),
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Search Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("passenger_search_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Route Selection with Swap button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.routeLabel(isKazakh),
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NavyPrimary.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (searchCriteria.route == "MARTUK_AKTOBE") {
                                        if (isKazakh) "Мәртөк → Ақтөбе" else "Мартук → Актобе"
                                    } else {
                                        if (isKazakh) "Ақтөбе → Мәртөк" else "Актобе → Мартук"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Swap Button
                        IconButton(
                            onClick = onSwapRoute,
                            modifier = Modifier
                                .padding(top = 18.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(KazakhGold)
                                .testTag("swap_route_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Route",
                                tint = NavyDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Date Selection Chips
                    Column {
                        Text(
                            text = Strings.dateLabel(isKazakh),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val dates = listOf(
                                "28 қыркүйек",
                                "29 қыркүйек",
                                "30 қыркүйек"
                            )
                            dates.forEach { d ->
                                val isSelected = searchCriteria.date == d
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateDate(d) },
                                    label = { Text(d, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Time selection (chips)
                    Column {
                        Text(
                            text = Strings.timeLabel(isKazakh),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val times = listOf(
                                "" to if (isKazakh) "Барлығы" else "Все",
                                "08:00" to "08:00",
                                "10:00" to "10:00",
                                "12:30" to "12:30",
                                "15:00" to "15:00",
                                "17:00" to "17:00",
                                "19:30" to "19:30"
                            )
                            items(times) { (tVal, tLabel) ->
                                val isSelected = searchCriteria.time == tVal
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateTime(tVal) },
                                    label = { Text(tLabel, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KazakhAzure,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Passenger Count selector
                    Column {
                        Text(
                            text = Strings.passengersCount(isKazakh),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (p in 1..4) {
                                val isSelected = searchCriteria.passengersCount == p
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) NavyPrimary else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clickable { onUpdatePassengersCount(p) }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$p ${if (isKazakh) "адам" else "пасс."}",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Find Taxi Button
                    Button(
                        onClick = onPerformSearch,
                        colors = ButtonDefaults.buttonColors(containerColor = KazakhGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("find_taxi_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = NavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.findTaxiBtn(isKazakh),
                            color = NavyDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // "Қалай жұмыс істейді?" (How it works card)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = KazakhAzure,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.howItWorksTitle(isKazakh),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StepBubble("1", Strings.step1(isKazakh))
                        StepBubble("2", Strings.step2(isKazakh))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StepBubble("3", Strings.step3(isKazakh))
                        StepBubble("4", Strings.step4(isKazakh))
                    }
                }
            }
        }

        // Sorting & Results Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKazakh) "Қолжетімді таксилер (${availableTrips.size})" else "Доступные такси (${availableTrips.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavyDark
                    )
                }

                // Sort Chips: Nearest Time, Lowest Price, Most Seats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val sortOptions = listOf(
                        "NEAREST_TIME" to if (isKazakh) "Уақыты бойынша" else "По времени",
                        "LOWEST_PRICE" to if (isKazakh) "Арзан баға" else "Дешевле",
                        "MOST_SEATS" to if (isKazakh) "Көп бос орын" else "Больше мест"
                    )
                    sortOptions.forEach { (sortKey, label) ->
                        val isSelected = searchCriteria.sortBy == sortKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { onUpdateSortBy(sortKey) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavySecondary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Trips List
        if (availableTrips.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Empty",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isKazakh) "Таңдалған критерийлер бойынша бос такси табылмады" else "По выбранным критериям свободных такси не найдено",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isKazakh) "Басқа күнді немесе уақытты таңдап көріңіз" else "Попробуйте выбрать другую дату или время",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(availableTrips, key = { it.id }) { trip ->
                TripCard(
                    trip = trip,
                    isKazakh = isKazakh,
                    onBookClick = { onBookTrip(trip) }
                )
            }
        }
    }
}

@Composable
private fun StepBubble(number: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(160.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(KazakhAzure),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )
    }
}
