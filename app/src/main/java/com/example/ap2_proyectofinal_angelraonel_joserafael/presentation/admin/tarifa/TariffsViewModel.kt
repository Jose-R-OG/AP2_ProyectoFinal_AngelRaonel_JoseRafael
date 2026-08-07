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
                    state.copy(
                        dailyRate = daily?.porcentajeInteres?.toString() ?: "",
                        biweeklyRate = biweekly?.porcentajeInteres?.toString() ?: "",
                        monthlyRate = monthly?.porcentajeInteres?.toString() ?: "",
                        fourWeeksRate = fourWeeks?.porcentajeInteres?.toString() ?: "",
                        sixWeeksRate = sixWeeks?.porcentajeInteres?.toString() ?: "",
                        twelveWeeksRate = twelveWeeks?.porcentajeInteres?.toString() ?: ""
                    )
                }
                isInitialized = true
            }
        }
    }

    fun onEvent(event: TariffsUiEvent) {
        when (event) {
            is TariffsUiEvent.DailyRateChanged -> _uiState.update { it.copy(dailyRate = event.value) }
            is TariffsUiEvent.BiweeklyRateChanged -> _uiState.update { it.copy(biweeklyRate = event.value) }
            is TariffsUiEvent.MonthlyRateChanged -> _uiState.update { it.copy(monthlyRate = event.value) }
            is TariffsUiEvent.FourWeeksChanged -> _uiState.update { it.copy(fourWeeksRate = event.value) }
            is TariffsUiEvent.SixWeeksChanged -> _uiState.update { it.copy(sixWeeksRate = event.value) }
            is TariffsUiEvent.TwelveWeeksChanged -> _uiState.update { it.copy(twelveWeeksRate = event.value) }
            is TariffsUiEvent.SaveTariffs -> saveTariffs()
        }
    }

    private fun idExistente(frecuencia: FrecuenciaPago, duracion: Int? = null): Long {
        return if (duracion != null) {
            tarifariosActuales.find { it.frecuencia == frecuencia && (it.duracion == duracion || it.duracion == null) }?.id ?: 0L
        } else {
            tarifariosActuales.find { it.frecuencia == frecuencia }?.id ?: 0L
        }
    }

    private fun saveTariffs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
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
                _uiState.update { it.copy(isSaving = false, showSuccessToast = true) }
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(showSuccessToast = false) }
            } else {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}