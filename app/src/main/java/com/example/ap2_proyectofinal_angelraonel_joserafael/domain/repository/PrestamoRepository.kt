package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    suspend fun guardarPrestamo(prestamo: Prestamo): Long
    suspend fun guardarCuotas(cuotas: List<Cuota>)
    fun obtenerTodosLosPrestamos(): Flow<List<Prestamo>>
    fun obtenerPrestamosPorEstado(estado: LoanStatus): Flow<List<Prestamo>>
    fun obtenerCuotasPorPrestamo(prestamoId: Long): Flow<List<Cuota>>
    fun obtenerRutaDeCobro(fechaLimite: Long): Flow<List<Cuota>>
}