package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cliente

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity): Long

    @Query("SELECT * FROM clients WHERE isActive = 1 ORDER BY fullName ASC")
    fun getActiveClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clients ORDER BY fullName ASC")
    fun getAllClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClienteById(id: Long): ClienteEntity?

    @Query(
        """
        UPDATE clients
        SET isActive = 0
        WHERE id = :id
          AND isActive = 1
          AND NOT EXISTS (
              SELECT 1
              FROM prestamos
              WHERE clienteId = :id
                AND estado IN ('BORRADOR', 'PENDIENTE_REVISION', 'APROBADO', 'ACTIVO')
          )
        """
    )
    suspend fun softDeleteClienteIfAllowed(id: Long): Int

    @Query(
        "SELECT COUNT(*) FROM prestamos WHERE clienteId = :clientId " +
            "AND estado IN ('BORRADOR', 'PENDIENTE_REVISION', 'APROBADO', 'ACTIVO')"
    )
    suspend fun countBlockingLoans(clientId: Long): Int
}
