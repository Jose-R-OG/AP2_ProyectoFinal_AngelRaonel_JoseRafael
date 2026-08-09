package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval

import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity

sealed class LoanApprovalUiEvent {
    data class SelectTab(val tab: LoanListTab) : LoanApprovalUiEvent()
    data class SelectPrestamo(val prestamo: PrestamoEntity) : LoanApprovalUiEvent()
    data object CloseDetail : LoanApprovalUiEvent()
    data class ApprovePrestamo(val prestamo: PrestamoEntity) : LoanApprovalUiEvent()
    data class RejectPrestamo(val prestamo: PrestamoEntity, val motivo: String? = null) : LoanApprovalUiEvent()
    data object PrintTicket : LoanApprovalUiEvent()
    data object DismissTicket : LoanApprovalUiEvent()
}
