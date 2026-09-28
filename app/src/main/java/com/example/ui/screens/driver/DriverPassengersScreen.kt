package com.example.ui.screens.driver

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DriverPassengersScreen(
    trips: List<TripEntity>,
    bookings: List<BookingEntity>,
    isKazakh: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTripId by remember(trips) {
        mutableStateOf(trips.firstOrNull()?.id ?: "")
    }

    val currentTrip = trips.find { it.id == selectedTripId }
    val tripBookings = bookings.filter { it.tripId == selectedTripId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
            Text(
                text = Strings.navPassengers(isKazakh),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = if (isKazakh) "Рейс бойынша жолаушылар тізімі мен байланыс" else "Список пассажиров по рейсам и связь",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Trip Selector Dropdown / Chips
        if (trips.isNotEmpty()) {
            Text(
                text = if (isKazakh) "Рейсті таңдаңыз:" else "Выберите рейс:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            ScrollableTabRow(
                selectedTabIndex = trips.indexOfFirst { it.id == selectedTripId }.coerceAtLeast(0),
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {},
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                trips.forEach { trip ->
                    val isSelected = trip.id == selectedTripId
                    val routeStr = if (trip.route == "MARTUK_AKTOBE") "Мәртөк→Ақтөбе" else "Ақтөбе→Мәртөк"
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTripId = trip.id },
                        text = {
                            Text(
                                text = "$routeStr (${trip.departureTime})",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NavyPrimary else TextSecondary
                            )
                        }
                    )
                }
            }
        }

        currentTrip?.let { trip ->
            // REIS Header Box as requested in requirement 6:
            // REIS: Мәртөк → Ақтөбе, 28.09.2026 | 08:00, Total: 4 / 4 seats occupied
            val routeDisplay = if (trip.route == "MARTUK_AKTOBE") "Мәртөк → Ақтөбе" else "Ақтөбе → Мәртөк"

            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REIS: $routeDisplay",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        StatusBadge(status = if (trip.availableSeats <= 0) "FULL" else trip.status, isKazakh = isKazakh)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${trip.departureDate} | ${trip.departureTime}",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total: ${trip.occupiedSeats} / ${trip.capacity} seats occupied",
                        color = KazakhGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            if (tripBookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isKazakh) "Бұл рейсте әзірге жолаушылар жоқ" else "На этом рейсе пока нет пассажиров",
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(tripBookings.withIndex().toList()) { (index, booking) ->
                        PassengerListItem(
                            index = index + 1,
                            booking = booking,
                            isKazakh = isKazakh,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${booking.passengerPhone}")
                                }
                                context.startActivity(intent)
                            },
                            onWhatsApp = {
                                val cleanPhone = booking.passengerPhone.replace("[^0-9]".toRegex(), "")
                                val url = "https://wa.me/$cleanPhone?text=Сәлеметсіз бе! Такси жүргізушісі."
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        } ?: run {
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
        }
    }
}

@Composable
private fun PassengerListItem(
    index: Int,
    booking: BookingEntity,
    isKazakh: Boolean,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$index. ${booking.passengerName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "— ${booking.seatsCount} орын (Орын: ${booking.seatNumbers})",
                        fontSize = 12.sp,
                        color = NavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = booking.passengerPhone,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                if (booking.pickupPoint.isNotBlank()) {
                    Text(
                        text = "Отырғызу: ${booking.pickupPoint}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Quick Call & WhatsApp
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavyPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = NavyPrimary, modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = onWhatsApp,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("WA", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
