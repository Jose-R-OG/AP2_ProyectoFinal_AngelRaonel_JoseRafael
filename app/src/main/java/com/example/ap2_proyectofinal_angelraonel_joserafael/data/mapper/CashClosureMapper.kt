package com.example.ap2_proyectofinal_angelraonel_joserafael.data.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.cierre.CashClosureEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.CashClosure

fun CashClosureEntity.toDomain() = CashClosure(
    id = id,
    userId = userId,
    businessDate = businessDate,
    closedAt = closedAt,
    totalCollected = totalCollected,
    cashRegistered = cashRegistered,
    cashInHand = cashInHand,
    transferAmount = transferAmount,
    transactionCount = transactionCount,
    visitedCount = visitedCount
)

fun CashClosure.toEntity() = CashClosureEntity(
    id = id,
    userId = userId,
    businessDate = businessDate,
    closedAt = closedAt,
    totalCollected = totalCollected,
    cashRegistered = cashRegistered,
    cashInHand = cashInHand,
    transferAmount = transferAmount,
    transactionCount = transactionCount,
    visitedCount = visitedCount
)
