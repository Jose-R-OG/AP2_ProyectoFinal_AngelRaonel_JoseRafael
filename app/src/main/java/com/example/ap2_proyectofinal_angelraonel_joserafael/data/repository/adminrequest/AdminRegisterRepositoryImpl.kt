package com.example.ap2_proyectofinal_angelraonel_joserafael.data.repository.adminrequest

import android.content.Context
import android.net.Uri
import android.util.Log
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
    @ApplicationContext private val context: Context,
    private val dao: AdminRegisterRequestDao
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
            Log.d("AdminRegisterRepo", "Iniciando registro para: $email")
            val localPath = FileStorageUtil.saveFileToInternalStorage(context, voucherUri, "vouchers")
            
            if (localPath == null) {
                Log.e("AdminRegisterRepo", "Error: FileStorageUtil devolvió null para el voucher $voucherUri")
                return Result.failure(Exception("No se pudo guardar el comprobante localmente. Verifique que seleccionó un archivo válido."))
            }

            val activationCode = "EF" + (100000..999999).random()
            
            val entity = AdminRegisterRequestEntity(
                email = email,
                username = username,
                fullName = fullName,
                phone = phone,
                cedula = cedula,
                selectedBank = bank,
                transferNumber = transferNum,
                depositorName = depositor,
                voucherLocalPath = localPath,
                status = "PENDIENTE",
                activationCode = activationCode,
                createdAt = System.currentTimeMillis(),
                pin = pin
            )
            
            dao.insertRequest(entity)
            Log.d("AdminRegisterRepo", "Registro insertado en DB. Código: $activationCode")
            Result.success(activationCode)
        } catch (e: Exception) {
            Log.e("AdminRegisterRepo", "Excepción durante registro", e)
            Result.failure(e)
        }
    }

    override suspend fun getRequestByEmail(email: String): AdminRegisterRequest? {
        return dao.getRequestByEmail(email)?.toDomain()
    }

    override fun getAllRequests(): Flow<List<AdminRegisterRequest>> {
        return dao.getAllRequests().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

// Mapper extension
fun AdminRegisterRequestEntity.toDomain(): AdminRegisterRequest {
    return AdminRegisterRequest(
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
