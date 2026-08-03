package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
