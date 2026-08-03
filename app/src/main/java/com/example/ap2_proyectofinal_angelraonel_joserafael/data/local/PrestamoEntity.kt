package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import java.time.LocalDate
import java.math.BigDecimal

@Entity(tableName = "loans")
data class PrestamoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clienteId: Long,
    val monto: BigDecimal,
    val cuotas: Int,
    val frecuencia: FrecuenciaPago,
    val interesPorcentaje: BigDecimal,
    val fechaInicio: LocalDate,
    val status: LoanStatus
)