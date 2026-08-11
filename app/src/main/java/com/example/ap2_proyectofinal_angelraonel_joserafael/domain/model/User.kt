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
    val isActive: Boolean = true,
    val route: String? = null,
    val address: String = "",
    val profilePhotoPath: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null,
    val businessName: String? = null,
    val businessLogoPath: String? = null,
    val canCreateClients: Boolean = true,
    val canCollectPayments: Boolean = true,
    val canViewRoute: Boolean = true,
    val canCloseCash: Boolean = true,
    val canShareDocuments: Boolean = true
)
