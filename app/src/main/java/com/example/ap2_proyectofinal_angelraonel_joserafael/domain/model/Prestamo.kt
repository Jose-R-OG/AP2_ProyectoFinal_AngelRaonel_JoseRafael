package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class Prestamo(
    val id: Long = 0,
    val clienteId: Long,
    val empleadoId: Long,                  // Quién registró la solicitud
    val aprobadoPorAdminId: Long? = null,  // Quién la aprobó (Auditoría)

    // --- Fotografía de las condiciones pactadas ---
    val montoSolicitado: BigDecimal,       // Capital prestado
    val porcentajeInteres: BigDecimal,     // Porcentaje aplicado (%)
    val interesTotal: BigDecimal,          // Interés total generado
    val totalAPagar: BigDecimal,           // Capital + Intereses
    val totalPagado: BigDecimal = BigDecimal.ZERO, // Acumulador de lo que ha pagado
    val montoCuota: BigDecimal,            // Valor de cada cuota
    val cantidadCuotas: Int,               // Plazo (ej. 12)
    val frecuenciaPago: FrecuenciaPago,    // DIARIO, SEMANAL...

    // --- Fechas de Ciclo de Vida ---
    val fechaCreacion: Long = System.currentTimeMillis(), // Fecha de solicitud
    val fechaInicio: Long? = null,         // Fecha de inicio (Desembolso)
    val fechaFin: Long? = null,            // Fecha proyectada de término

    // --- Control y Estado ---
    val estado: LoanStatus = LoanStatus.PENDIENTE_REVISION,
    val motivoRechazo: String? = null,
    val rutaFotoContratoFirmado: String? = null,
    val contratoFisicoEntregado: Boolean = false
)