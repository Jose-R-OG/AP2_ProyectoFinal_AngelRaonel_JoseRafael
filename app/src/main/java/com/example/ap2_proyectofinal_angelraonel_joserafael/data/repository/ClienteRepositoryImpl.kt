package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClienteRepositoryImpl @Inject constructor(
    private val clienteDao: ClienteDao
) : ClienteRepository {

    override fun getActiveClientes(): Flow<List<Cliente>> {
        return clienteDao.getActiveClientes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllClientes(): Flow<List<Cliente>> {
        return clienteDao.getAllClientes().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getClienteById(id: Long): Cliente? {
        return clienteDao.getClienteById(id)?.toDomain()
    }

    override suspend fun saveCliente(cliente: Cliente): Long {
        return clienteDao.insertCliente(cliente.toEntity())
    }

    override suspend fun softDeleteCliente(id: Long): Boolean {
        return clienteDao.softDeleteClienteIfAllowed(id) > 0
    }

    override suspend fun hasBlockingLoans(id: Long): Boolean = clienteDao.countBlockingLoans(id) > 0
}
