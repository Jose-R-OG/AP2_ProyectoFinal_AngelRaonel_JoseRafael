package com.example.ap2_proyectofinal_angelraonel_joserafael.util

object CedulaValidator {

    fun validate(cedula: String): Boolean {
        val cleanedCedula = cedula.replace("-", "").replace(" ", "")

        if (cleanedCedula.length != 11 || (!cleanedCedula.all { it.isDigit() })) {
            return false
        }

        val digits = cleanedCedula.map { it.toString().toInt() }
        val verifyDigit = digits[10]
        val multipliers = intArrayOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2)
        var totalSum = 0

        for (i in 0 until 10) {
            var product = digits[i] * multipliers[i]

            if (product >= 10) {
                product = (product / 10) + (product % 10)
            }

            totalSum += product
        }

        val calculatedVerifyDigit = (10 - (totalSum % 10)) % 10

        return calculatedVerifyDigit == verifyDigit
    }
}
