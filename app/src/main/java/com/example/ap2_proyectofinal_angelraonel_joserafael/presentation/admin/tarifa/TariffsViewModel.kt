package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.tarifario.GetActiveTarifariosUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.tarifario.UpsertTarifarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TariffsViewModel @Inject constructor(
    private val getActiveTarifariosUseCase: GetActiveTarifariosUseCase,
    private val upsertTarifarioUseCase: UpsertTarifarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TariffsUiState())
    val uiState: StateFlow<TariffsUiState> = _uiState.asStateFlow()

    private var tarifariosActuales: List<Tarifario> = emptyList()
    private var isInitialized = false

    init {
        loadTarifas()
    }

    private fun loadTarifas() {
        if (isInitialized) return
        viewModelScope.launch {
            getActiveTarifariosUseCase().first().let { tarifarios ->
                tarifariosActuales = tarifarios

                val daily = tarifarios.find { it.frecuencia == FrecuenciaPago.DIARIO }
                val biweekly = tarifarios.find { it.frecuencia == FrecuenciaPago.QUINCENAL }
                val monthly = tarifarios.find { it.frecuencia == FrecuenciaPago.MENSUAL }
                val fourWeeks = tarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == 4 }
                val sixWeeks = tarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == 6 }
                val twelveWeeks = tarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == 12 }

                _uiState.update { state ->
                    calculateMetrics(state.copy(
                        dailyRate = daily?.porcentajeInteres?.toString() ?: state.dailyRate,
                        biweeklyRate = biweekly?.porcentajeInteres?.toString() ?: state.biweeklyRate,
                        monthlyRate = monthly?.porcentajeInteres?.toString() ?: state.monthlyRate,
                        fourWeeksRate = fourWeeks?.porcentajeInteres?.toString() ?: state.fourWeeksRate,
                        sixWeeksRate = sixWeeks?.porcentajeInteres?.toString() ?: state.sixWeeksRate,
                        twelveWeeksRate = twelveWeeks?.porcentajeInteres?.toString() ?: state.twelveWeeksRate
                    ))
                }
                isInitialized = true
            }
        }
    }

    fun onEvent(event: TariffsUiEvent) {
        when (event) {
            is TariffsUiEvent.DailyRateChanged -> updateMetrics { copy(dailyRate = event.value, dailyRateError = null) }
            is TariffsUiEvent.BiweeklyRateChanged -> updateMetrics { copy(biweeklyRate = event.value, biweeklyRateError = null) }
            is TariffsUiEvent.MonthlyRateChanged -> updateMetrics { copy(monthlyRate = event.value, monthlyRateError = null) }
            is TariffsUiEvent.FourWeeksChanged -> updateMetrics { copy(fourWeeksRate = event.value, fourWeeksRateError = null) }
            is TariffsUiEvent.SixWeeksChanged -> updateMetrics { copy(sixWeeksRate = event.value, sixWeeksRateError = null) }
            is TariffsUiEvent.TwelveWeeksChanged -> updateMetrics { copy(twelveWeeksRate = event.value, twelveWeeksRateError = null) }
            is TariffsUiEvent.SaveTariffs -> saveTariffs()
        }
    }

    private fun updateMetrics(transform: TariffsUiState.() -> TariffsUiState) {
        _uiState.update { calculateMetrics(it.transform().copy(errorMessage = null)) }
    }

    private fun calculateMetrics(state: TariffsUiState): TariffsUiState {
        val rates = listOf(
            state.dailyRate,
            state.biweeklyRate,
            state.monthlyRate,
            state.fourWeeksRate,
            state.sixWeeksRate,
            state.twelveWeeksRate
        ).mapNotNull { it.toBigDecimalOrNull() }
        val average = if (rates.isEmpty()) BigDecimal.ZERO else
            rates.reduce(BigDecimal::add).divide(BigDecimal(rates.size), 1, java.math.RoundingMode.HALF_UP)
        val margin = average.multiply(BigDecimal("0.65")).setScale(1, java.math.RoundingMode.HALF_UP)
        val risk = when {
            average <= BigDecimal("15") -> "BAJO"
            average <= BigDecimal("25") -> "MODERADO"
            else -> "ALTO"
        }
        return state.copy(
            projectedNetMargin = "$margin%",
            averageMarketRate = "$average%",
            riskScore = risk
        )
    }

    private fun idExistente(frecuencia: FrecuenciaPago, duracion: Int? = null): Long {
        return if (duracion != null) {
            tarifariosActuales.find { it.frecuencia == frecuencia && it.duracion == duracion }?.id ?: 0L
        } else {
            tarifariosActuales.find { it.frecuencia == frecuencia }?.id ?: 0L
        }
    }

    private fun validateRate(rate: String): String? {
        val number = rate.toBigDecimalOrNull()
        return if (number == null || number < BigDecimal.ZERO || number > BigDecimal("100")) "Debe ser entre 0 y 100" else null
    }

    private fun saveTariffs() {
        val s = _uiState.value
        
        val dailyError = validateRate(s.dailyRate)
        val biweeklyError = validateRate(s.biweeklyRate)
        val monthlyError = validateRate(s.monthlyRate)
        val fourWeeksError = validateRate(s.fourWeeksRate)
        val sixWeeksError = validateRate(s.sixWeeksRate)
        val twelveWeeksError = validateRate(s.twelveWeeksRate)

        if (dailyError != null || biweeklyError != null || monthlyError != null || fourWeeksError != null || sixWeeksError != null || twelveWeeksError != null) {
            _uiState.update { it.copy(
                dailyRateError = dailyError,
                biweeklyRateError = biweeklyError,
                monthlyRateError = monthlyError,
                fourWeeksRateError = fourWeeksError,
                sixWeeksRateError = sixWeeksError,
                twelveWeeksRateError = twelveWeeksError
            ) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val currentState = _uiState.value
            val results = mutableListOf<Result<Unit>>()

            if (currentState.dailyRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    id = idExistente(FrecuenciaPago.DIARIO, 1),
                    frecuencia = FrecuenciaPago.DIARIO,
                    duracion = 1,
                    porcentajeInteres = currentState.dailyRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }
            if (currentState.biweeklyRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    id = idExistente(FrecuenciaPago.QUINCENAL, 1),
                    frecuencia = FrecuenciaPago.QUINCENAL,
                    duracion = 1,
                    porcentajeInteres = currentState.biweeklyRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }
            if (currentState.monthlyRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    id = idExistente(FrecuenciaPago.MENSUAL, 1),
                    frecuencia = FrecuenciaPago.MENSUAL,
                    duracion = 1,
                    porcentajeInteres = currentState.monthlyRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }

            if (currentState.fourWeeksRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    id = idExistente(FrecuenciaPago.SEMANAL, 4),
                    frecuencia = FrecuenciaPago.SEMANAL,
                    duracion = 4,
                    porcentajeInteres = currentState.fourWeeksRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }
            if (currentState.sixWeeksRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    id = idExistente(FrecuenciaPago.SEMANAL, 6),
                    frecuencia = FrecuenciaPago.SEMANAL,
                    duracion = 6,
                    porcentajeInteres = currentState.sixWeeksRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }
            if (currentState.twelveWeeksRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    id = idExistente(FrecuenciaPago.SEMANAL, 12),
                    frecuencia = FrecuenciaPago.SEMANAL,
                    duracion = 12,
                    porcentajeInteres = currentState.twelveWeeksRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }

            val hasError = results.any { it.isFailure }
            if (!hasError) {
                isInitialized = false
                _uiState.update { it.copy(isSaving = false, showSuccessToast = true, errorMessage = null) }
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(showSuccessToast = false) }
            } else {
                _uiState.update { it.copy(isSaving = false, errorMessage = "No fue posible guardar todas las tarifas.") }
            }
        }
    }
}
