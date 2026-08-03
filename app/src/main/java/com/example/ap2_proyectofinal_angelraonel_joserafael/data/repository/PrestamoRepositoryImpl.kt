package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.local.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrestamoRepositoryImpl @Inject constructor(
    private val prestamoDao: PrestamoDao
) : PrestamoRepository {

    override fun getPrestamosByCliente(clienteId: Long): Flow<List<Prestamo>> {
        return prestamoDao.getPrestamosByCliente(clienteId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPrestamoById(id: Long): Prestamo? {
        return prestamoDao.getPrestamoById(id)?.toDomain()
    }

    override suspend fun savePrestamo(prestamo: Prestamo): Long {
        return prestamoDao.insertPrestamo(prestamo.toEntity())
    }
}