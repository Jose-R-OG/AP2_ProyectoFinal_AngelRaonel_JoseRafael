package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetallePrestamoCobroViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetallePrestamoCobroUiState())
    val uiState: StateFlow<DetallePrestamoCobroUiState> = _uiState.asStateFlow()

    fun onEvent(event: DetallePrestamoCobroUiEvent) {
        when (event) {
            is DetallePrestamoCobroUiEvent.ToggleSelectCuota -> toggleSelectCuota(event.cuotaId)
            is DetallePrestamoCobroUiEvent.RealizarCobroSeleccionado -> realizarCobro()
            is DetallePrestamoCobroUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun toggleSelectCuota(cuotaId: Long) {
        _uiState.update { state ->
            val updatedList = state.cuotasList.map { item ->
                if (item.id == cuotaId && item.status != CuotaStatus.PAGADO) {
                    item.copy(isSelected = !item.isSelected)
                } else item
            }
            state.copy(cuotasList = updatedList)
        }
    }

    private fun realizarCobro() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingPayment = true) }
            _uiState.update { state ->
                val updatedList = state.cuotasList.map { item ->
                    if (item.isSelected) {
                        item.copy(status = CuotaStatus.PAGADO, isSelected = false, moraText = null, atrasoDaysText = null)
                    } else item
                }
                state.copy(
                    isProcessingPayment = false,
                    paymentSuccess = true,
                    cuotasList = updatedList
                )
            }
        }
    }
}
