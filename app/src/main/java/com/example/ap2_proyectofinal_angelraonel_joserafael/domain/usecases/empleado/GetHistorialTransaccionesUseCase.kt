package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistorialTransaccionesUseCase @Inject constructor(
    private val transaccionRepository: TransaccionRepository
) {
    operator fun invoke(prestamoId: Long): Flow<List<Transaccion>> {
        return transaccionRepository.obtenerHistorialPorPrestamo(prestamoId)
    }
}