package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EmpleadoDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmpleadoDashboardUiState())
    val uiState: StateFlow<EmpleadoDashboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: EmpleadoDashboardUiEvent) {
        when (event) {
            is EmpleadoDashboardUiEvent.RefreshData -> loadData()
            is EmpleadoDashboardUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            else -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.getAllActiveUsers().collect { users ->
                    val currentUser = users.firstOrNull()
                    val formattedDate = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
                        .format(Date())
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }

                    _uiState.update { state ->
                        state.copy(
                            userName = currentUser?.nombreCompleto?.takeIf { it.isNotBlank() } ?: "Carlos Alberto",
                            userRole = currentUser?.role ?: state.userRole,
                            formattedDate = formattedDate,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
