package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.adminregisterrequest

data class AdminRegisterRequest(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val cedula: String = "",
    val selectedBank: String = "",
    val transferNumber: String = "",
    val depositorName: String = "",
    val voucherUrl: String = "",
    val status: String = "PENDIENTE",
    val activationCode: String = "",
    val createdAt: Long = System.currentTimeMillis()
)