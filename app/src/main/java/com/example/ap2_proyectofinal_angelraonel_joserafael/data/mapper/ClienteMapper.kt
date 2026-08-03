package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente

fun ClienteEntity.toDomain(): Cliente {
    return Cliente(
        id = id,
        fullName = fullName,
        dni = dni,
        phone = phone,
        address = address,
        profilePhotoPath = profilePhotoPath,
        dniFrontPhotoPath = dniFrontPhotoPath,
        dniBackPhotoPath = dniBackPhotoPath,
        isActive = isActive
    )
}

fun Cliente.toEntity(): ClienteEntity {
    return ClienteEntity(
        id = id,
        fullName = fullName,
        dni = dni,
        phone = phone,
        address = address,
        profilePhotoPath = profilePhotoPath,
        dniFrontPhotoPath = dniFrontPhotoPath,
        dniBackPhotoPath = dniBackPhotoPath,
        isActive = isActive
    )
}