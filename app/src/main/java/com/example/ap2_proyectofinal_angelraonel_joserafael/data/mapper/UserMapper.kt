package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.user.UserEntity
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
        isActive = isActive,
        route = route,
        address = address,
        profilePhotoPath = profilePhotoPath,
        dniFrontPhotoPath = dniFrontPhotoPath,
        dniBackPhotoPath = dniBackPhotoPath,
        businessName = businessName,
        businessLogoPath = businessLogoPath,
        canCreateClients = canCreateClients,
        canCollectPayments = canCollectPayments,
        canViewRoute = canViewRoute,
        canCloseCash = canCloseCash,
        canShareDocuments = canShareDocuments
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
        isActive = isActive,
        route = route,
        address = address,
        profilePhotoPath = profilePhotoPath,
        dniFrontPhotoPath = dniFrontPhotoPath,
        dniBackPhotoPath = dniBackPhotoPath,
        businessName = businessName,
        businessLogoPath = businessLogoPath,
        canCreateClients = canCreateClients,
        canCollectPayments = canCollectPayments,
        canViewRoute = canViewRoute,
        canCloseCash = canCloseCash,
        canShareDocuments = canShareDocuments
    )
}
