package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User

data class LoginState(
    val username: String = "",
    val usernameError: String? = null,
    val pin: String = "",
    val pinError: String? = null,
    val isPinVisible: Boolean = false,
    val canRegisterAdmin: Boolean = false,
    val showThemeDialog: Boolean = false,
    val loginStatus: LoginStatus = LoginStatus.Idle
)

sealed class LoginStatus {
    object Idle : LoginStatus()
    object Loading : LoginStatus()
    data class Success(val user: User) : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}
