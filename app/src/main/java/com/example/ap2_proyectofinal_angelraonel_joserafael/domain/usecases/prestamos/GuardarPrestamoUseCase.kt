package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import javax.inject.Inject

class GuardarPrestamoUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    suspend operator fun invoke(prestamo: Prestamo): Result<Unit> {

        if (prestamo.clienteId <= 0 || prestamo.empleadoId <= 0) {
            return Result.failure(Exception("Debe seleccionar un cliente y un empleado válido."))
        }
        if (prestamo.montoSolicitado <= BigDecimal.ZERO || prestamo.cantidadCuotas <= 0) {
            return Result.failure(Exception("El monto y las cuotas deben ser mayores a cero."))
        }


        val interesTotalCalculado = prestamo.montoSolicitado
            .multiply(prestamo.porcentajeInteres)
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        val totalAPagarCalculado = prestamo.montoSolicitado.add(interesTotalCalculado)

        val montoCuotaCalculado = totalAPagarCalculado
            .divide(BigDecimal(prestamo.cantidadCuotas), 2, RoundingMode.HALF_UP)

        val fechaInicioReal = prestamo.fechaInicio ?: System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = fechaInicioReal }

        // Proyectar la fecha fin sumando el tiempo según la frecuencia
        for (i in 1..prestamo.cantidadCuotas) {
            when (prestamo.frecuenciaPago) {
                FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                FrecuenciaPago.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                FrecuenciaPago.QUINCENAL -> calendar.add(Calendar.DAY_OF_YEAR, 15)
                FrecuenciaPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            }
        }
        val fechaFinCalculada = calendar.timeInMillis


        val prestamoAutocalculado = prestamo.copy(
            interesTotal = interesTotalCalculado,
            totalAPagar = totalAPagarCalculado,
            montoCuota = montoCuotaCalculado,
            fechaInicio = fechaInicioReal,
            fechaFin = fechaFinCalculada // <-- Autocalculado con exactitud
        )

        repository.guardarPrestamo(prestamoAutocalculado)
        return Result.success(Unit)
    }
}