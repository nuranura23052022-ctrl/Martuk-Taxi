package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun AdminPassengersScreen(
    bookings: List<BookingEntity>,
    allTrips: List<TripEntity>,
    isKazakh: Boolean,
    onMovePassenger: (bookingId: String, targetTripId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var bookingToMove by remember { mutableStateOf<BookingEntity?>(null) }

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
                text = if (isKazakh) "Жолаушыларды көліктер арасында ауыстыру және басқару" else "Пересадка пассажиров между автомобилями и управление",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(if (isKazakh) "Жолаушылар табылмады" else "Пассажиров не найдено", color = Color(0xFF64748B))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(bookings, key = { it.id }) { booking ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = booking.passengerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${booking.passengerPhone} • Бронь: ${booking.id}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                StatusBadge(status = booking.status, isKazakh = isKazakh)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Қазіргі рейс: ${booking.driverName} (${booking.carInfo})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NavyPrimary
                                    )
                                    Text(
                                        text = "${booking.departureDate} ${booking.departureTime} • Орын(дар): ${booking.seatNumbers}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            if (booking.status == "CONFIRMED") {
                                Button(
                                    onClick = { bookingToMove = booking },
                                    colors = ButtonDefaults.buttonColors(containerColor = KazakhAzure),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .testTag("move_passenger_btn_${booking.id}")
                                ) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = "Move", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Strings.movePassenger(isKazakh),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Move Passenger Modal Dialog
    bookingToMove?.let { b ->
        val candidateTrips = allTrips.filter { it.id != b.tripId && it.availableSeats >= b.seatsCount && it.status != "CANCELLED" }
        AlertDialog(
            onDismissRequest = { bookingToMove = null },
            title = {
                Text(
                    text = "${b.passengerName} жолаушысын ауыстыру",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isKazakh) 
                            "Жолаушыны (${b.seatsCount} орын) ауыстыратын жаңа көлікті таңдаңыз:"
                            else "Выберите другой автомобиль для пересадки (${b.seatsCount} мест):",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    if (candidateTrips.isEmpty()) {
                        Text(
                            text = if (isKazakh) "Жеткілікті бос орны бар басқа рейс жоқ!" else "Нет других рейсов с достаточным количеством мест!",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                            items(candidateTrips) { targetTrip ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${targetTrip.driverName} (${targetTrip.carMake})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${targetTrip.departureTime} • Бос орын: ${targetTrip.availableSeats}",
                                                fontSize = 11.sp,
                                                color = SeatAvailableGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                onMovePassenger(b.id, targetTrip.id)
                                                bookingToMove = null
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(if (isKazakh) "Таңдау" else "Выбрать", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { bookingToMove = null }) {
                    Text(if (isKazakh) "Бас тарту" else "Отмена")
                }
            }
        )
    }
}
