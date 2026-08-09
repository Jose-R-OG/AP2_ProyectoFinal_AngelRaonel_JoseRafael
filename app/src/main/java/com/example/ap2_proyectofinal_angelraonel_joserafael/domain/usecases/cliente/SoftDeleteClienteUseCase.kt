package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cliente

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import javax.inject.Inject

class SoftDeleteClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        if (id <= 0) {
            return Result.failure(IllegalArgumentException("El cliente no es válido."))
        }

        return try {
            val cliente = repository.getClienteById(id)
                ?: return Result.failure(IllegalArgumentException("El cliente no existe."))

            if (!cliente.isActive) {
                return Result.failure(IllegalStateException("El cliente ya está desactivado."))
            }

            if (!repository.softDeleteCliente(id)) {
                return Result.failure(
                    IllegalStateException(
                        "No se puede desactivar: el cliente tiene una solicitud, deuda o préstamo activo."
                    )
                )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
