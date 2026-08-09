package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import android.content.Context
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cuota
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.AuthRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.NotificationRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.AppNotification
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import androidx.core.net.toUri
import kotlinx.coroutines.flow.first

class RegisterClientWithLoanUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository,
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(cliente: Cliente, prestamo: Prestamo): Result<Long> {
        return try {
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
            val prestamoId = prestamoRepository.guardarPrestamo(prestamoConId)
            prestamoRepository.guardarHistorial(
                LoanStatusHistory(
                    loanId = prestamoId,
                    status = prestamoConId.estado,
                    changedByUserId = prestamoConId.empleadoId,
                    note = "Solicitud enviada a revisión"
                )
            )

            val cuotas = generarCuotas(prestamoId, prestamoConId)
            prestamoRepository.guardarCuotas(cuotas)

            authRepository.getAllActiveUsers().first()
                .filter { it.role == UserRole.ADMINISTRADOR }
                .forEach { admin ->
                    notificationRepository.create(
                        AppNotification(
                            recipientUserId = admin.id,
                            title = "Nueva solicitud de préstamo",
                            message = "${updatedCliente.fullName} solicitó un préstamo pendiente de revisión.",
                            relatedLoanId = prestamoId
                        )
                    )
                }

            Result.success(clienteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generarCuotas(prestamoId: Long, prestamo: Prestamo): List<Cuota> {
        val fechaInicio = prestamo.fechaInicio ?: System.currentTimeMillis()
        return buildPaymentDates(prestamo, fechaInicio).mapIndexed { index, dueAt ->
                Cuota(
                    prestamoId = prestamoId,
                    numeroCuota = index + 1,
                    fechaVencimiento = dueAt,
                    montoEsperado = prestamo.montoCuota
                )
        }
    }
}
