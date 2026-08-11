package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun create(notification: AppNotification)
    fun observeForUser(userId: Long): Flow<List<AppNotification>>
    fun observeUnreadCount(userId: Long): Flow<Int>
    suspend fun markAllRead(userId: Long)
}
