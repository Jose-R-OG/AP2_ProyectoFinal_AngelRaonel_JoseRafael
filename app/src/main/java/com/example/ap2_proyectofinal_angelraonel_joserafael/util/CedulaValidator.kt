package com.example.ap2_proyectofinal_angelraonel_joserafael.util

object CedulaValidator {

    /**
     * Valida una cédula de identidad de la República Dominicana utilizando el Algoritmo de Luhn.
     * @param cedula El número de cédula a validar.
     * @return true si la cédula es válida, false en caso contrario.
     */
    fun validate(cedula: String): Boolean {
        // 1. Limpiar el string (eliminar guiones y espacios)
        val cleanedCedula = cedula.replace("-", "").replace(" ", "")

        // 2. Validar que tenga exactamente 11 caracteres numéricos
        if (cleanedCedula.length != 11 || (!cleanedCedula.all { it.isDigit() })) {
            return false
        }

        val digits = cleanedCedula.map { it.toString().toInt() }
        val verifyDigit = digits[10]
        val multipliers = intArrayOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2)
        var totalSum = 0

        // 3. Multiplicar los primeros 10 dígitos por la secuencia alternada
        for (i in 0 until 10) {
            var product = digits[i] * multipliers[i]

            // 4. Si el resultado es >= 10, sumar sus dígitos (ej. 14 -> 1 + 4 = 5)
            if (product >= 10) {
                product = (product / 10) + (product % 10)
            }

            totalSum += product
        }

        // 5. Calcular el dígito verificador usando módulo 10
        val calculatedVerifyDigit = (10 - (totalSum % 10)) % 10

        // 6. Comparar con el 11º dígito
        return calculatedVerifyDigit == verifyDigit
    }
}
