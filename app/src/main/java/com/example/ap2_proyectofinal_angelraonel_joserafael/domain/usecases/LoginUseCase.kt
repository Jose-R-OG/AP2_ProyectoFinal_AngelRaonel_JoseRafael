package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecase

import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, pin: String): Result<User> {
        if (username.isBlank() || pin.isBlank()) {
            return Result.failure(Exception("El usuario y el PIN no pueden estar vacíos."))
        }

        val user = repository.login(username, pin)

        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales incorrectas o usuario inactivo."))
        }
    }
}