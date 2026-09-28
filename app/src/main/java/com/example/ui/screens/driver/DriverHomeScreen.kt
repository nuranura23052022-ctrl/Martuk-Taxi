package com.example.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DriverHomeScreen(
    allDrivers: List<DriverEntity>,
    currentDriver: DriverEntity?,
    driverTrips: List<TripEntity>,
    isKazakh: Boolean,
    onSelectDriver: (String) -> Unit,
    onNavigateToTrips: () -> Unit,
    onOpenRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDriverMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Driver Switcher
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isKazakh) "Жүргізуші кабинеті" else "Кабинет водителя",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = currentDriver?.name ?: if (isKazakh) "Жүргізуші" else "Водитель",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        currentDriver?.let {
                            StatusBadge(status = it.status, isKazakh = isKazakh)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle Details
                    currentDriver?.let { driver ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = "Car",
                                        tint = KazakhGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${driver.carMake} ${driver.carModel}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${driver.licensePlate} • ${driver.capacity} орын",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = KazakhGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${driver.rating}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Driver selector dropdown & register button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Button(
                                onClick = { showDriverMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Switch", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isKazakh) "Жүргізушіні ауыстыру" else "Сменить водителя",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            DropdownMenu(
                                expanded = showDriverMenu,
                                onDismissRequest = { showDriverMenu = false }
                            ) {
                                allDrivers.forEach { drv ->
                                    DropdownMenuItem(
                                        text = {
                                            Text("${drv.name} (${drv.carMake} - ${drv.capacity} орын) [${drv.status}]")
                                        },
                                        onClick = {
                                            onSelectDriver(drv.id)
                                            showDriverMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = onOpenRegister,
                            colors = ButtonDefaults.buttonColors(containerColor = KazakhAzure),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Register", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isKazakh) "Тіркелу" else "Регистрация",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            val totalOccupied = driverTrips.sumOf { it.occupiedSeats }
            val totalCapacity = driverTrips.sumOf { it.capacity }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = if (isKazakh) "Рейстер" else "Рейсы",
                    value = "${driverTrips.size}",
                    color = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isKazakh) "Бос орын" else "Свободно",
                    value = "${totalCapacity - totalOccupied}",
                    color = SeatAvailableGreen,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (isKazakh) "Брондалған" else "Занято",
                    value = "$totalOccupied",
                    color = KazakhGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Next Trip Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isKazakh) "Келесі рейс" else "Ближайший рейс",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavyDark
                    )

                    TextButton(onClick = onNavigateToTrips) {
                        Text(if (isKazakh) "Барлық рейстер →" else "Все рейсы →", color = KazakhAzure)
                    }
                }

                val nextTrip = driverTrips.firstOrNull { it.status == "SCHEDULED" || it.status == "FULL" }
                if (nextTrip != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (nextTrip.route == "MARTUK_AKTOBE") "Мәртөк → Ақтөбе" else "Ақтөбе → Мәртөк",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NavyPrimary
                                )
                                StatusBadge(
                                    status = if (nextTrip.availableSeats <= 0) "FULL" else nextTrip.status,
                                    isKazakh = isKazakh
                                )
                            }

                            Text(
                                text = "${nextTrip.departureDate} • ${nextTrip.departureTime}",
                                fontSize = 13.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Орындар: ${nextTrip.occupiedSeats} / ${nextTrip.capacity} алынды (${nextTrip.availableSeats} бос)",
                                fontSize = 12.sp,
                                color = if (nextTrip.availableSeats <= 0) StatusFull else SeatAvailableGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isKazakh) "Белсенді рейстер жоқ. Жаңа рейс қосыңыз!" else "Нет активных рейсов. Добавьте новый рейс!",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
