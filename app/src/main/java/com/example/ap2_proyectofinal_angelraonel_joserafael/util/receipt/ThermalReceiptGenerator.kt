package com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ThermalReceiptGenerator {

    fun generate(receipt: PaymentReceipt): String {
        val localeDo = Locale.forLanguageTag("es-DO")
        val dateStr = SimpleDateFormat("dd/MM/yyyy", localeDo).format(Date(receipt.paidAt))
        val timeStr = SimpleDateFormat("HH:mm:ss", localeDo).format(Date(receipt.paidAt))

        val sb = StringBuilder()
        sb.append("--------------------------------\n")
        sb.append("           ${receipt.businessName.uppercase()}             \n")
        sb.append(if (receipt.debtPaidOff) "  COMPROBANTE DE DEUDA SALDADA  \n" else "      COMPROBANTE DE PAGO       \n")
        sb.append("--------------------------------\n")
        
        if (receipt.businessRnc.isNotBlank()) sb.append("RNC: ${receipt.businessRnc}\n")
        if (receipt.businessAddress.isNotBlank()) sb.append("${receipt.businessAddress}\n")
        
        sb.append("Comprobante: ${receipt.receiptNumber}\n")
        receipt.transactionId?.let { sb.append("Transacción: #$it\n") }
        sb.append("Préstamo: #${receipt.loanId}\n")
        sb.append("Fecha: $dateStr\n")
        sb.append("Hora: $timeStr\n")
        sb.append("Cobrador: ${receipt.employeeName}\n")
        sb.append("--------------------------------\n")
        sb.append("Cliente: ${receipt.clientName}\n")
        if (receipt.clientDni.isNotBlank()) sb.append("Cédula: ${receipt.clientDni}\n")
        sb.append("Método: ${receipt.paymentMethod}\n")
        sb.append("--------------------------------\n")
        sb.append("Cuota N.º: ${receipt.installmentLabel}\n")
        if (receipt.totalInstallments > 0) {
            sb.append("Pagos: ${receipt.totalInstallments - receipt.remainingInstallments} de ${receipt.totalInstallments}\n")
        }
        
        sb.append("\nMONTO PAGADO:\n")
        sb.append("RD$ ${String.format(Locale.US, "%,.2f", receipt.amount)}\n")
        sb.append("Saldo restante: RD$ ${String.format(Locale.US, "%,.2f", receipt.remainingBalance)}\n")
        sb.append("--------------------------------\n")
        
        if (receipt.debtPaidOff) {
            sb.append("        DEUDA SALDADA           \n")
            sb.append("El préstamo fue pagado\n")
            sb.append("completamente.\n")
        } else {
            sb.append("${receipt.note}\n")
        }
        
        sb.append("\n\n\n")
        sb.append("________________________________\n")
        sb.append("       Firma del Cliente        \n")
        sb.append("\n")
        sb.append("--------------------------------\n")
        sb.append(if (receipt.debtPaidOff) " ¡Gracias! Su deuda está saldada." else "  ¡Gracias por su puntualidad!  ")
        sb.append("\n--------------------------------\n\n\n")
        
        return sb.toString()
    }
}
