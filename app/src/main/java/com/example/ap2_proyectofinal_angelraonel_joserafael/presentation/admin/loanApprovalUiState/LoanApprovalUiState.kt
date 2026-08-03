package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApprovalUiState

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import java.math.BigDecimal

data class LoanApprovalUiState(
    val pendingPrestamos: List<PrestamoEntity> = emptyList(),
    val selectedPrestamo: PrestamoEntity? = null,
    val selectedCuotas: List<CuotaEntity> = emptyList(),

    val totalPendingCount: Int = 0,
    val totalRequestedVolume: BigDecimal = BigDecimal.ZERO,
    val avgInterestRate: BigDecimal = BigDecimal.ZERO,

    val isDetailOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val ticketParaImprimir: String? = null
)