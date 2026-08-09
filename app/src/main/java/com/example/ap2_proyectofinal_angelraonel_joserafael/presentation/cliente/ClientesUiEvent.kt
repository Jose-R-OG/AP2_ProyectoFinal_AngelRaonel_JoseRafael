package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente

sealed interface ClientesUiEvent {
    data class SearchChanged(val value: String) : ClientesUiEvent
    data class EditRequested(val cliente: Cliente) : ClientesUiEvent
    data class EditorNameChanged(val value: String) : ClientesUiEvent
    data class EditorDniChanged(val value: String) : ClientesUiEvent
    data class EditorPhoneChanged(val value: String) : ClientesUiEvent
    data class EditorAddressChanged(val value: String) : ClientesUiEvent
    data class EditorZoneChanged(val value: String) : ClientesUiEvent
    data object SaveEdit : ClientesUiEvent
    data object DismissEditor : ClientesUiEvent
    data class DeactivationRequested(val cliente: Cliente) : ClientesUiEvent
    data object ConfirmDeactivation : ClientesUiEvent
    data object DismissDeactivation : ClientesUiEvent
    data class AssignmentRequested(val cliente: Cliente) : ClientesUiEvent
    data class AssignToEmployee(val employeeId: Long) : ClientesUiEvent
    data object DismissAssignment : ClientesUiEvent
    data object MessageShown : ClientesUiEvent
}
