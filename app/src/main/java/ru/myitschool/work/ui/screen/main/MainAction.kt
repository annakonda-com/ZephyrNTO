package ru.myitschool.work.ui.screen.main

import ru.myitschool.work.ui.screen.auth.AuthAction

sealed interface MainAction {
    data class SetName(val name: String)
    data class ShowError(val message: String?) : MainAction
}