package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CashClosureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(closure: CashClosureEntity): Long

    @Query("SELECT * FROM cash_closures WHERE userId = :userId AND businessDate = :businessDate LIMIT 1")
    fun observeForDate(userId: Long, businessDate: String): Flow<CashClosureEntity?>
}
