package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Auth.local.UserEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        nombreCompleto = nombreCompleto,
        username = username,
        identificacion = identificacion,
        telefono = telefono,
        email = email,
        pin = pin,
        role = role,
        isActive = isActive
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        nombreCompleto = nombreCompleto,
        username = username,
        identificacion = identificacion,
        telefono = telefono,
        email = email,
        pin = pin,
        role = role,
        isActive = isActive
    )
}