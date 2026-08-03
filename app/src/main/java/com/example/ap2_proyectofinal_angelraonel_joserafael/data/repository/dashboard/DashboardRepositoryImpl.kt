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

class DashboardRepositoryImpl @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val clienteDao: ClienteDao,
    private val userDao: UserDao
) : DashboardRepository {

    override fun getDashboardMetrics(): Flow<DashboardMetrics> {
        return combine(
            prestamoDao.obtenerTodosLosPrestamos(),
            userDao.getAllActiveUsers(),
            // Aquí podrías agregar más flows como cuotas para calcular lo recaudado hoy
        ) { prestamos, users ->
            DashboardMetrics(
                totalCollectedToday = BigDecimal.ZERO, // Implementar lógica real con cobros/cuotas
                collectedPercentage = 0f,
                activeEmployees = users.size,
                totalEmployees = users.size, // Ajustar si hay lógica de empleados activos/totales
                pendingApprovals = 0,
                recentMovements = emptyList()
            )
        }
    }
}
