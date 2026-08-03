package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.tarifario

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import java.math.BigDecimal
import javax.inject.Inject

class UpsertTarifarioUseCase @Inject constructor(
    private val repository: TarifarioRepository
) {
    suspend operator fun invoke(tarifario: Tarifario): Result<Unit> {

        if (tarifario.duracion <= 0) {
            return Result.failure(Exception("La duración (plazo) debe ser mayor a 0."))
        }

        if (tarifario.porcentajeInteres < BigDecimal.ZERO) {
            return Result.failure(Exception("El porcentaje de interés no puede ser negativo."))
        }

        repository.saveTarifario(tarifario)
        return Result.success(Unit)
    }
}