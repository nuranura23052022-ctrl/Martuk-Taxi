package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: String,
    isKazakh: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        "FULL" -> Triple(
            SeatOccupiedBg,
            StatusFull,
            if (isKazakh) "КӨЛІК ТОЛДЫ" else "МАШИНА ЗАПОЛНЕНА"
        )
        "SCHEDULED" -> Triple(
            SeatAvailableBg,
            StatusScheduled,
            if (isKazakh) "Жоспарланған" else "Запланирован"
        )
        "DEPARTED" -> Triple(
            Color(0xFFFEF3C7),
            StatusDeparted,
            if (isKazakh) "Жолға шықты" else "В пути"
        )
        "COMPLETED" -> Triple(
            Color(0xFFF1F5F9),
            StatusCompleted,
            if (isKazakh) "Аяқталды" else "Завершен"
        )
        "CANCELLED" -> Triple(
            Color(0xFFFEE2E2),
            Color(0xFFDC2626),
            if (isKazakh) "Тоқтатылды" else "Отменен"
        )
        "PENDING" -> Triple(
            Color(0xFFFEF3C7),
            Color(0xFFD97706),
            if (isKazakh) "Тексерілуде" else "На проверке"
        )
        "APPROVED" -> Triple(
            Color(0xFFD1FAE5),
            Color(0xFF059669),
            if (isKazakh) "Мақұлданған" else "Одобрен"
        )
        "REJECTED" -> Triple(
            Color(0xFFFEE2E2),
            Color(0xFFDC2626),
            if (isKazakh) "Қабылданбады" else "Отклонен"
        )
        else -> Triple(
            Color(0xFFF1F5F9),
            Color(0xFF475569),
            status
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
