package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.auth.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login.LoginUiState
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var username by mutableStateOf("")
    var pin by mutableStateOf("")
    var isPinVisible by mutableStateOf(false)

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnUsernameChanged -> username = event.username
            is LoginUiEvent.OnPinChanged -> pin = event.pin
            is LoginUiEvent.TogglePinVisibility -> isPinVisible = !isPinVisible
            is LoginUiEvent.SubmitLogin -> onLoginSubmitted()
            is LoginUiEvent.ClearError -> clearError()
        }
    }


    fun onLoginSubmitted() {
        if (!validateInput()) return

        viewModelScope.launch {
            uiState = LoginUiState.Loading

            try {
                val user = authRepository.login(username.trim(), pin.trim())

                if (user != null) {
                    sessionManager.saveSession(user.id, user.role)
                    uiState = LoginUiState.Success(user)
                } else {
                    uiState = LoginUiState.Error("Usuario o PIN incorrectos")
                }
            } catch (e: Exception) {
                uiState = LoginUiState.Error(e.message ?: "Error al intentar iniciar sesión")
            }
        }
    }

    private fun validateInput(): Boolean {
        if (username.isBlank()) {
            uiState = LoginUiState.Error("Ingrese su usuario")
            return false
        }
        if (pin.isBlank()) {
            uiState = LoginUiState.Error("Ingrese su PIN")
            return false
        }
        return true
    }

    fun clearError() {
        if (uiState is LoginUiState.Error) {
            uiState = LoginUiState.Idle
        }
    }
}