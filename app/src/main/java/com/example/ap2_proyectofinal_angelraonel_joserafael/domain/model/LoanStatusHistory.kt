package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model

data class LoanStatusHistory(
    val id: Long = 0,
    val loanId: Long,
    val status: LoanStatus,
    val changedAt: Long = System.currentTimeMillis(),
    val changedByUserId: Long,
    val note: String? = null
)
