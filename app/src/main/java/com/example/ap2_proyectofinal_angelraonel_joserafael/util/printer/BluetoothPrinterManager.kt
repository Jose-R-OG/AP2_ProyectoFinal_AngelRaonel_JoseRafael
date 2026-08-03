package com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer

import android.annotation.SuppressLint
import android.content.Context
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.github.dantsu.escposprinter.EscPosPrinter
import com.github.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothPrinterManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission")
    fun imprimirTicket(contenido: String): Result<Unit> {
        return try {
            val connection = BluetoothPrintersConnections.selectFirstPaired()
            if (connection != null) {
                val printer = EscPosPrinter(connection, 203, 48f, 32)
                // El formato de la librería usa etiquetas como [C] para centrar, [L] izquierda, etc.
                // Convertimos el texto plano a un formato básico compatible
                val formattedText = contenido.lines().joinToString("\n") { "[L]$it" }
                printer.printFormattedText(formattedText)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se encontró ninguna impresora Bluetooth vinculada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
