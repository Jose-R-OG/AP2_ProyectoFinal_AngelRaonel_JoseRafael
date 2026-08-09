package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.cliente

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import javax.inject.Inject

class UpsertClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(cliente: Cliente): Result<Unit> {
        if (cliente.fullName.isBlank()) {
            return Result.failure(Exception("El nombre completo es obligatorio."))
        }

        if (cliente.dni.isBlank()) {
            return Result.failure(Exception("La cédula es obligatoria."))
        }
        if (cliente.dni.length != 11 || !cliente.dni.all(Char::isDigit)) {
            return Result.failure(Exception("La cédula debe tener exactamente 11 dígitos (${cliente.dni.length}/11)."))
        }

        if (cliente.phone.isBlank()) {
            return Result.failure(Exception("El teléfono es obligatorio."))
        }
        if (cliente.phone.length != 10 || !cliente.phone.all(Char::isDigit)) {
            return Result.failure(Exception("El teléfono debe tener exactamente 10 dígitos (${cliente.phone.length}/10)."))
        }

        if (cliente.id > 0 && repository.hasBlockingLoans(cliente.id)) {
            return Result.failure(
                IllegalStateException("No se puede modificar: el cliente tiene una solicitud, deuda o préstamo activo.")
            )
        }

        return try {
            repository.saveCliente(
                cliente.copy(
                    fullName = cliente.fullName.trim(),
                    dni = cliente.dni.trim(),
                    phone = cliente.phone.trim(),
                    address = cliente.address.trim()
                )
            )
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
