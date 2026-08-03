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

    init {
        loadTarifas()
    }

    private fun loadTarifas() {
        viewModelScope.launch {
            getActiveTarifariosUseCase().collect { tarifarios ->
                val fourWeeks = tarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == 4 }
                val sixWeeks = tarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == 6 }
                val twelveWeeks = tarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == 12 }

                _uiState.update { state ->
                    state.copy(
                        fourWeeksRate = fourWeeks?.porcentajeInteres?.toString() ?: "",
                        sixWeeksRate = sixWeeks?.porcentajeInteres?.toString() ?: "",
                        twelveWeeksRate = twelveWeeks?.porcentajeInteres?.toString() ?: ""
                    )
                }
            }
        }
    }

    fun onFourWeeksChange(newValue: String) {
        _uiState.update { it.copy(fourWeeksRate = newValue) }
    }

    fun onSixWeeksChange(newValue: String) {
        _uiState.update { it.copy(sixWeeksRate = newValue) }
    }

    fun onTwelveWeeksChange(newValue: String) {
        _uiState.update { it.copy(twelveWeeksRate = newValue) }
    }

    fun saveTariffs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val currentState = _uiState.value

            val results = mutableListOf<Result<Unit>>()

            if (currentState.fourWeeksRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    frecuencia = FrecuenciaPago.SEMANAL,
                    duracion = 4,
                    porcentajeInteres = currentState.fourWeeksRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }

            if (currentState.sixWeeksRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    frecuencia = FrecuenciaPago.SEMANAL,
                    duracion = 6,
                    porcentajeInteres = currentState.sixWeeksRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }

            if (currentState.twelveWeeksRate.isNotEmpty()) {
                results.add(upsertTarifarioUseCase(Tarifario(
                    frecuencia = FrecuenciaPago.SEMANAL,
                    duracion = 12,
                    porcentajeInteres = currentState.twelveWeeksRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )))
            }

            val hasError = results.any { it.isFailure }

            if (!hasError) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        showSuccessToast = true
                    )
                }
                // Ocultar el toast tras 3 segundos
                kotlinx.coroutines.delay(3000)
                _uiState.update { it.copy(showSuccessToast = false) }
            } else {
                _uiState.update { it.copy(isSaving = false) }
                // Aquí podrías manejar el error, p.ej. mostrar un errorToast
            }
        }
    }
}