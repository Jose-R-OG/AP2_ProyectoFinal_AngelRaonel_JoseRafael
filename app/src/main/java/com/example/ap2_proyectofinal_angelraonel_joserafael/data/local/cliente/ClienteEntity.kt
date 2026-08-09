package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val dni: String,
    val phone: String,
    val address: String,
    @ColumnInfo(defaultValue = "'SIN ASIGNAR'") val zone: String = "SIN ASIGNAR",
    val profilePhotoPath: String?,
    val dniFrontPhotoPath: String?,
    val dniBackPhotoPath: String?,
    val isActive: Boolean
)
