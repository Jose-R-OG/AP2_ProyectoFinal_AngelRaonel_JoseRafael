package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import java.math.BigDecimal

@Entity(tableName = "loan_configs")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val frequency: FrecuenciaPago,
    val durationUnits: Int,
    val interestPercent: BigDecimal,
    val isActive: Boolean
)