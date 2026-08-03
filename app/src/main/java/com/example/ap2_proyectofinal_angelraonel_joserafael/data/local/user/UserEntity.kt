package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreCompleto: String,
    val username: String,
    val identificacion: String,
    val telefono: String,
    val email: String? = null,
    val pin: String,
    val role: UserRole,
    val isActive: Boolean = true
)