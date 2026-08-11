package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class GetRutaCobroUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    operator fun invoke(): Flow<List<Cuota>> {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        val finDelDiaDeHoy = calendar.timeInMillis

        return prestamoRepository.obtenerRutaDeCobro(finDelDiaDeHoy)
    }
}