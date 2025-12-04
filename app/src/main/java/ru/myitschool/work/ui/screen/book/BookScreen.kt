package ru.myitschool.work.ui.screen.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.data.entity.Place

@Composable
fun BookScreen(
    onBack: () -> Unit,
    onBookSuccess: () -> Unit
) {
    val viewModel: BookViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Обработка действий
    val event = viewModel.actionFlow.collectAsState(initial = null)
    LaunchedEffect(event.value) {
        when (event.value) {
            is BookAction.BookSuccess -> {
                onBookSuccess()
            }
            else -> {}
        }
    }

    // Загрузка начальных данных
    LaunchedEffect(Unit) {
        viewModel.onIntent(BookIntent.LoadData)
    }

    when (uiState) {
        is BookState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is BookState.Data -> {
            BookContentScreen(
                uiState = uiState as BookState.Data,
                onSelectDate = { date -> viewModel.onIntent(BookIntent.SelectDate(date)) },
                onSelectPlace = { place -> viewModel.onIntent(BookIntent.SelectPlace(place)) },
                onBook = { viewModel.onIntent(BookIntent.BookPlace) },
                onBack = onBack,
                onRefresh = { viewModel.onIntent(BookIntent.Refresh) }
            )
        }
    }
}

@Composable
fun BookContentScreen(
    uiState: BookState.Data,
    onSelectDate: (LocalDate) -> Unit,
    onSelectPlace: (Place) -> Unit,
    onBook: () -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    // Сортировка дат по порядку
    val sortedDates = uiState.dates.sorted()
    // Фильтрация дат, для которых есть доступные места
    val availableDates = sortedDates.filter { date -> uiState.places[date]?.isNotEmpty() == true }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Вкладки для выбора дат
        if (availableDates.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = availableDates.indexOf(uiState.selectedDate),
            ) {
                availableDates.forEachIndexed { index, date ->
                    Tab(
                        selected = date == uiState.selectedDate,
                        onClick = { onSelectDate(date) },
                        text = {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd.MM")),
                                modifier = Modifier.testTag(TestIds.Book.getIdDateItemByPosition(index))
                            )
                        },
                        modifier = Modifier.testTag(TestIds.Book.ITEM_DATE)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Список мест для выбранной даты
        val placesForDate = uiState.selectedDate?.let { uiState.places[it] } ?: emptyList()

        if (placesForDate.isNotEmpty()) {
            Column {
                placesForDate.forEachIndexed { index, place ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = uiState.selectedPlace == place,
                                onClick = { onSelectPlace(place) }
                            )
                            .testTag(TestIds.Book.getIdPlaceItemByPosition(index)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = place.place,
                            modifier = Modifier.weight(1f).testTag(TestIds.Book.ITEM_PLACE_TEXT)
                        )
                        RadioButton(
                            selected = uiState.selectedPlace == place,
                            onClick = { onSelectPlace(place) },
                            modifier = Modifier.testTag(TestIds.Book.ITEM_PLACE_SELECTOR)
                        )
                    }
                }
            }
        }

        // пустой список (все забронировано)
        if (availableDates.isEmpty() && !uiState.isError) {
            Text(
                text = "Всё забронировано",
                modifier = Modifier.testTag(TestIds.Book.EMPTY)
            )
        }

        // ошибка
        if (uiState.isError) {
            Text(
                text = uiState.errorMessage ?: "Ошибка загрузки",
                color = Color.Red,
                modifier = Modifier.testTag(TestIds.Book.ERROR)
            )

            Button(
                onClick = onRefresh,
                modifier = Modifier.testTag(TestIds.Book.REFRESH_BUTTON)
            ) {
                Text("Обновить")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопки: Забронировать и Назад
        if (!uiState.isError && placesForDate.isNotEmpty()) {
            Button(
                onClick = onBook,
                enabled = uiState.selectedPlace != null, // активна только при выбранном месте
                modifier = Modifier.fillMaxWidth().testTag(TestIds.Book.BOOK_BUTTON)
            ) { Text("Забронировать") }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag(TestIds.Book.BACK_BUTTON)
        ) {
            Text("Назад")
        }
    }
}