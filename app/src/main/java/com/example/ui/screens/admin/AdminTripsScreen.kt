package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun AdminTripsScreen(
    trips: List<TripEntity>,
    drivers: List<DriverEntity>,
    isKazakh: Boolean,
    onCreateTrip: (driver: DriverEntity, route: String, date: String, time: String, price: Int, pickup: String, dropoff: String) -> Unit,
    onEditTrip: (tripId: String, price: Int, time: String) -> Unit,
    onCancelTrip: (tripId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.navTrips(isKazakh),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Text(
                    text = if (isKazakh) "Рейстерді қолмен қосу және басқару" else "Создание и управление рейсами",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = KazakhAzure),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("admin_add_trip_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isKazakh) "+ Жаңа рейс" else "+ Новый рейс", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(trips, key = { it.id }) { trip ->
                val isFull = trip.availableSeats <= 0 || trip.status == "FULL"
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (trip.route == "MARTUK_AKTOBE") "Мәртөк → Ақтөбе" else "Ақтөбе → Мәртөк",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NavyPrimary
                            )
                            StatusBadge(status = if (isFull) "FULL" else trip.status, isKazakh = isKazakh)
                        }

                        Text(
                            text = "${trip.driverName} (${trip.carMake} ${trip.carModel}, ${trip.licensePlate})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${trip.departureDate} • ${trip.departureTime}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Орындар: ${trip.occupiedSeats} / ${trip.capacity} (${trip.availableSeats} бос)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFull) StatusFull else SeatAvailableGreen
                            )
                        }

                        Text(
                            text = "Бағасы: ${trip.price} ₸",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = KazakhGold
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        val approvedDrivers = drivers.filter { it.status == "APPROVED" }
        AdminCreateTripDialog(
            approvedDrivers = approvedDrivers,
            isKazakh = isKazakh,
            onDismiss = { showCreateDialog = false },
            onCreate = { driver, route, date, time, price, pickup, dropoff ->
                onCreateTrip(driver, route, date, time, price, pickup, dropoff)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun AdminCreateTripDialog(
    approvedDrivers: List<DriverEntity>,
    isKazakh: Boolean,
    onDismiss: () -> Unit,
    onCreate: (driver: DriverEntity, route: String, date: String, time: String, price: Int, pickup: String, dropoff: String) -> Unit
) {
    var selectedDriverId by remember { mutableStateOf(approvedDrivers.firstOrNull()?.id ?: "") }
    var routeState by remember { mutableStateOf("MARTUK_AKTOBE") }
    var dateState by remember { mutableStateOf("28 қыркүйек") }
    var timeState by remember { mutableStateOf("08:00") }
    var priceState by remember { mutableStateOf("2500") }
    var pickupState by remember { mutableStateOf("Мәртөк: Орталық автобекет") }
    var dropoffState by remember { mutableStateOf("Ақтөбе: Автовокзал") }

    val driver = approvedDrivers.find { it.id == selectedDriverId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isKazakh) "Рейсті қолмен құру" else "Создать рейс вручную",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Жүргізушіні таңдаңыз:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                approvedDrivers.forEach { drv ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedDriverId == drv.id,
                            onClick = { selectedDriverId = drv.id }
                        )
                        Text(
                            text = "${drv.name} (${drv.carMake} - ${drv.capacity} орын)",
                            fontSize = 12.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = routeState == "MARTUK_AKTOBE",
                        onClick = { routeState = "MARTUK_AKTOBE" },
                        label = { Text("Мәртөк → Ақтөбе", fontSize = 10.sp) }
                    )
                    FilterChip(
                        selected = routeState == "AKTOBE_MARTUK",
                        onClick = { routeState = "AKTOBE_MARTUK" },
                        label = { Text("Ақтөбе → Мәртөк", fontSize = 10.sp) }
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    driver?.let {
                        val p = priceState.toIntOrNull() ?: 2500
                        onCreate(it, routeState, dateState, timeState, p, pickupState, dropoffState)
                    }
                },
                enabled = driver != null,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text(if (isKazakh) "Құру" else "Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isKazakh) "Бас тарту" else "Отмена")
            }
        }
    )
}
