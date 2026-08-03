package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Cliente.local

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

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClienteById(id: Long): ClienteEntity?

    @Query("UPDATE clients SET isActive = 0 WHERE id = :id")
    suspend fun softDeleteCliente(id: Long)
}