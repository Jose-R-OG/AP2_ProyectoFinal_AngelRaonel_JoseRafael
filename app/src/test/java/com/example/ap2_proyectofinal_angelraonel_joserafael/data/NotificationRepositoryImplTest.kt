package com.example.ap2_proyectofinal_angelraonel_joserafael.data

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.NotificationRepositoryImpl
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.AppNotification
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.notification.SystemNotificationHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NotificationRepositoryImplTest {

    private lateinit var dao: NotificationDao
    private lateinit var systemNotificationHelper: SystemNotificationHelper
    private lateinit var repository: NotificationRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        systemNotificationHelper = mockk(relaxed = true) // Inicializamos el helper falso
        repository = NotificationRepositoryImpl(dao, systemNotificationHelper) // Se lo pasamos al constructor
    }

    @Test
    fun `create guarda la notificacion correctamente y llama al helper del sistema`() = runTest {
        val notification = AppNotification(
            id = 0L,
            recipientUserId = 2L,
            title = "Nueva Notificación",
            message = "Tienes un nuevo préstamo",
            relatedLoanId = 5L,
            createdAt = 1620000000000L,
            isRead = false
        )

        val slot = slot<NotificationEntity>()

        coEvery { dao.insert(capture(slot)) } returns 1L

        repository.create(notification)

        // Verificamos que se guarde en la BD
        coVerify { dao.insert(any()) }
        TestCase.assertEquals(0L, slot.captured.id)
        TestCase.assertEquals(2L, slot.captured.recipientUserId)
        TestCase.assertEquals("Nueva Notificación", slot.captured.title)
        TestCase.assertEquals("Tienes un nuevo préstamo", slot.captured.message)
        TestCase.assertEquals(5L, slot.captured.relatedLoanId)
        TestCase.assertEquals(1620000000000L, slot.captured.createdAt)
        TestCase.assertFalse(slot.captured.isRead)

        // Verificamos que también se haya llamado al SystemNotificationHelper para mostrar la notificación nativa
        verify { systemNotificationHelper.showNotification("Nueva Notificación", "Tienes un nuevo préstamo", any()) }
    }

    @Test
    fun `observeForUser retorna flow de notificaciones mapeado correctamente`() = runTest {
        val userId = 2L
        val entities = listOf(
            NotificationEntity(1L, userId, "Título 1", "Mensaje 1", null, 1620000000000L, false),
            NotificationEntity(2L, userId, "Título 2", "Mensaje 2", 5L, 1620000000000L, true)
        )

        every { dao.observeForUser(userId) } returns flowOf(entities)

        val result = repository.observeForUser(userId).first()

        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals("Título 1", result[0].title)
        TestCase.assertEquals("Mensaje 2", result[1].message)
        TestCase.assertFalse(result[0].isRead)
        TestCase.assertTrue(result[1].isRead)
    }

    @Test
    fun `observeUnreadCount retorna flow con el conteo correcto`() = runTest {
        val userId = 2L
        every { dao.observeUnreadCount(userId) } returns flowOf(5)

        val result = repository.observeUnreadCount(userId).first()

        TestCase.assertEquals(5, result)
    }

    @Test
    fun `markAllRead llama al dao para marcar todas como leidas`() = runTest {
        val userId = 2L
        coEvery { dao.markAllRead(userId) } returns Unit

        repository.markAllRead(userId)

        coVerify { dao.markAllRead(userId) }
    }
}