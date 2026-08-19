package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

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
import kotlinx.coroutines.flow.*
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
    val uiState: StateFlow<RegistroClienteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first()
            val user = userId?.let { authRepository.getUserById(it) }
            val canUseCustom = user?.role == com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR || user?.canCreateClients == true
            _uiState.update { it.copy(canUseCustomRate = canUseCustom) }
            
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
                    ) }
                }
            }
        }
    }

    fun onEvent(event: RegistroClienteUiEvent) {
        when (event) {
            is RegistroClienteUiEvent.ProfilePhotoChanged -> _uiState.update { it.copy(profilePhotoPath = event.path, profilePhotoError = null) }
            is RegistroClienteUiEvent.FullNameChanged -> _uiState.update { it.copy(fullName = event.value.take(80), fullNameError = null) }
            is RegistroClienteUiEvent.DniChanged -> _uiState.update { it.copy(dni = event.value.filter(Char::isDigit).take(11), dniError = null) }
            is RegistroClienteUiEvent.DniFrontPhotoChanged -> _uiState.update { it.copy(dniFrontPhotoPath = event.path, dniFrontPhotoError = null) }
            is RegistroClienteUiEvent.DniBackPhotoChanged -> _uiState.update { it.copy(dniBackPhotoPath = event.path, dniBackPhotoError = null) }
            is RegistroClienteUiEvent.PhoneChanged -> _uiState.update { it.copy(phone = event.value.filter(Char::isDigit).take(10), phoneError = null) }
            is RegistroClienteUiEvent.AddressChanged -> _uiState.update { it.copy(address = event.value.take(160), addressError = null) }
            is RegistroClienteUiEvent.ZoneChanged -> _uiState.update { it.copy(zone = event.value, zoneError = null) }
            is RegistroClienteUiEvent.MontoChanged -> _uiState.update { it.copy(montoPrestamo = event.value.filter { it.isDigit() || it == '.' || it == ',' }.take(10), montoPrestamoError = null) }
            is RegistroClienteUiEvent.CuotasChanged -> _uiState.update { it.copy(numCuotas = event.value.filter(Char::isDigit).take(3), numCuotasError = null) }
            is RegistroClienteUiEvent.FrecuenciaChanged -> _uiState.update { it.copy(frecuenciaPago = event.frecuencia, diaPagoPreferido = null, diaPagoDescripcion = null, diaPagoError = null) }
            is RegistroClienteUiEvent.DiaPagoChanged -> _uiState.update { it.copy(diaPagoPreferido = event.value, diaPagoDescripcion = event.description, diaPagoError = null) }
            is RegistroClienteUiEvent.TasaPersonalizadaChanged -> _uiState.update { it.copy(tasaPersonalizada = event.value.filter { it.isDigit() || it == '.' || it == ',' }.take(5), tasaPersonalizadaError = null) }
            is RegistroClienteUiEvent.SaveCliente -> guardarCliente()
            is RegistroClienteUiEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun guardarCliente() {
        val s = _uiState.value
        
        val nameError = if (s.fullName.isBlank()) "El nombre es obligatorio" else null
        val addressError = if (s.address.isBlank()) "La dirección es obligatoria" else null
        val dniError = if (s.dni.length != 11) "La cédula debe tener 11 dígitos" else if (!CedulaValidator.validate(s.dni)) "Cédula inválida" else null
        val phoneError = if (s.phone.length != 10) "El teléfono debe tener 10 dígitos" else null
        
        val montoStr = s.montoPrestamo.replace(",", ".")
        val monto = montoStr.toBigDecimalOrNull()
        val cuotas = s.numCuotas.toIntOrNull()
        
        val montoError = if (monto == null || monto <= BigDecimal.ZERO) "Monto inválido" else null
        val cuotasError = if (cuotas == null || cuotas <= 0) "Cuotas inválidas" else null
        val diaPagoError = if (s.frecuenciaPago != FrecuenciaPago.DIARIO && s.diaPagoPreferido == null) "Debe elegir el día de pago" else null
        
        val profilePhotoError = if (s.profilePhotoPath == null) "Foto obligatoria" else null
        val dniFrontError = if (s.dniFrontPhotoPath == null) "Foto frontal obligatoria" else null
        val dniBackError = if (s.dniBackPhotoPath == null) "Foto trasera obligatoria" else null

        if (nameError != null || addressError != null || dniError != null || phoneError != null || montoError != null || cuotasError != null || diaPagoError != null || profilePhotoError != null || dniFrontError != null || dniBackError != null) {
            _uiState.update { it.copy(
                fullNameError = nameError,
                addressError = addressError,
                dniError = dniError,
                phoneError = phoneError,
                montoPrestamoError = montoError,
                numCuotasError = cuotasError,
                diaPagoError = diaPagoError,
                profilePhotoError = profilePhotoError,
                dniFrontPhotoError = dniFrontError,
                dniBackPhotoError = dniBackError
            ) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val empleadoId = sessionManager.currentUserId.first()
                if (empleadoId == null) {
                    _uiState.update { it.copy(error = "No se encontró la sesión. Vuelva a iniciar sesión.", isLoading = false) }
                    return@launch
                }

                val currentUser = authRepository.getUserById(empleadoId)
                if (currentUser?.role != com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR && currentUser?.canCreateClients != true) {
                    _uiState.update { it.copy(error = "No tiene permiso para realizar esta acción.", isLoading = false) }
                    return@launch
                }

                val customRate = s.tasaPersonalizada.replace(",", ".").toBigDecimalOrNull()
                val cuotasInt = s.numCuotas.toIntOrNull() ?: 0

                if (s.isExistingClient) {
                    val todosLosTarifarios = tarifarioRepository.getActiveTarifarios().first()
                    val configuredTariff = if (s.frecuenciaPago == FrecuenciaPago.SEMANAL) {
                        todosLosTarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == cuotasInt }
                            ?: todosLosTarifarios.firstOrNull { it.frecuencia == FrecuenciaPago.SEMANAL }
                    } else {
                        todosLosTarifarios.firstOrNull { it.frecuencia == s.frecuenciaPago }
                    }
                    val appliedRate = if (s.canUseCustomRate && customRate != null) customRate else configuredTariff?.porcentajeInteres ?: BigDecimal("10")
                    
                    val calc = com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos.CalculateLoanPercentageUseCase().execute(monto!!, appliedRate, cuotasInt)
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
                        frecuenciaPago = s.frecuenciaPago,
                        diaPagoPreferido = s.diaPagoPreferido,
                        diaPagoDescripcion = s.diaPagoDescripcion,
                        estado = LoanStatus.PENDIENTE_REVISION
                    )
                    guardarPrestamoUseCase.execute(prestamo)
                    _uiState.update { it.copy(success = true) }
                } else {
                    val result = registerUseCase(
                        s.fullName, s.dni, s.phone, s.address, s.zone, s.profilePhotoPath, s.dniFrontPhotoPath, s.dniBackPhotoPath,
                        monto!!, cuotasInt, s.frecuenciaPago, s.diaPagoPreferido, s.diaPagoDescripcion, empleadoId, customRate
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
