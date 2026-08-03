package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import javax.inject.Inject

class ToggleEmployeeStatusUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: User) {
        val updatedUser = user.copy(isActive = !user.isActive)
        repository.registerUser(updatedUser)
    }
}
