package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import android.content.Context
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

import androidx.core.net.toUri

class RegisterClientWithLoanUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(cliente: Cliente, prestamo: Prestamo): Result<Long> {
        return try {
            // Guardar fotos localmente si son URIs de contenido
            val localProfilePath = cliente.profilePhotoPath?.let { path ->
                if (path.startsWith("content://")) FileStorageUtil.saveFileToInternalStorage(context, path.toUri(), "clients/profiles") else path
            }
            val localDniFrontPath = cliente.dniFrontPhotoPath?.let { path ->
                if (path.startsWith("content://")) FileStorageUtil.saveFileToInternalStorage(context, path.toUri(), "clients/dni") else path
            }
            val localDniBackPath = cliente.dniBackPhotoPath?.let { path ->
                if (path.startsWith("content://")) FileStorageUtil.saveFileToInternalStorage(context, path.toUri(), "clients/dni") else path
            }

            val updatedCliente = cliente.copy(
                profilePhotoPath = localProfilePath,
                dniFrontPhotoPath = localDniFrontPath,
                dniBackPhotoPath = localDniBackPath
            )

            val clienteId = clienteRepository.saveCliente(updatedCliente)
            val prestamoConId = prestamo.copy(clienteId = clienteId)
            prestamoRepository.guardarPrestamo(prestamoConId)
            Result.success(clienteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}