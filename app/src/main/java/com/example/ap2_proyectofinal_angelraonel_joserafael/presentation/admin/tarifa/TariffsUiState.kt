package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

data class TariffItem(
    val weeks: Int,
    val description: String,
    val percentage: String
)

data class TariffsUiState(
    val dailyRate: String = "5",
    val dailyRateError: String? = null,
    val biweeklyRate: String = "10",
    val biweeklyRateError: String? = null,
    val monthlyRate: String = "15",
    val monthlyRateError: String? = null,
    val fourWeeksRate: String = "10",
    val fourWeeksRateError: String? = null,
    val sixWeeksRate: String = "15",
    val sixWeeksRateError: String? = null,
    val twelveWeeksRate: String = "25",
    val twelveWeeksRateError: String? = null,

    val projectedNetMargin: String = "8.6%",
    val averageMarketRate: String = "13.3%",
    val riskScore: String = "BAJO",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showSuccessToast: Boolean = false,
    val errorMessage: String? = null
)
