package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.model.UserRole
import com.example.ui.theme.KazakhGold
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

sealed class NavItem(val route: String, val icon: ImageVector) {
    // Passenger
    object PassengerHome : NavItem("p_home", Icons.Default.Home)
    object PassengerTrips : NavItem("p_trips", Icons.Default.DirectionsCar)
    object PassengerBookings : NavItem("p_bookings", Icons.Default.ConfirmationNumber)
    object PassengerProfile : NavItem("p_profile", Icons.Default.Person)

    // Driver
    object DriverHome : NavItem("d_home", Icons.Default.Home)
    object DriverTrips : NavItem("d_trips", Icons.Default.DirectionsCar)
    object DriverPassengers : NavItem("d_passengers", Icons.Default.Groups)
    object DriverProfile : NavItem("d_profile", Icons.Default.Person)

    // Admin
    object AdminDashboard : NavItem("a_dashboard", Icons.Default.Dashboard)
    object AdminTrips : NavItem("a_trips", Icons.Default.DirectionsCar)
    object AdminDrivers : NavItem("a_drivers", Icons.Default.SupervisorAccount)
    object AdminPassengers : NavItem("a_passengers", Icons.Default.Groups)
    object AdminBookings : NavItem("a_bookings", Icons.Default.ConfirmationNumber)
}

@Composable
fun BottomNavBar(
    currentRole: UserRole,
    currentTab: String,
    language: AppLanguage,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isKk = language == AppLanguage.KAZAKH

    NavigationBar(
        containerColor = NavyDark,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        when (currentRole) {
            UserRole.PASSENGER -> {
                NavigationBarItem(
                    selected = currentTab == NavItem.PassengerHome.route,
                    onClick = { onTabSelected(NavItem.PassengerHome.route) },
                    icon = { Icon(NavItem.PassengerHome.icon, contentDescription = "Home") },
                    label = { Text(Strings.navHome(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_passenger_home")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.PassengerTrips.route,
                    onClick = { onTabSelected(NavItem.PassengerTrips.route) },
                    icon = { Icon(NavItem.PassengerTrips.icon, contentDescription = "Trips") },
                    label = { Text(Strings.navTrips(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_passenger_trips")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.PassengerBookings.route,
                    onClick = { onTabSelected(NavItem.PassengerBookings.route) },
                    icon = { Icon(NavItem.PassengerBookings.icon, contentDescription = "Bookings") },
                    label = { Text(Strings.navMyBookings(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_passenger_bookings")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.PassengerProfile.route,
                    onClick = { onTabSelected(NavItem.PassengerProfile.route) },
                    icon = { Icon(NavItem.PassengerProfile.icon, contentDescription = "Profile") },
                    label = { Text(Strings.navProfile(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_passenger_profile")
                )
            }
            UserRole.DRIVER -> {
                NavigationBarItem(
                    selected = currentTab == NavItem.DriverHome.route,
                    onClick = { onTabSelected(NavItem.DriverHome.route) },
                    icon = { Icon(NavItem.DriverHome.icon, contentDescription = "Home") },
                    label = { Text(Strings.navHome(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_driver_home")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.DriverTrips.route,
                    onClick = { onTabSelected(NavItem.DriverTrips.route) },
                    icon = { Icon(NavItem.DriverTrips.icon, contentDescription = "My Trips") },
                    label = { Text(Strings.myTrips(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_driver_trips")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.DriverPassengers.route,
                    onClick = { onTabSelected(NavItem.DriverPassengers.route) },
                    icon = { Icon(NavItem.DriverPassengers.icon, contentDescription = "Passengers") },
                    label = { Text(Strings.navPassengers(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_driver_passengers")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.DriverProfile.route,
                    onClick = { onTabSelected(NavItem.DriverProfile.route) },
                    icon = { Icon(NavItem.DriverProfile.icon, contentDescription = "Profile") },
                    label = { Text(Strings.navProfile(isKk), fontSize = 11.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_driver_profile")
                )
            }
            UserRole.ADMIN -> {
                NavigationBarItem(
                    selected = currentTab == NavItem.AdminDashboard.route,
                    onClick = { onTabSelected(NavItem.AdminDashboard.route) },
                    icon = { Icon(NavItem.AdminDashboard.icon, contentDescription = "Dashboard") },
                    label = { Text(Strings.navDashboard(isKk), fontSize = 10.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_admin_dashboard")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.AdminTrips.route,
                    onClick = { onTabSelected(NavItem.AdminTrips.route) },
                    icon = { Icon(NavItem.AdminTrips.icon, contentDescription = "Trips") },
                    label = { Text(Strings.navTrips(isKk), fontSize = 10.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_admin_trips")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.AdminDrivers.route,
                    onClick = { onTabSelected(NavItem.AdminDrivers.route) },
                    icon = { Icon(NavItem.AdminDrivers.icon, contentDescription = "Drivers") },
                    label = { Text(Strings.navDrivers(isKk), fontSize = 10.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_admin_drivers")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.AdminPassengers.route,
                    onClick = { onTabSelected(NavItem.AdminPassengers.route) },
                    icon = { Icon(NavItem.AdminPassengers.icon, contentDescription = "Passengers") },
                    label = { Text(Strings.navPassengers(isKk), fontSize = 10.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_admin_passengers")
                )
                NavigationBarItem(
                    selected = currentTab == NavItem.AdminBookings.route,
                    onClick = { onTabSelected(NavItem.AdminBookings.route) },
                    icon = { Icon(NavItem.AdminBookings.icon, contentDescription = "Bookings") },
                    label = { Text(Strings.navMyBookings(isKk), fontSize = 10.sp) },
                    colors = navigationBarColors(),
                    modifier = Modifier.testTag("nav_admin_bookings")
                )
            }
        }
    }
}

@Composable
private fun navigationBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = NavyDark,
    selectedTextColor = KazakhGold,
    indicatorColor = KazakhGold,
    unselectedIconColor = Color(0xFF94A3B8),
    unselectedTextColor = Color(0xFF94A3B8)
)
