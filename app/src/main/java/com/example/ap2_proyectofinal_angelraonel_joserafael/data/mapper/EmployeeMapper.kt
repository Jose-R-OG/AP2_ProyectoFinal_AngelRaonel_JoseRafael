package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.Employee

fun User.toEmployee(): Employee {
    return Employee(
        id = id.toString(),
        name = nombreCompleto,
        username = username,
        identification = identificacion,
        phone = telefono,
        address = address,
        route = route ?: "SIN ASIGNAR",
        clientsAssigned = 0,
        isActive = isActive,
        photoUrl = profilePhotoPath,
        dniFrontPhotoPath = dniFrontPhotoPath,
        dniBackPhotoPath = dniBackPhotoPath,
        canCreateClients = canCreateClients,
        canCollectPayments = canCollectPayments,
        canViewRoute = canViewRoute,
        canCloseCash = canCloseCash,
        canShareDocuments = canShareDocuments
    )
}
