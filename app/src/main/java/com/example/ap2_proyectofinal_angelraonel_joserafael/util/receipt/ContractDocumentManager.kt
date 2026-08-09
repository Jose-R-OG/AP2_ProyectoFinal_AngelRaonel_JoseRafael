package com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LoanContractDocument(
    val loanId: Long,
    val clientName: String,
    val clientDni: String,
    val amount: BigDecimal,
    val rate: BigDecimal,
    val installments: Int,
    val installmentAmount: BigDecimal,
    val total: BigDecimal,
    val paymentDay: String?
)

object ContractDocumentManager {
    fun createPdf(context: Context, contract: LoanContractDocument): File {
        val folder = File(context.cacheDir, "contracts").apply { mkdirs() }
        val file = File(folder, "contrato_tacobrao_${contract.loanId}.pdf")
        val pdf = PdfDocument()
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(420, 680, 1).create())
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK; textSize = 15f }
        var y = 45f
        fun line(text: String, bold: Boolean = false, size: Float = 15f, gap: Float = 10f) {
            paint.textSize = size
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(text.take(56), 28f, y, paint)
            y += size + gap
        }
        line("TACOBRAO", true, 26f)
        line("CONTRATO / PAGARÉ DE PRÉSTAMO", true, 18f)
        line("────────────────────────────────────")
        line("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "DO")).format(Date())}")
        line("Contrato: TB-PR-${contract.loanId}")
        line("Cliente: ${contract.clientName}", true)
        line("Cédula: ${contract.clientDni.ifBlank { "No registrada" }}")
        line("────────────────────────────────────")
        line(String.format(Locale.US, "Capital entregado: RD$ %,.2f", contract.amount))
        line("Tasa acordada: ${contract.rate}%")
        line("Plan: ${contract.installments} cuotas")
        line(String.format(Locale.US, "Cuota: RD$ %,.2f", contract.installmentAmount))
        line(String.format(Locale.US, "Total a pagar: RD$ %,.2f", contract.total), true)
        contract.paymentDay?.let { line("Día de pago elegido: $it", true) }
        line("────────────────────────────────────")
        line("El cliente acepta pagar el total indicado según el")
        line("calendario acordado y autoriza el registro de pagos.")
        y += 70f
        line("____________________________________")
        line("Firma digital o manuscrita del cliente", true)
        line("────────────────────────────────────")
        line("Documento emitido por TacoBrao. Conserve una copia.")
        pdf.finishPage(page)
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()
        return file
    }

    fun shareWhatsApp(context: Context, contract: LoanContractDocument): Result<Unit> = runCatching {
        val file = createPdf(context, contract)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val base = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Contrato TacoBrao TB-PR-${contract.loanId}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(Intent(base).setPackage("com.whatsapp"))
        } catch (_: Exception) {
            context.startActivity(Intent.createChooser(base, "Enviar contrato").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
