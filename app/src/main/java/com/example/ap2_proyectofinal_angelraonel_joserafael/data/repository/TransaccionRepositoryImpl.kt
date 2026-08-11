package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion.TransaccionDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TransaccionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransaccionRepositoryImpl @Inject constructor(
    private val transaccionDao: TransaccionDao
) : TransaccionRepository {

    override suspend fun guardarTransaccion(transaccion: Transaccion) {
        transaccionDao.insertarTransaccion(transaccion.toEntity())
    }

    override fun obtenerTransaccionesPorDia(inicioDia: Long, finDia: Long): Flow<List<Transaccion>> {
        return transaccionDao.obtenerTransaccionesPorDia(inicioDia, finDia).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerHistorialPorPrestamo(prestamoId: Long): Flow<List<Transaccion>> {
        return transaccionDao.obtenerHistorialPorPrestamo(prestamoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerTodas(): Flow<List<Transaccion>> {
        return transaccionDao.obtenerTodas().map { entities -> entities.map { it.toDomain() } }
    }
}
