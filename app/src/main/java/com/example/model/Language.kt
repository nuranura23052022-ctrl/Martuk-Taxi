package com.example.model

enum class AppLanguage(val code: String, val label: String) {
    KAZAKH("kk", "ҚАЗ"),
    RUSSIAN("ru", "РУС")
}

enum class UserRole(val code: String) {
    PASSENGER("PASSENGER"),
    DRIVER("DRIVER"),
    ADMIN("ADMIN")
}

enum class RouteDirection(val id: String, val fromKk: String, val toKk: String, val fromRu: String, val toRu: String) {
    MARTUK_TO_AKTOBE("MARTUK_AKTOBE", "Мәртөк", "Ақтөбе", "Мартук", "Актобе"),
    AKTOBE_TO_MARTUK("AKTOBE_MARTUK", "Ақтөбе", "Мәртөк", "Актобе", "Мартук");

    fun getDisplayName(isKazakh: Boolean): String {
        return if (isKazakh) "$fromKk → $toKk" else "$fromRu → $toRu"
    }
}

enum class BookingStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW
}

enum class TripStatus {
    SCHEDULED,
    FULL,
    DEPARTED,
    COMPLETED,
    CANCELLED
}

enum class DriverStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED
}

object Strings {
    fun appTitle(isKk: Boolean) = if (isKk) "Мәртөк ↔ Ақтөбе" else "Мартук ↔ Актобе"
    fun appSubtitle(isKk: Boolean) = if (isKk) "Такси орныңды алдын ала бронда" else "Бронируйте места в такси заранее"
    
    // Roles
    fun rolePassenger(isKk: Boolean) = if (isKk) "Жолаушы" else "Пассажир"
    fun roleDriver(isKk: Boolean) = if (isKk) "Жүргізуші" else "Водитель"
    fun roleAdmin(isKk: Boolean) = if (isKk) "Диспетчер" else "Диспетчер"

    // Search card
    fun routeLabel(isKk: Boolean) = if (isKk) "Бағыт:" else "Маршрут:"
    fun dateLabel(isKk: Boolean) = if (isKk) "Күні" else "Дата"
    fun timeLabel(isKk: Boolean) = if (isKk) "Уақыты" else "Время"
    fun passengersCount(isKk: Boolean) = if (isKk) "Жолаушылар саны" else "Количество пассажиров"
    fun findTaxiBtn(isKk: Boolean) = if (isKk) "Такси табу" else "Найти такси"

    // How it works
    fun howItWorksTitle(isKk: Boolean) = if (isKk) "Қалай жұмыс істейді?" else "Как это работает?"
    fun step1(isKk: Boolean) = if (isKk) "1. Бағыт пен уақытты таңда" else "1. Выберите маршрут и время"
    fun step2(isKk: Boolean) = if (isKk) "2. Бос орынды таңда" else "2. Выберите свободное место"
    fun step3(isKk: Boolean) = if (isKk) "3. Бронь жаса" else "3. Оформите бронь"
    fun step4(isKk: Boolean) = if (isKk) "4. Жүргізушімен байланыс" else "4. Свяжитесь с водителем"

    // Trip card
    fun driverLabel(isKk: Boolean) = if (isKk) "Жүргізуші" else "Водитель"
    fun departureTimeLabel(isKk: Boolean) = if (isKk) "Шығу уақыты" else "Время выезда"
    fun availableSeatsLabel(isKk: Boolean) = if (isKk) "Бос орын" else "Свободно мест"
    fun priceLabel(isKk: Boolean) = if (isKk) "Бағасы" else "Цена"
    fun bookSeatBtn(isKk: Boolean) = if (isKk) "Орын брондау" else "Забронировать место"
    fun carFull(isKk: Boolean) = if (isKk) "КӨЛІК ТОЛДЫ" else "МАШИНА ЗАПОЛНЕНА"
    fun seatsOccupied(isKk: Boolean, occupied: Int, total: Int) = 
        if (isKk) "$occupied / $total орын алынған" else "$occupied / $total мест занято"

    // Booking system
    fun seatSelectionTitle(isKk: Boolean) = if (isKk) "Орын таңдау" else "Выбор мест"
    fun seatNumberLabel(isKk: Boolean, number: Int) = if (isKk) "$number-орын" else "Место $number"
    fun driverSeatLabel(isKk: Boolean) = if (isKk) "Жүргізуші орны" else "Место водителя"
    fun selectedSeats(isKk: Boolean) = if (isKk) "Таңдалған орындар" else "Выбранные места"
    fun confirmBookingBtn(isKk: Boolean) = if (isKk) "Брондауды растау" else "Подтвердить бронь"
    fun bookingSuccessTitle(isKk: Boolean) = if (isKk) "Бронь сәтті жасалды!" else "Бронь успешно создана!"
    fun bookingIdLabel(isKk: Boolean) = if (isKk) "Бронь №:" else "Бронь №:"
    fun contactDriverTitle(isKk: Boolean) = if (isKk) "Жүргізушімен байланысу" else "Связаться с водителем"
    fun callBtn(isKk: Boolean) = if (isKk) "Қоңырау шалу" else "Позвонить"
    fun whatsappBtn(isKk: Boolean) = "WhatsApp"
    fun cancelBookingBtn(isKk: Boolean) = if (isKk) "Броньды жою" else "Отменить бронь"
    fun bookingCancelledTitle(isKk: Boolean) = if (isKk) "Бронь жойылды" else "Бронь отменена"

