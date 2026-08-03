package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import kotlinx.coroutines.flow.Flow

interface ClienteRepository {
    fun getActiveClientes(): Flow<List<Cliente>>
    suspend fun getClienteById(id: Long): Cliente?
    suspend fun saveCliente(cliente: Cliente): Long
    suspend fun softDeleteCliente(id: Long)
}