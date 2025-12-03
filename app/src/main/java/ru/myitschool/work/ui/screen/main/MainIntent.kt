package ru.myitschool.work.ui.screen.main

sealed interface MainIntent {
    /* data class Send(val text: String): AuthIntent
    data class TextInput(val text: String): AuthIntent
    object CheckLogIntent: AuthIntent*/
    object LoadData: MainIntent
    object LogOut: MainIntent
}