package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    private val db = Firebase.firestore

    override suspend fun login(username: String, pin: String): User? {
        val localUser = userDao.login(username, pin)
        if (localUser != null) {
            return localUser.toDomain()
        }

        return try {
            val querySnapshot = db.collection("usuarios")
                .whereEqualTo("username", username)
                .whereEqualTo("pin", pin)
                .whereEqualTo("isActive", true)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents.first()
                val roleStr = doc.getString("role") ?: "EMPLEADO"
                val role = if (roleStr == "ADMINISTRADOR") UserRole.ADMINISTRADOR else UserRole.EMPLEADO

                val remoteUser = User(
                    id = 0L,
                    nombreCompleto = doc.getString("nombreCompleto") ?: "Usuario",
                    username = doc.getString("username") ?: username,
                    identificacion = doc.getString("identificacion") ?: doc.id,
                    telefono = doc.getString("telefono") ?: "S/D",
                    pin = doc.getString("pin") ?: pin,
                    role = role,
                    isActive = doc.getBoolean("isActive") ?: true,
                    email = doc.getString("email"),
                    route = doc.getString("route")
                )

                userDao.insertUser(remoteUser.toEntity())
                remoteUser
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun registerUser(user: User) {
        userDao.insertUser(user.toEntity())

        try {
            val userMap = hashMapOf(
                "nombreCompleto" to user.nombreCompleto,
                "username" to user.username,
                "identificacion" to user.identificacion,
                "telefono" to user.telefono,
                "pin" to user.pin,
                "role" to user.role.name,
                "isActive" to user.isActive,
                "email" to (user.email ?: ""),
                "route" to (user.route ?: "")
            )
            db.collection("usuarios").document(user.username).set(userMap)
        } catch (e: Exception) {
            // Continuar silente en modo offline
        }
    }

    override suspend fun hasAnyUser(): Boolean {
        return userDao.getUserCount() > 0
    }

    override fun getAllActiveUsers(): Flow<List<User>> {
        return userDao.getAllActiveUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)?.toDomain()
    }
}