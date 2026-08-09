package com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus

@Entity(
    tableName = "loan_status_history",
    indices = [Index("loanId"), Index("changedAt")]
)
data class LoanStatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loanId: Long,
    val status: LoanStatus,
    val changedAt: Long,
    val changedByUserId: Long,
    val note: String?
)
