package ru.myitschool.work.ui.screen.book

import java.time.LocalDate

data class BookingState(
    val dates: List<LocalDate> = emptyList(), // список доступных дат
    val places: Map<LocalDate, List<String>> = emptyMap(), // места по датам
    val selectedDate: LocalDate? = null, // выбранная дата
    val selectedPlace: String? = null, // выбранное место
    val isError: Boolean = false, // флаг ошибки
    val errorMessage: String? = null // сообщение об ошибке
)