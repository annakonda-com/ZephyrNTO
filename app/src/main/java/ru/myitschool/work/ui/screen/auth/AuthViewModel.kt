package ru.myitschool.work.ui.screen.auth

import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.myitschool.work.App
import ru.myitschool.work.R
import ru.myitschool.work.core.Utils.Companion.CheckCodeInput
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.data.source.DataStoreDataSource.authFlow
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase

class AuthViewModel : ViewModel() {
    private val checkAndSaveAuthCodeUseCase by lazy { CheckAndSaveAuthCodeUseCase(AuthRepository) }
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Data)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _actionFlow: MutableSharedFlow<AuthAction> = MutableSharedFlow(replay = 1)
    val actionFlow: SharedFlow<AuthAction> = _actionFlow

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Send -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.update { AuthState.Loading }
                    checkAndSaveAuthCodeUseCase.invoke(intent.text).fold(
                        onSuccess = {
                            _uiState.update { AuthState.LoggedIn }
                        },
                        onFailure = { error ->
                            error.printStackTrace()
                            if (error.message != null) {
                                _actionFlow.emit(AuthAction.ShowError(error.message))
                                _uiState.update { AuthState.Data }
                            }

                        }
                    )
                }
            }

            is AuthIntent.TextInput -> {
                viewModelScope.launch {
                    authFlow().collect {
                        if (CheckCodeInput(intent.text)) {
                            _actionFlow.emit(AuthAction.AuthBtnEnabled(true))
                        } else {
                            _actionFlow.emit(AuthAction.AuthBtnEnabled(false))
                        }
                    }
                }
            }

            is AuthIntent.CheckLogIntent -> {
                viewModelScope.launch {
                    _uiState.update { AuthState.Loading }
                    val authCode = authFlow().first()
                    if (authCode != "0") {
                        _actionFlow.emit(AuthAction.LogIn(true))
                        _uiState.update { AuthState.LoggedIn }
                    } else {
                        _uiState.update { AuthState.Data }
                    }

                }
            }
        }
    }
}