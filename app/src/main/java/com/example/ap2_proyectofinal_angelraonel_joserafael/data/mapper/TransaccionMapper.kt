package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion

fun TransaccionEntity.toDomain(): Transaccion {
    return Transaccion(
        id = id,
        prestamoId = prestamoId,
        cuotaId = cuotaId,
        empleadoId = empleadoId,
        monto = monto,
        fecha = fecha,
        tipo = tipo,
        paymentMethod = try { PaymentMethod.valueOf(paymentMethod) } catch (e: Exception) { PaymentMethod.EFECTIVO },
        nota = nota
    )
}

fun Transaccion.toEntity(): TransaccionEntity {
    return TransaccionEntity(
        id = id,
        prestamoId = prestamoId,
        cuotaId = cuotaId,
        empleadoId = empleadoId,
        monto = monto,
        fecha = fecha,
        tipo = tipo,
        paymentMethod = paymentMethod.name,
        nota = nota
    )
}
