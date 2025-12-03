package ru.myitschool.work.ui.screen.book

sealed interface BookAction {
    data class ShowError(val message: String?) : BookAction
    object BookSuccess : BookAction
}