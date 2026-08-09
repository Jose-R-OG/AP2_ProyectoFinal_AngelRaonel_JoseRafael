package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class Transaccion(
    val id: Long = 0,
    val prestamoId: Long,
    val cuotaId: Long? = null,
    val empleadoId: Long,
    val monto: BigDecimal,
    val fecha: Long = System.currentTimeMillis(),
    val tipo: TipoTransaccion,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val nota: String
)
