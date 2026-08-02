package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Query("SELECT * FROM loan_configs WHERE isActive = 1")
    fun getActiveConfigs(): Flow<List<ConfigEntity>>

    @Query("UPDATE loan_configs SET isActive = 0 WHERE id = :id")
    suspend fun disableConfig(id: Long)
}