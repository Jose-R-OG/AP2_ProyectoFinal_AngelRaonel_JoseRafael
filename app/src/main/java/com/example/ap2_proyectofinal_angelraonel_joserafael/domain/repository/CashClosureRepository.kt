package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.CashClosure
import kotlinx.coroutines.flow.Flow

interface CashClosureRepository {
    suspend fun save(closure: CashClosure)
    fun observeForDate(userId: Long, businessDate: String): Flow<CashClosure?>
}
