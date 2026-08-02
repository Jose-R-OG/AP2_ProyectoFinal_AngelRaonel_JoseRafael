package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, pin: String): User?
    suspend fun registerUser(user: User)
    suspend fun hasAnyUser(): Boolean
    fun getAllActiveUsers(): Flow<List<User>>
}