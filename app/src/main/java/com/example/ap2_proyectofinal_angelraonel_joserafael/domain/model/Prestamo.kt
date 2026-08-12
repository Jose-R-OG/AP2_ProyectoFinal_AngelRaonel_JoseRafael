package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class Prestamo(
    val id: Long = 0,
    val clienteId: Long,
    val empleadoId: Long,
    val aprobadoPorAdminId: Long? = null,

    val montoSolicitado: BigDecimal,
    val porcentajeInteres: BigDecimal,
    val interesTotal: BigDecimal,
    val totalAPagar: BigDecimal,
    val totalPagado: BigDecimal = BigDecimal.ZERO,
    val montoCuota: BigDecimal,
    val cantidadCuotas: Int,
    val frecuenciaPago: FrecuenciaPago,
    val diaPagoPreferido: Int? = null,
    val diaPagoDescripcion: String? = null,

    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaInicio: Long? = null,
    val fechaFin: Long? = null,

    val estado: LoanStatus,
    val motivoRechazo: String? = null,
    val rutaFotoContratoFirmado: String? = null,
    val contratoFisicoEntregado: Boolean = false
)
