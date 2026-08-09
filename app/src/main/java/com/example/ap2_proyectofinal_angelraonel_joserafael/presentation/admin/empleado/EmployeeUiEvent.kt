package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

enum class EmployeePhotoType { PROFILE, DNI_FRONT, DNI_BACK }

sealed class EmployeeUiEvent {
    data class SearchChanged(val value: String) : EmployeeUiEvent()
    data object OpenAddModal : EmployeeUiEvent()
    data class OpenEdit(val employeeId: String) : EmployeeUiEvent()
    data object CloseModal : EmployeeUiEvent()
    data class NameChanged(val value: String) : EmployeeUiEvent()
    data class UsernameChanged(val value: String) : EmployeeUiEvent()
    data class PinChanged(val value: String) : EmployeeUiEvent()
    data class PhoneChanged(val value: String) : EmployeeUiEvent()
    data class IdentificationChanged(val value: String) : EmployeeUiEvent()
    data class AddressChanged(val value: String) : EmployeeUiEvent()
    data class RouteSelected(val value: String) : EmployeeUiEvent()
    data class PhotoChanged(val type: EmployeePhotoType, val path: String) : EmployeeUiEvent()
    data class PermissionChanged(val permission: EmployeePermission, val enabled: Boolean) : EmployeeUiEvent()
    data object SaveEmployee : EmployeeUiEvent()
    data class ShowDetails(val employeeId: String) : EmployeeUiEvent()
    data object CloseDetails : EmployeeUiEvent()
    data class ToggleStatus(val employeeId: String) : EmployeeUiEvent()
    data object CancelDeactivation : EmployeeUiEvent()
    data object ConfirmDeactivation : EmployeeUiEvent()
    data class OpenAssignment(val employeeId: String) : EmployeeUiEvent()
    data object CloseAssignment : EmployeeUiEvent()
    data class AssignClient(val clientId: Long) : EmployeeUiEvent()
    data object ClearMessage : EmployeeUiEvent()
}

enum class EmployeePermission { CREATE_CLIENTS, COLLECT_PAYMENTS, VIEW_ROUTE, CLOSE_CASH, SHARE_DOCUMENTS }
