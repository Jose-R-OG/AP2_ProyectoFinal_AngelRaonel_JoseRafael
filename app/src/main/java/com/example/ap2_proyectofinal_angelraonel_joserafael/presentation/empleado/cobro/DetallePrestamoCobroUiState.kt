package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceipt

data class CuotaItemState(
    val id: Long,
    val numeroCuota: String,
    val fechaDue: String,
    val montoFormatted: String,
    val status: CuotaStatus,
    val moraText: String? = null,
    val atrasoDaysText: String? = null,
    val isSelected: Boolean = false
)

enum class CuotaStatus {
    PAGADO, VENCIDO, PENDIENTE, FUTURO
}

data class DetallePrestamoCobroUiState(
    val prestamoCode: String = "",
    val clientId: Long = -1L,
    val canCreateLoans: Boolean = false,
    val statusText: String = "",
    val clientName: String = "",
    val pendingBalanceFormatted: String = "$0.00",
    val percentagePaidText: String = "0% Pagado",
    val cuotasProgressText: String = "",
    val originalAmountFormatted: String = "$0.00",
    val interestRateText: String = "",
    val totalPlanFormatted: String = "",
    val progress: Float = 0f,
    val selectedCount: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val cuotasList: List<CuotaItemState> = emptyList(),
    val isProcessingPayment: Boolean = false,
    val paymentSuccess: Boolean = false,
    val generatedReceipt: PaymentReceipt? = null,
    val errorMessage: String? = null
)
