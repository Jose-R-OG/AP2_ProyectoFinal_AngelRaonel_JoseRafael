package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Transaccion
import kotlinx.coroutines.flow.Flow

interface TransaccionRepository {
    suspend fun guardarTransaccion(transaccion: Transaccion)
    fun obtenerTransaccionesPorDia(inicioDia: Long, finDia: Long): Flow<List<Transaccion>>
    fun obtenerHistorialPorPrestamo(prestamoId: Long): Flow<List<Transaccion>>
    fun obtenerTodas(): Flow<List<Transaccion>>
}
