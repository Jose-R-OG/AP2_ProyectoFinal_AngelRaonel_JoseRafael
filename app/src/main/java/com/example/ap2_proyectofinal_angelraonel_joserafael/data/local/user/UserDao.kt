package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username AND pin = :pin AND isActive = 1 LIMIT 1")
    suspend fun login(username: String, pin: String): UserEntity?

    @Query("SELECT * FROM users WHERE isActive = 1")
    fun getAllActiveUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?
}