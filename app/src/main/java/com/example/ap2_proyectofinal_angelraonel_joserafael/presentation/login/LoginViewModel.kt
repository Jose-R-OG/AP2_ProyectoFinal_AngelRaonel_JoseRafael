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
            Log.d("LoginViewModel", "signInWithGoogle: Pulse el botón")
            uiState = LoginUiState.Loading
            try {
                val googleAuthUiClient = GoogleAuthUiClient(context)
                Log.d("LoginViewModel", "signInWithGoogle: Llamando a googleAuthUiClient.signIn()...")
                val email = googleAuthUiClient.signIn()
                
                Log.d("LoginViewModel", "signInWithGoogle: Correo recibido de Google = $email")
                if (email != null) {
                    Log.d("LoginViewModel", "signInWithGoogle: Buscando usuario en BD local para $email...")
                    var user = authRepository.loginWithGoogle(email)
                    
                    if (user == null) {
                        Log.d("LoginViewModel", "signInWithGoogle: Usuario NO encontrado. Verificando si el sistema está vacío...")
                        val hasUsers = authRepository.hasAnyUser()
                        Log.d("LoginViewModel", "signInWithGoogle: ¿Tiene usuarios el sistema? $hasUsers")
                        
                        if (!hasUsers) {
                            Log.d("LoginViewModel", "signInWithGoogle: Sistema vacío. Registrando primer ADMINISTRADOR automáticamente...")
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
                            Log.d("LoginViewModel", "signInWithGoogle: Registro completado. Re-intentando login...")
                            user = authRepository.loginWithGoogle(email)
                            Log.d("LoginViewModel", "signInWithGoogle: Usuario re-obtenido tras registro: ${user?.username}")
                        }
                    }

                    if (user != null) {
                        Log.d("LoginViewModel", "signInWithGoogle: TODO OK. Guardando sesión para ID: ${user.id} y navegando...")
                        sessionManager.saveUserId(user.id)
                        uiState = LoginUiState.Success(user)
                        Log.d("LoginViewModel", "signInWithGoogle: Estado cambiado a SUCCESS")
                    } else {
                        Log.w("LoginViewModel", "signInWithGoogle: El usuario no existe y no es el primero. Mostrando error.")
                        uiState = LoginUiState.Error("Este correo ($email) no está registrado. Pida al administrador que lo registre.")
                    }
                } else {
                    Log.w("LoginViewModel", "signInWithGoogle: El correo es NULL (cancelado por usuario o error silencioso)")
                    uiState = LoginUiState.Idle
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "signInWithGoogle: EXCEPCIÓN CAPTURADA", e)
                val friendlyMessage = if (e.message?.contains("28444") == true) {
                    "Error de configuración (28444): Verifique SHA-1 en Google Console."
                } else {
                    "Error con Google: ${e.message}"
                }
                uiState = LoginUiState.Error(friendlyMessage)
            }
        }
    }
}
