package com.example.data.repository

import com.example.data.local.dao.AppDao
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TaxiRepository(private val appDao: AppDao) {

    val allTrips: Flow<List<TripEntity>> = appDao.getAllTrips()
    val allBookings: Flow<List<BookingEntity>> = appDao.getAllBookings()
    val allDrivers: Flow<List<DriverEntity>> = appDao.getAllDrivers()
    val approvedDrivers: Flow<List<DriverEntity>> = appDao.getApprovedDrivers()
    val allNotifications: Flow<List<NotificationEntity>> = appDao.getAllNotifications()

    fun getSeatsForTrip(tripId: String): Flow<List<SeatEntity>> = appDao.getSeatsForTripFlow(tripId)
    fun getTripsByDriver(driverId: String): Flow<List<TripEntity>> = appDao.getTripsByDriver(driverId)
    fun getBookingsByTrip(tripId: String): Flow<List<BookingEntity>> = appDao.getBookingsByTrip(tripId)
    fun getBookingsByPhone(phone: String): Flow<List<BookingEntity>> = appDao.getBookingsByPhone(phone)

    suspend fun getTripById(tripId: String): TripEntity? = appDao.getTripById(tripId)
    suspend fun getDriverById(driverId: String): DriverEntity? = appDao.getDriverById(driverId)
    suspend fun getBookingById(bookingId: String): BookingEntity? = appDao.getBookingById(bookingId)

    suspend fun bookSeats(
        tripId: String,
        passengerName: String,
        passengerPhone: String,
        selectedSeats: List<Int>,
        pickupPoint: String,
        dropoffPoint: String
    ): BookingEntity {
        return appDao.bookSeatsAtomic(
            tripId = tripId,
            passengerName = passengerName,
            passengerPhone = passengerPhone,
            selectedSeatNumbers = selectedSeats,
            pickupPoint = pickupPoint,
            dropoffPoint = dropoffPoint
        )
    }

    suspend fun cancelBooking(bookingId: String, reason: String = "") {
        appDao.cancelBookingAtomic(bookingId, reason)
    }

    suspend fun movePassenger(bookingId: String, targetTripId: String) {
        appDao.movePassengerAtomic(bookingId, targetTripId)
    }

    suspend fun createTrip(
        driver: DriverEntity,
        route: String,
        date: String,
        time: String,
        price: Int,
        pickupPoint: String,
        dropoffPoint: String
    ) {
        val tripId = "trip_${System.currentTimeMillis()}"
        val newTrip = TripEntity(
            id = tripId,
            driverId = driver.id,
            driverName = driver.name,
            driverPhone = driver.phone,
            carMake = driver.carMake,
            carModel = driver.carModel,
            licensePlate = driver.licensePlate,
            route = route,
            departureDate = date,
            departureTime = time,
            price = price,
            capacity = driver.capacity,
            availableSeats = driver.capacity,
            occupiedSeats = 0,
            status = "SCHEDULED",
            pickupPoint = pickupPoint,
            dropoffPoint = dropoffPoint
        )
        appDao.createTripWithSeats(newTrip)
    }

    suspend fun updateTripPriceAndTime(tripId: String, newPrice: Int, newTime: String) {
        val trip = appDao.getTripById(tripId) ?: return
        val updated = trip.copy(price = newPrice, departureTime = newTime)
        appDao.updateTrip(updated)

        // Notify booked passengers
        val bookings = appDao.getBookingsByTripList(tripId)
        bookings.filter { it.status == "CONFIRMED" }.forEach { b ->
            appDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Рейс уақыты өзгерді",
                    message = "${trip.route} рейсінің уақыты $newTime болып өзгертілді. Бағасы: $newPrice ₸",
                    type = "ADMIN",
                    recipientRole = "PASSENGER",
                    recipientPhone = b.passengerPhone,
                    relatedId = tripId
                )
            )
        }
    }

    suspend fun updateTripStatus(tripId: String, newStatus: String) {
        val trip = appDao.getTripById(tripId) ?: return
        appDao.updateTrip(trip.copy(status = newStatus))

        if (newStatus == "CANCELLED") {
            // Notify passengers
            val bookings = appDao.getBookingsByTripList(tripId)
            bookings.filter { it.status == "CONFIRMED" }.forEach { b ->
                appDao.insertNotification(
                    NotificationEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Рейс тоқтатылды!",
                        message = "${trip.departureDate} ${trip.departureTime} рейсі жүргізуші/диспетчер тарапынан тоқтатылды.",
                        type = "CANCEL",
                        recipientRole = "PASSENGER",
                        recipientPhone = b.passengerPhone,
                        relatedId = tripId
                    )
                )
            }
        }
    }

    suspend fun registerDriver(driver: DriverEntity) {
        appDao.insertDriver(driver)
        appDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Жаңа жүргізуші тіркелді",
                message = "${driver.name} (${driver.carMake} ${driver.carModel}) мақұлдауды күтуде.",
                type = "ADMIN",
                recipientRole = "ADMIN",
                recipientPhone = ""
            )
        )
    }

    suspend fun updateDriverStatus(driverId: String, status: String) {
        val driver = appDao.getDriverById(driverId) ?: return
        val updated = driver.copy(status = status)
        appDao.updateDriver(updated)

        appDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = if (status == "APPROVED") "Аккаунт мақұлданды!" else "Аккаунт мәртебесі: $status",
                message = if (status == "APPROVED") "Құттықтаймыз! Енді сіз жаңа рейстер қоса аласыз." else "Диспетчер аккаунт мәртебесін жаңартты: $status",
                type = "ADMIN",
                recipientRole = "DRIVER",
                recipientPhone = driver.phone
            )
        )
    }

    suspend fun markNotificationAsRead(id: String) {
        appDao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        appDao.markAllNotificationsAsRead()
    }
}
