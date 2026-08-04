package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import java.math.BigDecimal

@Entity(tableName = "transacciones")
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prestamoId: Long,
    val cuotaId: Long?,
    val empleadoId: Long,
    val monto: BigDecimal,
    val fecha: Long,
    val tipo: TipoTransaccion,
    val nota: String
)