package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.local.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo

fun PrestamoEntity.toDomain(): Prestamo {
    return Prestamo(
        id = id,
        clienteId = clienteId,
        monto = monto,
        cuotas = cuotas,
        frecuencia = frecuencia,
        interesPorcentaje = interesPorcentaje,
        fechaInicio = fechaInicio,
        status = status
    )
}

fun Prestamo.toEntity(): PrestamoEntity {
    return PrestamoEntity(
        id = id,
        clienteId = clienteId,
        monto = monto,
        cuotas = cuotas,
        frecuencia = frecuencia,
        interesPorcentaje = interesPorcentaje,
        fechaInicio = fechaInicio,
        status = status
    )
}