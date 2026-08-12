package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.TipoTransaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import java.math.BigDecimal
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RegistrarAbonoUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository,
    private val transaccionRepository: TransaccionRepository
) {
    suspend operator fun invoke(
        cuotaOriginal: Cuota,
        montoRecibido: BigDecimal,
        empleadoId: Long,
        paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO
    ): Result<Unit> {

        if (montoRecibido <= BigDecimal.ZERO) {
            return Result.failure(Exception("El monto debe ser mayor a cero."))
        }

        val balanceTotalCuota = cuotaOriginal.montoEsperado.add(cuotaOriginal.moraAcumulada)
        val balancePendiente = balanceTotalCuota.subtract(cuotaOriginal.montoPagado)

        if (montoRecibido > balancePendiente) {
            return Result.failure(Exception("El monto recibido supera el balance pendiente de la cuota."))
        }

        val nuevoMontoPagado = cuotaOriginal.montoPagado.add(montoRecibido)

        val estaTotalmentePagada = nuevoMontoPagado >= balanceTotalCuota

        val cuotaActualizada = cuotaOriginal.copy(
            montoPagado = nuevoMontoPagado,
            estaPagada = estaTotalmentePagada,
            fechaPago = if (estaTotalmentePagada) System.currentTimeMillis() else cuotaOriginal.fechaPago
        )

        val transaccion = Transaccion(
            prestamoId = cuotaOriginal.prestamoId,
            cuotaId = cuotaOriginal.id,
            empleadoId = empleadoId,
            monto = montoRecibido,
            tipo = TipoTransaccion.INGRESO,
            paymentMethod = paymentMethod,
            nota = if (estaTotalmentePagada) "Pago Completo" else "Abono Parcial (Mora y balance pendientes)"
        )

        return try {
            prestamoRepository.guardarCuotas(listOf(cuotaActualizada))
            transaccionRepository.guardarTransaccion(transaccion)

            val prestamo = prestamoRepository.obtenerPrestamoPorId(cuotaOriginal.prestamoId)
                ?: return Result.failure(Exception("No se encontró el préstamo de la cuota."))
            val cuotasActualizadas = prestamoRepository
                .obtenerCuotasPorPrestamo(cuotaOriginal.prestamoId)
                .first()

            val totalPagadoCalculado = prestamo.totalPagado.add(montoRecibido)
            val totalPagado = if (totalPagadoCalculado > prestamo.totalAPagar) {
                prestamo.totalAPagar
            } else {
                totalPagadoCalculado
            }
            val prestamoFinalizado = cuotasActualizadas.isNotEmpty() &&
                cuotasActualizadas.all { it.estaPagada }

            prestamoRepository.guardarPrestamo(
                prestamo.copy(
                    totalPagado = totalPagado,
                    estado = if (prestamoFinalizado) LoanStatus.FINALIZADO else LoanStatus.ACTIVO,
                    fechaFin = if (prestamoFinalizado) System.currentTimeMillis() else prestamo.fechaFin
                )
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
