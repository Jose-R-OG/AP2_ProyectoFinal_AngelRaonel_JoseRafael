package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class Cuota(
    val id: Long = 0,
    val prestamoId: Long,
    val numeroCuota: Int,
    val fechaVencimiento: Long,
    val fechaPago: Long? = null,
    val montoEsperado: BigDecimal,
    val montoPagado: BigDecimal = BigDecimal.ZERO,
    val moraAcumulada: BigDecimal = BigDecimal.ZERO,
    val estaPagada: Boolean = false
)
