package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import java.util.Calendar

/** Construye el calendario respetando el día que el cliente eligió. */
fun buildPaymentDates(loan: Prestamo, startedAt: Long): List<Long> {
    val calendar = Calendar.getInstance().apply { timeInMillis = startedAt }
    val preferred = loan.diaPagoPreferido

    if (preferred != null) {
        when (loan.frecuenciaPago) {
            FrecuenciaPago.SEMANAL -> {
                do { calendar.add(Calendar.DAY_OF_YEAR, 1) } while (calendar.get(Calendar.DAY_OF_WEEK) != preferred)
            }
            FrecuenciaPago.QUINCENAL, FrecuenciaPago.MENSUAL -> {
                calendar.set(Calendar.DAY_OF_MONTH, preferred.coerceIn(1, 28))
                if (calendar.timeInMillis <= startedAt) calendar.add(Calendar.MONTH, 1)
            }
            FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    } else {
        when (loan.frecuenciaPago) {
            FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            FrecuenciaPago.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            FrecuenciaPago.QUINCENAL -> calendar.add(Calendar.DAY_OF_YEAR, 15)
            FrecuenciaPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
        }
    }

    return (1..loan.cantidadCuotas).map { index ->
        if (index > 1) {
            when (loan.frecuenciaPago) {
                FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                FrecuenciaPago.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                FrecuenciaPago.QUINCENAL -> calendar.add(Calendar.DAY_OF_YEAR, 15)
                FrecuenciaPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            }
        }
        calendar.timeInMillis
    }
}
