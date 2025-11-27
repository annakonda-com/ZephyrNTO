package ru.myitschool.work.ui.screen.auth

import android.util.Log
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
import ru.myitschool.work.data.source.DataStoreDataSource.authFlow
import ru.myitschool.work.domain.auth.CheckAndSaveAuthCodeUseCase

class AuthViewModel : ViewModel() {
    private val checkAndSaveAuthCodeUseCase by lazy { CheckAndSaveAuthCodeUseCase(AuthRepository) }
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Data)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _actionFlow: MutableSharedFlow<AuthAction> = MutableSharedFlow()
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
                                _actionFlow.emit(AuthAction.ShowError(error.message.toString()))
                            }
                        }
                    )
                }
            }
            is AuthIntent.TextInput -> Unit
            is AuthIntent.CheckLogIntent -> {
                viewModelScope.launch {
                    _uiState.update { AuthState.Loading }
                    authFlow().collect {
                        Log.d("AnnaKonda", it)
                        if (it != "0"){
                            _uiState.update { AuthState.LoggedIn }
                        } else {
                            _uiState.update { AuthState.Data }
                        }
                    }
                }
            }
        }
    }

}