package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode

sealed class LoginUiEvent {
    data class OnUsernameChanged(val username: String) : LoginUiEvent()
    data class OnPinChanged(val pin: String) : LoginUiEvent()
    data object TogglePinVisibility : LoginUiEvent()
    data object SubmitLogin : LoginUiEvent()
    data object ClearError : LoginUiEvent()
    data object ShowThemeDialog : LoginUiEvent()
    data object HideThemeDialog : LoginUiEvent()
    data class ThemeModeChanged(val mode: ThemeMode) : LoginUiEvent()
}
