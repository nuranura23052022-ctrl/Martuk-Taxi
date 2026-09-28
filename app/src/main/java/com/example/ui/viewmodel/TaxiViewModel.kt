package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MartukTaxiDatabase
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.local.entity.TripEntity
import com.example.data.repository.TaxiRepository
import com.example.model.AppLanguage
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class SearchCriteria(
    val route: String = "MARTUK_AKTOBE",
    val date: String = "28 қыркүйек",
    val time: String = "",
    val passengersCount: Int = 1,
    val sortBy: String = "NEAREST_TIME", // NEAREST_TIME, LOWEST_PRICE, MOST_SEATS
    val maxPrice: Int = 5000,
    val hasSearched: Boolean = false
)

class TaxiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaxiRepository

    init {
        val db = MartukTaxiDatabase.getDatabase(application, viewModelScope)
        repository = TaxiRepository(db.appDao())
        // Run seed check
        viewModelScope.launch {
            MartukTaxiDatabase.populateInitialDemoData(db.appDao())
        }
    }

    // --- Localization State ---
    private val _language = MutableStateFlow(AppLanguage.KAZAKH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.KAZAKH) AppLanguage.RUSSIAN else AppLanguage.KAZAKH
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    // --- Role & User Session ---
    private val _currentRole = MutableStateFlow(UserRole.PASSENGER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    // Active passenger profile info
    val currentPassengerName = MutableStateFlow("Нұрлан Сабыр")
    val currentPassengerPhone = MutableStateFlow("+7 778 123 4567")

    // Active driver profile selection
    private val _selectedDriverId = MutableStateFlow("drv_ayan")
    val selectedDriverId: StateFlow<String> = _selectedDriverId.asStateFlow()

    fun selectDriver(driverId: String) {
        _selectedDriverId.value = driverId
    }

    // --- Reactive Data Streams ---
    val allTrips: StateFlow<List<TripEntity>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDrivers: StateFlow<List<DriverEntity>> = repository.allDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvedDrivers: StateFlow<List<DriverEntity>> = repository.approvedDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered passenger trips based on search criteria
    private val _searchCriteria = MutableStateFlow(SearchCriteria())
    val searchCriteria: StateFlow<SearchCriteria> = _searchCriteria.asStateFlow()

    fun updateSearchRoute(route: String) {
        _searchCriteria.value = _searchCriteria.value.copy(route = route)
    }

    fun updateSearchDate(date: String) {
        _searchCriteria.value = _searchCriteria.value.copy(date = date)
    }

    fun updateSearchTime(time: String) {
        _searchCriteria.value = _searchCriteria.value.copy(time = time)
    }

    fun updatePassengersCount(count: Int) {
        _searchCriteria.value = _searchCriteria.value.copy(passengersCount = count)
    }

    fun updateSortBy(sortBy: String) {
        _searchCriteria.value = _searchCriteria.value.copy(sortBy = sortBy)
    }

    fun performSearch() {
        _searchCriteria.value = _searchCriteria.value.copy(hasSearched = true)
    }

    fun swapSearchRoute() {
        val current = _searchCriteria.value.route
        val next = if (current == "MARTUK_AKTOBE") "AKTOBE_MARTUK" else "MARTUK_AKTOBE"
        _searchCriteria.value = _searchCriteria.value.copy(route = next)
    }

    // Smart filtered trips for Passenger
    val filteredTrips: StateFlow<List<TripEntity>> = combine(allTrips, _searchCriteria) { trips, criteria ->
        trips
            .filter { trip ->
                // Must match route
                val routeMatches = trip.route == criteria.route
                // Date matches
                val dateMatches = criteria.date.isBlank() || trip.departureDate.contains(criteria.date, ignoreCase = true)
                // Trip is scheduled (not departed, cancelled, or completed)
                val statusOk = trip.status == "SCHEDULED" || trip.status == "FULL"
                // Check enough seats for requested passenger count
                // Requirement 8: "Never show a vehicle with insufficient seats as bookable"
                // When all seats are occupied, requirement 5 says "The trip must disappear from normal passenger search results"
                val hasEnoughSeats = trip.availableSeats >= criteria.passengersCount && trip.availableSeats > 0

                routeMatches && dateMatches && statusOk && hasEnoughSeats
            }
            .sortedWith { t1, t2 ->
                when (criteria.sortBy) {
                    "LOWEST_PRICE" -> t1.price.compareTo(t2.price)
                    "MOST_SEATS" -> t2.availableSeats.compareTo(t1.availableSeats)
                    else -> {
                        // Nearest departure time
                        if (criteria.time.isNotBlank()) {
                            val diff1 = Math.abs(timeToMinutes(t1.departureTime) - timeToMinutes(criteria.time))
                            val diff2 = Math.abs(timeToMinutes(t2.departureTime) - timeToMinutes(criteria.time))
                            diff1.compareTo(diff2)
                        } else {
                            t1.departureTime.compareTo(t2.departureTime)
                        }
                    }
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun timeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size == 2) {
            return (parts[0].toIntOrNull() ?: 0) * 60 + (parts[1].toIntOrNull() ?: 0)
        }
        return 0
    }

    // --- Interactive Booking State ---
    private val _selectedTripForBooking = MutableStateFlow<TripEntity?>(null)
    val selectedTripForBooking: StateFlow<TripEntity?> = _selectedTripForBooking.asStateFlow()

    private val _tripSeats = MutableStateFlow<List<SeatEntity>>(emptyList())
    val tripSeats: StateFlow<List<SeatEntity>> = _tripSeats.asStateFlow()

    private val _selectedSeatNumbers = MutableStateFlow<Set<Int>>(emptySet())
    val selectedSeatNumbers: StateFlow<Set<Int>> = _selectedSeatNumbers.asStateFlow()

    private val _bookingSuccessDialog = MutableStateFlow<BookingEntity?>(null)
    val bookingSuccessDialog: StateFlow<BookingEntity?> = _bookingSuccessDialog.asStateFlow()

    private val _bookingErrorMessage = MutableStateFlow<String?>(null)
    val bookingErrorMessage: StateFlow<String?> = _bookingErrorMessage.asStateFlow()

    fun openBookingSheet(trip: TripEntity) {
        _selectedTripForBooking.value = trip
        _selectedSeatNumbers.value = emptySet()
        _bookingErrorMessage.value = null
        viewModelScope.launch {
            repository.getSeatsForTrip(trip.id).collect { seats ->
                _tripSeats.value = seats
            }
        }
    }

    fun closeBookingSheet() {
        _selectedTripForBooking.value = null
        _selectedSeatNumbers.value = emptySet()
        _bookingErrorMessage.value = null
    }

    fun toggleSeatSelection(seatNumber: Int) {
        val current = _selectedSeatNumbers.value.toMutableSet()
        if (current.contains(seatNumber)) {
            current.remove(seatNumber)
        } else {
            current.add(seatNumber)
        }
        _selectedSeatNumbers.value = current
    }

    fun confirmBooking(pickupPoint: String, dropoffPoint: String) {
        val trip = _selectedTripForBooking.value ?: return
        val selected = _selectedSeatNumbers.value.toList()
        if (selected.isEmpty()) {
            _bookingErrorMessage.value = if (_language.value == AppLanguage.KAZAKH) 
                "Кем дегенде 1 орын таңдаңыз!" else "Выберите хотя бы 1 место!"
            return
        }

        viewModelScope.launch {
            try {
                val booking = repository.bookSeats(
                    tripId = trip.id,
                    passengerName = currentPassengerName.value,
                    passengerPhone = currentPassengerPhone.value,
                    selectedSeats = selected,
                    pickupPoint = pickupPoint.ifBlank { trip.pickupPoint },
                    dropoffPoint = dropoffPoint.ifBlank { trip.dropoffPoint }
                )
                _bookingSuccessDialog.value = booking
                _selectedTripForBooking.value = null
                _selectedSeatNumbers.value = emptySet()
            } catch (e: Exception) {
                _bookingErrorMessage.value = e.message ?: "Брондау кезінде қате орын алды"
            }
        }
    }

    fun dismissBookingSuccess() {
        _bookingSuccessDialog.value = null
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    // --- Driver Features ---
    fun createTripByDriver(
        route: String,
        date: String,
        time: String,
        price: Int,
        pickupPoint: String,
        dropoffPoint: String
    ) {
        viewModelScope.launch {
            val driver = allDrivers.value.find { it.id == _selectedDriverId.value }
            if (driver != null) {
                if (driver.status != "APPROVED") {
                    _bookingErrorMessage.value = if (_language.value == AppLanguage.KAZAKH)
                        "Аккаунтыңыз тексерілуде! Әкімші мақұлдағаннан кейін ғана рейс қоса аласыз."
                        else "Ваш аккаунт на проверке! Рейсы можно публиковать после одобрения диспетчером."
                    return@launch
                }
                repository.createTrip(
                    driver = driver,
                    route = route,
                    date = date,
                    time = time,
                    price = price,
                    pickupPoint = pickupPoint,
                    dropoffPoint = dropoffPoint
                )
            }
        }
    }

    fun registerNewDriver(
        name: String,
        phone: String,
        carMake: String,
        carModel: String,
        licensePlate: String,
        capacity: Int,
        preferredRoute: String
    ) {
        viewModelScope.launch {
            val driverId = "drv_${System.currentTimeMillis()}"
            val newDriver = DriverEntity(
                id = driverId,
                name = name,
                phone = phone,
                carMake = carMake,
                carModel = carModel,
                licensePlate = licensePlate,
                capacity = capacity,
                status = "PENDING",
                preferredRoute = preferredRoute,
                rating = 5.0,
                tripsCount = 0
            )
            repository.registerDriver(newDriver)
            _selectedDriverId.value = driverId
        }
    }

    fun updateTripStatus(tripId: String, status: String) {
        viewModelScope.launch {
            repository.updateTripStatus(tripId, status)
        }
    }

    // --- Admin / Dispatcher Features ---
    fun approveDriver(driverId: String) {
        viewModelScope.launch {
            repository.updateDriverStatus(driverId, "APPROVED")
        }
    }

    fun rejectDriver(driverId: String) {
        viewModelScope.launch {
            repository.updateDriverStatus(driverId, "REJECTED")
        }
    }

    fun suspendDriver(driverId: String) {
        viewModelScope.launch {
            repository.updateDriverStatus(driverId, "SUSPENDED")
        }
    }

    fun updateTripDetails(tripId: String, price: Int, time: String) {
        viewModelScope.launch {
            repository.updateTripPriceAndTime(tripId, price, time)
        }
    }

    fun movePassengerToTrip(bookingId: String, targetTripId: String) {
        viewModelScope.launch {
            try {
                repository.movePassenger(bookingId, targetTripId)
            } catch (e: Exception) {
                _bookingErrorMessage.value = e.message
            }
        }
    }

    // --- Notifications ---
    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun clearErrorMessage() {
        _bookingErrorMessage.value = null
    }
}
