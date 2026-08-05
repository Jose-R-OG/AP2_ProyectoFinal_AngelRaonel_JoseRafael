package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

sealed class EmployeeUiEvent {
    data object OpenAddModal : EmployeeUiEvent()
    data object CloseModal : EmployeeUiEvent()
    data class NameChanged(val name: String) : EmployeeUiEvent()
    data class PhoneChanged(val phone: String) : EmployeeUiEvent()
    data class RouteSelected(val route: String) : EmployeeUiEvent()
    data object SaveEmployee : EmployeeUiEvent()
    data class ToggleStatus(val employeeId: String) : EmployeeUiEvent()
}
