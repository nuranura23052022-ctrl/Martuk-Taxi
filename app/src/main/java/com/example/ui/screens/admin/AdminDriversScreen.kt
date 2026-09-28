package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DriverEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDriversScreen(
    drivers: List<DriverEntity>,
    isKazakh: Boolean,
    onApproveDriver: (String) -> Unit,
    onRejectDriver: (String) -> Unit,
    onSuspendDriver: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("ALL") }

    val filteredDrivers = remember(drivers, statusFilter) {
        when (statusFilter) {
            "PENDING" -> drivers.filter { it.status == "PENDING" }
            "APPROVED" -> drivers.filter { it.status == "APPROVED" }
            else -> drivers
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
                text = Strings.navDrivers(isKazakh),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = if (isKazakh) "Жүргізушілерді тексеру, мақұлдау және басқару" else "Верификация, одобрение и управление водителями",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pendingCount = drivers.count { it.status == "PENDING" }
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text(if (isKazakh) "Барлығы (${drivers.size})" else "Все (${drivers.size})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = statusFilter == "PENDING",
                onClick = { statusFilter = "PENDING" },
                label = {
                    Text(
                        text = if (isKazakh) "Тексерілуде ($pendingCount)" else "На проверке ($pendingCount)",
                        fontSize = 11.sp,
                        color = if (pendingCount > 0) Color(0xFFD97706) else TextPrimary
                    )
                }
            )
            FilterChip(
                selected = statusFilter == "APPROVED",
                onClick = { statusFilter = "APPROVED" },
                label = { Text(if (isKazakh) "Мақұлданған" else "Одобренные", fontSize = 11.sp) }
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredDrivers, key = { it.id }) { driver ->
                DriverManagementCard(
                    driver = driver,
                    isKazakh = isKazakh,
                    onApprove = { onApproveDriver(driver.id) },
                    onReject = { onRejectDriver(driver.id) },
                    onSuspend = { onSuspendDriver(driver.id) }
                )
            }
        }
    }
}

@Composable
private fun DriverManagementCard(
    driver: DriverEntity,
    isKazakh: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onSuspend: () -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = driver.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${driver.phone})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                StatusBadge(status = driver.status, isKazakh = isKazakh)
            }

            // Car & Capacity details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = "Car", tint = KazakhAzure, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${driver.carMake} ${driver.carModel} • ${driver.licensePlate}",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${driver.capacity} орын",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            Divider(color = Color(0xFFF1F5F9))

            // Admin Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (driver.status == "PENDING") {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = SeatAvailableGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("admin_approve_driver_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Strings.approveDriver(isKazakh), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Strings.rejectDriver(isKazakh), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (driver.status == "APPROVED") {
                    OutlinedButton(
                        onClick = onSuspend,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(if (isKazakh) "Тоқтата тұру" else "Приостановить", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = SeatAvailableGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(if (isKazakh) "Қайта белсендіру" else "Активировать", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
