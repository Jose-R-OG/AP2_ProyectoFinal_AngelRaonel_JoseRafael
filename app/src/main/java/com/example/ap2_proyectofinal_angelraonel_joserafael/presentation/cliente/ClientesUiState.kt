package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente

data class ClienteEditorState(
    val original: Cliente,
    val fullName: String,
    val fullNameError: String? = null,
    val dni: String,
    val dniError: String? = null,
    val phone: String,
    val phoneError: String? = null,
    val address: String,
    val addressError: String? = null,
    val zone: String,
    val zoneError: String? = null
) {
    fun toCliente(): Cliente = original.copy(
        fullName = fullName,
        dni = dni,
        phone = phone,
        address = address,
        zone = zone
    )

    companion object {
        fun from(cliente: Cliente) = ClienteEditorState(
            original = cliente,
            fullName = cliente.fullName,
            dni = cliente.dni.filter(Char::isDigit).take(11),
            phone = cliente.phone.filter(Char::isDigit).take(10),
            address = cliente.address,
            zone = cliente.zone
        )
    }
}

data class ClientEmployeeOption(val id: Long, val name: String, val route: String)

data class ClientesUiState(
    val clientes: List<Cliente> = emptyList(),
    val assignedClientIds: Set<Long> = emptySet(),
    val employeeOptions: List<ClientEmployeeOption> = emptyList(),
    val pendingAssignment: Cliente? = null,
    val searchQuery: String = "",
    val editor: ClienteEditorState? = null,
    val pendingDeactivation: Cliente? = null,
    val canCreateLoans: Boolean = false,
    val isLoading: Boolean = true,
    val isMutating: Boolean = false,
    val message: String? = null
) {
    fun filteredClientes(isAdmin: Boolean): List<Cliente> {
            val query = searchQuery.trim()
            val available = if (isAdmin) clientes else clientes.filter {
                it.isActive && it.id in assignedClientIds
            }
            if (query.isEmpty()) return available

            return available.filter { cliente ->
                cliente.fullName.contains(query, ignoreCase = true) ||
                    cliente.dni.contains(query, ignoreCase = true) ||
                    cliente.phone.contains(query, ignoreCase = true)
            }
    }
}
