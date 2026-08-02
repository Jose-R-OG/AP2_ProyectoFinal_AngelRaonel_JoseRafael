package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
import kotlinx.coroutines.flow.Flow

interface TarifarioRepository {
    fun getActiveTarifarios(): Flow<List<Tarifario>>
    suspend fun saveTarifario(tarifario: Tarifario)
    suspend fun disableTarifario(id: Long)
}