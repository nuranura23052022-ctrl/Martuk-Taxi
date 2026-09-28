package com.example.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Strings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRegisterScreen(
    isKazakh: Boolean,
    onBack: () -> Unit,
    onSubmit: (
        name: String,
        phone: String,
        carMake: String,
        carModel: String,
        licensePlate: String,
        capacity: Int,
        preferredRoute: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+7 ") }
    var carMake by remember { mutableStateOf("Toyota") }
    var carModel by remember { mutableStateOf("Camry") }
    var licensePlate by remember { mutableStateOf("04 KZ ") }
    var capacity by remember { mutableStateOf(4) }
    var preferredRoute by remember { mutableStateOf("MARTUK_AKTOBE_BOTH") }
    var isSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.driverRegistrationTitle(isKazakh),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isSubmitted) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(SeatAvailableBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "OK", tint = SeatAvailableGreen, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isKazakh) "Өтінім сәтті қабылданды!" else "Заявка успешно принята!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isKazakh) 
                                "Аккаунт мәртебесі: «Тексерілуде». Диспетчер құжаттарды тексеріп, мақұлдаған соң сіз рейстер қоса аласыз."
                                else "Статус аккаунта: «На проверке». После проверки диспетчером вы сможете создавать рейсы.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isKazakh) "Түсінікті, артқа" else "Понятно, назад")
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isKazakh) "Жүргізуші ақпараты" else "Информация о водителе",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyDark
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(Strings.fullName(isKazakh)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_reg_name")
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(Strings.phoneNumber(isKazakh)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_reg_phone")
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isKazakh) "Автомобиль мәліметтері" else "Данные автомобиля",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NavyDark
                        )

                        OutlinedTextField(
                            value = carMake,
                            onValueChange = { carMake = it },
                            label = { Text(Strings.carMake(isKazakh)) },
                            leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = "Make") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = carModel,
                            onValueChange = { carModel = it },
                            label = { Text(Strings.carModel(isKazakh)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = licensePlate,
                            onValueChange = { licensePlate = it },
                            label = { Text(Strings.licensePlate(isKazakh)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Capacity Radio / Selector
                        Column {
                            Text(
                                text = Strings.vehicleCapacity(isKazakh) + ":",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FilterChip(
                                    selected = capacity == 4,
                                    onClick = { capacity = 4 },
                                    label = { Text(Strings.seats4(isKazakh), fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                FilterChip(
                                    selected = capacity == 6,
                                    onClick = { capacity = 6 },
                                    label = { Text(Strings.seats6(isKazakh), fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Status Note
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isKazakh) 
                                    "ЕСКЕРТПЕ: Тіркелген соң аккаунт автоматты түрде «Тексерілуде» мәртебесіне өтеді. Диспетчер мақұлдағанға дейін рейстер жариялай алмайсыз."
                                    else "ПРИМЕЧАНИЕ: После отправки заявки аккаунт получит статус «На проверке». Рейсы станут доступны после одобрения диспетчером.",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (name.isNotBlank() && phone.isNotBlank()) {
                                    onSubmit(name, phone, carMake, carModel, licensePlate, capacity, preferredRoute)
                                    isSubmitted = true
                                }
                            },
                            enabled = name.isNotBlank() && phone.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = KazakhAzure),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_driver_reg_btn")
                        ) {
                            Text(
                                text = Strings.submitRegistration(isKazakh),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
