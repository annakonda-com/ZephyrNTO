package ru.myitschool.work.ui.screen.auth

sealed interface AuthState {
    object Loading: AuthState
    object Data: AuthState
    object LoggedIn: AuthState
}