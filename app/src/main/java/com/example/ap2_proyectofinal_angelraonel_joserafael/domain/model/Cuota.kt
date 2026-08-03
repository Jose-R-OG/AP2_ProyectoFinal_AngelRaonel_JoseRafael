package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class Cuota(
    val id: Long = 0,
    val prestamoId: Long,
    val numeroCuota: Int,
    val fechaVencimiento: Long,            // Fecha en la que toca pagar
    val fechaPago: Long? = null,           // Fecha exacta en la que se pagó realmente
    val montoEsperado: BigDecimal,         // Monto base de la cuota
    val montoPagado: BigDecimal = BigDecimal.ZERO, // Abonos realizados
    val moraAcumulada: BigDecimal = BigDecimal.ZERO, // Mora sumada por atrasos
    val estaPagada: Boolean = false
)