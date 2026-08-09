package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.CuotaEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import java.math.BigDecimal
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory

enum class LoanListTab { ACTIVOS, RECHAZADOS, EN_ESPERA }

data class LoanClientSummary(
    val name: String,
    val dni: String,
    val phone: String,
    val address: String,
    val zone: String,
    val profilePhotoPath: String? = null,
    val dniFrontPhotoPath: String? = null,
    val dniBackPhotoPath: String? = null
)

data class LoanApprovalUiState(
    val pendingPrestamos: List<PrestamoEntity> = emptyList(),
    val selectedTab: LoanListTab = LoanListTab.EN_ESPERA,
    val clientSummaries: Map<Long, LoanClientSummary> = emptyMap(),
    val selectedPrestamo: PrestamoEntity? = null,
    val selectedCuotas: List<CuotaEntity> = emptyList(),
    val historyByLoan: Map<Long, List<LoanStatusHistory>> = emptyMap(),

    val totalPendingCount: Int = 0,
    val totalRequestedVolume: BigDecimal = BigDecimal.ZERO,
    val avgInterestRate: BigDecimal = BigDecimal.ZERO,

    val isDetailOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val ticketParaImprimir: String? = null
)
