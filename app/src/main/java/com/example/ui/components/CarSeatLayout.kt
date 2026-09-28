package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.SeatEntity
import com.example.model.Strings
import com.example.ui.theme.*

@Composable
fun CarSeatLayout(
    capacity: Int,
    seats: List<SeatEntity>,
    selectedSeatNumbers: Set<Int>,
    onSeatClick: (Int) -> Unit,
    isKazakh: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(24.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Car Roof / Windshield Top indicator
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color(0xFFCBD5E1))
            )
            Text(
                text = if (isKazakh) "▲ Алдыңғы әйнек (Бағыты)" else "▲ Лобовое стекло (Вперед)",
                color = Color(0xFF64748B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Car Interior Layout Box
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // FRONT ROW: Driver + Seat 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Driver Seat
                        DriverSeatView(isKazakh)

                        // Passenger Seat 1
                        val seat1 = seats.find { it.seatNumber == 1 }
                        SeatItemView(
                            seatNumber = 1,
                            seat = seat1,
                            isSelected = selectedSeatNumbers.contains(1),
                            isKazakh = isKazakh,
                            onSeatClick = onSeatClick
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // REAR ROWS based on Capacity (4 or 6)
                    if (capacity == 4) {
                        // 4-Seater: 3 seats in back row (Seat 2, 3, 4)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (sNum in 2..4) {
                                val sEntity = seats.find { it.seatNumber == sNum }
                                SeatItemView(
                                    seatNumber = sNum,
                                    seat = sEntity,
                                    isSelected = selectedSeatNumbers.contains(sNum),
                                    isKazakh = isKazakh,
                                    onSeatClick = onSeatClick
                                )
                            }
                        }
                    } else {
                        // 6-Seater: Row 2 (Seats 2, 3) & Row 3 (Seats 4, 5, 6)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (sNum in 2..3) {
                                val sEntity = seats.find { it.seatNumber == sNum }
                                SeatItemView(
                                    seatNumber = sNum,
                                    seat = sEntity,
                                    isSelected = selectedSeatNumbers.contains(sNum),
                                    isKazakh = isKazakh,
                                    onSeatClick = onSeatClick
                                )
                            }
                        }

                        Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (sNum in 4..6) {
                                val sEntity = seats.find { it.seatNumber == sNum }
                                SeatItemView(
                                    seatNumber = sNum,
                                    seat = sEntity,
                                    isSelected = selectedSeatNumbers.contains(sNum),
                                    isKazakh = isKazakh,
                                    onSeatClick = onSeatClick
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Seat Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(
                    color = SeatAvailableGreen,
                    bg = SeatAvailableBg,
                    label = if (isKazakh) "Бос" else "Свободно"
                )
                LegendItem(
                    color = SeatSelectedBlue,
                    bg = SeatSelectedBg,
                    label = if (isKazakh) "Таңдалған" else "Выбрано"
                )
                LegendItem(
                    color = SeatOccupiedRed,
                    bg = SeatOccupiedBg,
                    label = if (isKazakh) "Бос емес" else "Занято"
                )
            }
        }
    }
}

@Composable
private fun DriverSeatView(isKazakh: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Driver",
                tint = Color(0xFF475569),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isKazakh) "Жүргізуші" else "Водитель",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun SeatItemView(
    seatNumber: Int,
    seat: SeatEntity?,
    isSelected: Boolean,
    isKazakh: Boolean,
    onSeatClick: (Int) -> Unit
) {
    val isOccupied = seat?.isOccupied == true

    val (bgColor, borderColor, contentColor) = when {
        isSelected -> Triple(SeatSelectedBlue, Color(0xFF1D4ED8), Color.White)
        isOccupied -> Triple(SeatOccupiedBg, Color(0xFFFCA5A5), Color(0xFF991B1B))
        else -> Triple(Color.White, SeatAvailableGreen, SeatAvailableGreen)
    }

    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected || !isOccupied) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("seat_btn_$seatNumber")
            .clickable(enabled = !isOccupied) {
                onSeatClick(seatNumber)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(4.dp)
        ) {
            when {
                isSelected -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                isOccupied -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Occupied",
                        tint = SeatOccupiedRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.EventSeat,
                        contentDescription = "Available",
                        tint = SeatAvailableGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = Strings.seatNumberLabel(isKazakh, seatNumber),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            if (isOccupied) {
                Text(
                    text = if (isKazakh) "Занято" else "Занято",
                    fontSize = 8.sp,
                    color = Color(0xFFB91C1C),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, bg: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(bg)
                .border(1.dp, color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Medium
        )
    }
}
