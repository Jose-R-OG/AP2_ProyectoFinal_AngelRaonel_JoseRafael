package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrestamoRepositoryImpl @Inject constructor(
    private val prestamoDao: PrestamoDao
) : PrestamoRepository {

    override suspend fun guardarPrestamo(prestamo: Prestamo): Long {
        return prestamoDao.insertarPrestamo(prestamo.toEntity())
    }

    override suspend fun guardarCuotas(cuotas: List<Cuota>) {
        prestamoDao.insertarCuotas(cuotas.map { it.toEntity() })
    }

    override fun obtenerTodosLosPrestamos(): Flow<List<Prestamo>> {
        return prestamoDao.obtenerTodosLosPrestamos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerPrestamosPorEstado(estado: LoanStatus): Flow<List<Prestamo>> {
        return prestamoDao.obtenerPrestamosPorEstado(estado).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerCuotasPorPrestamo(prestamoId: Long): Flow<List<Cuota>> {
        return prestamoDao.obtenerCuotasPorPrestamo(prestamoId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun obtenerRutaDeCobro(fechaLimite: Long): Flow<List<Cuota>> {
        return prestamoDao.obtenerRutaDeCobro(fechaLimite).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun obtenerPrestamoPorId(prestamoId: Long): Prestamo? {
        return prestamoDao.obtenerPrestamoPorId(prestamoId)?.toDomain()
    }
}