package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPrestamosActivosUseCase @Inject constructor(
    private val prestamoRepository: PrestamoRepository
) {
    operator fun invoke(): Flow<List<Prestamo>> {
        return prestamoRepository.obtenerPrestamosPorEstado(LoanStatus.ACTIVO)
    }
}