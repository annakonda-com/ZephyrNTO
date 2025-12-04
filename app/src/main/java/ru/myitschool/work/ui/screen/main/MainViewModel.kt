package ru.myitschool.work.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.repo.MainRepository
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase
import ru.myitschool.work.domain.main.GetUserDataUseCase
import ru.myitschool.work.ui.screen.auth.AuthAction
import ru.myitschool.work.ui.screen.auth.AuthIntent
import ru.myitschool.work.ui.screen.auth.AuthState

class MainViewModel : ViewModel() {
    private val repository by lazy{ MainRepository() }
    private val getUserDataUseCase by lazy { GetUserDataUseCase(repository) }

    private val _uiState = MutableStateFlow<MainState>(MainState.Loading)
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    private val _actionFlow: MutableSharedFlow<MainAction> = MutableSharedFlow()
    val actionFlow: SharedFlow<MainAction> = _actionFlow

    init {
        loadData()
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadData ->  {
                loadData()
            }
            is MainIntent.LogOut -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.update { MainState.Data(null) }
                    repository.logOut()
                }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { MainState.Loading }

            getUserDataUseCase.invoke().fold(
                onSuccess = { employee ->
                    _uiState.update { MainState.Data(employee) }
                    _actionFlow.emit(MainAction.ShowError(null))
                },
                onFailure = { error ->
                    error.printStackTrace()
                    if (error.message != null) {
                        _actionFlow.emit(MainAction.ShowError(error.message.toString()))
                    }
                    _uiState.update { MainState.Data(null) }
                }
            )
        }
    }
}