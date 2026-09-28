package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminBookingsScreen(
    bookings: List<BookingEntity>,
    isKazakh: Boolean,
    onCancelBooking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("ALL") }

    val filteredBookings = remember(bookings, statusFilter) {
        when (statusFilter) {
            "CONFIRMED" -> bookings.filter { it.status == "CONFIRMED" }
            "CANCELLED" -> bookings.filter { it.status == "CANCELLED" }
            "COMPLETED" -> bookings.filter { it.status == "COMPLETED" }
            else -> bookings
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
                text = Strings.navMyBookings(isKazakh),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = if (isKazakh) "Барлық брондаулар мен төлемдер тарихы" else "Все бронирования и журнал заказов",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text(if (isKazakh) "Барлығы (${bookings.size})" else "Все (${bookings.size})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = statusFilter == "CONFIRMED",
                onClick = { statusFilter = "CONFIRMED" },
                label = { Text(if (isKazakh) "Белсенді" else "Активные", fontSize = 11.sp) }
            )
            FilterChip(
                selected = statusFilter == "CANCELLED",
                onClick = { statusFilter = "CANCELLED" },
                label = { Text(if (isKazakh) "Жойылған" else "Отмененные", fontSize = 11.sp) }
            )
        }

        if (filteredBookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(if (isKazakh) "Броньдар жоқ" else "Нет броней", color = Color(0xFF64748B))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filteredBookings, key = { it.id }) { booking ->
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
                                    text = booking.id,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = NavyPrimary
                                )
                                StatusBadge(status = booking.status, isKazakh = isKazakh)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${booking.passengerName} (${booking.passengerPhone})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${booking.totalPrice} ₸",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KazakhGold
                                )
                            }

                            Text(
                                text = "Рейс: ${booking.driverName} • ${booking.departureDate} ${booking.departureTime} • Орын: ${booking.seatNumbers}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            if (booking.status == "CONFIRMED") {
                                OutlinedButton(
                                    onClick = { onCancelBooking(booking.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(if (isKazakh) "Броньды жою" else "Отменить бронь", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
