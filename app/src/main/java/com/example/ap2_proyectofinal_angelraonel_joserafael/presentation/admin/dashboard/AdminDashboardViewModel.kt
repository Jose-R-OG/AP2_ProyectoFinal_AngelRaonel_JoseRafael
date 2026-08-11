package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.dashboard.GetDashboardMetricsUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.NotificationRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Locale
import kotlinx.coroutines.flow.first

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getDashboardMetricsUseCase: GetDashboardMetricsUseCase,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeProfileAndNotifications()
    }

    private fun observeProfileAndNotifications() {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first() ?: return@launch
            launch {
                authRepository.observeUserById(userId).collect { user ->
                    user?.let {
                        _uiState.update { state ->
                            state.copy(
                                adminAvatarUrl = it.profilePhotoPath,
                                businessName = it.businessName?.takeIf(String::isNotBlank) ?: "TacoBrao",
                                businessLogoUrl = it.businessLogoPath
                            )
                        }
                    }
                }
            }
            launch {
                notificationRepository.observeUnreadCount(userId).collect { count ->
                    _uiState.update { it.copy(unreadNotifications = count) }
                }
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                getDashboardMetricsUseCase().collect { metrics ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalCollectedToday = String.format(Locale.US, "RD$ %,.2f", metrics.totalCollectedToday),
                            capitalInStreet = String.format(Locale.US, "RD$ %,.2f", metrics.capitalInStreet),
                            outstandingPortfolio = String.format(Locale.US, "RD$ %,.2f", metrics.outstandingPortfolio),
                            collectedPercentage = "${(metrics.collectedPercentage * 100).toInt()}%",
                            activeEmployees = metrics.activeEmployees,
                            totalEmployees = metrics.totalEmployees,
                            pendingApprovals = metrics.pendingApprovals,
                            recentMovements = metrics.recentMovements,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: AdminDashboardUiEvent) {
        when (event) {
            is AdminDashboardUiEvent.Refresh -> loadDashboardData()
        }
    }

    fun onRefresh() {
        loadDashboardData()
    }
}
