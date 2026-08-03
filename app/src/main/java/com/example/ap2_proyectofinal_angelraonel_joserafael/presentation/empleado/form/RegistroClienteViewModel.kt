package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.RegisterClientWithLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class RegistroClienteViewModel @Inject constructor(
    private val registerClientWithLoanUseCase: RegisterClientWithLoanUseCase
) : ViewModel() {

    var fullName by mutableStateOf("")
    var dni by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var profilePhotoPath by mutableStateOf<String?>(null)
    var dniFrontPhotoPath by mutableStateOf<String?>(null)
    var dniBackPhotoPath by mutableStateOf<String?>(null)

    var montoPrestamo by mutableStateOf("")
    var numCuotas by mutableStateOf("")
    var frecuenciaPago by mutableStateOf(FrecuenciaPago.DIARIO)

    var isSaving by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    fun onSaveCliente() {
        if (!validate()) return

        viewModelScope.launch {
            isSaving = true
            error = null
            
            val cliente = Cliente(
                fullName = fullName,
                dni = dni,
                phone = phone,
                address = address,
                profilePhotoPath = profilePhotoPath,
                dniFrontPhotoPath = dniFrontPhotoPath,
                dniBackPhotoPath = dniBackPhotoPath
            )

            val prestamo = Prestamo(
                clienteId = 0, // Will be set in UseCase
                monto = BigDecimal(montoPrestamo),
                cuotas = numCuotas.toInt(),
                frecuencia = frecuenciaPago,
                interesPorcentaje = BigDecimal("20") // Default interest for now
            )

            val result = registerClientWithLoanUseCase(cliente, prestamo)
            
            if (result.isSuccess) {
                success = true
            } else {
                error = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
            isSaving = false
        }
    }

    private fun validate(): Boolean {
        if (fullName.isBlank()) {
            error = "El nombre es obligatorio"
            return false
        }
        if (dni.isBlank()) {
            error = "El DNI es obligatorio"
            return false
        }
        if (montoPrestamo.isBlank() || montoPrestamo.toDoubleOrNull() == null) {
            error = "Monto inválido"
            return false
        }
        if (numCuotas.isBlank() || numCuotas.toIntOrNull() == null) {
            error = "Número de cuotas inválido"
            return false
        }
        return true
    }
}