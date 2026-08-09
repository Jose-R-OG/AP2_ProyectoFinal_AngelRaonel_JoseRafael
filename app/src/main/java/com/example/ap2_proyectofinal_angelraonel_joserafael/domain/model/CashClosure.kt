package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

import java.math.BigDecimal

data class CashClosure(
    val id: Long = 0,
    val userId: Long,
    val businessDate: String,
    val closedAt: Long = System.currentTimeMillis(),
    val totalCollected: BigDecimal,
    val cashRegistered: BigDecimal,
    val cashInHand: BigDecimal,
    val transferAmount: BigDecimal,
    val transactionCount: Int,
    val visitedCount: Int
)
