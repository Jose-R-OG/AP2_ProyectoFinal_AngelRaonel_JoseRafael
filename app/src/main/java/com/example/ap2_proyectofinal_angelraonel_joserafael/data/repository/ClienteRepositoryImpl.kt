package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local.ClienteDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.mapper.toEntity
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

    override suspend fun getClienteById(id: Long): Cliente? {
        return clienteDao.getClienteById(id)?.toDomain()
    }

    override suspend fun saveCliente(cliente: Cliente) {
        clienteDao.insertCliente(cliente.toEntity())
    }

    override suspend fun softDeleteCliente(id: Long) {
        clienteDao.softDeleteCliente(id)
    }
}