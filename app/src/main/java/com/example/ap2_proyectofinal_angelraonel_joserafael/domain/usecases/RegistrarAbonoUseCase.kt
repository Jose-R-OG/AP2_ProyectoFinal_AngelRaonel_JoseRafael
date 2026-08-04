package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cobros

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import java.math.BigDecimal
import javax.inject.Inject

class RegistrarAbonoUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val transaccionRepository: TransaccionRepository
) {
    suspend operator fun invoke(
        cuotaOriginal: Cuota,
        montoRecibido: BigDecimal,
        empleadoId: Long
    ): Result<Unit> {

        if (montoRecibido <= BigDecimal.ZERO) {
            return Result.failure(Exception("El monto debe ser mayor a cero."))
        }

        // 1. Calcular deudas y saldos
        val balanceTotalCuota = cuotaOriginal.montoEsperado.add(cuotaOriginal.moraAcumulada)
        val nuevoMontoPagado = cuotaOriginal.montoPagado.add(montoRecibido)

        // 2. Evaluar si con este abono se liquida TODO (Cuota base + Mora)
        val estaTotalmentePagada = nuevoMontoPagado >= balanceTotalCuota

        // 3. Crear cuota actualizada
        val cuotaActualizada = cuotaOriginal.copy(
            montoPagado = nuevoMontoPagado,
            estaPagada = estaTotalmentePagada,
            fechaPago = if (estaTotalmentePagada) System.currentTimeMillis() else cuotaOriginal.fechaPago
        )

        // 4. Crear registro de transacción para el cuadre de caja
        val transaccion = Transaccion(
            prestamoId = cuotaOriginal.prestamoId,
            cuotaId = cuotaOriginal.id,
            empleadoId = empleadoId,
            monto = montoRecibido,
            tipo = TipoTransaccion.INGRESO,
            nota = if (estaTotalmentePagada) "Pago Completo" else "Abono Parcial (Mora y balance pendientes)"
        )


        prestamoRepository.guardarCuotas(listOf(cuotaActualizada))
        transaccionRepository.guardarTransaccion(transaccion)

        return Result.success(Unit)
    }
}