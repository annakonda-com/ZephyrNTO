package ru.myitschool.work.ui.screen.book

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.data.entity.Place
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BookScreen(
    onBack: () -> Unit,
    onBookSuccess: () -> Unit
) {
    val viewModel: BookViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.actionFlow) {
        viewModel.actionFlow.collect { action ->
            if (action is BookAction.BookSuccess) {
                onBookSuccess()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(BookIntent.LoadData)
    }

    when (val state = uiState) {
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
                uiState = state,
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
    val sortedDates = uiState.dates.sorted()
    val availableDates = sortedDates.filter { date -> uiState.places[date]?.isNotEmpty() == true }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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

        if (availableDates.isEmpty() && !uiState.isError) {
            Text(
                text = "Всё забронировано",
                modifier = Modifier.testTag(TestIds.Book.EMPTY)
            )
        }

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