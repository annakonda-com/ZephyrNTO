package ru.myitschool.work.ui.screen.book

import java.time.LocalDate

sealed interface BookIntent {
    object LoadData : BookIntent
    object Refresh : BookIntent
    object BookPlace : BookIntent
    data class SelectDate(val date: LocalDate) : BookIntent
    data class SelectPlace(val place: String) : BookIntent
}