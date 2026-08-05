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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
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
            is LoginUiEvent.OnGoogleSignInClick -> performGoogleSignIn(event.context)
            is LoginUiEvent.OnGoogleSignInResult -> {
                viewModelScope.launch {
                    authRepository.registerUser(event.user)
                    uiState = LoginUiState.Success(event.user)
                }
            }
            is LoginUiEvent.ClearError -> clearError()
        }
    }

    private fun performGoogleSignIn(context: Context) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            val googleClient = com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth.GoogleAuthUiClient(context)
            val result = googleClient.signIn()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    authRepository.registerUser(user)
                    uiState = LoginUiState.Success(user)
                } else {
                    uiState = LoginUiState.Error("Error al procesar el usuario de Google")
                }
            } else {
                uiState = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Cancelado o error en Google Sign-In")
            }
        }
    }

    fun onLoginSubmitted() {
        if (!validateInput()) return

        viewModelScope.launch {
            uiState = LoginUiState.Loading

            try {
                // Invoca a tu AuthRepository real con username y pin
                val user = authRepository.login(username.trim(), pin.trim())

                if (user != null) {
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