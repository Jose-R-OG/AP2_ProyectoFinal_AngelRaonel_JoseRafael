package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import java.math.BigDecimal
import javax.inject.Inject

class AplicarMoraUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(cuota: Cuota, montoMora: BigDecimal): Result<Unit> {
        if (cuota.estaPagada) {
            return Result.failure(Exception("No se puede aplicar mora a una cuota ya pagada."))
        }

        val cuotaConMora = cuota.copy(
            moraAcumulada = cuota.moraAcumulada.add(montoMora)
        )

        prestamoRepository.guardarCuotas(listOf(cuotaConMora))
        return Result.success(Unit)
    }
}