package ru.myitschool.work.ui.screen.auth

sealed interface AuthAction {
    data class ShowError(val message: String) : AuthAction
    
}