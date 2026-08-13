package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth.GoogleAuthUiClient
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var username by mutableStateOf("")
    var pin by mutableStateOf("")
    var isPinVisible by mutableStateOf(false)
    var canRegisterAdmin by mutableStateOf(false)
        private set

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    init {
        viewModelScope.launch { canRegisterAdmin = !authRepository.hasAnyAdmin() }
    }

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
                    sessionManager.saveUserId(user.id)
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
        if (uiState is LoginUiState.Idle || uiState is LoginUiState.Error) {
            uiState = LoginUiState.Idle
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            
            kotlinx.coroutines.delay(200)
            
            try {
                val googleAuthUiClient = GoogleAuthUiClient(context)
                
                val email = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    googleAuthUiClient.signIn()
                }
                
                if (email == null) {
                    uiState = LoginUiState.Error("Google no devolvió ningún correo. Verifique su conexión o intente de nuevo.")
                    return@launch
                }

                var user = authRepository.loginWithGoogle(email)
                
                if (user == null) {
                    val hasUsers = authRepository.hasAnyUser()
                    
                    if (!hasUsers) {
                        val newUser = User(
                            id = 0,
                            nombreCompleto = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                            username = email.substringBefore("@"),
                            identificacion = "00000000000",
                            telefono = "0000000000",
                            email = email,
                            pin = "1234",
                            role = UserRole.ADMINISTRADOR,
                            isActive = true
                        )
                        authRepository.registerUser(newUser)
                        user = authRepository.loginWithGoogle(email)
                    }
                }

                if (user != null) {
                    sessionManager.saveUserId(user.id)
                    uiState = LoginUiState.Success(user)
                } else {
                    uiState = LoginUiState.Error("La cuenta $email no está autorizada. Contacte al administrador.")
                }
                
            } catch (e: Exception) {
                val friendlyMessage = if (e.message?.contains("28444") == true) {
                    "Error de configuración (28444): Verifique SHA-1 en Google Console."
                } else {
                    "Error de autenticación: ${e.message ?: "Desconocido"}"
                }
                uiState = LoginUiState.Error(friendlyMessage)
            }
        }
    }
}
