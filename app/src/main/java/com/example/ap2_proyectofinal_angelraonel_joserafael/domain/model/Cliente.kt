package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

data class Cliente(
    val id: Long = 0,
    val fullName: String,
    val dni: String,
    val phone: String,
    val address: String,
    val zone: String = "SIN ASIGNAR",
    val profilePhotoPath: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null,
    val isActive: Boolean = true
)
