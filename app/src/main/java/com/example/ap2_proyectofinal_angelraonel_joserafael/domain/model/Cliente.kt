package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

data class Cliente(
    val id: Long = 0,
    val fullName: String,
    val dni: String,
    val phone: String,
    val address: String,
    val dniFrontPhotoPath: String? = null,
    val isActive: Boolean = true
)