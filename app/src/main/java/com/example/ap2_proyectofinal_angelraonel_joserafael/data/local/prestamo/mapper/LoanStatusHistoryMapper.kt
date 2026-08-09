package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.mapper

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.LoanStatusHistoryEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory

fun LoanStatusHistoryEntity.toDomain() = LoanStatusHistory(
    id = id,
    loanId = loanId,
    status = status,
    changedAt = changedAt,
    changedByUserId = changedByUserId,
    note = note
)

fun LoanStatusHistory.toEntity() = LoanStatusHistoryEntity(
    id = id,
    loanId = loanId,
    status = status,
    changedAt = changedAt,
    changedByUserId = changedByUserId,
    note = note
)
