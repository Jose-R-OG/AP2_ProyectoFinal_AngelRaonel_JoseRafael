package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.transaccion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTransaccion(transaccion: TransaccionEntity)

    @Query("SELECT * FROM transacciones WHERE fecha >= :inicioDia AND fecha <= :finDia ORDER BY fecha DESC")
    fun obtenerTransaccionesPorDia(inicioDia: Long, finDia: Long): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE prestamoId = :prestamoId ORDER BY fecha DESC")
    fun obtenerHistorialPorPrestamo(prestamoId: Long): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<TransaccionEntity>>
}
