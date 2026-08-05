package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.RegisterClientWithLoanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class RegistroClienteViewModel @Inject constructor(
    private val registerClientWithLoanUseCase: RegisterClientWithLoanUseCase
) : ViewModel() {

    // --- Datos del Cliente ---
    var fullName by mutableStateOf("")
    var dni by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var profilePhotoPath by mutableStateOf<String?>(null)
    var dniFrontPhotoPath by mutableStateOf<String?>(null)
    var dniBackPhotoPath by mutableStateOf<String?>(null)

    // --- Datos del Préstamo ---
    var montoPrestamo by mutableStateOf("")
    var porcentajeInteres by mutableStateOf("20") // Porcentaje por defecto (20%)
    var numCuotas by mutableStateOf("")
    var frecuenciaPago by mutableStateOf(FrecuenciaPago.DIARIO)

    // ID del empleado autenticado que realiza el registro
    var empleadoId by mutableStateOf(1L)

    // --- Estado de la UI ---
    var isSaving by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    fun onEvent(event: RegistroClienteUiEvent) {
        when (event) {
            is RegistroClienteUiEvent.FullNameChanged -> fullName = event.value
            is RegistroClienteUiEvent.DniChanged -> dni = event.value
            is RegistroClienteUiEvent.PhoneChanged -> phone = event.value
            is RegistroClienteUiEvent.AddressChanged -> address = event.value
            is RegistroClienteUiEvent.ProfilePhotoChanged -> profilePhotoPath = event.path
            is RegistroClienteUiEvent.DniFrontPhotoChanged -> dniFrontPhotoPath = event.path
            is RegistroClienteUiEvent.DniBackPhotoChanged -> dniBackPhotoPath = event.path
            is RegistroClienteUiEvent.MontoChanged -> montoPrestamo = event.value
            is RegistroClienteUiEvent.PorcentajeChanged -> porcentajeInteres = event.value
            is RegistroClienteUiEvent.CuotasChanged -> numCuotas = event.value
            is RegistroClienteUiEvent.FrecuenciaChanged -> frecuenciaPago = event.frecuencia
            is RegistroClienteUiEvent.EmpleadoIdChanged -> empleadoId = event.id
            is RegistroClienteUiEvent.SaveCliente -> onSaveCliente()
            is RegistroClienteUiEvent.ClearError -> error = null
        }
    }

    fun onSaveCliente() {
        if (!validate()) return

        viewModelScope.launch {
            isSaving = true
            error = null

            try {
                val montoDecimal = BigDecimal(montoPrestamo)
                val porcentajeDecimal = BigDecimal(porcentajeInteres)
                val cantidadCuotasInt = numCuotas.toInt()

                // Cálculo de la fotografía financiera
                val interesTotal = montoDecimal.multiply(porcentajeDecimal)
                    .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

                val totalAPagar = montoDecimal.add(interesTotal)

                val montoCuota = totalAPagar.divide(
                    BigDecimal(cantidadCuotasInt), 2, RoundingMode.HALF_UP
                )

                // 1. Objeto Cliente
                val cliente = Cliente(
                    fullName = fullName,
                    dni = dni,
                    phone = phone,
                    address = address,
                    profilePhotoPath = profilePhotoPath,
                    dniFrontPhotoPath = dniFrontPhotoPath,
                    dniBackPhotoPath = dniBackPhotoPath
                )

                // 2. Objeto Prestamo según la nueva estructura del modelo
                val prestamo = Prestamo(
                    id = 0L,
                    clienteId = 0L, // Se asigna automáticamente en el UseCase al insertar el cliente
                    empleadoId = empleadoId,
                    aprobadoPorAdminId = null,
                    montoSolicitado = montoDecimal,
                    porcentajeInteres = porcentajeDecimal,
                    interesTotal = interesTotal,
                    totalAPagar = totalAPagar,
                    totalPagado = BigDecimal.ZERO,
                    montoCuota = montoCuota,
                    cantidadCuotas = cantidadCuotasInt,
                    frecuenciaPago = frecuenciaPago,
                    fechaCreacion = System.currentTimeMillis(),
                    fechaInicio = null,
                    fechaFin = null,
                    estado = LoanStatus.PENDIENTE_REVISION,
                    motivoRechazo = null,
                    rutaFotoContratoFirmado = null,
                    contratoFisicoEntregado = false
                )

                val result = registerClientWithLoanUseCase(cliente, prestamo)

                if (result.isSuccess) {
                    success = true
                } else {
                    error = result.exceptionOrNull()?.message ?: "Error al registrar el cliente y préstamo"
                }
            } catch (e: Exception) {
                error = "Error al procesar los montos: ${e.message}"
            } finally {
                isSaving = false
            }
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

        val monto = montoPrestamo.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            error = "Ingrese un monto de préstamo válido mayor a 0"
            return false
        }

        val porcentaje = porcentajeInteres.toDoubleOrNull()
        if (porcentaje == null || porcentaje < 0) {
            error = "Ingrese un porcentaje de interés válido"
            return false
        }

        val cuotas = numCuotas.toIntOrNull()
        if (cuotas == null || cuotas <= 0) {
            error = "Número de cuotas inválido"
            return false
        }

        return true
    }
}