package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavItem
import com.example.ui.components.TopBar
import com.example.ui.screens.admin.AdminBookingsScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.AdminDriversScreen
import com.example.ui.screens.admin.AdminPassengersScreen
import com.example.ui.screens.admin.AdminTripsScreen
import com.example.ui.screens.driver.DriverHomeScreen
import com.example.ui.screens.driver.DriverPassengersScreen
import com.example.ui.screens.driver.DriverProfileScreen
import com.example.ui.screens.driver.DriverRegisterScreen
import com.example.ui.screens.driver.DriverTripsScreen
import com.example.ui.screens.notifications.NotificationsDialog
import com.example.ui.screens.passenger.BookingSuccessDialog
import com.example.ui.screens.passenger.PassengerBookingsScreen
import com.example.ui.screens.passenger.PassengerHomeScreen
import com.example.ui.screens.passenger.PassengerProfileScreen
import com.example.ui.screens.passenger.PassengerTripsScreen
import com.example.ui.screens.passenger.SeatBookingSheet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TaxiViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TaxiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: TaxiViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isKk = language == AppLanguage.KAZAKH
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()

    val allTrips by viewModel.allTrips.collectAsStateWithLifecycle()
    val filteredTrips by viewModel.filteredTrips.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val allDrivers by viewModel.allDrivers.collectAsStateWithLifecycle()
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val searchCriteria by viewModel.searchCriteria.collectAsStateWithLifecycle()

    val selectedDriverId by viewModel.selectedDriverId.collectAsStateWithLifecycle()
    val currentDriver = allDrivers.find { it.id == selectedDriverId } ?: allDrivers.firstOrNull()
    val driverTrips = allTrips.filter { it.driverId == currentDriver?.id }

    val passengerName by viewModel.currentPassengerName.collectAsStateWithLifecycle()
    val passengerPhone by viewModel.currentPassengerPhone.collectAsStateWithLifecycle()

    val selectedTripForBooking by viewModel.selectedTripForBooking.collectAsStateWithLifecycle()
    val tripSeats by viewModel.tripSeats.collectAsStateWithLifecycle()
    val selectedSeatNumbers by viewModel.selectedSeatNumbers.collectAsStateWithLifecycle()
    val bookingSuccessDialog by viewModel.bookingSuccessDialog.collectAsStateWithLifecycle()
    val bookingErrorMessage by viewModel.bookingErrorMessage.collectAsStateWithLifecycle()

    // Navigation Tab state per role
    var currentPassengerTab by remember { mutableStateOf(NavItem.PassengerHome.route) }
    var currentDriverTab by remember { mutableStateOf(NavItem.DriverHome.route) }
    var currentAdminTab by remember { mutableStateOf(NavItem.AdminDashboard.route) }

    // Dialog & overlay states
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var isRegisteringDriver by remember { mutableStateOf(false) }

    val unreadNotifsCount = allNotifications.count { !it.isRead }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Error message display
    LaunchedEffect(bookingErrorMessage) {
        bookingErrorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearErrorMessage()
        }
    }

    // Back handling for tabs
    BackHandler(
        enabled = (currentRole == UserRole.PASSENGER && currentPassengerTab != NavItem.PassengerHome.route) ||
                (currentRole == UserRole.DRIVER && (currentDriverTab != NavItem.DriverHome.route || isRegisteringDriver)) ||
                (currentRole == UserRole.ADMIN && currentAdminTab != NavItem.AdminDashboard.route)
    ) {
        if (isRegisteringDriver) {
            isRegisteringDriver = false
        } else if (currentRole == UserRole.PASSENGER) {
            currentPassengerTab = NavItem.PassengerHome.route
        } else if (currentRole == UserRole.DRIVER) {
            currentDriverTab = NavItem.DriverHome.route
        } else if (currentRole == UserRole.ADMIN) {
            currentAdminTab = NavItem.AdminDashboard.route
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                currentRole = currentRole,
                currentLanguage = language,
                unreadNotificationsCount = unreadNotifsCount,
                onRoleSelected = { newRole ->
                    viewModel.setRole(newRole)
                    isRegisteringDriver = false
                },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onNotificationsClick = { showNotificationsDialog = true }
            )
        },
        bottomBar = {
            if (!isRegisteringDriver) {
                BottomNavBar(
                    currentRole = currentRole,
                    currentTab = when (currentRole) {
                        UserRole.PASSENGER -> currentPassengerTab
                        UserRole.DRIVER -> currentDriverTab
                        UserRole.ADMIN -> currentAdminTab
                    },
                    language = language,
                    onTabSelected = { tabRoute ->
                        when (currentRole) {
                            UserRole.PASSENGER -> currentPassengerTab = tabRoute
                            UserRole.DRIVER -> currentDriverTab = tabRoute
                            UserRole.ADMIN -> currentAdminTab = tabRoute
                        }
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRole) {
                UserRole.PASSENGER -> {
                    when (currentPassengerTab) {
                        NavItem.PassengerHome.route -> {
                            PassengerHomeScreen(
                                searchCriteria = searchCriteria,
                                availableTrips = filteredTrips,
                                isKazakh = isKk,
                                onUpdateRoute = { viewModel.updateSearchRoute(it) },
                                onSwapRoute = { viewModel.swapSearchRoute() },
                                onUpdateDate = { viewModel.updateSearchDate(it) },
                                onUpdateTime = { viewModel.updateSearchTime(it) },
                                onUpdatePassengersCount = { viewModel.updatePassengersCount(it) },
                                onUpdateSortBy = { viewModel.updateSortBy(it) },
                                onPerformSearch = { viewModel.performSearch() },
                                onBookTrip = { trip -> viewModel.openBookingSheet(trip) }
                            )
                        }
                        NavItem.PassengerTrips.route -> {
                            PassengerTripsScreen(
                                allTrips = allTrips,
                                isKazakh = isKk,
                                onBookTrip = { trip -> viewModel.openBookingSheet(trip) }
                            )
                        }
                        NavItem.PassengerBookings.route -> {
                            PassengerBookingsScreen(
                                bookings = allBookings,
                                isKazakh = isKk,
                                onCancelBooking = { bookingId -> viewModel.cancelBooking(bookingId) }
                            )
                        }
                        NavItem.PassengerProfile.route -> {
                            PassengerProfileScreen(
                                currentName = passengerName,
                                currentPhone = passengerPhone,
                                currentRole = currentRole,
                                language = language,
                                onNameChange = { viewModel.currentPassengerName.value = it },
                                onPhoneChange = { viewModel.currentPassengerPhone.value = it },
                                onRoleSelected = { viewModel.setRole(it) },
                                onToggleLanguage = { viewModel.toggleLanguage() }
                            )
                        }
                    }
                }
                UserRole.DRIVER -> {
                    if (isRegisteringDriver) {
                        DriverRegisterScreen(
                            isKazakh = isKk,
                            onBack = { isRegisteringDriver = false },
                            onSubmit = { name, phone, make, model, plate, capacity, route ->
                                viewModel.registerNewDriver(name, phone, make, model, plate, capacity, route)
                                isRegisteringDriver = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isKk) "Өтінім қабылданды! Тексерілуде." else "Заявка отправлена на проверку!"
                                    )
                                }
                            }
                        )
                    } else {
                        when (currentDriverTab) {
                            NavItem.DriverHome.route -> {
                                DriverHomeScreen(
                                    allDrivers = allDrivers,
                                    currentDriver = currentDriver,
                                    driverTrips = driverTrips,
                                    isKazakh = isKk,
                                    onSelectDriver = { viewModel.selectDriver(it) },
                                    onNavigateToTrips = { currentDriverTab = NavItem.DriverTrips.route },
                                    onOpenRegister = { isRegisteringDriver = true }
                                )
                            }
                            NavItem.DriverTrips.route -> {
                                DriverTripsScreen(
                                    currentDriver = currentDriver,
                                    trips = driverTrips,
                                    isKazakh = isKk,
                                    onCreateTrip = { route, date, time, price, pickup, dropoff ->
                                        viewModel.createTripByDriver(route, date, time, price, pickup, dropoff)
                                    },
                                    onUpdateTripStatus = { tripId, status ->
                                        viewModel.updateTripStatus(tripId, status)
                                    },
                                    onViewTripPassengers = { trip ->
                                        currentDriverTab = NavItem.DriverPassengers.route
                                    }
                                )
                            }
                            NavItem.DriverPassengers.route -> {
                                DriverPassengersScreen(
                                    trips = driverTrips,
                                    bookings = allBookings,
                                    isKazakh = isKk
                                )
                            }
                            NavItem.DriverProfile.route -> {
                                DriverProfileScreen(
                                    driver = currentDriver,
                                    isKazakh = isKk,
                                    onRoleSelected = { viewModel.setRole(it) },
                                    onRegisterNewDriver = { isRegisteringDriver = true }
                                )
                            }
                        }
                    }
                }
                UserRole.ADMIN -> {
                    when (currentAdminTab) {
                        NavItem.AdminDashboard.route -> {
                            AdminDashboardScreen(
                                trips = allTrips,
                                drivers = allDrivers,
                                bookings = allBookings,
                                isKazakh = isKk,
                                onEditTrip = { tripId, price, time ->
                                    viewModel.updateTripDetails(tripId, price, time)
                                },
                                onCancelTrip = { tripId ->
                                    viewModel.updateTripStatus(tripId, "CANCELLED")
                                }
                            )
                        }
                        NavItem.AdminTrips.route -> {
                            AdminTripsScreen(
                                trips = allTrips,
                                drivers = allDrivers,
                                isKazakh = isKk,
                                onCreateTrip = { driver, route, date, time, price, pickup, dropoff ->
                                    viewModel.createTripByDriver(route, date, time, price, pickup, dropoff)
                                },
                                onEditTrip = { tripId, price, time ->
                                    viewModel.updateTripDetails(tripId, price, time)
                                },
                                onCancelTrip = { tripId ->
                                    viewModel.updateTripStatus(tripId, "CANCELLED")
                                }
                            )
                        }
                        NavItem.AdminDrivers.route -> {
                            AdminDriversScreen(
                                drivers = allDrivers,
                                isKazakh = isKk,
                                onApproveDriver = { viewModel.approveDriver(it) },
                                onRejectDriver = { viewModel.rejectDriver(it) },
                                onSuspendDriver = { viewModel.suspendDriver(it) }
                            )
                        }
                        NavItem.AdminPassengers.route -> {
                            AdminPassengersScreen(
                                bookings = allBookings,
                                allTrips = allTrips,
                                isKazakh = isKk,
                                onMovePassenger = { bookingId, targetTripId ->
                                    viewModel.movePassengerToTrip(bookingId, targetTripId)
                                }
                            )
                        }
                        NavItem.AdminBookings.route -> {
                            AdminBookingsScreen(
                                bookings = allBookings,
                                isKazakh = isKk,
                                onCancelBooking = { viewModel.cancelBooking(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Seat Selection Modal Sheet
    selectedTripForBooking?.let { trip ->
        SeatBookingSheet(
            trip = trip,
            seats = tripSeats,
            selectedSeats = selectedSeatNumbers,
            passengerName = passengerName,
            passengerPhone = passengerPhone,
            errorMessage = bookingErrorMessage,
            isKazakh = isKk,
            onSeatClick = { seatNumber -> viewModel.toggleSeatSelection(seatNumber) },
            onConfirmBooking = { pickup, dropoff ->
                viewModel.confirmBooking(pickup, dropoff)
            },
            onDismiss = { viewModel.closeBookingSheet() }
        )
    }

    // Booking Success Dialog (with MTK-XXXXX and Call/WhatsApp actions)
    bookingSuccessDialog?.let { booking ->
        BookingSuccessDialog(
            booking = booking,
            isKazakh = isKk,
            onDismiss = { viewModel.dismissBookingSuccess() },
            onViewBookings = {
                viewModel.dismissBookingSuccess()
                viewModel.setRole(UserRole.PASSENGER)
                currentPassengerTab = NavItem.PassengerBookings.route
            }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            notifications = allNotifications,
            isKazakh = isKk,
            onDismiss = { showNotificationsDialog = false },
            onMarkAllRead = { viewModel.markAllNotificationsRead() },
            onNotificationClick = { notifId -> viewModel.markNotificationRead(notifId) }
        )
    }
}
