package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import android.content.Context
import android.net.Uri
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.adminrequest.AdminRegisterRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AdminRegisterRepositoryImplTest {

    private lateinit var dao: AdminRegisterRequestDao
    private lateinit var context: Context
    private lateinit var repository: AdminRegisterRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        context = mockk(relaxed = true)
        repository = AdminRegisterRepositoryImpl(dao, context)
        mockkObject(FileStorageUtil)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `submitRegistration exitoso retorna codigo de activacion y guarda solicitud`() = runTest {
        val uriMock = mockk<Uri>()
        val fakePath = "/fake/internal/path/voucher.jpg"

        every { FileStorageUtil.saveFileToInternalStorage(any(), any(), any()) } returns fakePath

        val entitySlot = slot<AdminRegisterRequestEntity>()
        coEvery { dao.insertRequest(capture(entitySlot)) } just Runs

        val result = repository.submitRegistration(
            fullName = "Angel Raonel",
            username = "angelr",
            email = "angel@test.com",
            phone = "809-123-4567",
            cedula = "402-1234567-8",
            bank = "Banreservas",
            transferNum = "987654321",
            depositor = "Angel Raonel",
            voucherUri = uriMock,
            pin = "1234"
        )

        TestCase.assertTrue(result.isSuccess)

        val activationCode = result.getOrNull()
        TestCase.assertNotNull(activationCode)
        TestCase.assertTrue(activationCode!!.length == 6)

        coVerify { dao.insertRequest(any()) }

        val capturedEntity = entitySlot.captured
        TestCase.assertEquals("angel@test.com", capturedEntity.email)
        TestCase.assertEquals("angelr", capturedEntity.username)
        TestCase.assertEquals("Angel Raonel", capturedEntity.fullName)
        TestCase.assertEquals(fakePath, capturedEntity.voucherLocalPath)
        TestCase.assertEquals("PENDIENTE", capturedEntity.status)
        TestCase.assertEquals(activationCode, capturedEntity.activationCode)
    }

    @Test
    fun `submitRegistration falla si no se puede guardar el comprobante`() = runTest {
        val uriMock = mockk<Uri>()

        every { FileStorageUtil.saveFileToInternalStorage(any(), any(), any()) } returns null

        val result = repository.submitRegistration(
            fullName = "Angel Raonel",
            username = "angelr",
            email = "angel@test.com",
            phone = "809-123-4567",
            cedula = "402-1234567-8",
            bank = "Banreservas",
            transferNum = "987654321",
            depositor = "Angel Raonel",
            voucherUri = uriMock,
            pin = "1234"
        )

        TestCase.assertTrue(result.isFailure)
        coVerify(exactly = 0) { dao.insertRequest(any()) }
    }

    @Test
    fun `getRequestByEmail retorna solicitud mapeada correctamente`() = runTest {
        val email = "admin@test.com"
        val entity = AdminRegisterRequestEntity(
            email = email,
            username = "admin",
            fullName = "Admin Test",
            phone = "809",
            cedula = "402",
            selectedBank = "BHD",
            transferNumber = "123",
            depositorName = "Admin",
            voucherLocalPath = "/path",
            pin = "1234",
            status = "PENDIENTE",
            activationCode = "555555",
            createdAt = 123456789L
        )

        coEvery { dao.getRequestByEmail(email) } returns entity

        val result = repository.getRequestByEmail(email)

        TestCase.assertNotNull(result)
        TestCase.assertEquals(email, result?.email)
        TestCase.assertEquals("admin", result?.username)
        TestCase.assertEquals("555555", result?.activationCode)
    }

    @Test
    fun `getRequestByEmail retorna null si no encuentra solicitud`() = runTest {
        val email = "noexiste@test.com"
        coEvery { dao.getRequestByEmail(email) } returns null

        val result = repository.getRequestByEmail(email)

        TestCase.assertNull(result)
    }

    @Test
    fun `getAllRequests retorna flow de solicitudes`() = runTest {
        val entities = listOf(
            AdminRegisterRequestEntity("1@test.com", "user1", "User 1", "809", "402", "BHD", "111", "User 1", "/path1", "1234", "PENDIENTE", "111111", 1L),
            AdminRegisterRequestEntity("2@test.com", "user2", "User 2", "809", "402", "Banreservas", "222", "User 2", "/path2", "4321", "APROBADO", "222222", 2L)
        )

        every { dao.getAllRequests() } returns flowOf(entities)

        val result = repository.getAllRequests().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals("1@test.com", result[0].email)
        TestCase.assertEquals("PENDIENTE", result[0].status)
        TestCase.assertEquals("2@test.com", result[1].email)
        TestCase.assertEquals("APROBADO", result[1].status)
    }
}