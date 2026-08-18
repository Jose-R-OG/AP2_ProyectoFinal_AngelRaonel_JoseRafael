package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.AuthRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
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

class AuthRepositoryImplTest {

    private lateinit var userDao: UserDao
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        userDao = mockk(relaxed = true)
        repository = AuthRepositoryImpl(userDao)
    }

    @Test
    fun `login retorna usuario correctamente`() = runTest {
        val entity = UserEntity(
            id = 1L,
            nombreCompleto = "Juan",
            username = "juan",
            identificacion = "123",
            telefono = "809",
            pin = "1234",
            role = mockk()
        )
        coEvery { userDao.login("juan", "1234") } returns entity

        val result = repository.login("juan", "1234")

        TestCase.assertNotNull(result)
        TestCase.assertEquals(1L, result?.id)
        TestCase.assertEquals("Juan", result?.nombreCompleto)
    }

    @Test
    fun `login retorna null si las credenciales son incorrectas`() = runTest {
        coEvery { userDao.login(any(), any()) } returns null

        val result = repository.login("juan", "error")

        TestCase.assertNull(result)
    }

    @Test
    fun `loginWithGoogle retorna usuario correctamente`() = runTest {
        val entity = UserEntity(
            id = 1L,
            nombreCompleto = "Juan",
            username = "juan",
            identificacion = "123",
            telefono = "809",
            email = "juan@gmail.com",
            pin = "1234",
            role = mockk()
        )
        coEvery { userDao.getUserByEmail("juan@gmail.com") } returns entity

        val result = repository.loginWithGoogle("juan@gmail.com")

        TestCase.assertNotNull(result)
        TestCase.assertEquals("juan@gmail.com", result?.email)
    }

    @Test
    fun `registerUser guarda usuario correctamente`() = runTest {
        val user = User(
            id = 0L,
            nombreCompleto = "Juan",
            username = "juan",
            identificacion = "123",
            telefono = "809",
            pin = "1234",
            role = mockk()
        )
        val userSlot = slot<UserEntity>()
        coEvery { userDao.insertUser(capture(userSlot)) } just Runs

        repository.registerUser(user)

        coVerify { userDao.insertUser(any()) }
        TestCase.assertEquals("Juan", userSlot.captured.nombreCompleto)
        TestCase.assertEquals("juan", userSlot.captured.username)
        TestCase.assertEquals("123", userSlot.captured.identificacion)
    }

    @Test
    fun `updateUser actualiza usuario correctamente`() = runTest {
        val user = User(
            id = 1L,
            nombreCompleto = "Juan Editado",
            username = "juan",
            identificacion = "123",
            telefono = "809",
            pin = "1234",
            role = mockk()
        )
        val userSlot = slot<UserEntity>()
        coEvery { userDao.insertUser(capture(userSlot)) } just Runs

        repository.updateUser(user)

        coVerify { userDao.insertUser(any()) }
        TestCase.assertEquals(1L, userSlot.captured.id)
        TestCase.assertEquals("Juan Editado", userSlot.captured.nombreCompleto)
    }

    @Test
    fun `hasAnyUser retorna true si el conteo es mayor a 0`() = runTest {
        coEvery { userDao.getUserCount() } returns 5

        val result = repository.hasAnyUser()

        TestCase.assertTrue(result)
        coVerify { userDao.getUserCount() }
    }

    @Test
    fun `hasAnyUser retorna false si el conteo es 0`() = runTest {
        coEvery { userDao.getUserCount() } returns 0

        val result = repository.hasAnyUser()

        TestCase.assertFalse(result)
        coVerify { userDao.getUserCount() }
    }

    @Test
    fun `hasAnyAdmin retorna true si el conteo es mayor a 0`() = runTest {
        coEvery { userDao.getAdminCount() } returns 1

        val result = repository.hasAnyAdmin()

        TestCase.assertTrue(result)
        coVerify { userDao.getAdminCount() }
    }

    @Test
    fun `hasAnyAdmin retorna false si el conteo es 0`() = runTest {
        coEvery { userDao.getAdminCount() } returns 0

        val result = repository.hasAnyAdmin()

        TestCase.assertFalse(result)
        coVerify { userDao.getAdminCount() }
    }

    @Test
    fun `getAllActiveUsers retorna flow de usuarios activos`() = runTest {
        val entities = listOf(
            UserEntity(id = 1L, nombreCompleto = "Juan", username = "juan", identificacion = "123", telefono = "809", pin = "1234", role = mockk(), isActive = true),
            UserEntity(id = 2L, nombreCompleto = "Maria", username = "maria", identificacion = "456", telefono = "829", pin = "4321", role = mockk(), isActive = true)
        )
        every { userDao.getAllActiveUsers() } returns flowOf(entities)

        val result = repository.getAllActiveUsers().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals("Juan", result[0].nombreCompleto)
        TestCase.assertEquals("Maria", result[1].nombreCompleto)
        TestCase.assertTrue(result[0].isActive)
        TestCase.assertTrue(result[1].isActive)
    }

    @Test
    fun `getAllUsers retorna flow de todos los usuarios`() = runTest {
        val entities = listOf(
            UserEntity(id = 1L, nombreCompleto = "Juan", username = "juan", identificacion = "123", telefono = "809", pin = "1234", role = mockk(), isActive = true),
            UserEntity(id = 2L, nombreCompleto = "Maria", username = "maria", identificacion = "456", telefono = "829", pin = "4321", role = mockk(), isActive = false)
        )
        every { userDao.getAllUsers() } returns flowOf(entities)

        val result = repository.getAllUsers().first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertTrue(result[0].isActive)
        TestCase.assertFalse(result[1].isActive)
    }

    @Test
    fun `getUserById retorna usuario correctamente`() = runTest {
        val entity = UserEntity(
            id = 1L,
            nombreCompleto = "Juan",
            username = "juan",
            identificacion = "123",
            telefono = "809",
            pin = "1234",
            role = mockk()
        )
        coEvery { userDao.getUserById(1L) } returns entity

        val result = repository.getUserById(1L)

        TestCase.assertNotNull(result)
        TestCase.assertEquals(1L, result?.id)
        TestCase.assertEquals("Juan", result?.nombreCompleto)
    }

    @Test
    fun `observeUserById retorna flow de usuario correctamente`() = runTest {
        val entity = UserEntity(
            id = 1L,
            nombreCompleto = "Juan",
            username = "juan",
            identificacion = "123",
            telefono = "809",
            pin = "1234",
            role = mockk()
        )
        every { userDao.observeUserById(1L) } returns flowOf(entity)

        val result = repository.observeUserById(1L).first()

        TestCase.assertNotNull(result)
        TestCase.assertEquals(1L, result?.id)
        TestCase.assertEquals("Juan", result?.nombreCompleto)
    }
}