package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.TransaccionRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class TransaccionRepositoryImplTest {

    private lateinit var transaccionDao: TransaccionDao
    private lateinit var repository: TransaccionRepositoryImpl

    @Before
    fun setUp() {
        transaccionDao = mockk(relaxed = true)
        repository = TransaccionRepositoryImpl(transaccionDao)
    }

    @Test
    fun `guardarTransaccion guarda la transaccion correctamente`() = runTest {
        val transaccion = Transaccion(
            id = 0L,
            prestamoId = 1L,
            cuotaId = 2L,
            empleadoId = 3L,
            monto = BigDecimal("1500.00"),
            fecha = 1620000000000L,
            tipo = TipoTransaccion.INGRESO,
            paymentMethod = PaymentMethod.EFECTIVO,
            nota = "Pago de cuota"
        )

        val slot = slot<TransaccionEntity>()
        coEvery { transaccionDao.insertarTransaccion(capture(slot)) } just Runs

        repository.guardarTransaccion(transaccion)

        coVerify { transaccionDao.insertarTransaccion(any()) }
        TestCase.assertEquals(0L, slot.captured.id)
        TestCase.assertEquals(1L, slot.captured.prestamoId)
        TestCase.assertEquals(2L, slot.captured.cuotaId)
        TestCase.assertEquals(3L, slot.captured.empleadoId)
        TestCase.assertEquals(BigDecimal("1500.00"), slot.captured.monto)
        TestCase.assertEquals(1620000000000L, slot.captured.fecha)
        TestCase.assertEquals("Pago de cuota", slot.captured.nota)
    }

    @Test
    fun `obtenerTransaccionesPorDia retorna flow mapeado correctamente`() = runTest {
        val entities = listOf(
            TransaccionEntity(
                id = 1L, prestamoId = 1L, cuotaId = null, empleadoId = 2L,
                monto = BigDecimal("500"), fecha = 1000L, tipo = TipoTransaccion.INGRESO,
                paymentMethod = "EFECTIVO", nota = "Nota 1"
            ),
            TransaccionEntity(
                id = 2L, prestamoId = 2L, cuotaId = 3L, empleadoId = 2L,
                monto = BigDecimal("1000"), fecha = 2000L, tipo = TipoTransaccion.EGRESO,
                paymentMethod = "TRANSFERENCIA", nota = "Nota 2"
            )
        )
        every { transaccionDao.obtenerTransaccionesPorDia(1000L, 2000L) } returns flowOf(entities)

        val result = repository.obtenerTransaccionesPorDia(1000L, 2000L).first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals(1L, result[0].id)
        TestCase.assertEquals(BigDecimal("500"), result[0].monto)
        TestCase.assertEquals("Nota 1", result[0].nota)
    }

    @Test
    fun `obtenerHistorialPorPrestamo retorna flow mapeado correctamente`() = runTest {
        val prestamoId = 5L
        val entities = listOf(
            TransaccionEntity(
                id = 3L, prestamoId = prestamoId, cuotaId = 1L, empleadoId = 1L,
                monto = BigDecimal("300"), fecha = 1500L, tipo = TipoTransaccion.INGRESO,
                paymentMethod = "EFECTIVO", nota = "Historial"
            )
        )
        every { transaccionDao.obtenerHistorialPorPrestamo(prestamoId) } returns flowOf(entities)

        val result = repository.obtenerHistorialPorPrestamo(prestamoId).first()

        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(prestamoId, result[0].prestamoId)
        TestCase.assertEquals(BigDecimal("300"), result[0].monto)
        TestCase.assertEquals("Historial", result[0].nota)
    }

    @Test
    fun `obtenerTodas retorna flow mapeado correctamente`() = runTest {
        val entities = listOf(
            TransaccionEntity(
                id = 1L, prestamoId = 1L, cuotaId = null, empleadoId = 1L,
                monto = BigDecimal("100"), fecha = 1000L, tipo = TipoTransaccion.INGRESO,
                paymentMethod = "EFECTIVO", nota = "Nota A"
            ),
            TransaccionEntity(
                id = 2L, prestamoId = 2L, cuotaId = null, empleadoId = 2L,
                monto = BigDecimal("200"), fecha = 2000L, tipo = TipoTransaccion.EGRESO,
                paymentMethod = "EFECTIVO", nota = "Nota B"
            )
        )
        every { transaccionDao.obtenerTodas() } returns flowOf(entities)

        val result = repository.obtenerTodas().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals(1L, result[0].id)
        TestCase.assertEquals(2L, result[1].id)
    }
}