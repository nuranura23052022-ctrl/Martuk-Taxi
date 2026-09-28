package com.example.ui.screens.passenger

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BookingEntity
import com.example.model.Strings
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun PassengerBookingsScreen(
    bookings: List<BookingEntity>,
    isKazakh: Boolean,
    onCancelBooking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bookingToCancel by remember { mutableStateOf<BookingEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp)
    ) {
        // Title
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)) {
            Text(
                text = Strings.navMyBookings(isKazakh),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyDark
            )
            Text(
                text = if (isKazakh) "Сіздің барлық белсенді және өткен броньдарыңыз" else "Все ваши активные и завершенные брони",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = "Empty",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isKazakh) "Сізде әзірге бронь жоқ" else "У вас пока нет броней",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isKazakh) "Басты беттен қажетті бағытқа такси табыңыз" else "Найдите такси на главном экране",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(bookings, key = { it.id }) { booking ->
                    BookingCardItem(
                        booking = booking,
                        isKazakh = isKazakh,
                        onCallDriver = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${booking.driverPhone}")
                            }
                            context.startActivity(intent)
                        },
                        onWhatsappDriver = {
                            val cleanPhone = booking.driverPhone.replace("[^0-9]".toRegex(), "")
                            val url = "https://wa.me/$cleanPhone?text=Сәлеметсіз бе! Мен ${booking.id} броны бойынша жазып тұрмын."
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        onCancelClick = {
                            bookingToCancel = booking
                        }
                    )
                }
            }
        }
    }

    // Cancellation confirmation alert dialog
    bookingToCancel?.let { b ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color(0xFFDC2626)) },
            title = {
                Text(
                    text = Strings.cancelBookingBtn(isKazakh),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isKazakh) 
                        "Бронь № ${b.id} күшін жойғыңыз келе ме? Брондалған орындар автоматты түрде қайта босатылады."
                        else "Вы действительно хотите отменить бронь № ${b.id}? Забронированные места снова станут доступными."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelBooking(b.id)
                        bookingToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_cancel_booking_btn")
                ) {
                    Text(
                        text = if (isKazakh) "Иә, жою" else "Да, отменить",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text(if (isKazakh) "Артқа" else "Назад")
                }
            }
        )
    }
}

@Composable
private fun BookingCardItem(
    booking: BookingEntity,
    isKazakh: Boolean,
    onCallDriver: () -> Unit,
    onWhatsappDriver: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isCancelled = booking.status == "CANCELLED"
    val routeDisplay = if (booking.route == "MARTUK_AKTOBE") {
        if (isKazakh) "Мәртөк → Ақтөбе" else "Мартук → Актобе"
    } else {
        if (isKazakh) "Ақтөбе → Мәртөк" else "Актобе → Мартук"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "Ticket",
                        tint = NavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = booking.id,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NavyDark
                    )
                }

                StatusBadge(
                    status = booking.status,
                    isKazakh = isKazakh
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Route & Time info
            Text(
                text = routeDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${booking.departureDate} • ${booking.departureTime}",
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Details Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (isKazakh) "Орындар:" else "Места:", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "${booking.seatNumbers} (${booking.seatsCount} орын)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = Strings.driverLabel(isKazakh) + ":", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "${booking.driverName} (${booking.carInfo})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = Strings.priceLabel(isKazakh) + ":", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "${booking.totalPrice} ₸", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = KazakhGold)
                }
            }

            if (!isCancelled) {
                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons: Call, WhatsApp, Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCallDriver,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Strings.callBtn(isKazakh), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onWhatsappDriver,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(if (isKazakh) "Жою" else "Отмена", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
