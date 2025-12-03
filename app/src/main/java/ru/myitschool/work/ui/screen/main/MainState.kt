package ru.myitschool.work.ui.screen.main

import ru.myitschool.work.data.entity.Employee
import ru.myitschool.work.ui.screen.auth.AuthState

sealed interface MainState {
    object Loading: MainState
    data class Data (val employee: Employee?): MainState
}