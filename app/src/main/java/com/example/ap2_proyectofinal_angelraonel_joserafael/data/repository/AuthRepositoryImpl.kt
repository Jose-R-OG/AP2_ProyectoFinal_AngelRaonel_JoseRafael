package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(username: String, pin: String): User? {
        return userDao.login(username, pin)?.toDomain()
    }

    override suspend fun loginWithGoogle(email: String): User? {
        return userDao.getUserByEmail(email)?.toDomain()
    }

    override suspend fun registerUser(user: User) {
        userDao.insertUser(user.toEntity())

    }

    override suspend fun updateUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun hasAnyUser(): Boolean {
        return userDao.getUserCount() > 0
    }

    override suspend fun hasAnyAdmin(): Boolean = userDao.getAdminCount() > 0

    override fun getAllActiveUsers(): Flow<List<User>> {
        return userDao.getAllActiveUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)?.toDomain()
    }

    override fun observeUserById(userId: Long): Flow<User?> {
        return userDao.observeUserById(userId).map { it?.toDomain() }
    }
}
