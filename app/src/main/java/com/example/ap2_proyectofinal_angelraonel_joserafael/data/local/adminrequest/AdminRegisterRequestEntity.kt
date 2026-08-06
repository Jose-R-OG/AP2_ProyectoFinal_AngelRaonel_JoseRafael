package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_register_requests")
data class AdminRegisterRequestEntity(
    @PrimaryKey
    val email: String,
    val username: String,
    val fullName: String,
    val phone: String,
    val cedula: String,
    val selectedBank: String,
    val transferNumber: String,
    val depositorName: String,
    val voucherLocalPath: String,
    val pin: String,
    val status: String,
    val activationCode: String,
    val createdAt: Long
)
