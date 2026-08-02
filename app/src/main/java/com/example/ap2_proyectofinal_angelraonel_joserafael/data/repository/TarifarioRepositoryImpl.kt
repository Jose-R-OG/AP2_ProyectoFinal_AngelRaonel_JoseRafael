package com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.local.ConfigDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.Tarifario.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TarifarioRepositoryImpl @Inject constructor(
    private val configDao: ConfigDao
) : TarifarioRepository {

    override fun getActiveTarifarios(): Flow<List<Tarifario>> {
        return configDao.getActiveConfigs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTarifario(tarifario: Tarifario) {
        configDao.insertConfig(tarifario.toEntity())
    }

    override suspend fun disableTarifario(id: Long) {
        configDao.disableConfig(id)
    }
}