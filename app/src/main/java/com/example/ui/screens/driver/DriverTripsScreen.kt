package com.example.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DriverTripsScreen(
    currentDriver: DriverEntity?,
    trips: List<TripEntity>,
    isKazakh: Boolean,
    onCreateTrip: (route: String, date: String, time: String, price: Int, pickup: String, dropoff: String) -> Unit,
    onUpdateTripStatus: (tripId: String, status: String) -> Unit,
    onViewTripPassengers: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddTripDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
    ) {
        // Header with "+ Жаңа рейс қосу"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.myTrips(isKazakh),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Text(
                    text = "${currentDriver?.name ?: ""} (${currentDriver?.carMake} ${currentDriver?.carModel})",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { showAddTripDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = KazakhAzure),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(40.dp)
                    .testTag("add_new_trip_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = Strings.addNewTripBtn(isKazakh),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Driver Pending Approval Banner
        if (currentDriver?.status == "PENDING") {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isKazakh) 
                            "Сіздің жүргізуші аккаунтыңыз тексерілуде. Диспетчер мақұлдағаннан кейін жаңа рейстер қосу мүмкіндігі ашылады."
                            else "Ваш аккаунт водителя на проверке. После одобрения диспетчером появится возможность добавлять рейсы.",
                        fontSize = 12.sp,
                        color = Color(0xFF92400E)
                    )
                }
            }
        }

        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isKazakh) "Сізде әзірге рейстер жоқ" else "У вас пока нет рейсов",
                    color = Color(0xFF64748B)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(trips, key = { it.id }) { trip ->
                    DriverTripCardItem(
                        trip = trip,
                        isKazakh = isKazakh,
                        onViewPassengers = { onViewTripPassengers(trip) },
                        onUpdateStatus = { newStatus -> onUpdateTripStatus(trip.id, newStatus) }
                    )
                }
            }
        }
    }

    if (showAddTripDialog) {
        AddTripModalDialog(
            capacity = currentDriver?.capacity ?: 4,
            isKazakh = isKazakh,
            onDismiss = { showAddTripDialog = false },
            onConfirm = { route, date, time, price, pickup, dropoff ->
                onCreateTrip(route, date, time, price, pickup, dropoff)
                showAddTripDialog = false
            }
        )
    }
}

@Composable
private fun DriverTripCardItem(
    trip: TripEntity,
    isKazakh: Boolean,
    onViewPassengers: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    val isFull = trip.availableSeats <= 0 || trip.status == "FULL"
    val routeDisplay = if (trip.route == "MARTUK_AKTOBE") {
        if (isKazakh) "Мәртөк → Ақтөбе" else "Мартук → Актобе"
    } else {
        if (isKazakh) "Ақтөбе → Мәртөк" else "Актобе → Мартук"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row: Route & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routeDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = NavyPrimary
                )

                StatusBadge(
                    status = if (isFull) "FULL" else trip.status,
                    isKazakh = isKazakh
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${trip.departureDate} • ${trip.departureTime}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Occupancy Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.seatsOccupied(isKazakh, trip.occupiedSeats, trip.capacity),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFull) StatusFull else SeatAvailableGreen
                )

                Text(
                    text = "${trip.price} ₸ / орын",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KazakhGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewPassengers,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Icon(Icons.Default.Groups, contentDescription = "Passengers", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isKazakh) "Жолаушылар (${trip.occupiedSeats})" else "Пассажиры (${trip.occupiedSeats})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (trip.status == "SCHEDULED" || trip.status == "FULL") {
                    Button(
                        onClick = { onUpdateStatus("DEPARTED") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = if (isKazakh) "Шығу" else "Выезд",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (trip.status == "DEPARTED") {
                    Button(
                        onClick = { onUpdateStatus("COMPLETED") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = if (isKazakh) "Аяқтау" else "Завершить",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTripModalDialog(
    capacity: Int,
    isKazakh: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (route: String, date: String, time: String, price: Int, pickup: String, dropoff: String) -> Unit
) {
    var routeState by remember { mutableStateOf("MARTUK_AKTOBE") }
    var dateState by remember { mutableStateOf("28 қыркүйек") }
    var timeState by remember { mutableStateOf("08:00") }
    var priceState by remember { mutableStateOf("2500") }
    var pickupState by remember { mutableStateOf("Мәртөк: Орталық автобекет") }
    var dropoffState by remember { mutableStateOf("Ақтөбе: Автовокзал") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Strings.addNewTripBtn(isKazakh),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = Strings.routeLabel(isKazakh),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = routeState == "MARTUK_AKTOBE",
                        onClick = {
                            routeState = "MARTUK_AKTOBE"
                            pickupState = "Мәртөк: Орталық автобекет"
                            dropoffState = "Ақтөбе: Автовокзал"
                        },
                        label = { Text("Мәртөк → Ақтөбе", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = routeState == "AKTOBE_MARTUK",
                        onClick = {
                            routeState = "AKTOBE_MARTUK"
                            pickupState = "Ақтөбе: Автовокзал"
                            dropoffState = "Мәртөк: Орталық автобекет"
                        },
                        label = { Text("Ақтөбе → Мәртөк", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = dateState,
                    onValueChange = { dateState = it },
                    label = { Text(Strings.dateLabel(isKazakh)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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

                OutlinedTextField(
                    value = pickupState,
                    onValueChange = { pickupState = it },
                    label = { Text(if (isKazakh) "Отырғызу орны" else "Место посадки") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Info note about capacity
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isKazakh) 
                            "Көлік сыйымдылығы ($capacity орын) сіздің профиліңізден алынады және жүйе автоматты түрде $capacity бос орын жасайды."
                            else "Вместимость авто ($capacity мест) берется из вашего профиля. Система автоматически создаст $capacity свободных мест.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceState.toIntOrNull() ?: 2500
                    onConfirm(routeState, dateState, timeState, p, pickupState, dropoffState)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("submit_new_trip_btn")
            ) {
                Text(if (isKazakh) "Қосу" else "Добавить", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isKazakh) "Бас тарту" else "Отмена")
            }
        }
    )
}
