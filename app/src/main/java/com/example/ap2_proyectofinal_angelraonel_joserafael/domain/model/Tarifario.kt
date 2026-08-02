package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class Tarifario(
    val id: Long = 0,
    val frecuencia: FrecuenciaPago,
    val duracion: Int,
    val porcentajeInteres: BigDecimal,
    val isActive: Boolean = true
)