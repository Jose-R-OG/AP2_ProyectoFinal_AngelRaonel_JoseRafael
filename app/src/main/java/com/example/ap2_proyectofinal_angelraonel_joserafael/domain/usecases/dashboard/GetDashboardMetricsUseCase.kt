package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.dashboard

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.dashboard.DashboardMetrics
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.dashboard.DashboardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDashboardMetricsUseCase @Inject constructor(
    private val repository: DashboardRepository
) {
    operator fun invoke(): Flow<DashboardMetrics> {
        return repository.getDashboardMetrics()
    }
}
