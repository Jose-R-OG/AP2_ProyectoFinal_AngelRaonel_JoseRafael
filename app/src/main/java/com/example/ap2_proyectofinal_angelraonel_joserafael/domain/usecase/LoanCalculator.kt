package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import java.math.BigDecimal
import java.math.RoundingMode

object LoanCalculator {
    fun calculate(amount: BigDecimal, interestPercent: BigDecimal, durationUnits: Int):
            Pair<BigDecimal, BigDecimal>{
        val totalInterest = amount.multiply(interestPercent).divide(BigDecimal("100"), 2,
            RoundingMode.HALF_UP)
        val totalToPay = amount.add(totalInterest)
        val installment = totalToPay.divide(BigDecimal(durationUnits), 2, RoundingMode.HALF_UP)
        return Pair(totalToPay, installment)
    }
}