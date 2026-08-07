package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

data class TariffItem(
    val weeks: Int,
    val description: String,
    val percentage: String
)

data class TariffsUiState(
    val dailyRate: String = "",
    val biweeklyRate: String = "",
    val monthlyRate: String = "",
    val fourWeeksRate: String = "",
    val sixWeeksRate: String = "",
    val twelveWeeksRate: String = "",
    val projectedNetMargin: String = "18.4%",
    val averageMarketRate: String = "42.0%",
    val riskScore: String = "MODERADO",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showSuccessToast: Boolean = false,
    val errorMessage: String? = null
)