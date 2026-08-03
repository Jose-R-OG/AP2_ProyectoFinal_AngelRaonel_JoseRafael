package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val dni: String,
    val phone: String,
    val address: String,
    val dniFrontPhotoPath: String?,
    val isActive: Boolean
)