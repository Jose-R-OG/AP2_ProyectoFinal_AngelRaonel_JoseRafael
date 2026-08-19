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
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        observeData()
        observeSettings()
    }

    fun onEvent(event: AdminProfileUiEvent) {
        when (event) {
            AdminProfileUiEvent.StartEdit -> _uiState.update { state ->
                state.copy(
                    isEditing = true,
                    editName = state.adminName,
                    editNameError = null,
                    editEmail = state.adminEmail,
                    editEmailError = null,
                    editPhone = state.adminPhone,
                    editPhoneError = null,
                    editBusinessName = state.businessName,
                    editBusinessNameError = null,
                    pendingProfilePhoto = state.profilePhotoPath,
                    pendingBusinessLogo = state.businessLogoPath
                )
            }
            AdminProfileUiEvent.CancelEdit -> _uiState.update { it.copy(isEditing = false) }
            is AdminProfileUiEvent.NameChanged -> _uiState.update { it.copy(editName = event.value, editNameError = null) }
            is AdminProfileUiEvent.EmailChanged -> _uiState.update { it.copy(editEmail = event.value, editEmailError = null) }
            is AdminProfileUiEvent.PhoneChanged -> _uiState.update { it.copy(editPhone = event.value, editPhoneError = null) }
            is AdminProfileUiEvent.BusinessNameChanged -> _uiState.update { it.copy(editBusinessName = event.value, editBusinessNameError = null) }
            is AdminProfileUiEvent.ProfilePhotoSelected -> _uiState.update { it.copy(pendingProfilePhoto = event.uri) }
            is AdminProfileUiEvent.BusinessLogoSelected -> _uiState.update { it.copy(pendingBusinessLogo = event.uri) }
            AdminProfileUiEvent.SaveProfile -> saveProfile()
            AdminProfileUiEvent.ShowPinDialog -> _uiState.update { it.copy(showPinDialog = true, oldPin = "", oldPinError = null, newPin = "", newPinError = null, confirmPin = "", confirmPinError = null) }
            AdminProfileUiEvent.HidePinDialog -> _uiState.update { it.copy(showPinDialog = false) }
            is AdminProfileUiEvent.OldPinChanged -> _uiState.update { it.copy(oldPin = event.value.take(4), oldPinError = null) }
            is AdminProfileUiEvent.NewPinChanged -> _uiState.update { it.copy(newPin = event.value.take(4), newPinError = null) }
            is AdminProfileUiEvent.ConfirmPinChanged -> _uiState.update { it.copy(confirmPin = event.value.take(4), confirmPinError = null) }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        viewModelScope.launch {
            sessionManager.currentUserId
                .flatMapLatest { id ->
                    if (id != null) authRepository.observeUserById(id)
                    else flowOf(null)
                }
                .collect { user ->
                    if (user != null) {
                        _uiState.update { it.copy(
                            currentUser = user,
                            adminName = user.nombreCompleto,
                            adminEmail = user.email ?: "",
                            adminPhone = user.telefono,
                            businessName = user.businessName ?: "TaCobrao",
                            profilePhotoPath = user.profilePhotoPath,
                            businessLogoPath = user.businessLogoPath,
                            isLoading = false
                        ) }
                    }
                }
        }
    }

    private fun saveProfile() {
        val s = _uiState.value
        val user = s.currentUser ?: return
        
        val nameError = if (s.editName.isBlank()) "El nombre es obligatorio" else null
        val emailError = if (s.editEmail.isBlank()) "El email es obligatorio" 
                         else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.editEmail).matches()) "Email inválido" 
                         else null
        val phoneError = if (s.editPhone.length != 10) "El teléfono debe tener 10 dígitos" else null
        val businessError = if (s.editBusinessName.isBlank()) "El nombre del negocio es obligatorio" else null

        if (nameError != null || emailError != null || phoneError != null || businessError != null) {
            _uiState.update { it.copy(
                editNameError = nameError,
                editEmailError = emailError,
                editPhoneError = phoneError,
                editBusinessNameError = businessError
            ) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val avatar = persist(s.pendingProfilePhoto, "admin/profiles")
            val logo = persist(s.pendingBusinessLogo, "admin/logos")
            val updated = user.copy(
                nombreCompleto = s.editName.trim(),
                email = s.editEmail.trim(),
                telefono = s.editPhone,
                businessName = s.editBusinessName.trim(),
                profilePhotoPath = avatar,
                businessLogoPath = logo
            )
            authRepository.updateUser(updated)
            _uiState.update { it.copy(isSaving = false, isEditing = false, message = "Perfil actualizado") }
        }
    }

    private fun savePin() {
        val s = _uiState.value
        val user = s.currentUser ?: return
        
        val oldError = if (s.oldPin != user.pin) "El PIN actual es incorrecto" else null
        val newError = if (s.newPin.length != 4) "El PIN debe tener 4 dígitos" else null
        val confirmError = if (s.newPin != s.confirmPin) "Los PINs no coinciden" else null

        if (oldError != null || newError != null || confirmError != null) {
            _uiState.update { it.copy(
                oldPinError = oldError,
                newPinError = newError,
                confirmPinError = confirmError
            ) }
            return
        }

        viewModelScope.launch {
            authRepository.updateUser(user.copy(pin = s.newPin))
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
