package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.empleado

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<List<User>> {
        return repository.getAllActiveUsers().map { users ->
            users.filter { it.role == UserRole.EMPLEADO }
        }
    }
}
