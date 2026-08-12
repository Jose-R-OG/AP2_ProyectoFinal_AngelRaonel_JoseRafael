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
            Log.d("LoginViewModel", "signInWithGoogle started")
            uiState = LoginUiState.Loading
            try {
                val googleAuthUiClient = GoogleAuthUiClient(context)
                val email = googleAuthUiClient.signIn()
                
                Log.d("LoginViewModel", "Google sign in result email: $email")
                if (email != null) {
                    var user = authRepository.loginWithGoogle(email)
                    Log.d("LoginViewModel", "User found in DB: ${user?.username}")
                    
                    if (user == null && !authRepository.hasAnyUser()) {
                        Log.d("LoginViewModel", "System is empty, registering first admin: $email")
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
                        Log.d("LoginViewModel", "User registered and re-fetched: ${user?.username}")
                    }

                    if (user != null) {
                        Log.d("LoginViewModel", "Success! Saving session for userId: ${user.id}")
                        sessionManager.saveUserId(user.id)
                        uiState = LoginUiState.Success(user)
                    } else {
                        Log.w("LoginViewModel", "User not registered and system not empty")
                        uiState = LoginUiState.Error("Este correo ($email) no está registrado. Pida al administrador que lo registre.")
                    }
                } else {
                    Log.w("LoginViewModel", "Email is null (cancelled or error)")
                    uiState = LoginUiState.Idle
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error in signInWithGoogle", e)
                uiState = LoginUiState.Error("Error con Google: ${e.message}")
            }
        }
    }
}
