package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

data class CierreCajaUiState(
    val totalCollectedTurn: String = "$ 4,850.00",
    val isTurnActive: Boolean = true,
    val totalCobrosCount: Int = 42,
    val visitedCount: Int = 38,
    val totalTargetVisited: Int = 45,
    val cashAmount: String = "$ 3,200.00",
    val transferAmount: String = "$ 1,650.00",
    val registeredCash: String = "$ 3,200.00",
    val cashInHand: String = "$ 3,200.00",
    val differenceAmount: String = "$ 0.00",
    val isPrinting: Boolean = false,
    val isFinalizingTurn: Boolean = false,
    val turnFinalizedSuccess: Boolean = false,
    val errorMessage: String? = null
)
