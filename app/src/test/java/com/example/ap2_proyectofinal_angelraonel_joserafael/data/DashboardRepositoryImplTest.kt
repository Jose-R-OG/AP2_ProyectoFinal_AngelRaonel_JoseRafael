package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.dashboard

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

class DashboardRepositoryImplTest {

    private lateinit var prestamoDao: PrestamoDao
    private lateinit var userDao: UserDao
    private lateinit var transaccionDao: TransaccionDao
    private lateinit var repository: DashboardRepositoryImpl

    @Before
    fun setUp() {
        prestamoDao = mockk(relaxed = true)
        userDao = mockk(relaxed = true)
        transaccionDao = mockk(relaxed = true)
        repository = DashboardRepositoryImpl(prestamoDao, userDao, transaccionDao)
    }

    @Test
    fun getDashboardMetrics_returnsCorrectMetrics() = runTest {
        val prestamo1 = PrestamoEntity(
            id = 1L, clienteId = 1L, empleadoId = 1L, aprobadoPorAdminId = null,
            montoSolicitado = BigDecimal("1000.00"), porcentajeInteres = BigDecimal("20.00"),
            interesTotal = BigDecimal("200.00"), totalAPagar = BigDecimal("1200.00"),
            totalPagado = BigDecimal("600.00"), montoCuota = BigDecimal("100.00"),
            cantidadCuotas = 12, frecuenciaPago = FrecuenciaPago.MENSUAL,
            diaPagoPreferido = null, diaPagoDescripcion = null,
            fechaCreacion = 1L, fechaInicio = null, fechaFin = null,
            estado = LoanStatus.ACTIVO, motivoRechazo = null,
            rutaFotoContratoFirmado = null, contratoFisicoEntregado = false
        )
        val prestamo2 = PrestamoEntity(
            id = 2L, clienteId = 2L, empleadoId = 1L, aprobadoPorAdminId = null,
            montoSolicitado = BigDecimal("500.00"), porcentajeInteres = BigDecimal("10.00"),
            interesTotal = BigDecimal("50.00"), totalAPagar = BigDecimal("550.00"),
            totalPagado = BigDecimal("0.00"), montoCuota = BigDecimal("55.00"),
            cantidadCuotas = 10, frecuenciaPago = FrecuenciaPago.SEMANAL,
            diaPagoPreferido = null, diaPagoDescripcion = null,
            fechaCreacion = 1L, fechaInicio = null, fechaFin = null,
            estado = LoanStatus.PENDIENTE_REVISION, motivoRechazo = null,
            rutaFotoContratoFirmado = null, contratoFisicoEntregado = false
        )
        every { prestamoDao.obtenerTodosLosPrestamos() } returns flowOf(listOf(prestamo1, prestamo2))

        val user1 = UserEntity(
            id = 1L, nombreCompleto = "Empleado 1", username = "emp1", identificacion = "123",
            telefono = "809", pin = "1234", role = UserRole.EMPLEADO, isActive = true
        )
        val user2 = UserEntity(
            id = 2L, nombreCompleto = "Empleado 2", username = "emp2", identificacion = "124",
            telefono = "809", pin = "1234", role = UserRole.EMPLEADO, isActive = false
        )
        every { userDao.getAllUsers() } returns flowOf(listOf(user1, user2))

        val transaccion = TransaccionEntity(
            id = 1L, prestamoId = 1L, cuotaId = null, empleadoId = 1L,
            monto = BigDecimal("500.00"), fecha = System.currentTimeMillis(),
            tipo = TipoTransaccion.INGRESO, nota = "Pago", paymentMethod = "EFECTIVO"
        )
        every { transaccionDao.obtenerTransaccionesPorDia(any(), any()) } returns flowOf(listOf(transaccion))

        val result = repository.getDashboardMetrics().first()

        TestCase.assertEquals(BigDecimal("500.00"), result.totalCollectedToday)
        TestCase.assertEquals(BigDecimal("1000.00"), result.capitalInStreet)
        TestCase.assertEquals(BigDecimal("600.00"), result.outstandingPortfolio)

        val expectedPercentage = BigDecimal("600.00").divide(BigDecimal("1200.00"), 4, RoundingMode.HALF_UP).toFloat()
        TestCase.assertEquals(expectedPercentage, result.collectedPercentage)

        TestCase.assertEquals(1, result.activeEmployees)
        TestCase.assertEquals(2, result.totalEmployees)
        TestCase.assertEquals(1, result.pendingApprovals)
        TestCase.assertEquals(1, result.recentMovements.size)
        TestCase.assertEquals("Cobro recibido", result.recentMovements[0].title)
    }

    @Test
    fun getDashboardMetrics_returnsZero_whenNoData() = runTest {
        every { prestamoDao.obtenerTodosLosPrestamos() } returns flowOf(emptyList())
        every { userDao.getAllUsers() } returns flowOf(emptyList())
        every { transaccionDao.obtenerTransaccionesPorDia(any(), any()) } returns flowOf(emptyList())

        val result = repository.getDashboardMetrics().first()

        TestCase.assertEquals(BigDecimal.ZERO, result.totalCollectedToday)
        TestCase.assertEquals(BigDecimal.ZERO, result.capitalInStreet)
        TestCase.assertEquals(BigDecimal.ZERO, result.outstandingPortfolio)
        TestCase.assertEquals(0f, result.collectedPercentage)
        TestCase.assertEquals(0, result.activeEmployees)
        TestCase.assertEquals(0, result.totalEmployees)
        TestCase.assertEquals(0, result.pendingApprovals)
        TestCase.assertTrue(result.recentMovements.isEmpty())
    }
}