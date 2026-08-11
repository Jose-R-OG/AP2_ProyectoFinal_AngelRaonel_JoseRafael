package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Tarifario
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.TarifarioRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.RegisterClientWithLoanUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.CedulaValidator
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class RegistroClienteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val registerClientWithLoanUseCase: RegisterClientWithLoanUseCase,
    private val tarifarioRepository: TarifarioRepository,
    private val clienteRepository: ClienteRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val existingClientId: Long = savedStateHandle.get<Long>("clientId") ?: -1L

    var profilePhotoPath by mutableStateOf<String?>(null)
    var fullName by mutableStateOf("")
    var dni by mutableStateOf("")
    var dniFrontPhotoPath by mutableStateOf<String?>(null)
    var dniBackPhotoPath by mutableStateOf<String?>(null)
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var zone by mutableStateOf("Zona Norte")

    var montoPrestamo by mutableStateOf("")
    var numCuotas by mutableStateOf("")
    var frecuenciaPago by mutableStateOf(FrecuenciaPago.DIARIO)
    var diaPagoPreferido by mutableStateOf<Int?>(null)
    var diaPagoDescripcion by mutableStateOf<String?>(null)
    var tasaPersonalizada by mutableStateOf("")
    var canUseCustomRate by mutableStateOf(false)
    var isExistingClient by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var success by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            val userId = sessionManager.currentUserId.first()
            val user = userId?.let { authRepository.getUserById(it) }
            canUseCustomRate = user?.role == com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR
            if (existingClientId > 0) {
                clienteRepository.getClienteById(existingClientId)?.let { client ->
                    isExistingClient = true
                    profilePhotoPath = client.profilePhotoPath
                    fullName = client.fullName
                    dni = client.dni
                    dniFrontPhotoPath = client.dniFrontPhotoPath
                    dniBackPhotoPath = client.dniBackPhotoPath
                    phone = client.phone
                    address = client.address
                    zone = client.zone
                }
            }
        }
    }

    fun onEvent(event: RegistroClienteUiEvent) {
        when (event) {
            is RegistroClienteUiEvent.ProfilePhotoChanged -> profilePhotoPath = event.path
            is RegistroClienteUiEvent.FullNameChanged -> fullName = event.value.take(80)
            is RegistroClienteUiEvent.DniChanged -> dni = event.value.filter(Char::isDigit).take(11)
            is RegistroClienteUiEvent.DniFrontPhotoChanged -> dniFrontPhotoPath = event.path
            is RegistroClienteUiEvent.DniBackPhotoChanged -> dniBackPhotoPath = event.path
            is RegistroClienteUiEvent.PhoneChanged -> phone = event.value.filter(Char::isDigit).take(10)
            is RegistroClienteUiEvent.AddressChanged -> address = event.value.take(160)
            is RegistroClienteUiEvent.ZoneChanged -> zone = event.value
            is RegistroClienteUiEvent.MontoChanged -> montoPrestamo = event.value.filter { it.isDigit() || it == '.' || it == ',' }.take(10)
            is RegistroClienteUiEvent.CuotasChanged -> numCuotas = event.value.filter(Char::isDigit).take(3)
            is RegistroClienteUiEvent.FrecuenciaChanged -> {
                frecuenciaPago = event.frecuencia
                diaPagoPreferido = null
                diaPagoDescripcion = null
            }
            is RegistroClienteUiEvent.DiaPagoChanged -> {
                diaPagoPreferido = event.value
                diaPagoDescripcion = event.description
            }
            is RegistroClienteUiEvent.TasaPersonalizadaChanged -> tasaPersonalizada = event.value.filter { it.isDigit() || it == '.' || it == ',' }.take(5)
            is RegistroClienteUiEvent.SaveCliente -> guardarCliente()
            is RegistroClienteUiEvent.ClearError -> error = null
        }
    }

    private fun guardarCliente() {
        if (fullName.isBlank() || address.isBlank()) {
            error = "Nombre y dirección son obligatorios (máximos: 80 y 160 caracteres)."
            return
        }
        if (dni.length != 11 || phone.length != 10) {
            error = "La cédula debe tener 11 dígitos (${dni.length}/11) y el teléfono 10 (${phone.length}/10)."
            return
        }

        // Validación estricta de Cédula Dominicana
        if (!CedulaValidator.validate(dni)) {
            error = "Número de cédula inválido. Por favor verifique."
            return
        }
        if (profilePhotoPath == null || dniFrontPhotoPath == null || dniBackPhotoPath == null) {
            error = "Debes tomar la foto del cliente y ambos lados de su cédula."
            return
        }
        val monto = montoPrestamo.replace(",", ".").toBigDecimalOrNull()
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

                val currentUser = authRepository.getUserById(empleadoId)
                if (currentUser?.role != com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole.ADMINISTRADOR && currentUser?.canCreateClients != true) {
                    error = "El administrador no te ha dado permiso para registrar clientes o préstamos."
                    isLoading = false
                    return@launch
                }

                if (frecuenciaPago != FrecuenciaPago.DIARIO && diaPagoPreferido == null) {
                    error = "El cliente debe elegir su día preferido de pago."
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
                
                val configuredTariff = if (frecuenciaPago == FrecuenciaPago.SEMANAL) {
                    // Para semanal, buscar coincidencia exacta de duración (4, 6, 12)
                    todosLosTarifarios.find { it.frecuencia == FrecuenciaPago.SEMANAL && it.duracion == cuotas }
                        ?: todosLosTarifarios.firstOrNull { it.frecuencia == FrecuenciaPago.SEMANAL }
                } else {
                    todosLosTarifarios.firstOrNull { it.frecuencia == frecuenciaPago }
                }

                val tarifario = configuredTariff ?: defaultTariff(frecuenciaPago, cuotas)
                    .also { tarifarioRepository.saveTarifario(it) }

                val customRate = tasaPersonalizada.replace(",", ".").toBigDecimalOrNull()
                if (customRate != null && (customRate < BigDecimal.ZERO || customRate > BigDecimal("100"))) {
                    error = "La tasa personalizada debe estar entre 0 y 100%."
                    isLoading = false
                    return@launch
                }
                val appliedRate = if (canUseCustomRate && customRate != null) customRate else tarifario.porcentajeInteres

                val interesTotal = monto.multiply(appliedRate)
                    .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
                val totalAPagar = monto.add(interesTotal)
                val montoCuota = totalAPagar.divide(BigDecimal(cuotas), 2, RoundingMode.HALF_UP)

                val nuevoCliente = Cliente(
                    id = existingClientId.takeIf { it > 0 } ?: 0L,
                    fullName = fullName.trim(),
                    dni = dni.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    zone = zone,
                    profilePhotoPath = profilePhotoPath,
                    dniFrontPhotoPath = dniFrontPhotoPath,
                    dniBackPhotoPath = dniBackPhotoPath,
                    isActive = true
                )

                val nuevoPrestamo = Prestamo(
                    clienteId = 0L,
                    empleadoId = empleadoId,
                    montoSolicitado = monto,
                    porcentajeInteres = appliedRate,
                    interesTotal = interesTotal,
                    totalAPagar = totalAPagar,
                    montoCuota = montoCuota,
                    cantidadCuotas = cuotas,
                    frecuenciaPago = frecuenciaPago,
                    diaPagoPreferido = diaPagoPreferido,
                    diaPagoDescripcion = diaPagoDescripcion,
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

    private fun defaultTariff(frequency: FrecuenciaPago, installments: Int): Tarifario {
        val percentage = when (frequency) {
            FrecuenciaPago.DIARIO -> BigDecimal("5")
            FrecuenciaPago.QUINCENAL -> BigDecimal("10")
            FrecuenciaPago.MENSUAL -> BigDecimal("15")
            FrecuenciaPago.SEMANAL -> when (installments) {
                in 1..4 -> BigDecimal("10")
                in 5..6 -> BigDecimal("15")
                else -> BigDecimal("25")
            }
        }
        return Tarifario(
            frecuencia = frequency,
            duracion = installments,
            porcentajeInteres = percentage
        )
    }
}
