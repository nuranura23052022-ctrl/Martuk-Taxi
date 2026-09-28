package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AppDao {

    // --- TRIPS ---
    @Query("SELECT * FROM trips ORDER BY departureDate ASC, departureTime ASC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripEntity?

    @Query("SELECT * FROM trips WHERE driverId = :driverId ORDER BY departureDate ASC, departureTime ASC")
    fun getTripsByDriver(driverId: String): Flow<List<TripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTrip(tripId: String)

    // --- SEATS ---
    @Query("SELECT * FROM seats WHERE tripId = :tripId ORDER BY seatNumber ASC")
    fun getSeatsForTripFlow(tripId: String): Flow<List<SeatEntity>>

    @Query("SELECT * FROM seats WHERE tripId = :tripId ORDER BY seatNumber ASC")
    suspend fun getSeatsForTrip(tripId: String): List<SeatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<SeatEntity>)

    @Update
    suspend fun updateSeat(seat: SeatEntity)

    // --- BOOKINGS ---
    @Query("SELECT * FROM bookings ORDER BY bookingTime DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE passengerPhone = :phone ORDER BY bookingTime DESC")
    fun getBookingsByPhone(phone: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE tripId = :tripId ORDER BY bookingTime DESC")
    fun getBookingsByTrip(tripId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE tripId = :tripId ORDER BY bookingTime DESC")
    suspend fun getBookingsByTripList(tripId: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    // --- DRIVERS ---
    @Query("SELECT * FROM drivers ORDER BY createdAt DESC")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE id = :driverId")
    suspend fun getDriverById(driverId: String): DriverEntity?

    @Query("SELECT * FROM drivers WHERE status = 'APPROVED' ORDER BY rating DESC")
    fun getApprovedDrivers(): Flow<List<DriverEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    // --- NOTIFICATIONS ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // --- ATOMIC TRANSACTIONS ---

    /**
     * Initializes a trip and creates available seat records atomically.
     */
    @Transaction
    suspend fun createTripWithSeats(trip: TripEntity) {
        insertTrip(trip)
        val seats = (1..trip.capacity).map { seatNum ->
            SeatEntity(
                id = "${trip.id}_$seatNum",
                tripId = trip.id,
                seatNumber = seatNum,
                isOccupied = false
            )
        }
        insertSeats(seats)
    }

    /**
     * ATOMIC BOOKING TRANSACTION:
     * - Locks verification on the server/Room DB side
     * - Prevents overbooking and concurrent race conditions
     * - Re-verifies seat availability
     * - Decreases available seats and flags trip as FULL when 0 seats remain
     */
    @Transaction
    suspend fun bookSeatsAtomic(
        tripId: String,
        passengerName: String,
        passengerPhone: String,
        selectedSeatNumbers: List<Int>,
        pickupPoint: String,
        dropoffPoint: String
    ): BookingEntity {
        val trip = getTripById(tripId) ?: throw IllegalStateException("Рейс табылмады!")
        if (trip.status == "CANCELLED" || trip.status == "COMPLETED" || trip.status == "DEPARTED") {
            throw IllegalStateException("Бұл рейс қазіргі уақытта белсенді емес!")
        }

        val seats = getSeatsForTrip(tripId)
        val requestedSeats = seats.filter { selectedSeatNumbers.contains(it.seatNumber) }

        if (requestedSeats.size != selectedSeatNumbers.size) {
            throw IllegalStateException("Кейбір таңдалған орындар табылмады!")
        }

        // Check if any seat is already occupied
        val alreadyOccupied = requestedSeats.filter { it.isOccupied }
        if (alreadyOccupied.isNotEmpty()) {
            val occNums = alreadyOccupied.map { it.seatNumber }.joinToString(", ")
            throw IllegalStateException("Кешіріңіз, мына орын(дар) бос емес: $occNums!")
        }

        // Check available count
        if (trip.availableSeats < selectedSeatNumbers.size) {
            throw IllegalStateException("Көлікте жеткілікті бос орын жоқ! Қалғаны: ${trip.availableSeats}")
        }

        // Generate unique booking ID (e.g. MTK-28451)
        val randomNum = (10000..99999).random()
        val bookingId = "MTK-$randomNum"
        val seatNumbersStr = selectedSeatNumbers.sorted().joinToString(", ")

        // Mark seats as occupied
        val updatedSeats = requestedSeats.map {
            it.copy(
                isOccupied = true,
                bookingId = bookingId,
                passengerName = passengerName,
                passengerPhone = passengerPhone
            )
        }
        updatedSeats.forEach { updateSeat(it) }

        // Recalculate trip seats
        val newOccupied = trip.occupiedSeats + selectedSeatNumbers.size
        val newAvailable = trip.capacity - newOccupied
        val newStatus = if (newAvailable <= 0) "FULL" else "SCHEDULED"

        val updatedTrip = trip.copy(
            occupiedSeats = newOccupied,
            availableSeats = newAvailable.coerceAtLeast(0),
            status = newStatus
        )
        updateTrip(updatedTrip)

        val totalCost = selectedSeatNumbers.size * trip.price
        val booking = BookingEntity(
            id = bookingId,
            tripId = trip.id,
            passengerName = passengerName,
            passengerPhone = passengerPhone,
            seatNumbers = seatNumbersStr,
            seatsCount = selectedSeatNumbers.size,
            totalPrice = totalCost,
            status = "CONFIRMED",
            pickupPoint = pickupPoint,
            dropoffPoint = dropoffPoint,
            bookingTime = System.currentTimeMillis(),
            route = trip.route,
            departureDate = trip.departureDate,
            departureTime = trip.departureTime,
            driverName = trip.driverName,
            driverPhone = trip.driverPhone,
            carInfo = "${trip.carMake} ${trip.carModel} (${trip.licensePlate})"
        )
        insertBooking(booking)

        // Generate Driver Notification
        insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Жаңа бронь: $bookingId",
                message = "Жолаушы: $passengerName ($passengerPhone), $seatNumbersStr орын.",
                type = "BOOKING",
                recipientRole = "DRIVER",
                recipientPhone = trip.driverPhone,
                relatedId = bookingId
            )
        )

        // If trip is full, notify driver
        if (newStatus == "FULL") {
            insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "КӨЛІК ТОЛДЫ!",
                    message = "${trip.departureTime} рейсіне барлық ${trip.capacity} орын брондалды.",
                    type = "FULL",
                    recipientRole = "DRIVER",
                    recipientPhone = trip.driverPhone,
                    relatedId = trip.id
                )
            )
        }

        // Notify Passenger
        insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Бронь сәтті жасалды!",
                message = "Бронь № $bookingId. Жүргізуші: ${trip.driverName} (${trip.driverPhone})",
                type = "BOOKING",
                recipientRole = "PASSENGER",
                recipientPhone = passengerPhone,
                relatedId = bookingId
            )
        )

        return booking
    }

    /**
     * ATOMIC BOOKING CANCELLATION:
     * - Frees the seats atomically
     * - Decreases occupied seats and increments available seats
     * - Restores trip status from FULL to SCHEDULED
     * - Generates notification
     */
    @Transaction
    suspend fun cancelBookingAtomic(bookingId: String, reason: String = "") {
        val booking = getBookingById(bookingId) ?: return
        if (booking.status == "CANCELLED") return

        // 1. Mark booking as cancelled
        val updatedBooking = booking.copy(status = "CANCELLED")
        updateBooking(updatedBooking)

        // 2. Free up seats
        val seats = getSeatsForTrip(booking.tripId)
        val bookedSeats = seats.filter { it.bookingId == bookingId }
        val freedSeats = bookedSeats.map {
            it.copy(
                isOccupied = false,
                bookingId = null,
                passengerName = null,
                passengerPhone = null
            )
        }
        freedSeats.forEach { updateSeat(it) }

        // 3. Update Trip
        val trip = getTripById(booking.tripId)
        if (trip != null) {
            val freedCount = bookedSeats.size.coerceAtLeast(booking.seatsCount)
            val newOccupied = (trip.occupiedSeats - freedCount).coerceAtLeast(0)
            val newAvailable = (trip.capacity - newOccupied).coerceIn(0, trip.capacity)
            val newStatus = if (trip.status == "FULL") "SCHEDULED" else trip.status

            updateTrip(
                trip.copy(
                    occupiedSeats = newOccupied,
                    availableSeats = newAvailable,
                    status = newStatus
                )
            )

            // Notify Driver
            insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    title = "Бронь жойылды: $bookingId",
                    message = "Жолаушы ${booking.passengerName} броньды жойды. $freedCount орын қайта босатылды.",
                    type = "CANCEL",
                    recipientRole = "DRIVER",
                    recipientPhone = trip.driverPhone,
                    relatedId = bookingId
                )
            )
        }

        // Notify Passenger
        insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Бронь жойылды",
                message = "Бронь № $bookingId сәтті жойылды.",
                type = "CANCEL",
                recipientRole = "PASSENGER",
                recipientPhone = booking.passengerPhone,
                relatedId = bookingId
            )
        )
    }

    /**
     * DISPATCHER / ADMIN: Move passenger to another vehicle/trip atomically
     */
    @Transaction
    suspend fun movePassengerAtomic(bookingId: String, targetTripId: String) {
        val booking = getBookingById(bookingId) ?: throw IllegalStateException("Бронь табылмады!")
        val oldTrip = getTripById(booking.tripId) ?: throw IllegalStateException("Бастапқы рейс табылмады!")
        val newTrip = getTripById(targetTripId) ?: throw IllegalStateException("Жаңа көлік/рейс табылмады!")

        if (newTrip.availableSeats < booking.seatsCount) {
            throw IllegalStateException("Жаңа көлікте орын жеткіліксіз! Бос орын: ${newTrip.availableSeats}")
        }

        // 1. Free seats from old trip
        val oldSeats = getSeatsForTrip(oldTrip.id).filter { it.bookingId == bookingId }
        oldSeats.forEach {
            updateSeat(it.copy(isOccupied = false, bookingId = null, passengerName = null, passengerPhone = null))
        }
        val oldTripOccupied = (oldTrip.occupiedSeats - booking.seatsCount).coerceAtLeast(0)
        val oldTripAvailable = oldTrip.capacity - oldTripOccupied
        updateTrip(
            oldTrip.copy(
                occupiedSeats = oldTripOccupied,
                availableSeats = oldTripAvailable,
                status = if (oldTrip.status == "FULL") "SCHEDULED" else oldTrip.status
            )
        )

        // 2. Occupy seats in new trip
        val newTripSeats = getSeatsForTrip(newTrip.id)
        val availableInNew = newTripSeats.filter { !it.isOccupied }.take(booking.seatsCount)
        if (availableInNew.size < booking.seatsCount) {
            throw IllegalStateException("Жаңа рейс бойынша орындар бос емес!")
        }
        availableInNew.forEach {
            updateSeat(
                it.copy(
                    isOccupied = true,
                    bookingId = bookingId,
                    passengerName = booking.passengerName,
                    passengerPhone = booking.passengerPhone
                )
            )
        }
        val newOccupied = newTrip.occupiedSeats + booking.seatsCount
        val newAvailable = newTrip.capacity - newOccupied
        updateTrip(
            newTrip.copy(
                occupiedSeats = newOccupied,
                availableSeats = newAvailable,
                status = if (newAvailable <= 0) "FULL" else "SCHEDULED"
            )
        )

        // 3. Update booking record
        val newSeatNumbers = availableInNew.map { it.seatNumber }.sorted().joinToString(", ")
        val updatedBooking = booking.copy(
            tripId = newTrip.id,
            seatNumbers = newSeatNumbers,
            route = newTrip.route,
            departureDate = newTrip.departureDate,
            departureTime = newTrip.departureTime,
            driverName = newTrip.driverName,
            driverPhone = newTrip.driverPhone,
            carInfo = "${newTrip.carMake} ${newTrip.carModel} (${newTrip.licensePlate})"
        )
        updateBooking(updatedBooking)

        // Notify
        insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Көлік ауыстырылды",
                message = "Диспетчер $bookingId бронын ${newTrip.driverName} (${newTrip.carMake}) көлігіне ауыстырды.",
                type = "ADMIN",
                recipientRole = "PASSENGER",
                recipientPhone = booking.passengerPhone,
                relatedId = bookingId
            )
        )
    }

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripsCount(): Int
}