    // Driver dashboard
    fun myTrips(isKk: Boolean) = if (isKk) "Менің рейстерім" else "Мои рейсы"
    fun addNewTripBtn(isKk: Boolean) = if (isKk) "+ Жаңа рейс қосу" else "+ Добавить новый рейс"
    fun driverStatusPending(isKk: Boolean) = if (isKk) "Тексерілуде" else "На проверке"
    fun driverStatusApproved(isKk: Boolean) = if (isKk) "Мақұлданған" else "Одобрен"
    fun driverStatusRejected(isKk: Boolean) = if (isKk) "Қабылданбаған" else "Отклонен"
    fun driverRegistrationTitle(isKk: Boolean) = if (isKk) "Жүргізушіні тіркеу" else "Регистрация водителя"
    fun fullName(isKk: Boolean) = if (isKk) "Аты-жөні" else "ФИО"
    fun phoneNumber(isKk: Boolean) = if (isKk) "Телефон нөмірі" else "Номер телефона"
    fun carMake(isKk: Boolean) = if (isKk) "Автомобиль маркасы" else "Марка автомобиля"
    fun carModel(isKk: Boolean) = if (isKk) "Автомобиль моделі" else "Модель автомобиля"
    fun licensePlate(isKk: Boolean) = if (isKk) "Мемлекеттік нөмірі" else "Гос. номер"
    fun vehicleCapacity(isKk: Boolean) = if (isKk) "Көлік сыйымдылығы" else "Вместимость автомобиля"
    fun seats4(isKk: Boolean) = if (isKk) "4 орын" else "4 места"
    fun seats6(isKk: Boolean) = if (isKk) "6 орын" else "6 мест"
    fun submitRegistration(isKk: Boolean) = if (isKk) "Тіркелуге өтінім жіберу" else "Отправить заявку на регистрацию"

    // Admin
    fun adminDashboard(isKk: Boolean) = if (isKk) "Диспетчер басқару панелі" else "Панель диспетчера"
    fun todaysTrips(isKk: Boolean) = if (isKk) "Бүгінгі рейстер" else "Рейсы на сегодня"
    fun totalDrivers(isKk: Boolean) = if (isKk) "Жүргізушілер" else "Все водители"
    fun totalPassengers(isKk: Boolean) = if (isKk) "Жолаушылар" else "Пассажиры"
    fun activeBookings(isKk: Boolean) = if (isKk) "Белсенді броньдар" else "Активные брони"
    fun availableSeatsStat(isKk: Boolean) = if (isKk) "Бос орындар" else "Свободные места"
    fun completedTrips(isKk: Boolean) = if (isKk) "Аяқталған рейстер" else "Завершенные рейсы"
    fun realTimeTripBoard(isKk: Boolean) = if (isKk) "Диспетчерлік тақта (Real-Time)" else "Диспетчерская доска (Real-Time)"
    fun approveDriver(isKk: Boolean) = if (isKk) "Мақұлдау" else "Одобрить"
    fun rejectDriver(isKk: Boolean) = if (isKk) "Қабылдамау" else "Отклонить"
    fun movePassenger(isKk: Boolean) = if (isKk) "Басқа көлікке ауыстыру" else "Пересадить на другое авто"
    fun manualAssign(isKk: Boolean) = if (isKk) "Жолаушы қосу" else "Назначить пассажира"

    // Navigation
    fun navHome(isKk: Boolean) = if (isKk) "Басты бет" else "Главная"
    fun navTrips(isKk: Boolean) = if (isKk) "Рейстер" else "Рейсы"
    fun navMyBookings(isKk: Boolean) = if (isKk) "Менің броньдарым" else "Мои брони"
    fun navPassengers(isKk: Boolean) = if (isKk) "Жолаушылар" else "Пассажиры"
    fun navDrivers(isKk: Boolean) = if (isKk) "Жүргізушілер" else "Водители"
    fun navProfile(isKk: Boolean) = if (isKk) "Профиль" else "Профиль"
    fun navDashboard(isKk: Boolean) = if (isKk) "Статистика" else "Статистика"
}
