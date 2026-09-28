package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.theme.*

@Composable
fun TripCard(
    trip: TripEntity,
    isKazakh: Boolean,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxWidth()
            .testTag("trip_card_${trip.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Car info, License plate badge & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavyPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Car",
                            tint = NavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${trip.carMake} ${trip.carModel}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = trip.licensePlate,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                StatusBadge(
                    status = if (isFull) "FULL" else trip.status,
                    isKazakh = isKazakh
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Driver & Route details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Driver name & rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Driver",
                        tint = KazakhAzure,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${Strings.driverLabel(isKazakh)}: ",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = trip.driverName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }

                // Rating badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = KazakhGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "4.9",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Route & Date & Departure Time
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${Strings.routeLabel(isKazakh)} ",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = routeDisplay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NavyPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${Strings.dateLabel(isKazakh)}: ${trip.departureDate}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Time",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${Strings.departureTimeLabel(isKazakh)}: ",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = trip.departureTime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Pickup & Dropoff hints
            if (trip.pickupPoint.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trip.pickupPoint,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Available seats, Price & Booking Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seats and Price
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EventSeat,
                            contentDescription = "Seats",
                            tint = if (isFull) SeatOccupiedRed else SeatAvailableGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${Strings.availableSeatsLabel(isKazakh)}: ",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${trip.availableSeats} / ${trip.capacity}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFull) SeatOccupiedRed else SeatAvailableGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${trip.price} ₸",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyPrimary
                    )
                }

                // Booking button
                Button(
                    onClick = onBookClick,
                    enabled = !isFull,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KazakhAzure,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("book_trip_btn_${trip.id}")
                ) {
                    Text(
                        text = if (isFull) Strings.carFull(isKazakh) else Strings.bookSeatBtn(isKazakh),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isFull) Color(0xFF94A3B8) else Color.White
                    )
                }
            }
        }
    }
}
