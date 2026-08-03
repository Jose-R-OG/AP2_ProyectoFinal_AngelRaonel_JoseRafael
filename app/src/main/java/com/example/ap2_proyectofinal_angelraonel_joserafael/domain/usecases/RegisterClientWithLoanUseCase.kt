package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import javax.inject.Inject

class RegisterClientWithLoanUseCase @Inject constructor(
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(cliente: Cliente, prestamo: Prestamo): Result<Long> {
        return try {
            val clienteId = clienteRepository.saveCliente(cliente)
            val prestamoConId = prestamo.copy(clienteId = clienteId)
            prestamoRepository.guardarPrestamo(prestamoConId)
            Result.success(clienteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}