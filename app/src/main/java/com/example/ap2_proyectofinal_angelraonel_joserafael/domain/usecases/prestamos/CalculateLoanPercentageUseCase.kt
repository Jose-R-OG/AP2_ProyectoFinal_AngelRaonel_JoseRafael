package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CalcularPorcentajePrestamoUseCase @Inject constructor() {

    data class ResultadoCalculo(
        val totalAPagar: BigDecimal,
        val montoCuota: BigDecimal,
        val interesTotal: BigDecimal
    )

    operator fun invoke(montoSolicitado: BigDecimal, porcentajeInteres: BigDecimal, cantidadCuotas: Int): ResultadoCalculo {
        // Interés = Monto * (Porcentaje / 100)
        val interesTotal = montoSolicitado
            .multiply(porcentajeInteres)
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        // Total a Pagar = Capital + Intereses
        val totalAPagar = montoSolicitado.add(interesTotal)

        // Valor Cuota Fija = Total a Pagar / Plazo
        val montoCuota = totalAPagar
            .divide(BigDecimal(cantidadCuotas), 2, RoundingMode.HALF_UP)

        return ResultadoCalculo(
            totalAPagar = totalAPagar,
            montoCuota = montoCuota,
            interesTotal = interesTotal
        )
    }
}