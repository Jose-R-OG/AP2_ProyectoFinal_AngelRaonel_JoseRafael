package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import javax.inject.Inject

class SoftDeleteClienteUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.softDeleteCliente(id)
    }
}