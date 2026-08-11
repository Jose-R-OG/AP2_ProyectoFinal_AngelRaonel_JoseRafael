package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.tarifa.ConfigEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario

fun ConfigEntity.toDomain(): Tarifario {
    return Tarifario(
        id = id,
        frecuencia = frequency,
        duracion = durationUnits,
        porcentajeInteres = interestPercent,
        isActive = isActive
    )
}

fun Tarifario.toEntity(): ConfigEntity {
    return ConfigEntity(
        id = id,
        frequency = frecuencia,
        durationUnits = duracion,
        interestPercent = porcentajeInteres,
        isActive = isActive
    )
}