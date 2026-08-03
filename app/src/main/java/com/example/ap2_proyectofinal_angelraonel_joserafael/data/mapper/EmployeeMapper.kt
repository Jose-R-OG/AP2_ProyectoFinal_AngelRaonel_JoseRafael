package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado.Employee

fun User.toEmployee(): Employee {
    return Employee(
        id = id.toString(),
        name = nombreCompleto,
        phone = telefono,
        route = "SIN ASIGNAR",
        clientsAssigned = 0,
        isActive = isActive,
        photoUrl = null
    )
}
