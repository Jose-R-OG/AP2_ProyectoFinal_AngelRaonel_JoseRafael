package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val role: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun loginWithEmail(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = LoginState.Error("Por favor completa todos los campos.")
            return
        }

        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    checkUserRole(uid)
                }
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Credenciales incorrectas o error de conexión.")
            }
    }

    private fun checkUserRole(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role") ?: "EMPLEADO"
                    _loginState.value = LoginState.Success(role)
                } else {
                    _loginState.value = LoginState.Error("El usuario no tiene un perfil registrado.")
                }
            }
            .addOnFailureListener {
                _loginState.value = LoginState.Error("Error al consultar permisos de usuario.")
            }
    }
}