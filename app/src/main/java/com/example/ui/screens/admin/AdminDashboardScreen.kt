package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    trips: List<TripEntity>,
    drivers: List<DriverEntity>,
    bookings: List<BookingEntity>,
    isKazakh: Boolean,
    onEditTrip: (tripId: String, newPrice: Int, newTime: String) -> Unit,
    onCancelTrip: (tripId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRouteFilter by remember { mutableStateOf("ALL") }
    var tripToEdit by remember { mutableStateOf<TripEntity?>(null) }
    var tripToViewPassengers by remember { mutableStateOf<TripEntity?>(null) }

    // Stats calculations
    val todayTrips = trips.size
    val totalDrivers = drivers.size
    val totalPassengers = bookings.map { it.passengerPhone }.distinct().size
    val activeBookings = bookings.count { it.status == "CONFIRMED" }
    val availableSeats = trips.filter { it.status != "CANCELLED" && it.status != "COMPLETED" }.sumOf { it.availableSeats }
    val completedTrips = trips.count { it.status == "COMPLETED" }

    // Filter trips for real-time board
    val boardTrips = remember(trips, selectedRouteFilter) {
        trips.filter { trip ->
            when (selectedRouteFilter) {
                "MARTUK" -> trip.route == "MARTUK_AKTOBE"
                "AKTOBE" -> trip.route == "AKTOBE_MARTUK"
                else -> true
            }
        }.sortedBy { it.departureTime }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = Strings.adminDashboard(isKazakh),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Text(
                    text = if (isKazakh) "Мәртөк ↔ Ақтөбе бағытының орталық диспетчерлік жүйесі" else "Центральная диспетчерская система Мартук ↔ Актобе",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Dashboard Statistics (6 key metrics)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBox(
                        title = Strings.todaysTrips(isKazakh),
                        value = "$todayTrips",
                        color = NavyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = Strings.totalDrivers(isKazakh),
                        value = "$totalDrivers",
                        color = KazakhAzure,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = Strings.totalPassengers(isKazakh),
                        value = "$totalPassengers",
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBox(
                        title = Strings.activeBookings(isKazakh),
                        value = "$activeBookings",
                        color = KazakhGold,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = Strings.availableSeatsStat(isKazakh),
                        value = "$availableSeats",
                        color = SeatAvailableGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = Strings.completedTrips(isKazakh),
                        value = "$completedTrips",
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Real-time Dispatcher Board (Requirement 17)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.realTimeTripBoard(isKazakh),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavyDark
                    )

                    // Route filter
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = selectedRouteFilter == "ALL",
                            onClick = { selectedRouteFilter = "ALL" },
                            label = { Text(if (isKazakh) "Барлығы" else "Все", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = selectedRouteFilter == "MARTUK",
                            onClick = { selectedRouteFilter = "MARTUK" },
                            label = { Text("Мәртөк→", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = selectedRouteFilter == "AKTOBE",
                            onClick = { selectedRouteFilter = "AKTOBE" },
                            label = { Text("Ақтөбе→", fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        // Real-Time Trip Board List
        items(boardTrips, key = { it.id }) { trip ->
            DispatcherBoardItem(
                trip = trip,
                isKazakh = isKazakh,
                onEdit = { tripToEdit = trip },
                onViewPassengers = { tripToViewPassengers = trip }
            )
        }
    }

    // Edit Trip Price & Time Modal
    tripToEdit?.let { t ->
        EditTripDialog(
            trip = t,
            isKazakh = isKazakh,
            onDismiss = { tripToEdit = null },
            onSave = { newPrice, newTime ->
                onEditTrip(t.id, newPrice, newTime)
                tripToEdit = null
            },
            onCancelTrip = {
                onCancelTrip(t.id)
                tripToEdit = null
            }
        )
    }

    // Trip Passengers Sheet
    tripToViewPassengers?.let { t ->
        val tripBookings = bookings.filter { it.tripId == t.id }
        AdminTripPassengersDialog(
            trip = t,
            bookings = tripBookings,
            isKazakh = isKazakh,
            onDismiss = { tripToViewPassengers = null }
        )
    }
}

@Composable
private fun MetricBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

/**
 * Requirement 17 Dispatcher Board Card:
 * МӘРТӨК → АҚТӨБЕ
 * 07:00 | Аян | Toyota | 4/4 FULL
 * 08:00 | Ерлан | Hyundai | 2/4 2 BOS
 */
@Composable
private fun DispatcherBoardItem(
    trip: TripEntity,
    isKazakh: Boolean,
    onEdit: () -> Unit,
    onViewPassengers: () -> Unit
) {
    val isFull = trip.availableSeats <= 0 || trip.status == "FULL"
    val routeDisplay = if (trip.route == "MARTUK_AKTOBE") "МӘРТӨК → АҚТӨБЕ" else "АҚТӨБЕ → МӘРТӨК"

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Route header
                Text(
                    text = routeDisplay,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Time, Driver, Car, Ratio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = trip.departureTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDark
                    )

                    Text(
                        text = "• ${trip.driverName}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Text(
                        text = "(${trip.carMake})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Seat Ratio and BOS / FULL status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${trip.occupiedSeats} / ${trip.capacity}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFull) StatusFull else NavyDark
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isFull) SeatOccupiedBg else SeatAvailableBg
                    ) {
                        Text(
                            text = if (isFull) {
                                "FULL"
                            } else {
                                "${trip.availableSeats} BOS"
                            },
                            color = if (isFull) StatusFull else SeatAvailableGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "${trip.price} ₸",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = KazakhGold
                    )
                }
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onViewPassengers,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(Icons.Default.Groups, contentDescription = "Passengers", tint = NavyPrimary, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = KazakhAzure, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun EditTripDialog(
    trip: TripEntity,
    isKazakh: Boolean,
    onDismiss: () -> Unit,
    onSave: (price: Int, time: String) -> Unit,
    onCancelTrip: () -> Unit
) {
    var priceState by remember { mutableStateOf("${trip.price}") }
    var timeState by remember { mutableStateOf(trip.departureTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isKazakh) "Рейсті өңдеу" else "Редактировать рейс",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${trip.driverName} (${trip.carMake} ${trip.carModel})",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = timeState,
                    onValueChange = { timeState = it },
                    label = { Text(Strings.departureTimeLabel(isKazakh)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceState,
                    onValueChange = { priceState = it },
                    label = { Text(Strings.priceLabel(isKazakh) + " (₸)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = onCancelTrip,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isKazakh) "Рейсті тоқтату (Cancel)" else "Отменить рейс")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceState.toIntOrNull() ?: trip.price
                    onSave(p, timeState)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isKazakh) "Сақтау" else "Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isKazakh) "Жабу" else "Закрыть")
            }
        }
    )
}

@Composable
private fun AdminTripPassengersDialog(
    trip: TripEntity,
    bookings: List<BookingEntity>,
    isKazakh: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "${trip.driverName} — ${trip.departureTime}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Орындар: ${trip.occupiedSeats} / ${trip.capacity} толтырылды",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                if (bookings.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(if (isKazakh) "Жолаушылар жоқ" else "Нет пассажиров", color = Color(0xFF94A3B8))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(bookings) { b ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = b.passengerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "${b.passengerPhone} • Орын: ${b.seatNumbers}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    StatusBadge(status = b.status, isKazakh = isKazakh)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isKazakh) "Жабу" else "Закрыть")
            }
        }
    )
}
