package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first() ?: return@launch
            authRepository.observeUserById(userId).collect { user ->
                user ?: return@collect
                _uiState.update { state ->
                    state.copy(
                        currentUser = user,
                        adminName = user.nombreCompleto,
                        adminEmail = user.email ?: user.username,
                        adminPhone = user.telefono,
                        businessName = user.businessName?.takeIf(String::isNotBlank) ?: "TacoBrao",
                        profilePhotoPath = user.profilePhotoPath,
                        businessLogoPath = user.businessLogoPath,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: AdminProfileUiEvent) {
        when (event) {
            AdminProfileUiEvent.StartEdit -> startEdit()
            AdminProfileUiEvent.CancelEdit -> _uiState.update { it.copy(isEditing = false, message = null) }
            is AdminProfileUiEvent.NameChanged -> _uiState.update { it.copy(editName = event.value.take(80)) }
            is AdminProfileUiEvent.EmailChanged -> _uiState.update { it.copy(editEmail = event.value.take(120)) }
            is AdminProfileUiEvent.PhoneChanged -> _uiState.update { it.copy(editPhone = event.value.filter(Char::isDigit).take(10)) }
            is AdminProfileUiEvent.BusinessNameChanged -> _uiState.update { it.copy(editBusinessName = event.value.take(60)) }
            is AdminProfileUiEvent.ProfilePhotoSelected -> _uiState.update { it.copy(pendingProfilePhoto = event.uri) }
            is AdminProfileUiEvent.BusinessLogoSelected -> _uiState.update { it.copy(pendingBusinessLogo = event.uri) }
            AdminProfileUiEvent.SaveProfile -> saveProfile()
            AdminProfileUiEvent.ShowPinDialog -> _uiState.update { it.copy(showPinDialog = true, message = null) }
            AdminProfileUiEvent.HidePinDialog -> _uiState.update { it.copy(showPinDialog = false, message = null) }
            is AdminProfileUiEvent.OldPinChanged -> _uiState.update { it.copy(oldPin = event.value.filter(Char::isDigit).take(8)) }
            is AdminProfileUiEvent.NewPinChanged -> _uiState.update { it.copy(newPin = event.value.filter(Char::isDigit).take(8)) }
            is AdminProfileUiEvent.ConfirmPinChanged -> _uiState.update { it.copy(confirmPin = event.value.filter(Char::isDigit).take(8)) }
            AdminProfileUiEvent.SavePin -> savePin()
            AdminProfileUiEvent.ShowNotifications -> _uiState.update { it.copy(showNotificationDialog = true) }
            AdminProfileUiEvent.HideNotifications -> _uiState.update { it.copy(showNotificationDialog = false) }
            is AdminProfileUiEvent.NotificationsChanged -> _uiState.update { it.copy(notificationsEnabled = event.enabled) }
            AdminProfileUiEvent.ShowHelp -> _uiState.update { it.copy(showHelpDialog = true) }
            AdminProfileUiEvent.HideHelp -> _uiState.update { it.copy(showHelpDialog = false) }
            AdminProfileUiEvent.MessageShown -> _uiState.update { it.copy(message = null) }
            AdminProfileUiEvent.RequestLogout -> _uiState.update { it.copy(showLogoutConfirmation = true) }
            AdminProfileUiEvent.CancelLogout -> _uiState.update { it.copy(showLogoutConfirmation = false) }
            AdminProfileUiEvent.ConfirmLogout -> logout()
        }
    }

    private fun startEdit() {
        val user = _uiState.value.currentUser ?: return
        _uiState.update {
            it.copy(
                isEditing = true,
                editName = user.nombreCompleto,
                editEmail = user.email.orEmpty(),
                editPhone = user.telefono.filter(Char::isDigit).take(10),
                editBusinessName = user.businessName ?: "TacoBrao",
                pendingProfilePhoto = user.profilePhotoPath,
                pendingBusinessLogo = user.businessLogoPath,
                message = null
            )
        }
    }

    private fun saveProfile() {
        val state = _uiState.value
        val user = state.currentUser ?: return
        if (state.editName.isBlank() || state.editPhone.isBlank() || state.editBusinessName.isBlank()) {
            _uiState.update { it.copy(message = "Nombre, teléfono y nombre del negocio son obligatorios.") }
            return
        }
        if (state.editPhone.length != 10) {
            _uiState.update { it.copy(message = "El teléfono debe tener exactamente 10 dígitos (${state.editPhone.length}/10).") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val profilePath = persistIfNeeded(state.pendingProfilePhoto, "profiles/admin")
                val logoPath = persistIfNeeded(state.pendingBusinessLogo, "profiles/business")
                authRepository.updateUser(
                    user.copy(
                        nombreCompleto = state.editName.trim(),
                        email = state.editEmail.trim().ifBlank { null },
                        telefono = state.editPhone.trim(),
                        businessName = state.editBusinessName.trim(),
                        profilePhotoPath = profilePath,
                        businessLogoPath = logoPath
                    )
                )
                _uiState.update { it.copy(isSaving = false, isEditing = false, message = "Perfil actualizado correctamente.") }
            } catch (exception: Exception) {
                _uiState.update { it.copy(isSaving = false, message = exception.message ?: "No se pudo guardar el perfil.") }
            }
        }
    }

    private fun persistIfNeeded(path: String?, folder: String): String? {
        if (path.isNullOrBlank() || !path.startsWith("content://")) return path
        return FileStorageUtil.saveFileToInternalStorage(context, path.toUri(), folder)
            ?: throw IllegalStateException("No se pudo guardar la imagen seleccionada.")
    }

    private fun savePin() {
        val state = _uiState.value
        val user = state.currentUser ?: return
        when {
            state.oldPin != user.pin -> _uiState.update { it.copy(message = "El PIN actual no es correcto.") }
            state.newPin.length < 4 -> _uiState.update { it.copy(message = "El PIN nuevo debe tener al menos 4 dígitos.") }
            state.newPin != state.confirmPin -> _uiState.update { it.copy(message = "Los PIN nuevos no coinciden.") }
            else -> viewModelScope.launch {
                authRepository.updateUser(user.copy(pin = state.newPin))
                _uiState.update {
                    it.copy(
                        showPinDialog = false,
                        oldPin = "",
                        newPin = "",
                        confirmPin = "",
                        message = "PIN actualizado correctamente."
                    )
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiState.update { it.copy(isLoggedOut = true, showLogoutConfirmation = false) }
        }
    }
}
