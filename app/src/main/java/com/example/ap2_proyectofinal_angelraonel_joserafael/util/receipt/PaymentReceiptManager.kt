package com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.printer.BluetoothPrinterManager
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PaymentReceipt(
    val receiptNumber: String,
    val transactionId: Long? = null,
    val loanId: Long,
    val clientName: String,
    val clientDni: String = "",
    val employeeName: String,
    val amount: BigDecimal,
    val paymentMethod: String,
    val paidAt: Long,
    val note: String = "Abono de préstamo",
    val installmentLabel: String = "Pago de cuota",
    val totalInstallments: Int = 0,
    val remainingInstallments: Int = 0,
    val remainingBalance: BigDecimal = BigDecimal.ZERO,
    val debtPaidOff: Boolean = false,
    val signaturePath: String? = null,
    val businessName: String = "TacoBrao",
    val businessRnc: String = "",
    val businessAddress: String = ""
)

object PaymentReceiptManager {
    fun saveSignature(context: Context, receiptNumber: String, bitmap: Bitmap): String {
        val folder = File(context.filesDir, "signatures").apply { mkdirs() }
        val file = File(folder, "firma_${receiptNumber.replace(Regex("[^A-Za-z0-9_-]"), "_")}.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file.absolutePath
    }

    fun createPdf(context: Context, receipt: PaymentReceipt): File {
        val folder = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val file = File(folder, "comprobante_${receipt.receiptNumber.replace(Regex("[^A-Za-z0-9_-]"), "_")}.pdf")
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(320, 690, 1).create())
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK; textSize = 12f }
        var y = 30f
        fun line(text: String, bold: Boolean = false, size: Float = 12f) {
            paint.textSize = size
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(text.take(48), 18f, y, paint)
            y += size + 8f
        }
        line(receipt.businessName.uppercase(), true, 22f)
        line(if (receipt.debtPaidOff) "COMPROBANTE DE DEUDA SALDADA" else "COMPROBANTE DE PAGO", true, 15f)
        if (receipt.businessRnc.isNotBlank()) line("RNC: ${receipt.businessRnc}")
        if (receipt.businessAddress.isNotBlank()) line(receipt.businessAddress)
        line("────────────────────────────────")
        line("Comprobante: ${receipt.receiptNumber}")
        receipt.transactionId?.let { line("Transacción: #$it") }
        line("Préstamo: #${receipt.loanId}")
        line("Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale("es", "DO")).format(Date(receipt.paidAt))}")
        line("Hora: ${SimpleDateFormat("HH:mm:ss", Locale("es", "DO")).format(Date(receipt.paidAt))}")
        line("Cobrador: ${receipt.employeeName}")
        line("────────────────────────────────")
        line("Cliente: ${receipt.clientName}", true)
        if (receipt.clientDni.isNotBlank()) line("Cédula: ${receipt.clientDni}")
        line("Método: ${receipt.paymentMethod}")
        line("────────────────────────────────")
        line("Cuota N.º: ${receipt.installmentLabel}", true)
        if (receipt.totalInstallments > 0) {
            line("Pagos restantes: ${receipt.remainingInstallments} de ${receipt.totalInstallments}", true)
        }
        line("MONTO PAGADO", true, 14f)
        line(String.format(Locale.US, "RD$ %,.2f", receipt.amount), true, 23f)
        line(String.format(Locale.US, "Saldo restante: RD$ %,.2f", receipt.remainingBalance), true)
        line("────────────────────────────────")
        if (receipt.debtPaidOff) {
            line("DEUDA SALDADA", true, 21f)
            line("El préstamo fue pagado completamente.", true)
        } else {
            line(receipt.note)
        }
        drawVerificationCode(canvas, receipt.receiptNumber, 112f, y + 2f)
        y += 104f
        line("Código de validación: ${receipt.receiptNumber}", true, 10f)
        receipt.signaturePath?.let { path ->
            BitmapFactory.decodeFile(path)?.let { signature ->
                canvas.drawBitmap(signature, null, android.graphics.RectF(55f, y, 265f, y + 62f), paint)
                y += 70f
                line("Firma digital", true, 10f)
            }
        }
        line("────────────────────────────────")
        line("Conserve este comprobante.")
        line(if (receipt.debtPaidOff) "¡Gracias! Su deuda está saldada." else "¡Gracias por su puntualidad!", true)
        document.finishPage(page)
        FileOutputStream(file).use { output -> document.writeTo(output) }
        document.close()
        return file
    }

    private fun drawVerificationCode(canvas: android.graphics.Canvas, seed: String, left: Float, top: Float) {
        val size = 9
        val cell = 9f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK }
        val hash = seed.hashCode()
        for (row in 0 until size) for (column in 0 until size) {
            val finder = (row < 3 && column < 3) || (row < 3 && column >= 6) || (row >= 6 && column < 3)
            val bit = ((hash shr ((row * size + column) % 28)) and 1) == 1
            if (finder || bit) canvas.drawRect(left + column * cell, top + row * cell, left + (column + 1) * cell, top + (row + 1) * cell, paint)
        }
    }

    fun print(context: Context, receipt: PaymentReceipt, printerManager: BluetoothPrinterManager? = null): Result<Unit> {
        return if (printerManager != null) {
            val text = ThermalReceiptGenerator.generate(receipt)
            printerManager.imprimirTicket(text)
        } else {
            runCatching {
                val file = createPdf(context, receipt)
                val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                manager.print("TacoBrao ${receipt.receiptNumber}", PdfAdapter(file), PrintAttributes.Builder().build())
                Unit
            }
        }
    }

    fun shareWhatsApp(context: Context, receipt: PaymentReceipt): Result<Unit> = runCatching {
        val file = createPdf(context, receipt)
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val base = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Comprobante de pago TacoBrao ${receipt.receiptNumber}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val whatsapp = Intent(base).setPackage("com.whatsapp")
        try {
            context.startActivity(whatsapp)
        } catch (_: Exception) {
            context.startActivity(Intent.createChooser(base, "Enviar comprobante por WhatsApp").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    private class PdfAdapter(private val file: File) : PrintDocumentAdapter() {
        override fun onLayout(
            oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?, cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback, extras: android.os.Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) { callback.onLayoutCancelled(); return }
            callback.onLayoutFinished(
                PrintDocumentInfo.Builder(file.name).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(1).build(),
                true
            )
        }

        override fun onWrite(
            pages: Array<out PageRange>?, destination: ParcelFileDescriptor, cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback
        ) {
            runCatching {
                file.inputStream().use { input -> FileOutputStream(destination.fileDescriptor).use { input.copyTo(it) } }
            }.onSuccess { callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES)) }
                .onFailure { callback.onWriteFailed(it.message) }
        }
    }
}
