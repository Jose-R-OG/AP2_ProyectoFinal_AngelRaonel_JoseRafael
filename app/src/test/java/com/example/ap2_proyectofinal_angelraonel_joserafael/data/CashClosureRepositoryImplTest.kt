package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.CashClosure
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

class CashClosureRepositoryImplTest {

    private lateinit var dao: CashClosureDao
    private lateinit var repository: CashClosureRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = CashClosureRepositoryImpl(dao)
    }

    @Test
    fun `save guarda el cierre de caja correctamente`() = runTest {
        val closure = CashClosure(
            id = 1L,
            userId = 2L,
            businessDate = "2026-08-18",
            closedAt = 1620000000000L,
            totalCollected = BigDecimal("5000.00"),
            cashRegistered = BigDecimal("3000.00"),
            cashInHand = BigDecimal("3000.00"),
            transferAmount = BigDecimal("2000.00"),
            transactionCount = 15,
            visitedCount = 20
        )

        val slot = slot<CashClosureEntity>()
        coEvery { dao.upsert(capture(slot)) } returns 1L

        repository.save(closure)

        coVerify { dao.upsert(any()) }
        TestCase.assertEquals(1L, slot.captured.id)
        TestCase.assertEquals(2L, slot.captured.userId)
        TestCase.assertEquals("2026-08-18", slot.captured.businessDate)
        TestCase.assertEquals(BigDecimal("5000.00"), slot.captured.totalCollected)
        TestCase.assertEquals(15, slot.captured.transactionCount)
    }

    @Test
    fun `observeForDate retorna el flow del cierre mapeado correctamente`() = runTest {
        val entity = CashClosureEntity(
            id = 1L,
            userId = 2L,
            businessDate = "2026-08-18",
            closedAt = 1620000000000L,
            totalCollected = BigDecimal("5000.00"),
            cashRegistered = BigDecimal("3000.00"),
            cashInHand = BigDecimal("3000.00"),
            transferAmount = BigDecimal("2000.00"),
            transactionCount = 15,
            visitedCount = 20
        )

        every { dao.observeForDate(2L, "2026-08-18") } returns flowOf(entity)

        val result = repository.observeForDate(2L, "2026-08-18").first()

        TestCase.assertNotNull(result)
        TestCase.assertEquals(1L, result?.id)
        TestCase.assertEquals("2026-08-18", result?.businessDate)
        TestCase.assertEquals(BigDecimal("3000.00"), result?.cashInHand)
    }

    @Test
    fun `observeForDate retorna flow nulo cuando no existe el cierre`() = runTest {
        every { dao.observeForDate(2L, "2026-08-18") } returns flowOf(null)

        val result = repository.observeForDate(2L, "2026-08-18").first()

        TestCase.assertNull(result)
    }
}