package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPrestamo(prestamo: PrestamoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCuotas(cuotas: List<CuotaEntity>)

    @Query("SELECT * FROM prestamos ORDER BY fechaCreacion DESC")
    fun obtenerTodosLosPrestamos(): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM cuotas WHERE prestamoId = :prestamoId ORDER BY numeroCuota ASC")
    fun obtenerCuotasPorPrestamo(prestamoId: Long): Flow<List<CuotaEntity>>

    @Query("SELECT * FROM prestamos WHERE estado = :estado ORDER BY fechaCreacion ASC")
    fun obtenerPrestamosPorEstado(estado: LoanStatus): Flow<List<PrestamoEntity>>
}