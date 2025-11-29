package ru.myitschool.work.ui.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import ru.myitschool.work.ui.nav.BookScreenDestination

// Модель данных для бронирования
data class BookingItem(
    val date: String, // Формат "dd.MM.yyyy"
    val place: String,
    val id: Int
)

@Composable
fun MainScreen(
    navController: NavController,
    onNavigateToBooking: () -> Unit
) {
    // Состояния
    var userName by remember { mutableStateOf("Иван Иванов") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var bookingItems by remember { mutableStateOf(emptyList<BookingItem>()) }
    var hasError by remember { mutableStateOf(false) }

    // Для корутин
    val coroutineScope = rememberCoroutineScope()

    // Функция загрузки данных
    fun loadData() {
        isLoading = true
        hasError = false

        coroutineScope.launch {
            kotlinx.coroutines.delay(1000) // Имитация задержки

            // Имитация ответа от сервера
            val response = listOf(
                BookingItem("20.12.2023", "Конференц-зал А", 1),
                BookingItem("15.12.2023", "Переговорная Б", 2),
                BookingItem("25.12.2023", "Спортзал", 3)
            )

            // Сортировка по дате (увеличение)
            bookingItems = response.sortedBy {
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(it.date)
            }

            isLoading = false
        }
    }

    // Первая загрузка при открытии экрана
    LaunchedEffect(Unit) {
        loadData()
    }

    // Если ошибка - показываем только ошибку и кнопку обновления
    if (hasError) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Текстовое поле с ошибкой (main_error)
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка обновления (main_refresh_button)
            Button(onClick = { loadData() }) {
                Text("Обновить")
            }
        }
    } else {
        // Нормальное состояние
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Верхняя строка
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Фото пользователя (main_photo)
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Фото",
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Имя пользователя (main_name)
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Кнопка выхода (main_logout_button)
                Button(onClick = {
                    // Очистка данных и переход на авторизацию
                    userName = ""
                    bookingItems = emptyList()
                    navController.navigate("auth") { popUpTo(0) }
                }) {
                    Text("Выход")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Кнопка обновления (main_refresh_button)
                Button(
                    onClick = { loadData() },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Обновить")
                    }
                }

                Button(
                    onClick = { navController.navigate(BookScreenDestination) }
                ) {
                    Text("Перейти к бронированию")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список бронирований
            if (bookingItems.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(bookingItems) { item ->
                        // Элемент списка (main_book_pos_{index})
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Дата бронирования (main_item_date)
                                Text(
                                    text = "Дата: ${item.date}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Место бронирования (main_item_place)
                                Text(
                                    text = "Место: ${item.place}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет бронирований",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
