package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Prestamo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrestamo(prestamo: PrestamoEntity): Long

    @Query("SELECT * FROM loans WHERE clienteId = :clienteId")
    fun getPrestamosByCliente(clienteId: Long): Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getPrestamoById(id: Long): PrestamoEntity?
}