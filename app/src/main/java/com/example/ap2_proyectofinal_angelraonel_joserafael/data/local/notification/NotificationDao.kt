package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications WHERE recipientUserId = :userId ORDER BY createdAt DESC")
    fun observeForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE recipientUserId = :userId AND isRead = 0")
    fun observeUnreadCount(userId: Long): Flow<Int>

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientUserId = :userId")
    suspend fun markAllRead(userId: Long)
}
