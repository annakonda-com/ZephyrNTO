package ru.myitschool.work.ui.screen.main

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.data.entity.Booking
import ru.myitschool.work.data.entity.Employee
import ru.myitschool.work.ui.nav.AuthScreenDestination
import ru.myitschool.work.ui.nav.BookScreenDestination

@Composable
fun MainScreen(
    navController: NavController,
) {
    val viewModel = MainViewModel()
    // Состояния
    val event = viewModel.actionFlow.collectAsState(initial = null)
    // Функция загрузки данных
    LaunchedEffect(Unit) {
        viewModel.onIntent(MainIntent.LoadData)
    }

    var errorMessage: String? by remember { mutableStateOf("") }
    LaunchedEffect(event.value) {
        if (event.value is MainAction.ShowError) {
            errorMessage = (event.value as MainAction.ShowError).message
        }
    }
    Log.d("AnnaKonda", errorMessage.toString())
    // Если ошибка - показываем только ошибку и кнопку обновления
    if (errorMessage != null) {
        ErrorScreen(viewModel = viewModel, navController = navController, errorMessage)
    } else {
        DefaultScreen(viewModel = viewModel, navController = navController)
    }
}
@Composable
fun DefaultScreen(viewModel: MainViewModel,
                  navController: NavController){
    val state by viewModel.uiState.collectAsState()
    var employee : Employee? by remember { mutableStateOf(null) }
    var errorMessage by remember { mutableStateOf("") }
    var bookingItems : List<Booking?>? by remember { mutableStateOf(emptyList<Booking>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(state) {
        when (state) {
            is MainState.Loading -> {
                errorMessage = ""
                isLoading = true
            }
            is MainState.Data -> {
                isLoading = false
                employee = (state as MainState.Data).employee
                if (employee == null){
                    navController.navigate(AuthScreenDestination) { popUpTo(0) }
                } else {
                    bookingItems = employee?.bookingList?.sortedBy { item ->
                        item?.date
                    }
                }
            }
        }
    }
    employee?.let {
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
                employee?.photoUrl?.let { msg -> Log.d("AnnaKonda", msg) }
                AsyncImage(
                    model = employee?.photoUrl ?: "",
                    contentDescription = "Фото",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .testTag(TestIds.Main.PROFILE_IMAGE),
                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Имя пользователя (main_name)
                Text(
                    text = employee!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f).testTag(TestIds.Main.PROFILE_NAME),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Кнопка выхода (main_logout_button)
                Button(
                    onClick = {
                        // Очистка данных и переход на авторизацию
                        viewModel.onIntent(MainIntent.LogOut)
                        bookingItems = emptyList()
                    },
                    modifier = Modifier.testTag(TestIds.Main.LOGOUT_BUTTON)
                ) {
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
                    onClick = { viewModel.onIntent(MainIntent.LoadData) },
                    enabled = state !is MainState.Loading,
                    modifier = Modifier.testTag(TestIds.Main.REFRESH_BUTTON)
                ) {
                    if (state is MainState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Обновить")
                    }
                }
                // кнопка бронирования
                Button(
                    onClick = {
                        navController.navigate(BookScreenDestination)
                    },
                    modifier = Modifier.testTag(TestIds.Main.ADD_BUTTON)
                ) {
                    Text("Перейти к бронированию")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список бронирований
            if (!bookingItems.isNullOrEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f)

                ) {
                    itemsIndexed(bookingItems as List<Booking?>) { index, item ->
                        // Элемент списка (main_book_pos_{index})
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                                    .testTag(TestIds.Main.getIdItemByPosition(index))
                            ) {
                                // Дата бронирования (main_item_date)
                                Text(
                                    text = "Дата: ${item?.date}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.testTag(TestIds.Main.ITEM_DATE)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Место бронирования (main_item_place)
                                Text(
                                    text = "Место: ${item?.place?.place}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.testTag(TestIds.Main.ITEM_PLACE)
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




@Composable
fun ErrorScreen(viewModel: MainViewModel,
                navController: NavController,
                errorMessage: String?){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Текстовое поле с ошибкой (main_error)

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.testTag(TestIds.Main.ERROR)

            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.onIntent(MainIntent.LoadData) }) {
            Text("Обновить")
        }
    }
}
