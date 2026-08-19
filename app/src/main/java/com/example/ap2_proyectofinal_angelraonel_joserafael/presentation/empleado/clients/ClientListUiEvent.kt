package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.clients

sealed class ClientListUiEvent {
    data object Refresh : ClientListUiEvent()
}
