package ru.myitschool.work.ui.screen.auth

sealed interface AuthAction {
    data class ShowError(val message: String?) : AuthAction
    data class LogIn(val isLogged: Boolean): AuthAction
    data class AuthBtnEnabled(val enabled: Boolean) : AuthAction
}