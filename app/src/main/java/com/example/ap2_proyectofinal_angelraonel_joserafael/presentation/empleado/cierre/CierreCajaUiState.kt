package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

data class CierreCajaUiState(
    val totalCollectedTurn: String = "RD$ 0.00",
    val isTurnActive: Boolean = true,
    val canCloseCash: Boolean = true,
    val totalCobrosCount: Int = 0,
    val visitedCount: Int = 0,
    val totalTargetVisited: Int = 0,
    val cashAmount: String = "RD$ 0.00",
    val transferAmount: String = "RD$ 0.00",
    val registeredCash: String = "RD$ 0.00",
    val cashInHand: String = "RD$ 0.00",
    val cashInHandInput: String = "0.00",
    val cashInHandError: String? = null,
    val differenceAmount: String = "RD$ 0.00",
    val isPrinting: Boolean = false,
    val isFinalizingTurn: Boolean = false,
    val turnFinalizedSuccess: Boolean = false,
    val errorMessage: String? = null
)
