package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import android.content.Context
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User

sealed class LoginUiEvent {
    data class OnUsernameChanged(val username: String) : LoginUiEvent()
    data class OnPinChanged(val pin: String) : LoginUiEvent()
    data object TogglePinVisibility : LoginUiEvent()
    data object SubmitLogin : LoginUiEvent()
    data object ClearError : LoginUiEvent()
}
