package com.example.ui.screens.passenger

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.BookingEntity
import com.example.data.local.entity.SeatEntity
import com.example.data.local.entity.TripEntity
import com.example.model.Strings
import com.example.ui.components.CarSeatLayout
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatBookingSheet(
    trip: TripEntity,
    seats: List<SeatEntity>,
    selectedSeats: Set<Int>,
    passengerName: String,
    passengerPhone: String,
    errorMessage: String?,
    isKazakh: Boolean,
    onSeatClick: (Int) -> Unit,
    onConfirmBooking: (pickup: String, dropoff: String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameState by remember { mutableStateOf(passengerName) }
    var phoneState by remember { mutableStateOf(passengerPhone) }
    var pickupState by remember { mutableStateOf(trip.pickupPoint) }
    var dropoffState by remember { mutableStateOf(trip.dropoffPoint) }

    val totalCost = selectedSeats.size * trip.price

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        modifier = Modifier.testTag("seat_booking_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Strings.seatSelectionTitle(isKazakh),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${trip.carMake} ${trip.carModel} • ${trip.departureTime}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Error alert if any
            if (errorMessage != null) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Visual Car Seat Selector
            CarSeatLayout(
                capacity = trip.capacity,
                seats = seats,
                selectedSeatNumbers = selectedSeats,
                onSeatClick = onSeatClick,
                isKazakh = isKazakh
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Passenger Info Fields
            Text(
                text = if (isKazakh) "Жолаушы мәліметтері" else "Данные пассажира",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = nameState,
                onValueChange = { nameState = it },
                label = { Text(Strings.fullName(isKazakh)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_input_name")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneState,
                onValueChange = { phoneState = it },
                label = { Text(Strings.phoneNumber(isKazakh)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_input_phone")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pickupState,
                onValueChange = { pickupState = it },
                label = { Text(if (isKazakh) "Отырғызу орны (Pickup)" else "Место посадки") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Pickup") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = dropoffState,
                onValueChange = { dropoffState = it },
                label = { Text(if (isKazakh) "Түсіру орны (Dropoff)" else "Место высадки") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Dropoff") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary: Selected seats & Total price
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.selectedSeats(isKazakh),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = if (selectedSeats.isEmpty()) {
                                if (isKazakh) "Орын таңдалмады" else "Не выбрано"
                            } else {
                                selectedSeats.sorted().joinToString(", ") { "$it-орын" }
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = NavyPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Strings.priceLabel(isKazakh),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$totalCost ₸",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = KazakhGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Confirm Button
            Button(
                onClick = { onConfirmBooking(pickupState, dropoffState) },
                enabled = selectedSeats.isNotEmpty() && nameState.isNotBlank() && phoneState.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_booking_submit_btn")
            ) {
                Text(
                    text = "${Strings.confirmBookingBtn(isKazakh)} ($totalCost ₸)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BookingSuccessDialog(
    booking: BookingEntity,
    isKazakh: Boolean,
    onDismiss: () -> Unit,
    onViewBookings: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("booking_success_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Success Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(SeatAvailableBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SeatAvailableGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = Strings.bookingSuccessTitle(isKazakh),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "${Strings.bookingIdLabel(isKazakh)} ${booking.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                // Trip Details Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    InfoRow(
                        label = Strings.routeLabel(isKazakh),
                        value = if (booking.route == "MARTUK_AKTOBE") "Мәртөк → Ақтөбе" else "Ақтөбе → Мәртөк"
                    )
                    InfoRow(label = Strings.timeLabel(isKazakh), value = "${booking.departureDate}, ${booking.departureTime}")
                    InfoRow(label = if (isKazakh) "Орын:" else "Места:", value = booking.seatNumbers)
                    InfoRow(label = Strings.driverLabel(isKazakh), value = booking.driverName)
                    InfoRow(label = Strings.phoneNumber(isKazakh), value = booking.driverPhone)
                    InfoRow(label = Strings.priceLabel(isKazakh), value = "${booking.totalPrice} ₸")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Strings.contactDriverTitle(isKazakh),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Call and WhatsApp buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Call Button
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${booking.driverPhone}")
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("driver_call_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Strings.callBtn(isKazakh), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // WhatsApp Button
                    Button(
                        onClick = {
                            val cleanPhone = booking.driverPhone.replace("[^0-9]".toRegex(), "")
                            val url = "https://wa.me/$cleanPhone?text=Сәлеметсіз бе! Мен $booking.id броны бойынша (${booking.departureDate} ${booking.departureTime}) жазып тұрмын."
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("driver_whatsapp_btn")
                    ) {
                        Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Close / View Bookings buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (isKazakh) "Жабу" else "Закрыть", color = TextSecondary)
                    }

                    TextButton(onClick = {
                        onDismiss()
                        onViewBookings()
                    }) {
                        Text(
                            text = if (isKazakh) "Броньдарды көру →" else "Мои брони →",
                            fontWeight = FontWeight.Bold,
                            color = KazakhAzure
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
