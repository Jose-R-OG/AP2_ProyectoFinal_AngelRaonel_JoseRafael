package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

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

        repository.saveCliente(cliente)
        return Result.success(Unit)
    }
}