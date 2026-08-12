package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import java.math.BigDecimal
import java.util.Calendar
import javax.inject.Inject

class GuardarPrestamoUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    suspend fun execute(prestamo: Prestamo) {
        val loanId = repository.guardarPrestamo(prestamo)

        val cuotas = mutableListOf<Cuota>()
        val calendar = Calendar.getInstance()
        prestamo.fechaInicio?.let { calendar.timeInMillis = it }
            ?: run { calendar.timeInMillis = System.currentTimeMillis() }

        var fechaFinCalculada = calendar.timeInMillis

        for (i in 1..prestamo.cantidadCuotas) {
            when (prestamo.frecuenciaPago) {
                FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                FrecuenciaPago.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                FrecuenciaPago.QUINCENAL -> calendar.add(Calendar.DAY_OF_YEAR, 15)
                FrecuenciaPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            }

            if (i == 1 && prestamo.diaPagoPreferido != null) {
                if (prestamo.frecuenciaPago == FrecuenciaPago.SEMANAL) {
                    calendar.set(Calendar.DAY_OF_WEEK, prestamo.diaPagoPreferido)
                    if (calendar.timeInMillis < System.currentTimeMillis()) {
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    }
                } else if (prestamo.frecuenciaPago == FrecuenciaPago.QUINCENAL || prestamo.frecuenciaPago == FrecuenciaPago.MENSUAL) {
                    calendar.set(Calendar.DAY_OF_MONTH, prestamo.diaPagoPreferido)
                    if (calendar.timeInMillis < System.currentTimeMillis()) {
                        calendar.add(Calendar.MONTH, 1)
                    }
                }
            }

            cuotas.add(
                Cuota(
                    prestamoId = loanId,
                    numeroCuota = i,
                    fechaVencimiento = calendar.timeInMillis,
                    montoEsperado = prestamo.montoCuota,
                    montoPagado = BigDecimal.ZERO,
                    estaPagada = false
                )
            )
            fechaFinCalculada = calendar.timeInMillis
        }

        repository.guardarCuotas(cuotas)

        repository.guardarPrestamo(prestamo.copy(id = loanId, fechaFin = fechaFinCalculada))
    }
}
