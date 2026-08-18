package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.LoanStatusHistoryEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.PrestamoRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class PrestamoRepositoryImplTest {

    private lateinit var prestamoDao: PrestamoDao
    private lateinit var repository: PrestamoRepositoryImpl

    @Before
    fun setUp() {
        prestamoDao = mockk(relaxed = true)
        repository = PrestamoRepositoryImpl(prestamoDao)
    }

    @Test
    fun `guardarPrestamo guarda correctamente`() = runTest {
        val prestamo = Prestamo(
            id = 0L, clienteId = 1L, empleadoId = 2L,
            montoSolicitado = BigDecimal("5000.00"), porcentajeInteres = BigDecimal("10.0"),
            interesTotal = BigDecimal("500.00"), totalAPagar = BigDecimal("5500.00"),
            montoCuota = BigDecimal("550.00"), cantidadCuotas = 10,
            frecuenciaPago = mockk(), estado = mockk()
        )
        val slot = slot<PrestamoEntity>()
        coEvery { prestamoDao.insertarPrestamo(capture(slot)) } returns 10L

        val result = repository.guardarPrestamo(prestamo)

        TestCase.assertEquals(10L, result)
        coVerify { prestamoDao.insertarPrestamo(any()) }
        TestCase.assertEquals(1L, slot.captured.clienteId)
        TestCase.assertEquals(BigDecimal("5000.00"), slot.captured.montoSolicitado)
        TestCase.assertEquals(10, slot.captured.cantidadCuotas)
    }

    @Test
    fun `guardarCuotas inserta la lista de cuotas`() = runTest {
        val cuotas = listOf(
            Cuota(id = 1L, prestamoId = 10L, numeroCuota = 1, fechaVencimiento = 1000L, montoEsperado = BigDecimal("100.0")),
            Cuota(id = 2L, prestamoId = 10L, numeroCuota = 2, fechaVencimiento = 2000L, montoEsperado = BigDecimal("100.0"))
        )
        val slot = slot<List<CuotaEntity>>()
        coEvery { prestamoDao.insertarCuotas(capture(slot)) } returns Unit

        repository.guardarCuotas(cuotas)

        coVerify { prestamoDao.insertarCuotas(any()) }
        TestCase.assertEquals(2, slot.captured.size)
        TestCase.assertEquals(1L, slot.captured[0].id)
        TestCase.assertEquals(2L, slot.captured[1].id)
    }

    @Test
    fun `obtenerTodosLosPrestamos mapea correctamente`() = runTest {
        val entities = listOf(
            PrestamoEntity(id = 1L, clienteId = 1L, empleadoId = 2L, aprobadoPorAdminId = null, montoSolicitado = BigDecimal("1000"), porcentajeInteres = BigDecimal("10"), interesTotal = BigDecimal("100"), totalAPagar = BigDecimal("1100"), totalPagado = BigDecimal("0"), montoCuota = BigDecimal("110"), cantidadCuotas = 10, frecuenciaPago = mockk(), diaPagoPreferido = null, diaPagoDescripcion = null, fechaCreacion = 1L, fechaInicio = null, fechaFin = null, estado = mockk(), motivoRechazo = null, rutaFotoContratoFirmado = null, contratoFisicoEntregado = false)
        )
        every { prestamoDao.obtenerTodosLosPrestamos() } returns flowOf(entities)

        val result = repository.obtenerTodosLosPrestamos().first()

        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(1L, result[0].id)
        TestCase.assertEquals(BigDecimal("1000"), result[0].montoSolicitado)
    }

    @Test
    fun `obtenerPrestamosPorEstado mapea correctamente`() = runTest {
        val estadoMock: LoanStatus = mockk()
        val entities = listOf(
            PrestamoEntity(id = 1L, clienteId = 1L, empleadoId = 2L, aprobadoPorAdminId = null, montoSolicitado = BigDecimal("1000"), porcentajeInteres = BigDecimal("10"), interesTotal = BigDecimal("100"), totalAPagar = BigDecimal("1100"), totalPagado = BigDecimal("0"), montoCuota = BigDecimal("110"), cantidadCuotas = 10, frecuenciaPago = mockk(), diaPagoPreferido = null, diaPagoDescripcion = null, fechaCreacion = 1L, fechaInicio = null, fechaFin = null, estado = estadoMock, motivoRechazo = null, rutaFotoContratoFirmado = null, contratoFisicoEntregado = false)
        )
        every { prestamoDao.obtenerPrestamosPorEstado(estadoMock) } returns flowOf(entities)

        val result = repository.obtenerPrestamosPorEstado(estadoMock).first()

        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(1L, result[0].id)
    }

    @Test
    fun `obtenerCuotasPorPrestamo mapea correctamente`() = runTest {
        val prestamoId = 10L
        val entities = listOf(
            CuotaEntity(id = 1L, prestamoId = prestamoId, numeroCuota = 1, fechaVencimiento = 1000L, fechaPago = null, montoEsperado = BigDecimal("500"), montoPagado = BigDecimal("0"), moraAcumulada = BigDecimal("0"), estaPagada = false)
        )
        every { prestamoDao.obtenerCuotasPorPrestamo(prestamoId) } returns flowOf(entities)

        val result = repository.obtenerCuotasPorPrestamo(prestamoId).first()

        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(prestamoId, result[0].prestamoId)
        TestCase.assertEquals(BigDecimal("500"), result[0].montoEsperado)
    }

    @Test
    fun `obtenerRutaDeCobro mapea correctamente`() = runTest {
        val fechaLimite = 2000L
        val entities = listOf(
            CuotaEntity(id = 2L, prestamoId = 10L, numeroCuota = 2, fechaVencimiento = 1000L, fechaPago = null, montoEsperado = BigDecimal("500"), montoPagado = BigDecimal("0"), moraAcumulada = BigDecimal("0"), estaPagada = false)
        )
        every { prestamoDao.obtenerRutaDeCobro(fechaLimite) } returns flowOf(entities)

        val result = repository.obtenerRutaDeCobro(fechaLimite).first()

        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(2L, result[0].id)
    }

    @Test
    fun `obtenerPrestamoPorId retorna mapeo correcto`() = runTest {
        val prestamoId = 1L
        val entity = PrestamoEntity(id = prestamoId, clienteId = 1L, empleadoId = 2L, aprobadoPorAdminId = null, montoSolicitado = BigDecimal("1000"), porcentajeInteres = BigDecimal("10"), interesTotal = BigDecimal("100"), totalAPagar = BigDecimal("1100"), totalPagado = BigDecimal("0"), montoCuota = BigDecimal("110"), cantidadCuotas = 10, frecuenciaPago = mockk(), diaPagoPreferido = null, diaPagoDescripcion = null, fechaCreacion = 1L, fechaInicio = null, fechaFin = null, estado = mockk(), motivoRechazo = null, rutaFotoContratoFirmado = null, contratoFisicoEntregado = false)
        coEvery { prestamoDao.obtenerPrestamoPorId(prestamoId) } returns entity

        val result = repository.obtenerPrestamoPorId(prestamoId)

        TestCase.assertNotNull(result)
        TestCase.assertEquals(prestamoId, result?.id)
        TestCase.assertEquals(BigDecimal("1000"), result?.montoSolicitado)
    }

    @Test
    fun `guardarHistorial inserta correctamente`() = runTest {
        val historial = LoanStatusHistory(id = 0L, loanId = 10L, status = mockk(), changedAt = 1000L, changedByUserId = 2L, note = "Test note")
        val slot = slot<LoanStatusHistoryEntity>()
        coEvery { prestamoDao.insertarHistorial(capture(slot)) } returns 1L

        repository.guardarHistorial(historial)

        coVerify { prestamoDao.insertarHistorial(any()) }
        TestCase.assertEquals(10L, slot.captured.loanId)
        TestCase.assertEquals(2L, slot.captured.changedByUserId)
        TestCase.assertEquals("Test note", slot.captured.note)
    }

    @Test
    fun `observarHistorialPrestamo mapea correctamente`() = runTest {
        val prestamoId = 10L
        val entities = listOf(
            LoanStatusHistoryEntity(id = 1L, loanId = prestamoId, status = mockk(), changedAt = 1000L, changedByUserId = 2L, note = "Note")
        )
        every { prestamoDao.observarHistorialPrestamo(prestamoId) } returns flowOf(entities)

        val result = repository.observarHistorialPrestamo(prestamoId).first()

        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(prestamoId, result[0].loanId)
        TestCase.assertEquals("Note", result[0].note)
    }

    @Test
    fun `observarTodoElHistorial mapea correctamente`() = runTest {
        val entities = listOf(
            LoanStatusHistoryEntity(id = 1L, loanId = 10L, status = mockk(), changedAt = 1000L, changedByUserId = 2L, note = "Note 1"),
            LoanStatusHistoryEntity(id = 2L, loanId = 20L, status = mockk(), changedAt = 2000L, changedByUserId = 3L, note = "Note 2")
        )
        every { prestamoDao.observarTodoElHistorial() } returns flowOf(entities)

        val result = repository.observarTodoElHistorial().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals(10L, result[0].loanId)
        TestCase.assertEquals(20L, result[1].loanId)
    }
}