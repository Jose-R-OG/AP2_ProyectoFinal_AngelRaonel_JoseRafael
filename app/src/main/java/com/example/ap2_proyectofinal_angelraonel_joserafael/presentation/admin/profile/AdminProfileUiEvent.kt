package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode

sealed interface AdminProfileUiEvent {
    data object StartEdit : AdminProfileUiEvent
    data object CancelEdit : AdminProfileUiEvent
    data class NameChanged(val value: String) : AdminProfileUiEvent
    data class EmailChanged(val value: String) : AdminProfileUiEvent
    data class PhoneChanged(val value: String) : AdminProfileUiEvent
    data class BusinessNameChanged(val value: String) : AdminProfileUiEvent
    data class ProfilePhotoSelected(val uri: String) : AdminProfileUiEvent
    data class BusinessLogoSelected(val uri: String) : AdminProfileUiEvent
    data object SaveProfile : AdminProfileUiEvent
    data object ShowPinDialog : AdminProfileUiEvent
    data object HidePinDialog : AdminProfileUiEvent
    data class OldPinChanged(val value: String) : AdminProfileUiEvent
    data class NewPinChanged(val value: String) : AdminProfileUiEvent
    data class ConfirmPinChanged(val value: String) : AdminProfileUiEvent
    data object SavePin : AdminProfileUiEvent
    data object ShowNotifications : AdminProfileUiEvent
    data object HideNotifications : AdminProfileUiEvent
    data class NotificationsChanged(val enabled: Boolean) : AdminProfileUiEvent
    data object ShowThemeDialog : AdminProfileUiEvent
    data object HideThemeDialog : AdminProfileUiEvent
    data class ThemeModeChanged(val mode: ThemeMode) : AdminProfileUiEvent
    data object ShowHelp : AdminProfileUiEvent
    data object HideHelp : AdminProfileUiEvent
    data object MessageShown : AdminProfileUiEvent
    data object RequestLogout : AdminProfileUiEvent
    data object CancelLogout : AdminProfileUiEvent
    data object ConfirmLogout : AdminProfileUiEvent
}
