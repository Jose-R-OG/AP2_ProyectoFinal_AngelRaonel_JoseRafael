package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import java.util.Calendar
import javax.inject.Inject

class AprobarPrestamoUseCase @Inject constructor(
    private val repository: PrestamoRepository
) {
    suspend operator fun invoke(prestamo: Prestamo, adminId: Long): Result<Unit> {

        val prestamoAprobado = prestamo.copy(
            estado = LoanStatus.APROBADO,
            aprobadoPorAdminId = adminId
        )

        val prestamoIdGenerado = repository.guardarPrestamo(prestamoAprobado)

        val cuotasGeneradas = mutableListOf<Cuota>()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = prestamo.fechaInicio ?: System.currentTimeMillis()
        }

        for (numeroCuota in 1..prestamo.cantidadCuotas) {
            when (prestamo.frecuenciaPago) {
                FrecuenciaPago.DIARIO -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                FrecuenciaPago.SEMANAL -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                FrecuenciaPago.QUINCENAL -> calendar.add(Calendar.DAY_OF_YEAR, 15)
                FrecuenciaPago.MENSUAL -> calendar.add(Calendar.MONTH, 1)
            }

            cuotasGeneradas.add(
                Cuota(
                    prestamoId = prestamoIdGenerado,
                    numeroCuota = numeroCuota,
                    fechaVencimiento = calendar.timeInMillis,
                    montoEsperado = prestamo.montoCuota
                )
            )
        }
        repository.guardarCuotas(cuotasGeneradas)

        return Result.success(Unit)
    }
}