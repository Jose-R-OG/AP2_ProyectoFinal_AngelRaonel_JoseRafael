package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo

fun PrestamoEntity.toDomain(): Prestamo {
    return Prestamo(
        id,
        clienteId,
        empleadoId,
        aprobadoPorAdminId,
        montoSolicitado,
        porcentajeInteres,
        interesTotal,
        totalAPagar,
        totalPagado,
        montoCuota,
        cantidadCuotas,
        frecuenciaPago,
        fechaCreacion,
        fechaInicio,
        fechaFin,
        estado,
        motivoRechazo,
        rutaFotoContratoFirmado,
        contratoFisicoEntregado
    )
}

fun Prestamo.toEntity(): PrestamoEntity {
    return PrestamoEntity(
        id = id, clienteId = clienteId, empleadoId = empleadoId, aprobadoPorAdminId = aprobadoPorAdminId,
        montoSolicitado = montoSolicitado, porcentajeInteres = porcentajeInteres, interesTotal = interesTotal,
        totalAPagar = totalAPagar, totalPagado = totalPagado, montoCuota = montoCuota,
        cantidadCuotas = cantidadCuotas, frecuenciaPago = frecuenciaPago, fechaCreacion = fechaCreacion,
        fechaInicio = fechaInicio, fechaFin = fechaFin, estado = estado, motivoRechazo = motivoRechazo,
        rutaFotoContratoFirmado = rutaFotoContratoFirmado, contratoFisicoEntregado = contratoFisicoEntregado
    )
}

fun CuotaEntity.toDomain(): Cuota {
    return Cuota(
        id = id, prestamoId = prestamoId, numeroCuota = numeroCuota, fechaVencimiento = fechaVencimiento,
        fechaPago = fechaPago, montoEsperado = montoEsperado, montoPagado = montoPagado,
        moraAcumulada = moraAcumulada, estaPagada = estaPagada
    )
}

fun Cuota.toEntity(): CuotaEntity {
    return CuotaEntity(
        id = id, prestamoId = prestamoId, numeroCuota = numeroCuota, fechaVencimiento = fechaVencimiento,
        fechaPago = fechaPago, montoEsperado = montoEsperado, montoPagado = montoPagado,
        moraAcumulada = moraAcumulada, estaPagada = estaPagada
    )
}