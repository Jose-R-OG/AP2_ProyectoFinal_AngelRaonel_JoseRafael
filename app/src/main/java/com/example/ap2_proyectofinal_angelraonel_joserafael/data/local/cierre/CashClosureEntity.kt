package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "cash_closures",
    indices = [Index(value = ["userId", "businessDate"], unique = true)]
)
data class CashClosureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val businessDate: String,
    val closedAt: Long,
    val totalCollected: BigDecimal,
    val cashRegistered: BigDecimal,
    val cashInHand: BigDecimal,
    val transferAmount: BigDecimal,
    val transactionCount: Int,
    val visitedCount: Int
)
