package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroClienteViewModel @Inject constructor(
    private val clienteRepository: ClienteRepository
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

        viewModelScope.launch {
            isLoading = true
            try {
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

                clienteRepository.saveCliente(nuevoCliente)
                isLoading = false
                success = true
            } catch (e: Exception) {
                isLoading = false
                error = e.message ?: "Error al registrar el cliente."
            }
        }
    }
}
