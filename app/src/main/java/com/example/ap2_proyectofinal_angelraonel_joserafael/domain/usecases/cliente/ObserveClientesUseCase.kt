package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveClientesUseCase @Inject constructor(
    private val repository: ClienteRepository
) {
    operator fun invoke(): Flow<List<Cliente>> {
        return repository.getActiveClientes()
    }
}