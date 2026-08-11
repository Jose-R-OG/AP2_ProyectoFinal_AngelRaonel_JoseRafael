package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_requests")
data class AdminRequestEntity(
    @PrimaryKey
    val email: String,
    val fullName: String,
    val phone: String,
    val cedula: String,
    val selectedBank: String,
    val transferNumber: String,
    val depositorName: String,
    val voucherUrl: String,
    val pin: String,
    val status: String,
    val activationCode: String
)
