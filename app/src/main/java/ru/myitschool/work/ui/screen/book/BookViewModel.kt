package ru.myitschool.work.ui.screen.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<BookState>(BookState.Loading)
    val uiState: StateFlow<BookState> = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<BookAction>()
    val actionFlow: SharedFlow<BookAction> = _actionFlow

    init {
        loadBookData()
    }

    fun onIntent(intent: BookIntent) {
        when (intent) {
            is BookIntent.LoadData -> loadBookData()
            is BookIntent.Refresh -> refresh()
            is BookIntent.BookPlace -> bookPlace()
            is BookIntent.SelectDate -> selectDate(intent.date)
            is BookIntent.SelectPlace -> selectPlace(intent.place)
        }
    }

    private fun loadBookData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { BookState.Loading }

            try {
                // Временные mock данные
                val mockDates = listOf(
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(2),
                    LocalDate.now().plusDays(3)
                )

                val mockPlaces = mapOf(
                    mockDates[0] to listOf("Место 1", "Место 2", "Место 3"),
                    mockDates[1] to listOf("Место 1", "Место 2"),
                    mockDates[2] to listOf("Место 1")
                )

                val sortedDates = mockDates.sorted()
                val availableDates = sortedDates.filter { mockPlaces[it]?.isNotEmpty() == true }
                val defaultDate = availableDates.firstOrNull()

                _uiState.update {
                    BookState.Data(
                        dates = sortedDates,
                        places = mockPlaces,
                        selectedDate = defaultDate,
                        selectedPlace = null,
                        isError = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    BookState.Data(
                        isError = true,
                        errorMessage = "Ошибка загрузки данных"
                    )
                }
                _actionFlow.emit(BookAction.ShowError("Ошибка загрузки данных"))
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        _uiState.update { currentState ->
            when (currentState) {
                is BookState.Data -> currentState.copy(
                    selectedDate = date,
                    selectedPlace = null
                )
                else -> currentState
            }
        }
    }

    private fun selectPlace(place: String) {
        _uiState.update { currentState ->
            when (currentState) {
                is BookState.Data -> currentState.copy(selectedPlace = place)
                else -> currentState
            }
        }
    }

    private fun bookPlace() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // вызов API для бронирования
                // временная имитация успеха
                _actionFlow.emit(BookAction.BookSuccess)
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    when (currentState) {
                        is BookState.Data -> currentState.copy(
                            isError = true,
                            errorMessage = "Ошибка бронирования"
                        )
                        else -> currentState
                    }
                }
                _actionFlow.emit(BookAction.ShowError("Ошибка бронирования"))
            }
        }
    }

    private fun refresh() {
        loadBookData()
    }
}