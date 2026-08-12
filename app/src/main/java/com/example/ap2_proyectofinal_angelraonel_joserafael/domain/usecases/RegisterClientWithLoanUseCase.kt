package com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases

import android.content.Context
import androidx.core.net.toUri
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Prestamo
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.ClienteRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.repository.PrestamoRepository
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.usecases.prestamos.CalculateLoanPercentageUseCase
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage.FileStorageUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import java.math.BigDecimal
import javax.inject.Inject

class RegisterClientWithLoanUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clienteRepository: ClienteRepository,
    private val prestamoRepository: PrestamoRepository,
    private val calculateLoanPercentageUseCase: CalculateLoanPercentageUseCase
) {
    suspend operator fun invoke(
        fullName: String, dni: String, phone: String, address: String, zone: String,
        profilePhoto: String?, dniFront: String?, dniBack: String?,
        monto: BigDecimal, cuotas: Int, frecuencia: FrecuenciaPago,
        diaPago: Int?, diaPagoDesc: String?,
        empleadoId: Long,
        tasaPersonalizada: BigDecimal? = null
    ): Result<Unit> {
        return try {
            fun persist(path: String?, folder: String): String? = path?.let {
                if (it.startsWith("content://")) FileStorageUtil.saveFileToInternalStorage(context, it.toUri(), folder) else it
            }

            val cliente = Cliente(
                fullName = fullName, dni = dni, phone = phone, address = address, zone = zone,
                profilePhotoPath = persist(profilePhoto, "clients/profiles"),
                dniFrontPhotoPath = persist(dniFront, "clients/dni"),
                dniBackPhotoPath = persist(dniBack, "clients/dni"),
                isActive = true
            )
            val clienteId = clienteRepository.saveCliente(cliente)

            val tasa = tasaPersonalizada ?: when (frecuencia) {
                FrecuenciaPago.DIARIO -> BigDecimal("10")
                FrecuenciaPago.SEMANAL -> if (cuotas <= 4) BigDecimal("10") else if (cuotas <= 6) BigDecimal("15") else BigDecimal("20")
                FrecuenciaPago.QUINCENAL -> BigDecimal("20")
                FrecuenciaPago.MENSUAL -> BigDecimal("25")
            }

            val calc = calculateLoanPercentageUseCase.execute(monto, tasa, cuotas)

            val prestamo = Prestamo(
                clienteId = clienteId,
                empleadoId = empleadoId,
                montoSolicitado = monto,
                porcentajeInteres = tasa,
                interesTotal = calc.interesTotal,
                totalAPagar = calc.totalAPagar,
                montoCuota = calc.montoCuota,
                cantidadCuotas = cuotas,
                frecuenciaPago = frecuencia,
                diaPagoPreferido = diaPago,
                diaPagoDescripcion = diaPagoDesc,
                estado = LoanStatus.PENDIENTE_REVISION
            )

            prestamoRepository.guardarPrestamo(prestamo)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
