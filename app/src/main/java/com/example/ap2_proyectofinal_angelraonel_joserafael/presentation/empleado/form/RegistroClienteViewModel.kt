package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.RegisterClientWithLoanUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos.GuardarPrestamoUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.CedulaValidator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class RegistroClienteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val registerUseCase: RegisterClientWithLoanUseCase,
    private val guardarPrestamoUseCase: GuardarPrestamoUseCase,
    private val tarifarioRepository: TarifarioRepository,
    private val clienteRepository: ClienteRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val existingClientId: Long = savedStateHandle.get<Long>("clientId") ?: -1L

    private val _uiState = MutableStateFlow(RegistroClienteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first()
            val user = userId?.let { authRepository.getUserById(it) }
            _uiState.update { it.copy(
                canUseCustomRate = user?.role == com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR || user?.canCreateClients == true
            )}
            
            if (existingClientId > 0) {
                clienteRepository.getClienteById(existingClientId)?.let { client ->
                    _uiState.update { it.copy(
                        isExistingClient = true,
                        profilePhotoPath = client.profilePhotoPath,
                        fullName = client.fullName,
                        dni = client.dni,
                        dniFrontPhotoPath = client.dniFrontPhotoPath,
                        dniBackPhotoPath = client.dniBackPhotoPath,
                        phone = client.phone,
                        address = client.address,
                        zone = client.zone
                    )}
                }
            }
        }
    }

    fun onEvent(event: RegistroClienteUiEvent) {
        when (event) {
            is RegistroClienteUiEvent.ProfilePhotoChanged -> _uiState.update { it.copy(profilePhotoPath = event.path) }
            is RegistroClienteUiEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.value.take(80)) }
            is RegistroClienteUiEvent.DniChanged -> _uiState.update { it.copy(dni = event.value.filter(Char::isDigit).take(11)) }
            is RegistroClienteUiEvent.DniFrontPhotoChanged -> _uiState.update { it.copy(dniFrontPhotoPath = event.path) }
            is RegistroClienteUiEvent.DniBackPhotoChanged -> _uiState.update { it.copy(dniBackPhotoPath = event.path) }
            is RegistroClienteUiEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.value.filter(Char::isDigit).take(10)) }
            is RegistroClienteUiEvent.AddressChanged -> _uiState.update { it.copy(address = event.value.take(160)) }
            is RegistroClienteUiEvent.ZoneChanged -> _uiState.update { it.copy(zone = event.value) }
            is RegistroClienteUiEvent.MontoChanged -> _uiState.update { it.copy(montoPrestamo = event.value.filter { char -> char.isDigit() || char == '.' || char == ',' }.take(10)) }
            is RegistroClienteUiEvent.CuotasChanged -> _uiState.update { it.copy(numCuotas = event.value.filter(Char::isDigit).take(3)) }
            is RegistroClienteUiEvent.FrecuenciaChanged -> _uiState.update { it.copy(
                frecuenciaPago = event.frecuencia,
                diaPagoPreferido = null,
                diaPagoDescripcion = null
            )}
            is RegistroClienteUiEvent.DiaPagoChanged -> _uiState.update { it.copy(
                diaPagoPreferido = event.value,
                diaPagoDescripcion = event.description
            )}
            is RegistroClienteUiEvent.TasaPersonalizadaChanged -> _uiState.update { it.copy(tasaPersonalizada = event.value.filter { char -> char.isDigit() || char == '.' || char == ',' }.take(5)) }
            is RegistroClienteUiEvent.SaveCliente -> guardarCliente()
            is RegistroClienteUiEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun guardarCliente() {
        val currentState = _uiState.value
        if (currentState.fullName.isBlank() || currentState.address.isBlank()) {
            _uiState.update { it.copy(error = "Nombre y dirección son obligatorios (máximos: 80 y 160 caracteres).") }
            return
        }
        if (currentState.dni.length != 11 || currentState.phone.length != 10) {
            _uiState.update { it.copy(error = "La cédula debe tener 11 dígitos (${currentState.dni.length}/11) y el teléfono 10 (${currentState.phone.length}/10).") }
            return
        }

        if (!CedulaValidator.validate(currentState.dni)) {
            _uiState.update { it.copy(error = "Número de cédula inválido. Por favor verifique.") }
            return
        }
        if (currentState.profilePhotoPath == null || currentState.dniFrontPhotoPath == null || currentState.dniBackPhotoPath == null) {
            _uiState.update { it.copy(error = "Debes tomar la foto del cliente y ambos lados de su cédula.") }
            return
        }
        val montoStr = currentState.montoPrestamo.replace(",", ".")
        val monto = montoStr.toBigDecimalOrNull()
        val cuotas = currentState.numCuotas.toIntOrNull()
        if (monto == null || monto <= BigDecimal.ZERO || cuotas == null || cuotas <= 0) {
            _uiState.update { it.copy(error = "Ingrese un monto y una cantidad de cuotas válidos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val empleadoId = sessionManager.currentUserId.first()
                if (empleadoId == null) {
                    _uiState.update { it.copy(error = "No se encontró la sesión del empleado. Vuelva a iniciar sesión.", isLoading = false) }
                    return@launch
                }

                val currentUser = authRepository.getUserById(empleadoId)
                if (currentUser?.role != com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR && currentUser?.canCreateClients != true) {
                    _uiState.update { it.copy(error = "El administrador no te ha dado permiso para registrar clientes o préstamos.", isLoading = false) }
                    return@launch
                }

                if (currentState.frecuenciaPago != FrecuenciaPago.DIARIO && currentState.diaPagoPreferido == null) {
                    _uiState.update { it.copy(error = "El cliente debe elegir su día preferido de pago.", isLoading = false) }
                    return@launch
                }

                val customRate = currentState.tasaPersonalizada.replace(",", ".").toBigDecimalOrNull()
                val cuotasInt = currentState.numCuotas.toIntOrNull() ?: 0

                if (currentState.isExistingClient) {
                    val todosLosTarifarios = tarifarioRepository.getActiveTarifarios().first()
                    val configuredTariff = if (currentState.frecuenciaPago == FrecuenciaPago.SEMANAL) {
                        todosLosTarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == cuotasInt }
                            ?: todosLosTarifarios.firstOrNull { it.frecuencia == FrecuenciaPago.SEMANAL }
                    } else {
                        todosLosTarifarios.firstOrNull { it.frecuencia == currentState.frecuenciaPago }
                    }
                    val appliedRate = if (currentState.canUseCustomRate && customRate != null) customRate else configuredTariff?.porcentajeInteres ?: BigDecimal("10")
                    
                    val calc = com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos.CalculateLoanPercentageUseCase().execute(monto, appliedRate, cuotasInt)
                    val prestamo = Prestamo(
                        clienteId = existingClientId,
                        empleadoId = empleadoId,
                        montoSolicitado = monto,
                        porcentajeInteres = appliedRate,
                        interesTotal = calc.interesTotal,
                        totalAPagar = calc.totalAPagar,
                        totalPagado = BigDecimal.ZERO,
                        montoCuota = calc.montoCuota,
                        cantidadCuotas = cuotasInt,
                        frecuenciaPago = currentState.frecuenciaPago,
                        diaPagoPreferido = currentState.diaPagoPreferido,
                        diaPagoDescripcion = currentState.diaPagoDescripcion,
                        estado = LoanStatus.PENDIENTE_REVISION
                    )
                    guardarPrestamoUseCase.execute(prestamo)
                    _uiState.update { it.copy(success = true) }
                } else {
                    val result = registerUseCase(
                        currentState.fullName, currentState.dni, currentState.phone, currentState.address, currentState.zone, currentState.profilePhotoPath, currentState.dniFrontPhotoPath, currentState.dniBackPhotoPath,
                        monto, cuotasInt, currentState.frecuenciaPago, currentState.diaPagoPreferido, currentState.diaPagoDescripcion, empleadoId, customRate
                    )
                    if (result.isSuccess) _uiState.update { it.copy(success = true) } else _uiState.update { it.copy(error = result.exceptionOrNull()?.message) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Error al registrar el cliente.") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
