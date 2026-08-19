package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.clients

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente

data class ClientListUiState(
    val clients: List<Cliente> = emptyList(),
    val isLoading: Boolean = false
)
