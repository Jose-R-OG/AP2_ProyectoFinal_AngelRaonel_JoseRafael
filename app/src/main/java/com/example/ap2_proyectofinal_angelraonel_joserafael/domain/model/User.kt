package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

data class User(
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