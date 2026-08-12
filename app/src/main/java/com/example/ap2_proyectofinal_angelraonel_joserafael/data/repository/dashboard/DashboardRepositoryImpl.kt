package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.dashboard

import android.util.Log
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.dashboard.DashboardMetrics
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.dashboard.DashboardRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard.MovementItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val prestamoDao: PrestamoDao,
    private val userDao: UserDao,
    private val transaccionDao: TransaccionDao
) : DashboardRepository {

    override fun getDashboardMetrics(): Flow<DashboardMetrics> {
        val inicioDia = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val finDia = Calendar.getInstance().apply {
            timeInMillis = inicioDia
            add(Calendar.DAY_OF_YEAR, 1)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis

        Log.d("DashboardRepo", "getDashboardMetrics: starting flows...")
        return combine(
            prestamoDao.obtenerTodosLosPrestamos().onStart { Log.d("DashboardRepo", "Prestamos flow started") },
            userDao.getAllUsers().onStart { Log.d("DashboardRepo", "Users flow started") },
            transaccionDao.obtenerTransaccionesPorDia(inicioDia, finDia).onStart { Log.d("DashboardRepo", "Transacciones flow started") }
        ) { prestamos, users, transacciones ->
            Log.d("DashboardRepo", "Flows combined! P:${prestamos.size}, U:${users.size}, T:${transacciones.size}")
            val totalCobradoHoy = transacciones
                .filter { it.tipo == TipoTransaccion.INGRESO }
                .fold(BigDecimal.ZERO) { total, transaccion -> total.add(transaccion.monto) }

            val cartera = prestamos.filter {
                it.estado == LoanStatus.ACTIVO || it.estado == LoanStatus.FINALIZADO
            }
            val totalCartera = cartera.fold(BigDecimal.ZERO) { total, prestamo ->
                total.add(prestamo.totalAPagar)
            }
            val totalPagado = cartera.fold(BigDecimal.ZERO) { total, prestamo ->
                total.add(prestamo.totalPagado)
            }
            val porcentajeCobrado = if (totalCartera > BigDecimal.ZERO) {
                totalPagado.divide(totalCartera, 4, RoundingMode.HALF_UP)
                    .toFloat()
                    .coerceIn(0f, 1f)
            } else {
                0f
            }
            val loansInStreet = prestamos.filter { it.estado == LoanStatus.APROBADO || it.estado == LoanStatus.ACTIVO }
            val capitalInStreet = loansInStreet.fold(BigDecimal.ZERO) { total, loan -> total.add(loan.montoSolicitado) }
            val outstandingPortfolio = loansInStreet.fold(BigDecimal.ZERO) { total, loan ->
                total.add(loan.totalAPagar.subtract(loan.totalPagado).max(BigDecimal.ZERO))
            }

            val recentMovements = transacciones.take(5).map { transaccion ->
                MovementItem(
                    id = transaccion.id.toString(),
                    title = if (transaccion.tipo == TipoTransaccion.INGRESO) {
                        "Cobro recibido"
                    } else {
                        "Desembolso realizado"
                    },
                    subtitle = "Préstamo #${transaccion.prestamoId} · ${transaccion.nota}",
                    amountOrStatus = String.format(Locale.US, "RD$ %,.2f", transaccion.monto),
                    time = SimpleDateFormat("h:mm a", Locale("es", "DO"))
                        .format(Date(transaccion.fecha)),
                    isAlert = false
                )
            }

            DashboardMetrics(
                totalCollectedToday = totalCobradoHoy,
                capitalInStreet = capitalInStreet,
                outstandingPortfolio = outstandingPortfolio,
                collectedPercentage = porcentajeCobrado,
                activeEmployees = users.count {
                    it.role == UserRole.EMPLEADO && it.isActive
                },
                totalEmployees = users.count { it.role == UserRole.EMPLEADO },
                pendingApprovals = prestamos.count {
                    it.estado == LoanStatus.PENDIENTE_REVISION
                },
                recentMovements = recentMovements
            )
        }.onStart { Log.d("DashboardRepo", "Combined flow fully started") }
    }
}
