package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

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
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth.GoogleAuthUiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject


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
        if (uiState is LoginUiState.Error) {
            uiState = LoginUiState.Idle
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            GoogleAuthUiClient(context).signIn().fold(
                onSuccess = { googleUser ->
                    val users = authRepository.getAllUsers().first()
                    val existing = users.find { user ->
                        user.email?.equals(googleUser.email, true) == true || user.username.equals(googleUser.username, true)
                    }
                    if (existing != null) {
                        if (!existing.isActive) uiState = LoginUiState.Error("La cuenta está desactivada.")
                        else { sessionManager.saveUserId(existing.id); uiState = LoginUiState.Success(existing) }
                    } else if (authRepository.hasAnyAdmin()) {
                        uiState = LoginUiState.Error("Este teléfono ya tiene otro administrador. Usa su cuenta registrada.")
                    } else {
                        authRepository.registerUser(googleUser)
                        val saved = authRepository.login(googleUser.username, googleUser.pin)
                        if (saved == null) uiState = LoginUiState.Error("No fue posible guardar la cuenta de Google.")
                        else { sessionManager.saveUserId(saved.id); canRegisterAdmin = false; uiState = LoginUiState.Success(saved) }
                    }
                },
                onFailure = { uiState = LoginUiState.Error(it.message ?: "No fue posible iniciar sesión con Google.") }
            )
        }
    }
} 
