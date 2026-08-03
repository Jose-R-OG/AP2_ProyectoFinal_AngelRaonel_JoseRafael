package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class Prestamo(
    val id: Long = 0,
    val clienteId: Long,
    val monto: BigDecimal,
    val cuotas: Int,
    val frecuencia: FrecuenciaPago,
    val interesPorcentaje: BigDecimal,
    val fechaInicio: LocalDate = LocalDate.now(),
    val status: LoanStatus = LoanStatus.ACTIVO
)