package ru.myitschool.work.ui.screen.book

import ru.myitschool.work.data.entity.Place
import java.time.LocalDate

sealed interface BookState {
    object Loading : BookState
    data class Data(
        val dates: List<LocalDate> = emptyList(),
        val places: Map<LocalDate, List<Place>> = emptyMap(),
        val selectedDate: LocalDate? = null,
        val selectedPlace: Place? = null,
        val isError: Boolean = false,
        val errorMessage: String? = null
    ) : BookState
}