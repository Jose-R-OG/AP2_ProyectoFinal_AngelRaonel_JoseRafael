package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.dashboard

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.dashboard.DashboardMetrics
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.dashboard.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import javax.inject.Inject

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

class DashboardRepositoryImpl @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val clienteDao: ClienteDao,
    private val userDao: UserDao
) : DashboardRepository {

    override fun getDashboardMetrics(): Flow<DashboardMetrics> {
        return combine(
            prestamoDao.obtenerTodosLosPrestamos(),
            userDao.getAllActiveUsers(),
        ) { prestamos, users ->
            val pending = prestamos.count { it.estado == LoanStatus.PENDIENTE_REVISION }
            val totalEmpl = users.count { it.role == UserRole.EMPLEADO }
            
            DashboardMetrics(
                totalCollectedToday = BigDecimal.ZERO,
                collectedPercentage = 0f,
                activeEmployees = totalEmpl,
                totalEmployees = totalEmpl,
                pendingApprovals = pending,
                recentMovements = emptyList()
            )
        }
    }
}
