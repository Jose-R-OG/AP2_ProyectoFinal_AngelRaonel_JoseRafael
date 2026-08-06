package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

data class CuotaItemState(
    val id: Long,
    val numeroCuota: String, // e.g. "Cuota 1/12"
    val fechaDue: String,   // e.g. "15 Ago 2023"
    val montoFormatted: String, // e.g. "$3,325.00"
    val status: CuotaStatus,
    val moraText: String? = null, // e.g. "+ Mora $150.00"
    val atrasoDaysText: String? = null, // e.g. "(Atraso: 9 días)"
    val isSelected: Boolean = false
)

enum class CuotaStatus {
    PAGADO, VENCIDO, PENDIENTE, FUTURO
}

data class DetallePrestamoCobroUiState(
    val prestamoCode: String = "#PT-8492",
    val statusText: String = "Activo",
    val clientName: String = "María Rodríguez",
    val pendingBalanceFormatted: String = "$12,450.00 DOP",
    val percentagePaidText: String = "64% Pagado",
    val cuotasProgressText: String = "4 de 12 cuotas",
    val originalAmountFormatted: String = "$35,000.00",
    val interestRateText: String = "3.5% mensual",
    val totalPlanFormatted: String = "Total: $39,900.00",
    val cuotasList: List<CuotaItemState> = listOf(
        CuotaItemState(1, "Cuota 1/12", "15 Ago 2023", "$3,325.00", CuotaStatus.PAGADO),
        CuotaItemState(2, "Cuota 2/12", "15 Sep 2023", "$3,325.00", CuotaStatus.PAGADO),
        CuotaItemState(3, "Cuota 3/12", "15 Oct 2023", "$3,325.00", CuotaStatus.VENCIDO, moraText = "+ Mora $150.00", atrasoDaysText = "(Atraso: 9 días)", isSelected = true),
        CuotaItemState(4, "Cuota 4/12", "15 Nov 2023", "$3,325.00", CuotaStatus.PENDIENTE),
        CuotaItemState(5, "Cuota 5/12", "15 Dic 2023", "$3,325.00", CuotaStatus.FUTURO)
    ),
    val isProcessingPayment: Boolean = false,
    val paymentSuccess: Boolean = false,
    val errorMessage: String? = null
)
