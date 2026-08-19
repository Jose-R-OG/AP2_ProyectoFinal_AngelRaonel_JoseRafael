package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth.GoogleAuthUiClient
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.SettingsManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    val themeMode: StateFlow<ThemeMode> = settingsManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM
    )

    init {
        viewModelScope.launch {
            val canRegister = !authRepository.hasAnyAdmin()
            _uiState.update { it.copy(canRegisterAdmin = canRegister) }
        }
    }

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnUsernameChanged -> _uiState.update { it.copy(username = event.username, usernameError = null) }
            is LoginUiEvent.OnPinChanged -> _uiState.update { it.copy(pin = event.pin, pinError = null) }
            is LoginUiEvent.TogglePinVisibility -> _uiState.update { it.copy(isPinVisible = !it.isPinVisible) }
            is LoginUiEvent.SubmitLogin -> onLoginSubmitted()
            is LoginUiEvent.ClearError -> clearError()
            LoginUiEvent.ShowThemeDialog -> _uiState.update { it.copy(showThemeDialog = true) }
            LoginUiEvent.HideThemeDialog -> _uiState.update { it.copy(showThemeDialog = false) }
            is LoginUiEvent.ThemeModeChanged -> {
                viewModelScope.launch {
                    settingsManager.setThemeMode(event.mode)
                }
            }
        }
    }

    private fun onLoginSubmitted() {
        val s = _uiState.value
        
        val usernameError = if (s.username.isBlank()) "Ingrese su usuario" else null
        val pinError = if (s.pin.isBlank()) "Ingrese su PIN" else null

        if (usernameError != null || pinError != null) {
            _uiState.update { it.copy(usernameError = usernameError, pinError = pinError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loginStatus = LoginStatus.Loading) }
            try {
                val user = authRepository.login(s.username.trim(), s.pin.trim())
                if (user != null) {
                    sessionManager.saveUserId(user.id)
                    _uiState.update { it.copy(loginStatus = LoginStatus.Success(user)) }
                } else {
                    _uiState.update { it.copy(loginStatus = LoginStatus.Error("Usuario o PIN incorrectos")) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(loginStatus = LoginStatus.Error(e.message ?: "Error al intentar iniciar sesión")) }
            }
        }
    }

    fun clearError() {
        _uiState.update { 
            if (it.loginStatus is LoginStatus.Error) it.copy(loginStatus = LoginStatus.Idle) else it
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(loginStatus = LoginStatus.Loading) }
            
            kotlinx.coroutines.delay(200)
            
            try {
                val googleAuthUiClient = GoogleAuthUiClient(context)
                
                val email = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    googleAuthUiClient.signIn()
                }
                
                if (email == null) {
                    _uiState.update { it.copy(loginStatus = LoginStatus.Error("Google no devolvió ningún correo. Verifique su conexión o intente de nuevo.")) }
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
                    _uiState.update { it.copy(loginStatus = LoginStatus.Success(user)) }
                } else {
                    _uiState.update { it.copy(loginStatus = LoginStatus.Error("La cuenta $email no está autorizada. Contacte al administrador.")) }
                }
                
            } catch (e: Exception) {
                val friendlyMessage = if (e.message?.contains("28444") == true) {
                    "Error de configuración (28444): Verifique SHA-1 en Google Console."
                } else {
                    "Error de autenticación: ${e.message ?: "Desconocido"}"
                }
                _uiState.update { it.copy(loginStatus = LoginStatus.Error(friendlyMessage)) }
            }
        }
    }
}
