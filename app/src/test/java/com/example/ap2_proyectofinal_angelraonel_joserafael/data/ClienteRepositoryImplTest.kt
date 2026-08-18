package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.ClienteRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
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

class ClienteRepositoryImplTest {

    private lateinit var clienteDao: ClienteDao
    private lateinit var repository: ClienteRepositoryImpl

    @Before
    fun setUp() {
        clienteDao = mockk(relaxed = true)
        repository = ClienteRepositoryImpl(clienteDao)
    }

    @Test
    fun `saveCliente guarda cliente correctamente`() = runTest {
        val cliente = Cliente(
            id = 0L,
            fullName = "José Rafael",
            dni = "402-0000000-1",
            phone = "809-000-0000",
            address = "San Francisco de Macorís",
            zone = "NORTE",
            profilePhotoPath = null,
            dniFrontPhotoPath = null,
            dniBackPhotoPath = null,
            isActive = true
        )

        val clienteSlot = slot<ClienteEntity>()
        coEvery { clienteDao.insertCliente(capture(clienteSlot)) } returns 1L

        val result = repository.saveCliente(cliente)

        TestCase.assertEquals(1L, result)
        coVerify { clienteDao.insertCliente(any()) }
        TestCase.assertEquals("José Rafael", clienteSlot.captured.fullName)
        TestCase.assertEquals("402-0000000-1", clienteSlot.captured.dni)
        TestCase.assertEquals("San Francisco de Macorís", clienteSlot.captured.address)
        TestCase.assertEquals("NORTE", clienteSlot.captured.zone)
    }

    @Test
    fun `softDeleteCliente retorna true al eliminar correctamente`() = runTest {
        val id = 1L
        coEvery { clienteDao.softDeleteClienteIfAllowed(id) } returns 1

        val result = repository.softDeleteCliente(id)

        TestCase.assertTrue(result)
        coVerify { clienteDao.softDeleteClienteIfAllowed(id) }
    }

    @Test
    fun `softDeleteCliente retorna false si no elimina`() = runTest {
        val id = 1L
        coEvery { clienteDao.softDeleteClienteIfAllowed(id) } returns 0

        val result = repository.softDeleteCliente(id)

        TestCase.assertFalse(result)
        coVerify { clienteDao.softDeleteClienteIfAllowed(id) }
    }

    @Test
    fun `hasBlockingLoans retorna true si existen prestamos activos`() = runTest {
        val id = 1L
        coEvery { clienteDao.countBlockingLoans(id) } returns 2

        val result = repository.hasBlockingLoans(id)

        TestCase.assertTrue(result)
        coVerify { clienteDao.countBlockingLoans(id) }
    }

    @Test
    fun `hasBlockingLoans retorna false si no hay prestamos activos`() = runTest {
        val id = 1L
        coEvery { clienteDao.countBlockingLoans(id) } returns 0

        val result = repository.hasBlockingLoans(id)

        TestCase.assertFalse(result)
        coVerify { clienteDao.countBlockingLoans(id) }
    }

    @Test
    fun `getActiveClientes retorna flow de clientes activos`() = runTest {
        val entities = listOf(
            ClienteEntity(1L, "Juan", "123", "809", "Dir 1", "SUR", null, null, null, true),
            ClienteEntity(2L, "Maria", "456", "829", "Dir 2", "NORTE", null, null, null, true)
        )
        every { clienteDao.getActiveClientes() } returns flowOf(entities)

        val result = repository.getActiveClientes().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals("Juan", result[0].fullName)
        TestCase.assertEquals("Maria", result[1].fullName)
        TestCase.assertTrue(result[0].isActive)
        TestCase.assertTrue(result[1].isActive)
    }

    @Test
    fun `getAllClientes retorna flow de todos los clientes`() = runTest {
        val entities = listOf(
            ClienteEntity(1L, "Juan", "123", "809", "Dir 1", "SUR", null, null, null, true),
            ClienteEntity(2L, "Maria", "456", "829", "Dir 2", "NORTE", null, null, null, false)
        )
        every { clienteDao.getAllClientes() } returns flowOf(entities)

        val result = repository.getAllClientes().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertTrue(result[0].isActive)
        TestCase.assertFalse(result[1].isActive)
    }

    @Test
    fun `getClienteById retorna el cliente correctamente`() = runTest {
        val entity = ClienteEntity(1L, "José Rafael", "123", "809", "Dir", "ESTE", null, null, null, true)
        coEvery { clienteDao.getClienteById(1L) } returns entity

        val result = repository.getClienteById(1L)

        TestCase.assertNotNull(result)
        TestCase.assertEquals(1L, result?.id)
        TestCase.assertEquals("José Rafael", result?.fullName)
        TestCase.assertEquals("ESTE", result?.zone)
    }
}