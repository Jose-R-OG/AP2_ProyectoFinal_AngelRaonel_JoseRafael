package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest

import android.net.Uri
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.adminregisterrequest.AdminRegisterRequest
import kotlinx.coroutines.flow.Flow

interface AdminRegisterRepository {
    suspend fun submitRegistration(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        cedula: String,
        bank: String,
        transferNum: String,
        depositor: String,
        voucherUri: Uri,
        pin: String
    ): Result<String>

    suspend fun getRequestByEmail(email: String): AdminRegisterRequest?

    fun getAllRequests(): Flow<List<AdminRegisterRequest>>
}