package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(username: String, pin: String): User? {
        val userEntity = userDao.login(username, pin)
        return userEntity?.toDomain()
    }

    override suspend fun registerUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun hasAnyUser(): Boolean {
        return userDao.getUserCount() > 0
    }

    override fun getAllActiveUsers(): Flow<List<User>> {
        return userDao.getAllActiveUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}