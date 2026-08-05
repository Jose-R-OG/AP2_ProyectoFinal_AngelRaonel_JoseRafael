package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CierreCajaViewModel @Inject constructor(
    private val printerManager: BluetoothPrinterManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CierreCajaUiState())
    val uiState: StateFlow<CierreCajaUiState> = _uiState.asStateFlow()

    fun onEvent(event: CierreCajaUiEvent) {
        when (event) {
            is CierreCajaUiEvent.FinalizarTurno -> finalizarTurno()
            is CierreCajaUiEvent.ImprimirResumen -> imprimirResumen()
            is CierreCajaUiEvent.OnCashInHandChanged -> updateCashInHand(event.amount)
            is CierreCajaUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun updateCashInHand(amount: String) {
        _uiState.update { it.copy(cashInHand = amount) }
    }

    private fun finalizarTurno() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFinalizingTurn = true) }
            _uiState.update {
                it.copy(
                    isFinalizingTurn = false,
                    isTurnActive = false,
                    turnFinalizedSuccess = true
                )
            }
        }
    }

    private fun imprimirResumen() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPrinting = true) }
            val ticketText = """
                === TACOBRAO APP ===
                CIERRE DE CAJA / TURNO
                --------------------
                Total Recaudado: ${uiState.value.totalCollectedTurn}
                Cobros Realizados: ${uiState.value.totalCobrosCount}
                Clientes Visitados: ${uiState.value.visitedCount}/${uiState.value.totalTargetVisited}
                Efectivo: ${uiState.value.cashAmount}
                Transferencias: ${uiState.value.transferAmount}
                --------------------
                ¡Turno Cuadrado!
            """.trimIndent()

            val result = printerManager.imprimirTicket(ticketText)
            _uiState.update {
                it.copy(
                    isPrinting = false,
                    errorMessage = if (result.isFailure) result.exceptionOrNull()?.message else null
                )
            }
        }
    }
}
