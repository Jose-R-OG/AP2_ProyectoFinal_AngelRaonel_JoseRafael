package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.adminrequest

import android.content.Context
import android.net.Uri
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestDao
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.adminrequest.AdminRegisterRequestEntity
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.adminregisterrequest.AdminRegisterRequest
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.adminrequest.AdminRegisterRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdminRegisterRepositoryImpl @Inject constructor(
    private val dao: AdminRegisterRequestDao,
    @ApplicationContext private val context: Context
) : AdminRegisterRepository {

    override suspend fun submitRegistration(
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
    ): Result<String> {
        return try {
            val internalVoucherPath = FileStorageUtil.saveFileToInternalStorage(
                context, voucherUri, "admin_vouchers"
            ) ?: throw Exception("No se pudo guardar el comprobante")

            val activationCode = (100000..999999).random().toString()

            val entity = AdminRegisterRequestEntity(
                email = email,
                username = username,
                fullName = fullName,
                phone = phone,
                cedula = cedula,
                selectedBank = bank,
                transferNumber = transferNum,
                depositorName = depositor,
                voucherLocalPath = internalVoucherPath,
                pin = pin,
                status = "PENDIENTE",
                activationCode = activationCode,
                createdAt = System.currentTimeMillis()
            )

            dao.insertRequest(entity)
            Result.success(activationCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRequestByEmail(email: String): AdminRegisterRequest? {
        return dao.getRequestByEmail(email)?.toDomain()
    }

    override fun getAllRequests(): Flow<List<AdminRegisterRequest>> {
        return dao.getAllRequests().map { list -> list.map { it.toDomain() } }
    }

    private fun AdminRegisterRequestEntity.toDomain() = AdminRegisterRequest(
        uid = email,
        fullName = fullName,
        username = username,
        email = email,
        phone = phone,
        cedula = cedula,
        selectedBank = selectedBank,
        transferNumber = transferNumber,
        depositorName = depositorName,
        voucherUrl = voucherLocalPath,
        pin = pin,
        status = status,
        activationCode = activationCode,
        createdAt = createdAt
    )
}
