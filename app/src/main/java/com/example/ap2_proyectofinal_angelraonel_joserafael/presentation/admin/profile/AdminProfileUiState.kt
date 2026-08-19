package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode

data class AdminProfileUiState(
    val currentUser: User? = null,
    val adminName: String = "",
    val adminEmail: String = "",
    val adminPhone: String = "",
    val businessName: String = "TaCobrao",
    val profilePhotoPath: String? = null,
    val businessLogoPath: String? = null,
    
    val editName: String = "",
    val editNameError: String? = null,
    val editEmail: String = "",
    val editEmailError: String? = null,
    val editPhone: String = "",
    val editPhoneError: String? = null,
    val editBusinessName: String = "",
    val editBusinessNameError: String? = null,
    
    val pendingProfilePhoto: String? = null,
    val pendingBusinessLogo: String? = null,
    
    val oldPin: String = "",
    val oldPinError: String? = null,
    val newPin: String = "",
    val newPinError: String? = null,
    val confirmPin: String = "",
    val confirmPinError: String? = null,

    val notificationsEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isEditing: Boolean = false,
    val showPinDialog: Boolean = false,
    val showNotificationDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isLoggedOut: Boolean = false,
    val message: String? = null
)
