package com.example.ap2_proyectofinal_angelraonel_joserafael.util.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReceiptGenerator {

    fun generarReciboPdf(
        context: Context,
        clienteNombre: String,
        montoPagado: String,
        balancePendiente: String,
        numeroCuota: Int,
        nota: String
    ): File? {
        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date())

        val centroX = pageInfo.pageWidth / 2f

        canvas.drawText("RECIBO DE PAGO", centroX, 50f, paintTitle)
        canvas.drawText("================================", centroX, 80f, paintTitle)

        canvas.drawText("Fecha: $fechaActual", 20f, 120f, paintText)
        canvas.drawText("Cliente: $clienteNombre", 20f, 150f, paintText)
        canvas.drawText("Cuota #: $numeroCuota", 20f, 180f, paintText)

        canvas.drawText("Monto Recibido: RD$ $montoPagado", 20f, 230f, paintText)
        canvas.drawText("Balance Restante: RD$ $balancePendiente", 20f, 260f, paintText)
        canvas.drawText("Estado: $nota", 20f, 290f, paintText)

        canvas.drawText("================================", centroX, 340f, paintTitle)
        canvas.drawText("¡Gracias por su pago!", centroX, 380f, paintTitle)

        pdfDocument.finishPage(page)

        return try {
            val pdfDir = File(context.cacheDir, "pdfs")
            if (!pdfDir.exists()) pdfDir.mkdirs()

            val file = File(pdfDir, "Recibo_${System.currentTimeMillis()}.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
