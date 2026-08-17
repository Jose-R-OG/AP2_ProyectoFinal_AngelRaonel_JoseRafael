package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User

data class AdminProfileUiState(
    val currentUser: User? = null,
    val adminName: String = "",
    val adminEmail: String = "",
    val adminPhone: String = "",
    val businessName: String = "TaCobrao",
    val profilePhotoPath: String? = null,
    val businessLogoPath: String? = null,
    val editName: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val editBusinessName: String = "",
    val pendingProfilePhoto: String? = null,
    val pendingBusinessLogo: String? = null,
    val oldPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val notificationsEnabled: Boolean = true,
    val isEditing: Boolean = false,
    val showPinDialog: Boolean = false,
    val showNotificationDialog: Boolean = false,
    val showHelpDialog: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isLoggedOut: Boolean = false,
    val message: String? = null
)
