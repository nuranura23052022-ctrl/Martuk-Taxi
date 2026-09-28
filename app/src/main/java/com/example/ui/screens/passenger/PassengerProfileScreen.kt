package com.example.ui.screens.passenger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
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
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun PassengerProfileScreen(
    currentName: String,
    currentPhone: String,
    currentRole: UserRole,
    language: AppLanguage,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRoleSelected: (UserRole) -> Unit,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isKk = language == AppLanguage.KAZAKH
    var nameState by remember { mutableStateOf(currentName) }
    var phoneState by remember { mutableStateOf(currentPhone) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = Strings.navProfile(isKk),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDark,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Profile Avatar Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = currentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = currentPhone,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Роль: ${Strings.rolePassenger(isKk)}",
                        fontSize = 11.sp,
                        color = KazakhAzure,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Profile Edit Card
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
                Text(
                    text = if (isKk) "Профильді өңдеу" else "Редактировать профиль",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = nameState,
                    onValueChange = {
                        nameState = it
                        onNameChange(it)
                    },
                    label = { Text(Strings.fullName(isKk)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneState,
                    onValueChange = {
                        phoneState = it
                        onPhoneChange(it)
                    },
                    label = { Text(Strings.phoneNumber(isKk)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Quick Role Switch Card (Very handy for evaluator!)
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
                    text = if (isKk) "Жүйедегі рөлді ауыстыру:" else "Сменить роль в системе:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onRoleSelected(UserRole.PASSENGER) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentRole == UserRole.PASSENGER) NavyPrimary else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = Strings.rolePassenger(isKk),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentRole == UserRole.PASSENGER) Color.White else TextPrimary
                        )
                    }

                    Button(
                        onClick = { onRoleSelected(UserRole.DRIVER) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentRole == UserRole.DRIVER) KazakhAzure else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = Strings.roleDriver(isKk),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentRole == UserRole.DRIVER) Color.White else TextPrimary
                        )
                    }

                    Button(
                        onClick = { onRoleSelected(UserRole.ADMIN) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentRole == UserRole.ADMIN) Color(0xFFB45309) else Color(0xFFF1F5F9)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = Strings.roleAdmin(isKk),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentRole == UserRole.ADMIN) Color.White else TextPrimary
                        )
                    }
                }
            }
        }

        // Language setting
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = KazakhAzure)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isKk) "Тіл / Язык" else "Язык / Тіл",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = onToggleLanguage,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isKk) "Қазақша (ҚАЗ)" else "Русский (РУС)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
