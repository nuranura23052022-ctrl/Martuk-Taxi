package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Notifications
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
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.model.UserRole
import com.example.ui.theme.KazakhAzure
import com.example.ui.theme.KazakhGold
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentRole: UserRole,
    currentLanguage: AppLanguage,
    unreadNotificationsCount: Int,
    onRoleSelected: (UserRole) -> Unit,
    onToggleLanguage: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isKk = currentLanguage == AppLanguage.KAZAKH
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = NavyDark,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(KazakhGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Taxi",
                            tint = NavyDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.appTitle(isKk),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isKk) "Мәртөк ↔ Ақтөбе Такси" else "Мартук ↔ Актобе Такси",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Controls: Role switch, Lang toggle, Notifs
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Role Selector Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when (currentRole) {
                                UserRole.PASSENGER -> Color(0xFF1E293B)
                                UserRole.DRIVER -> Color(0xFF0369A1)
                                UserRole.ADMIN -> Color(0xFFB45309)
                            },
                            modifier = Modifier
                                .testTag("role_selector_btn")
                                .clickable { showRoleMenu = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = when (currentRole) {
                                        UserRole.PASSENGER -> "👤 " + Strings.rolePassenger(isKk)
                                        UserRole.DRIVER -> "🚕 " + Strings.roleDriver(isKk)
                                        UserRole.ADMIN -> "🛡️ " + Strings.roleAdmin(isKk)
                                    },
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("👤 " + Strings.rolePassenger(isKk)) },
                                onClick = {
                                    onRoleSelected(UserRole.PASSENGER)
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🚕 " + Strings.roleDriver(isKk)) },
                                onClick = {
                                    onRoleSelected(UserRole.DRIVER)
                                    showRoleMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🛡️ " + Strings.roleAdmin(isKk)) },
                                onClick = {
                                    onRoleSelected(UserRole.ADMIN)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }

                    // Language Toggle
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        modifier = Modifier
                            .testTag("lang_toggle_btn")
                            .clickable { onToggleLanguage() }
                    ) {
                        Text(
                            text = if (isKk) "🇰🇿 ҚАЗ" else "🇷🇺 РУС",
                            color = KazakhGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }

                    // Notification Icon with Badge
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("notifications_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White
                                    ) {
                                        Text(text = "$unreadNotificationsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Хабарламалар",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
