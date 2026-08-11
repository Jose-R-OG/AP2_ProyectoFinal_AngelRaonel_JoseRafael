package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toDomain
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper.toEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.CashClosure
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.CashClosureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CashClosureRepositoryImpl @Inject constructor(
    private val dao: CashClosureDao
) : CashClosureRepository {
    override suspend fun save(closure: CashClosure) {
        dao.upsert(closure.toEntity())
    }

    override fun observeForDate(userId: Long, businessDate: String): Flow<CashClosure?> =
        dao.observeForDate(userId, businessDate).map { it?.toDomain() }
}
