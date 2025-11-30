package ru.myitschool.work.ui.screen.book

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BookingScreen(
    uiState: BookingUiState, // состояние интерфейса
    onSelectDate: (LocalDate) -> Unit, // callback при выборе даты
    onSelectPlace: (String) -> Unit, // callback при выборе места
    onBook: () -> Unit, // callback при бронировании
    onBack: () -> Unit, // callback при нажатии "Назад"
    onRefresh: () -> Unit // callback при обновлении
) {
    // Сортировка дат по порядку
    val sortedDates = uiState.dates.sorted()
    // Фильтрация дат, для которых есть доступные места
    val availableDates = sortedDates.filter { date -> uiState.places[date]?.isNotEmpty() == true }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Вкладки для выбора дат
        if (availableDates.isNotEmpty()) {
            ScrollableTabRow(selectedTabIndex = availableDates.indexOf(uiState.selectedDate)) {
                availableDates.forEachIndexed { index, date ->
                    Tab(
                        selected = date == uiState.selectedDate,
                        onClick = { onSelectDate(date) },
                        text = {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd.MM")),
                                modifier = Modifier.testTag("book_date_pos_$index")
                            )
                        },
                        modifier = Modifier.testTag("book_date")
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
                            .testTag("book_place_pos_$index"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = place,
                            modifier = Modifier.weight(1f).testTag("book_place_text")
                        )
                        RadioButton(
                            selected = uiState.selectedPlace == place,
                            onClick = { onSelectPlace(place) },
                            modifier = Modifier.testTag("book_place_selector")
                        )
                    }
                }
            }
        }

        // пустой список (все забронировано)
        if (availableDates.isEmpty() && !uiState.isError) {
            Text(
                text = "Всё забронировано",
                modifier = Modifier.testTag("book_empty")
            )
        }

        // ошибка
        if (uiState.isError) {
            Text(
                text = uiState.errorMessage ?: "Ошибка загрузки",
                color = Color.Red,
                modifier = Modifier.testTag("book_error")
            )

            Button(
                onClick = onRefresh,
                modifier = Modifier.testTag("book_refresh_button")
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
                modifier = Modifier.fillMaxWidth().testTag("book_book_button")
            ) { Text("Забронировать") }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).testTag("book_back_button")
        ) {
            Text("Назад")
        }
    }
}

// Модель состояния интерфейса
data class BookingUiState(
    val dates: List<LocalDate> = emptyList(), // список доступных дат
    val places: Map<LocalDate, List<String>> = emptyMap(), // места по датам
    val selectedDate: LocalDate? = null, // выбранная дата
    val selectedPlace: String? = null, // выбранное место
    val isError: Boolean = false, // флаг ошибки
    val errorMessage: String? = null // сообщение об ошибке
)

@Composable
fun BookScreen(
    onBack: () -> Unit, // callback при возврате назад
    onBookingSuccess: () -> Unit // callback при успешном бронировании
) {
    val viewModel: BookingViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    BookingScreen(
        uiState = uiState,
        onSelectDate = { date -> viewModel.selectDate(date) },
        onSelectPlace = { place -> viewModel.selectPlace(place) },
        onBook = {
            viewModel.bookPlace()
            onBookingSuccess()
        },
        onBack = onBack,
        onRefresh = { viewModel.refresh() }
    )
}