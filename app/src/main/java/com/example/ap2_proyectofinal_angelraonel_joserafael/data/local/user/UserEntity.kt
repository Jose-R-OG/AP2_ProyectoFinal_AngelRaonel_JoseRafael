package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user

import androidx.room.Entity
import androidx.room.ColumnInfo
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
    val isActive: Boolean = true,
    val route: String? = null,
    @ColumnInfo(defaultValue = "''") val address: String = "",
    val profilePhotoPath: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null,
    val businessName: String? = null,
    val businessLogoPath: String? = null,
    @ColumnInfo(defaultValue = "1") val canCreateClients: Boolean = true,
    @ColumnInfo(defaultValue = "1") val canCollectPayments: Boolean = true,
    @ColumnInfo(defaultValue = "1") val canViewRoute: Boolean = true,
    @ColumnInfo(defaultValue = "1") val canCloseCash: Boolean = true,
    @ColumnInfo(defaultValue = "1") val canShareDocuments: Boolean = true
)
