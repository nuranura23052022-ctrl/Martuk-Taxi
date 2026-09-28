package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val carMake: String,
    val carModel: String,
    val licensePlate: String,
    val capacity: Int, // 4 or 6
    val status: String, // PENDING, APPROVED, REJECTED, SUSPENDED
    val preferredRoute: String, // MARTUK_AKTOBE, AKTOBE_MARTUK, BOTH
    val rating: Double = 5.0,
    val tripsCount: Int = 0,
    val profilePhotoUrl: String = "",
    val carPhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val carMake: String,
    val carModel: String,
    val licensePlate: String,
    val route: String, // MARTUK_AKTOBE or AKTOBE_MARTUK
    val departureDate: String, // e.g. "2026-09-28"
    val departureTime: String, // e.g. "08:00"
    val price: Int, // e.g. 2500
    val capacity: Int, // 4 or 6
    val availableSeats: Int,
    val occupiedSeats: Int,
    val status: String, // SCHEDULED, FULL, DEPARTED, COMPLETED, CANCELLED
    val pickupPoint: String,
    val dropoffPoint: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "seats")
data class SeatEntity(
    @PrimaryKey val id: String, // e.g. "${tripId}_$seatNumber"
    val tripId: String,
    val seatNumber: Int, // 1, 2, 3, 4, 5, 6
    val isOccupied: Boolean = false,
    val bookingId: String? = null,
    val passengerName: String? = null,
    val passengerPhone: String? = null
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String, // e.g. "MTK-28451"
    val tripId: String,
    val passengerName: String,
    val passengerPhone: String,
    val seatNumbers: String, // comma separated "1, 2"
    val seatsCount: Int,
    val totalPrice: Int,
    val status: String, // CONFIRMED, CANCELLED, COMPLETED, NO_SHOW
    val pickupPoint: String,
    val dropoffPoint: String,
    val bookingTime: Long = System.currentTimeMillis(),
    val route: String,
    val departureDate: String,
    val departureTime: String,
    val driverName: String,
    val driverPhone: String,
    val carInfo: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // BOOKING, CANCEL, FULL, ADMIN
    val recipientRole: String, // PASSENGER, DRIVER, ADMIN
    val recipientPhone: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedId: String? = null
)
