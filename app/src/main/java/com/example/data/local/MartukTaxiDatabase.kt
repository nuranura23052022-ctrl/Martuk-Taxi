package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AppDao
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.DriverEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.local.entity.TripEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DriverEntity::class,
        TripEntity::class,
        SeatEntity::class,
        BookingEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MartukTaxiDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: MartukTaxiDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MartukTaxiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MartukTaxiDatabase::class.java,
                    "martuk_taxi_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDemoData(database.appDao())
                    }
                }
            }
        }

        suspend fun populateInitialDemoData(dao: AppDao) {
            if (dao.getTripsCount() > 0) return

            // 1. Seed Drivers
            val driver1 = DriverEntity(
                id = "drv_ayan",
                name = "Аян",
                phone = "+7 701 234 5678",
                carMake = "Toyota",
                carModel = "Camry 70",
                licensePlate = "04 KZ 777 AA",
                capacity = 4,
                status = "APPROVED",
                preferredRoute = "MARTUK_AKTOBE_BOTH",
                rating = 4.9,
                tripsCount = 142
            )
            val driver2 = DriverEntity(
                id = "drv_erlan",
                name = "Ерлан",
                phone = "+7 702 345 6789",
                carMake = "Hyundai",
                carModel = "Sonata",
                licensePlate = "04 KZ 555 BB",
                capacity = 4,
                status = "APPROVED",
                preferredRoute = "MARTUK_AKTOBE_BOTH",
                rating = 4.8,
                tripsCount = 98
            )
            val driver3 = DriverEntity(
                id = "drv_nurlan",
                name = "Нұрлан",
                phone = "+7 705 456 7890",
                carMake = "Toyota",
                carModel = "Alphard",
                licensePlate = "04 KZ 888 CC",
                capacity = 6,
                status = "APPROVED",
                preferredRoute = "MARTUK_AKTOBE_BOTH",
                rating = 5.0,
                tripsCount = 210
            )
            val driver4 = DriverEntity(
                id = "drv_askar",
                name = "Асқар",
                phone = "+7 777 567 8901",
                carMake = "Kia",
                carModel = "Carnival",
                licensePlate = "04 KZ 123 DD",
                capacity = 6,
                status = "APPROVED",
                preferredRoute = "MARTUK_AKTOBE_BOTH",
                rating = 4.9,
                tripsCount = 85
            )
            val driver5 = DriverEntity(
                id = "drv_bauyrzhan",
                name = "Бауыржан",
                phone = "+7 708 678 9012",
                carMake = "Chevrolet",
                carModel = "Cobalt",
                licensePlate = "04 KZ 999 EE",
                capacity = 4,
                status = "PENDING",
                preferredRoute = "MARTUK_AKTOBE",
                rating = 5.0,
                tripsCount = 0
            )

            dao.insertDriver(driver1)
            dao.insertDriver(driver2)
            dao.insertDriver(driver3)
            dao.insertDriver(driver4)
            dao.insertDriver(driver5)

            // 2. Seed Trips
            val todayDate = "28 қыркүйек"

            // Trip 1: Аян (Toyota Camry) Мәртөк → Ақтөбе 08:00 (4 seats, 1 occupied, 3 available)
            val trip1 = TripEntity(
                id = "trip_1",
                driverId = driver1.id,
                driverName = driver1.name,
                driverPhone = driver1.phone,
                carMake = driver1.carMake,
                carModel = driver1.carModel,
                licensePlate = driver1.licensePlate,
                route = "MARTUK_AKTOBE",
                departureDate = todayDate,
                departureTime = "08:00",
                price = 2500,
                capacity = 4,
                availableSeats = 3,
                occupiedSeats = 1,
                status = "SCHEDULED",
                pickupPoint = "Мәртөк: Орталық автобекет",
                dropoffPoint = "Ақтөбе: Автовокзал / Мега"
            )
            dao.insertTrip(trip1)
            val seatsTrip1 = listOf(
                SeatEntity("trip_1_1", "trip_1", 1, isOccupied = true, bookingId = "MTK-10291", passengerName = "Қайрат", passengerPhone = "+7 771 111 2233"),
                SeatEntity("trip_1_2", "trip_1", 2, isOccupied = false),
                SeatEntity("trip_1_3", "trip_1", 3, isOccupied = false),
                SeatEntity("trip_1_4", "trip_1", 4, isOccupied = false)
            )
            dao.insertSeats(seatsTrip1)
            dao.insertBooking(
                BookingEntity(
                    id = "MTK-10291",
                    tripId = "trip_1",
                    passengerName = "Қайрат",
                    passengerPhone = "+7 771 111 2233",
                    seatNumbers = "1",
                    seatsCount = 1,
                    totalPrice = 2500,
                    status = "CONFIRMED",
                    pickupPoint = trip1.pickupPoint,
                    dropoffPoint = trip1.dropoffPoint,
                    route = trip1.route,
                    departureDate = trip1.departureDate,
                    departureTime = trip1.departureTime,
                    driverName = trip1.driverName,
                    driverPhone = trip1.driverPhone,
                    carInfo = "${trip1.carMake} ${trip1.carModel} (${trip1.licensePlate})"
                )
            )

            // Trip 2: Ерлан (Hyundai Sonata) Мәртөк → Ақтөбе 10:00 (4 seats, 2 occupied, 2 available)
            val trip2 = TripEntity(
                id = "trip_2",
                driverId = driver2.id,
                driverName = driver2.name,
                driverPhone = driver2.phone,
                carMake = driver2.carMake,
                carModel = driver2.carModel,
                licensePlate = driver2.licensePlate,
                route = "MARTUK_AKTOBE",
                departureDate = todayDate,
                departureTime = "10:00",
                price = 2500,
                capacity = 4,
                availableSeats = 2,
                occupiedSeats = 2,
                status = "SCHEDULED",
                pickupPoint = "Мәртөк: Базар алды",
                dropoffPoint = "Ақтөбе: 12 мкр, Керуен Сити"
            )
            dao.insertTrip(trip2)
            val seatsTrip2 = listOf(
                SeatEntity("trip_2_1", "trip_2", 1, isOccupied = true, bookingId = "MTK-28451", passengerName = "Айдана", passengerPhone = "+7 775 222 3344"),
                SeatEntity("trip_2_2", "trip_2", 2, isOccupied = true, bookingId = "MTK-28451", passengerName = "Айдана", passengerPhone = "+7 775 222 3344"),
                SeatEntity("trip_2_3", "trip_2", 3, isOccupied = false),
                SeatEntity("trip_2_4", "trip_2", 4, isOccupied = false)
            )
            dao.insertSeats(seatsTrip2)
            dao.insertBooking(
                BookingEntity(
                    id = "MTK-28451",
                    tripId = "trip_2",
                    passengerName = "Айдана",
                    passengerPhone = "+7 775 222 3344",
                    seatNumbers = "1, 2",
                    seatsCount = 2,
                    totalPrice = 5000,
                    status = "CONFIRMED",
                    pickupPoint = trip2.pickupPoint,
                    dropoffPoint = trip2.dropoffPoint,
                    route = trip2.route,
                    departureDate = trip2.departureDate,
                    departureTime = trip2.departureTime,
                    driverName = trip2.driverName,
                    driverPhone = trip2.driverPhone,
                    carInfo = "${trip2.carMake} ${trip2.carModel} (${trip2.licensePlate})"
                )
            )

            // Trip 3: Нұрлан (Toyota Alphard) Мәртөк → Ақтөбе 12:30 (6 seats, 5 occupied, 1 available)
            val trip3 = TripEntity(
                id = "trip_3",
                driverId = driver3.id,
                driverName = driver3.name,
                driverPhone = driver3.phone,
                carMake = driver3.carMake,
                carModel = driver3.carModel,
                licensePlate = driver3.licensePlate,
                route = "MARTUK_AKTOBE",
                departureDate = todayDate,
                departureTime = "12:30",
                price = 2500,
                capacity = 6,
                availableSeats = 1,
                occupiedSeats = 5,
                status = "SCHEDULED",
                pickupPoint = "Мәртөк: Орталық автобекет",
                dropoffPoint = "Ақтөбе: Орталық базар"
            )
            dao.insertTrip(trip3)
            val seatsTrip3 = listOf(
                SeatEntity("trip_3_1", "trip_3", 1, isOccupied = true, bookingId = "MTK-33411", passengerName = "Дәулет", passengerPhone = "+7 701 999 8877"),
                SeatEntity("trip_3_2", "trip_3", 2, isOccupied = true, bookingId = "MTK-33411", passengerName = "Дәулет", passengerPhone = "+7 701 999 8877"),
                SeatEntity("trip_3_3", "trip_3", 3, isOccupied = true, bookingId = "MTK-45120", passengerName = "Гүлнар", passengerPhone = "+7 707 555 4433"),
                SeatEntity("trip_3_4", "trip_3", 4, isOccupied = true, bookingId = "MTK-45120", passengerName = "Гүлнар", passengerPhone = "+7 707 555 4433"),
                SeatEntity("trip_3_5", "trip_3", 5, isOccupied = true, bookingId = "MTK-45120", passengerName = "Гүлнар", passengerPhone = "+7 707 555 4433"),
                SeatEntity("trip_3_6", "trip_3", 6, isOccupied = false)
            )
            dao.insertSeats(seatsTrip3)

            // Trip 4: Асқар (Kia Carnival) Ақтөбе → Мәртөк 15:00 (6 seats, 6 occupied - FULL!)
            val trip4 = TripEntity(
                id = "trip_4",
                driverId = driver4.id,
                driverName = driver4.name,
                driverPhone = driver4.phone,
                carMake = driver4.carMake,
                carModel = driver4.carModel,
                licensePlate = driver4.licensePlate,
                route = "AKTOBE_MARTUK",
                departureDate = todayDate,
                departureTime = "15:00",
                price = 2500,
                capacity = 6,
                availableSeats = 0,
                occupiedSeats = 6,
                status = "FULL",
                pickupPoint = "Ақтөбе: Автовокзал",
                dropoffPoint = "Мәртөк: Орталық автобекет"
            )
            dao.insertTrip(trip4)
            val seatsTrip4 = (1..6).map { s ->
                SeatEntity("trip_4_$s", "trip_4", s, isOccupied = true, bookingId = "MTK-5590$s", passengerName = "Жолаушы $s", passengerPhone = "+7 700 123 450$s")
            }
            dao.insertSeats(seatsTrip4)

            // Trip 5: Аян (Toyota Camry) Ақтөбе → Мәртөк 17:00 (4 seats, 4 available)
            val trip5 = TripEntity(
                id = "trip_5",
                driverId = driver1.id,
                driverName = driver1.name,
                driverPhone = driver1.phone,
                carMake = driver1.carMake,
                carModel = driver1.carModel,
                licensePlate = driver1.licensePlate,
                route = "AKTOBE_MARTUK",
                departureDate = todayDate,
                departureTime = "17:00",
                price = 2500,
                capacity = 4,
                availableSeats = 4,
                occupiedSeats = 0,
                status = "SCHEDULED",
                pickupPoint = "Ақтөбе: Сапар автовокзалы",
                dropoffPoint = "Мәртөк: Мекен-жайға дейін"
            )
            dao.createTripWithSeats(trip5)

            // Trip 6: Ерлан (Hyundai Sonata) Ақтөбе → Мәртөк 19:30 (4 seats, 4 available)
            val trip6 = TripEntity(
                id = "trip_6",
                driverId = driver2.id,
                driverName = driver2.name,
                driverPhone = driver2.phone,
                carMake = driver2.carMake,
                carModel = driver2.carModel,
                licensePlate = driver2.licensePlate,
                route = "AKTOBE_MARTUK",
                departureDate = todayDate,
                departureTime = "19:30",
                price = 2500,
                capacity = 4,
                availableSeats = 4,
                occupiedSeats = 0,
                status = "SCHEDULED",
                pickupPoint = "Ақтөбе: Мега Ақтөбе (Керуен)",
                dropoffPoint = "Мәртөк: Орталық автобекет"
            )
            dao.createTripWithSeats(trip6)

            // 3. Seed Notifications
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_welcome",
                    title = "Мәртөк Таксиге қош келдіңіз!",
                    message = "Мәртөк пен Ақтөбе арасында такси орындарын сенімді әрі оңай брондаңыз.",
                    type = "ADMIN",
                    recipientRole = "PASSENGER",
                    recipientPhone = ""
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = "notif_trip_booked",
                    title = "Жаңа бронь MTK-28451",
                    message = "Ерлан жүргізушіге 2 орын брондалды.",
                    type = "BOOKING",
                    recipientRole = "DRIVER",
                    recipientPhone = driver2.phone,
                    relatedId = "MTK-28451"
                )
            )
        }
    }
}
