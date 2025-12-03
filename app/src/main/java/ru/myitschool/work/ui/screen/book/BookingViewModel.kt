package ru.myitschool.work.ui.screen.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BookingState())
    val uiState: StateFlow<BookingState> = _uiState.asStateFlow()

    init {
        loadBookingData()
    }

    fun loadBookingData() {
        viewModelScope.launch {
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

                _uiState.value = _uiState.value.copy(
                    dates = sortedDates,
                    places = mockPlaces,
                    selectedDate = defaultDate,
                    selectedPlace = null,
                    isError = false,
                    errorMessage = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = "Ошибка загрузки данных"
                )
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            selectedPlace = null
        )
    }

    fun selectPlace(place: String) {
        _uiState.value = _uiState.value.copy(
            selectedPlace = place
        )
    }

    fun bookPlace() {
        viewModelScope.launch {
            try {
                //вызов API для бронирования
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isError = true,
                    errorMessage = "Ошибка бронирования"
                )
            }
        }
    }

    fun refresh() {
        loadBookingData()
    }
}