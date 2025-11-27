package ru.myitschool.work.ui.screen.auth

sealed interface AuthIntent {
    data class Send(val text: String): AuthIntent
    data class TextInput(val text: String): AuthIntent
    object CheckLogIntent: AuthIntent
}