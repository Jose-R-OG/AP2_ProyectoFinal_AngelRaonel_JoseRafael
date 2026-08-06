package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

sealed class TariffsUiEvent {
    data class DailyRateChanged(val value: String) : TariffsUiEvent()
    data class BiweeklyRateChanged(val value: String) : TariffsUiEvent()
    data class MonthlyRateChanged(val value: String) : TariffsUiEvent()
    data class FourWeeksChanged(val value: String) : TariffsUiEvent()
    data class SixWeeksChanged(val value: String) : TariffsUiEvent()
    data class TwelveWeeksChanged(val value: String) : TariffsUiEvent()
    data object SaveTariffs : TariffsUiEvent()
}
