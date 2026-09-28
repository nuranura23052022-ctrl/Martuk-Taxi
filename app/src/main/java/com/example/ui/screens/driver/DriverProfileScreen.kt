package com.example.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DriverEntity
import com.example.model.Strings
import com.example.model.UserRole
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun DriverProfileScreen(
    driver: DriverEntity?,
    isKazakh: Boolean,
    onRoleSelected: (UserRole) -> Unit,
    onRegisterNewDriver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = Strings.navProfile(isKazakh),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDark,
            modifier = Modifier.padding(top = 8.dp)
        )

        driver?.let { d ->
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(KazakhAzure),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Avatar", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = d.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Text(text = d.phone, fontSize = 13.sp, color = TextSecondary)
                            }
                        }

                        StatusBadge(status = d.status, isKazakh = isKazakh)
                    }

                    Divider(color = Color(0xFFF1F5F9))

                    // Car details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Автокөлік:", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "${d.carMake} ${d.carModel}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyPrimary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Мемл. нөмірі:", fontSize = 12.sp, color = TextSecondary)
                            Text(text = d.licensePlate, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Сыйымдылық:", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "${d.capacity} орын", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Рейтинг:", fontSize = 12.sp, color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "Star", tint = KazakhGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${d.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Switch role
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isKazakh) "Рөлді ауыстыру:" else "Сменить роль:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onRoleSelected(UserRole.PASSENGER) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Strings.rolePassenger(isKazakh), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onRoleSelected(UserRole.ADMIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Strings.roleAdmin(isKazakh), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
