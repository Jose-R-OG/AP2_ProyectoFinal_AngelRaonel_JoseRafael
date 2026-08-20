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
        val cuotas = generarCuotas(loanId, prestamo)

        repository.guardarCuotas(cuotas)

        val fechaFinCalculada = cuotas.lastOrNull()?.fechaVencimiento ?: System.currentTimeMillis()
        repository.guardarPrestamo(prestamo.copy(id = loanId, fechaFin = fechaFinCalculada))
    }

    private fun generarCuotas(loanId: Long, prestamo: Prestamo): List<Cuota> {
        val cuotas = mutableListOf<Cuota>()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = prestamo.fechaInicio ?: System.currentTimeMillis()
        }

        for (i in 1..prestamo.cantidadCuotas) {
            avanzarCalendarioSegunFrecuencia(calendar, prestamo.frecuenciaPago)

            if (i == 1) {
                aplicarPreferenciaDiaPago(calendar, prestamo)
            }

            cuotas.add(
                buildCuota(loanId, i, calendar.timeInMillis, prestamo.montoCuota)
            )
        }
        return cuotas
    }

    private fun avanzarCalendarioSegunFrecuencia(calendar: Calendar, frecuencia: FrecuenciaPago) {
        when (frecuencia) {
            FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            FrecuenciaPago.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            FrecuenciaPago.QUINCENAL -> calendar.add(Calendar.DAY_OF_YEAR, 15)
            FrecuenciaPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
        }
    }

    private fun aplicarPreferenciaDiaPago(calendar: Calendar, prestamo: Prestamo) {
        val diaPreferido = prestamo.diaPagoPreferido ?: return

        when (prestamo.frecuenciaPago) {
            FrecuenciaPago.SEMANAL -> {
                ajustarDiaSemana(calendar, diaPreferido)
            }
            FrecuenciaPago.QUINCENAL, FrecuenciaPago.MENSUAL -> {
                ajustarDiaMes(calendar, diaPreferido)
            }
            else -> {}
        }
    }

    private fun ajustarDiaSemana(calendar: Calendar, dia: Int) {
        calendar.set(Calendar.DAY_OF_WEEK, dia)
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
    }

    private fun ajustarDiaMes(calendar: Calendar, dia: Int) {
        calendar.set(Calendar.DAY_OF_MONTH, dia)
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.MONTH, 1)
        }
    }

    private fun buildCuota(loanId: Long, numero: Int, fecha: Long, monto: BigDecimal) = Cuota(
        prestamoId = loanId,
        numeroCuota = numero,
        fechaVencimiento = fecha,
        montoEsperado = monto,
        montoPagado = BigDecimal.ZERO,
        estaPagada = false
    )
}
