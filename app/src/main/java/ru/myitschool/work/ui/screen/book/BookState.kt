package ru.myitschool.work.ui.screen.book

import java.time.LocalDate

sealed interface BookState {
    object Loading : BookState
    data class Data(
        val dates: List<LocalDate> = emptyList(),
        val places: Map<LocalDate, List<String>> = emptyMap(),
        val selectedDate: LocalDate? = null,
        val selectedPlace: String? = null,
        val isError: Boolean = false,
        val errorMessage: String? = null
    ) : BookState
}