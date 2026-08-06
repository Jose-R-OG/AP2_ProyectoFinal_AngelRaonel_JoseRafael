package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.dashboard.GetDashboardMetricsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.Locale

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getDashboardMetricsUseCase: GetDashboardMetricsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                getDashboardMetricsUseCase().collect { metrics ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            adminAvatarUrl = metrics.adminAvatarUrl,
                            totalCollectedToday = String.format(Locale.US, "$ %.2f", metrics.totalCollectedToday),
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