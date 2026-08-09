package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCuotasPrestamoUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    operator fun invoke(prestamoId: Long): Flow<List<Cuota>> {
        return prestamoRepository.obtenerCuotasPorPrestamo(prestamoId)
    }
}