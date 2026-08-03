package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun getPrestamosByCliente(clienteId: Long): Flow<List<Prestamo>>
    suspend fun getPrestamoById(id: Long): Prestamo?
    suspend fun savePrestamo(prestamo: Prestamo): Long
}