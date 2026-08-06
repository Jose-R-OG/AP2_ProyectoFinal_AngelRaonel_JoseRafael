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
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.RegisterClientWithLoanUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class RegistroClienteViewModel @Inject constructor(
    private val registerClientWithLoanUseCase: RegisterClientWithLoanUseCase,
    private val tarifarioRepository: TarifarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var profilePhotoPath by mutableStateOf<String?>(null)
    var fullName by mutableStateOf("")
    var dni by mutableStateOf("")
    var dniFrontPhotoPath by mutableStateOf<String?>(null)
    var dniBackPhotoPath by mutableStateOf<String?>(null)
    var phone by mutableStateOf("")
    var address by mutableStateOf("")

    var montoPrestamo by mutableStateOf("")
    var numCuotas by mutableStateOf("")
    var frecuenciaPago by mutableStateOf(FrecuenciaPago.DIARIO)

    var isLoading by mutableStateOf(false)
    var success by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun onEvent(event: RegistroClienteUiEvent) {
        when (event) {
            is RegistroClienteUiEvent.ProfilePhotoChanged -> profilePhotoPath = event.path
            is RegistroClienteUiEvent.FullNameChanged -> fullName = event.value
            is RegistroClienteUiEvent.DniChanged -> dni = event.value
            is RegistroClienteUiEvent.DniFrontPhotoChanged -> dniFrontPhotoPath = event.path
            is RegistroClienteUiEvent.DniBackPhotoChanged -> dniBackPhotoPath = event.path
            is RegistroClienteUiEvent.PhoneChanged -> phone = event.value
            is RegistroClienteUiEvent.AddressChanged -> address = event.value
            is RegistroClienteUiEvent.MontoChanged -> montoPrestamo = event.value
            is RegistroClienteUiEvent.CuotasChanged -> numCuotas = event.value
            is RegistroClienteUiEvent.FrecuenciaChanged -> frecuenciaPago = event.frecuencia
            is RegistroClienteUiEvent.SaveCliente -> guardarCliente()
            is RegistroClienteUiEvent.ClearError -> error = null
        }
    }

    private fun guardarCliente() {
        if (fullName.isBlank() || dni.isBlank() || phone.isBlank()) {
            error = "Por favor complete los campos obligatorios del cliente (Nombre, Cédula y Teléfono)."
            return
        }
        val monto = montoPrestamo.toBigDecimalOrNull()
        val cuotas = numCuotas.toIntOrNull()
        if (monto == null || monto <= BigDecimal.ZERO || cuotas == null || cuotas <= 0) {
            error = "Ingrese un monto y una cantidad de cuotas válidos."
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val empleadoId = sessionManager.currentUserId.first()
                if (empleadoId == null) {
                    error = "No se encontró la sesión del empleado. Vuelva a iniciar sesión."
                    isLoading = false
                    return@launch
                }

                val montoValido = montoPrestamo.replace(",", ".")
                val monto = montoValido.toBigDecimalOrNull()
                val cuotas = numCuotas.toIntOrNull()

                if (monto == null || monto <= BigDecimal.ZERO || cuotas == null || cuotas <= 0) {
                    error = "Ingrese un monto y una cantidad de cuotas válidos."
                    isLoading = false
                    return@launch
                }

                val todosLosTarifarios = tarifarioRepository.getActiveTarifarios().first()
                
                val tarifario = if (frecuenciaPago == FrecuenciaPago.SEMANAL) {
                    // Para semanal, buscar coincidencia exacta de duración (4, 6, 12)
                    todosLosTarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == cuotas }
                        ?: todosLosTarifarios.firstOrNull { it.frecuencia == FrecuenciaPago.SEMANAL }
                } else {
                    todosLosTarifarios.firstOrNull { it.frecuencia == frecuenciaPago }
                }

                if (tarifario == null) {
                    error = "No existe una tarifa activa para $frecuenciaPago ($cuotas cuotas). Pídale al administrador que la configure."
                    isLoading = false
                    return@launch
                }

                val interesTotal = monto.multiply(tarifario.porcentajeInteres)
                    .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
                val totalAPagar = monto.add(interesTotal)
                val montoCuota = totalAPagar.divide(BigDecimal(cuotas), 2, RoundingMode.HALF_UP)

                val nuevoCliente = Cliente(
                    id = 0L,
                    fullName = fullName.trim(),
                    dni = dni.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    profilePhotoPath = profilePhotoPath,
                    dniFrontPhotoPath = dniFrontPhotoPath,
                    dniBackPhotoPath = dniBackPhotoPath,
                    isActive = true
                )

                val nuevoPrestamo = Prestamo(
                    clienteId = 0L,
                    empleadoId = empleadoId,
                    montoSolicitado = monto,
                    porcentajeInteres = tarifario.porcentajeInteres,
                    interesTotal = interesTotal,
                    totalAPagar = totalAPagar,
                    montoCuota = montoCuota,
                    cantidadCuotas = cuotas,
                    frecuenciaPago = frecuenciaPago,
                    estado = LoanStatus.PENDIENTE_REVISION
                )

                val result = registerClientWithLoanUseCase(nuevoCliente, nuevoPrestamo)
                isLoading = false
                result.onSuccess { success = true }
                    .onFailure { error = it.message ?: "Error al registrar el cliente y el préstamo." }

            } catch (e: Exception) {
                isLoading = false
                error = e.message ?: "Error al registrar el cliente."
            }
        }
    }
}
