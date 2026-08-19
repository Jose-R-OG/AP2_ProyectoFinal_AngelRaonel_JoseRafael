package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification.NotificationDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.AppNotification
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.NotificationRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.notification.SystemNotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val dao: NotificationDao,
    private val systemNotificationHelper: SystemNotificationHelper
) : NotificationRepository {
    override suspend fun create(notification: AppNotification) {
        dao.insert(notification.toEntity())
        systemNotificationHelper.showNotification(
            title = notification.title,
            message = notification.message
        )
    }

    override fun observeForUser(userId: Long): Flow<List<AppNotification>> =
        dao.observeForUser(userId).map { items -> items.map { it.toDomain() } }

    override fun observeUnreadCount(userId: Long): Flow<Int> = dao.observeUnreadCount(userId)

    override suspend fun markAllRead(userId: Long) = dao.markAllRead(userId)
}
