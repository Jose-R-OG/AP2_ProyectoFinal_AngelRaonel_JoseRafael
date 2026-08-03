package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "cuotas")
data class CuotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prestamoId: Long,
    val numeroCuota: Int,
    val fechaVencimiento: Long,
    val fechaPago: Long?,
    val montoEsperado: BigDecimal,
    val montoPagado: BigDecimal,
    val moraAcumulada: BigDecimal,
    val estaPagada: Boolean
)