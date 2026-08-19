package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.SettingsManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeSettings()
    }

    fun onEvent(event: AdminProfileUiEvent) {
        when (event) {
            AdminProfileUiEvent.StartEdit -> _uiState.update { state ->
                state.copy(
                    isEditing = true,
                    editName = state.adminName,
                    editEmail = state.adminEmail,
                    editPhone = state.adminPhone,
                    editBusinessName = state.businessName,
                    pendingProfilePhoto = state.profilePhotoPath,
                    pendingBusinessLogo = state.businessLogoPath
                )
            }
            AdminProfileUiEvent.CancelEdit -> _uiState.update { it.copy(isEditing = false) }
            is AdminProfileUiEvent.NameChanged -> _uiState.update { it.copy(editName = event.value) }
            is AdminProfileUiEvent.EmailChanged -> _uiState.update { it.copy(editEmail = event.value) }
            is AdminProfileUiEvent.PhoneChanged -> _uiState.update { it.copy(editPhone = event.value) }
            is AdminProfileUiEvent.BusinessNameChanged -> _uiState.update { it.copy(editBusinessName = event.value) }
            is AdminProfileUiEvent.ProfilePhotoSelected -> _uiState.update { it.copy(pendingProfilePhoto = event.uri) }
            is AdminProfileUiEvent.BusinessLogoSelected -> _uiState.update { it.copy(pendingBusinessLogo = event.uri) }
            AdminProfileUiEvent.SaveProfile -> saveProfile()
            AdminProfileUiEvent.ShowPinDialog -> _uiState.update { it.copy(showPinDialog = true, oldPin = "", newPin = "", confirmPin = "") }
            AdminProfileUiEvent.HidePinDialog -> _uiState.update { it.copy(showPinDialog = false) }
            is AdminProfileUiEvent.OldPinChanged -> _uiState.update { it.copy(oldPin = event.value.take(4)) }
            is AdminProfileUiEvent.NewPinChanged -> _uiState.update { it.copy(newPin = event.value.take(4)) }
            is AdminProfileUiEvent.ConfirmPinChanged -> _uiState.update { it.copy(confirmPin = event.value.take(4)) }
            AdminProfileUiEvent.SavePin -> savePin()
            AdminProfileUiEvent.ShowNotifications -> _uiState.update { it.copy(showNotificationDialog = true) }
            AdminProfileUiEvent.HideNotifications -> _uiState.update { it.copy(showNotificationDialog = false) }
            is AdminProfileUiEvent.NotificationsChanged -> _uiState.update { it.copy(notificationsEnabled = event.enabled) }
            AdminProfileUiEvent.ShowThemeDialog -> _uiState.update { it.copy(showThemeDialog = true) }
            AdminProfileUiEvent.HideThemeDialog -> _uiState.update { it.copy(showThemeDialog = false) }
            is AdminProfileUiEvent.ThemeModeChanged -> {
                viewModelScope.launch {
                    settingsManager.setThemeMode(event.mode)
                }
            }
            AdminProfileUiEvent.ShowHelp -> _uiState.update { it.copy(showHelpDialog = true) }
            AdminProfileUiEvent.HideHelp -> _uiState.update { it.copy(showHelpDialog = false) }
            AdminProfileUiEvent.MessageShown -> _uiState.update { it.copy(message = null) }
            AdminProfileUiEvent.RequestLogout -> _uiState.update { it.copy(showLogoutConfirmation = true) }
            AdminProfileUiEvent.CancelLogout -> _uiState.update { it.copy(showLogoutConfirmation = false) }
            AdminProfileUiEvent.ConfirmLogout -> performLogout()
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsManager.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            sessionManager.currentUserId.collect { id ->
                if (id != null) {
                    val user = authRepository.getUserById(id)
                    if (user != null) {
                        _uiState.update { it.copy(
                            currentUser = user, adminName = user.nombreCompleto,
                            adminEmail = user.email ?: "", adminPhone = user.telefono,
                            businessName = user.businessName ?: "TaCobrao",
                            profilePhotoPath = user.profilePhotoPath,
                            businessLogoPath = user.businessLogoPath,
                            isLoading = false
                        ) }
                    }
                }
            }
        }
    }

    private fun saveProfile() {
        val current = _uiState.value
        val user = current.currentUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val avatar = persist(current.pendingProfilePhoto, "admin/profiles")
            val logo = persist(current.pendingBusinessLogo, "admin/logos")
            val updated = user.copy(
                nombreCompleto = current.editName,
                email = current.editEmail,
                telefono = current.editPhone,
                businessName = current.editBusinessName,
                profilePhotoPath = avatar,
                businessLogoPath = logo
            )
            authRepository.updateUser(updated)
            _uiState.update { it.copy(isSaving = false, isEditing = false, message = "Perfil actualizado") }
        }
    }

    private fun savePin() {
        val state = _uiState.value
        val user = state.currentUser ?: return
        if (state.oldPin != user.pin) {
            _uiState.update { it.copy(message = "El PIN actual no coincide") }
            return
        }
        if (state.newPin.length != 4 || state.newPin != state.confirmPin) {
            _uiState.update { it.copy(message = "Los nuevos PINs no coinciden o son inválidos") }
            return
        }
        viewModelScope.launch {
            authRepository.updateUser(user.copy(pin = state.newPin))
            _uiState.update { it.copy(showPinDialog = false, message = "PIN actualizado correctamente") }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    private fun persist(path: String?, folder: String): String? {
        if (path == null || !path.startsWith("content://")) return path
        return FileStorageUtil.saveFileToInternalStorage(context, path.toUri(), folder)
    }
}
