package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.TarifarioRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
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

class TarifarioRepositoryImplTest {

    private lateinit var configDao: ConfigDao
    private lateinit var repository: TarifarioRepositoryImpl

    @Before
    fun setUp() {
        configDao = mockk(relaxed = true)
        repository = TarifarioRepositoryImpl(configDao)
    }

    @Test
    fun `getActiveTarifarios retorna flow de tarifarios mapeados correctamente`() = runTest {
        val entities = listOf(
            ConfigEntity(
                id = 1L,
                frequency = mockk(),
                durationUnits = 30,
                interestPercent = BigDecimal("10.0"),
                isActive = true
            ),
            ConfigEntity(
                id = 2L,
                frequency = mockk(),
                durationUnits = 15,
                interestPercent = BigDecimal("5.0"),
                isActive = true
            )
        )
        every { configDao.getActiveConfigs() } returns flowOf(entities)

        val result = repository.getActiveTarifarios().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals(1L, result[0].id)
        TestCase.assertEquals(30, result[0].duracion)
        TestCase.assertEquals(BigDecimal("10.0"), result[0].porcentajeInteres)
        TestCase.assertTrue(result[0].isActive)
        TestCase.assertEquals(2L, result[1].id)
    }

    @Test
    fun `saveTarifario guarda el tarifario correctamente`() = runTest {
        val tarifario = Tarifario(
            id = 0L,
            frecuencia = mockk(),
            duracion = 30,
            porcentajeInteres = BigDecimal("15.5"),
            isActive = true
        )

        val slot = slot<ConfigEntity>()
        coEvery { configDao.insertConfig(capture(slot)) } returns Unit

        repository.saveTarifario(tarifario)

        coVerify { configDao.insertConfig(any()) }
        TestCase.assertEquals(0L, slot.captured.id)
        TestCase.assertEquals(30, slot.captured.durationUnits)
        TestCase.assertEquals(BigDecimal("15.5"), slot.captured.interestPercent)
        TestCase.assertTrue(slot.captured.isActive)
    }

    @Test
    fun `disableTarifario deshabilita el tarifario correctamente`() = runTest {
        val id = 1L
        coEvery { configDao.disableConfig(id) } returns Unit

        repository.disableTarifario(id)

        coVerify { configDao.disableConfig(id) }
    }
}