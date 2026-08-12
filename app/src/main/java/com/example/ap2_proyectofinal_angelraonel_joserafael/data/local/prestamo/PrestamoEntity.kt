package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import java.math.BigDecimal

@Entity(tableName = "prestamos")
data class PrestamoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clienteId: Long,
    val empleadoId: Long,
    val aprobadoPorAdminId: Long?,
    val montoSolicitado: BigDecimal,
    val porcentajeInteres: BigDecimal,
    val interesTotal: BigDecimal,
    val totalAPagar: BigDecimal,
    val totalPagado: BigDecimal,
    val montoCuota: BigDecimal,
    val cantidadCuotas: Int,
    val frecuenciaPago: FrecuenciaPago,
    val diaPagoPreferido: Int?,
    val diaPagoDescripcion: String?,
    val fechaCreacion: Long,
    val fechaInicio: Long?,
    val fechaFin: Long?,
    val estado: LoanStatus,
    val motivoRechazo: String?,
    val rutaFotoContratoFirmado: String?,
    val contratoFisicoEntregado: Boolean
)
