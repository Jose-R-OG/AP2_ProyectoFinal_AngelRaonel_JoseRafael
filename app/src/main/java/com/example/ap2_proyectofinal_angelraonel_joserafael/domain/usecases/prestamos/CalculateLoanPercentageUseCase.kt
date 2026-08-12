package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CalculateLoanPercentageUseCase @Inject constructor() {

    fun execute(
        monto: BigDecimal,
        porcentaje: BigDecimal,
        plazo: Int
    ): LoanCalculationResult {
        val interesTotal = monto.multiply(porcentaje)
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        val totalAPagar = monto.add(interesTotal)

        val montoCuota = totalAPagar.divide(
            BigDecimal(plazo), 2, RoundingMode.HALF_UP
        )

        return LoanCalculationResult(
            interesTotal = interesTotal,
            totalAPagar = totalAPagar,
            montoCuota = montoCuota
        )
    }
}

data class LoanCalculationResult(
    val interesTotal: BigDecimal,
    val totalAPagar: BigDecimal,
    val montoCuota: BigDecimal
)
