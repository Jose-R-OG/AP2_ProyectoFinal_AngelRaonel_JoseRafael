package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

data class TariffItem(
    val weeks: Int,
    val description: String,
    val percentage: String
)

data class TariffsUiState(
    val fourWeeksRate: String = "24",
    val sixWeeksRate: String = "36",
    val twelveWeeksRate: String = "92",
    val projectedNetMargin: String = "18.4%",
    val averageMarketRate: String = "42.0%",
    val riskScore: String = "MODERADO",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val showSuccessToast: Boolean = false,
    val errorMessage: String? = null
)