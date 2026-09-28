package com.example.ui.screens.passenger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.TripCard
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

@Composable
fun PassengerTripsScreen(
    allTrips: List<TripEntity>,
    isKazakh: Boolean,
    onBookTrip: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRouteFilter by remember { mutableStateOf("ALL") }

    val filtered = remember(allTrips, selectedRouteFilter) {
        allTrips.filter { trip ->
            val routeOk = when (selectedRouteFilter) {
                "MARTUK" -> trip.route == "MARTUK_AKTOBE"
                "AKTOBE" -> trip.route == "AKTOBE_MARTUK"
                else -> true
            }
            routeOk && trip.status != "CANCELLED"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
            Text(
                text = Strings.navTrips(isKazakh),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = if (isKazakh) "Мәртөк ↔ Ақтөбе бағытындағы барлық рейстер" else "Все рейсы по направлению Мартук ↔ Актобе",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }

        // Route Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                "ALL" to if (isKazakh) "Барлығы" else "Все",
                "MARTUK" to if (isKazakh) "Мәртөк → Ақтөбе" else "Мартук → Актобе",
                "AKTOBE" to if (isKazakh) "Ақтөбе → Мәртөк" else "Актобе → Мартук"
            )
            tabs.forEach { (key, label) ->
                val isSelected = selectedRouteFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedRouteFilter = key },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isKazakh) "Рейстер табылмады" else "Рейсов не найдено",
                    color = Color(0xFF64748B)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filtered, key = { it.id }) { trip ->
                    TripCard(
                        trip = trip,
                        isKazakh = isKazakh,
                        onBookClick = { onBookTrip(trip) }
                    )
                }
            }
        }
    }
}
