package com.example.ap2_proyectofinal_angelraonel_joserafael.util.pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ShiftSummaryPrinter {
    fun print(context: Context, title: String, lines: List<String>): Result<Unit> = runCatching {
        val file = createPdf(context, title, lines)
        val manager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        manager.print(title, PdfFileAdapter(file), PrintAttributes.Builder().build())
        Unit
    }

    private fun createPdf(context: Context, title: String, lines: List<String>): File {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 15f
        }
        page.canvas.drawText(title, 42f, 58f, titlePaint)
        var y = 96f
        lines.forEach { line ->
            page.canvas.drawText(line, 42f, y, textPaint)
            y += 28f
        }
        document.finishPage(page)
        val folder = File(context.cacheDir, "shift_summaries").apply { mkdirs() }
        val file = File(folder, "cierre_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { output -> document.writeTo(output) }
        document.close()
        return file
    }
}

private class PdfFileAdapter(private val file: File) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }
        callback.onLayoutFinished(
            PrintDocumentInfo.Builder(file.name)
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build(),
            true
        )
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        try {
            FileInputStream(file).use { input ->
                FileOutputStream(destination.fileDescriptor).use { output -> input.copyTo(output) }
            }
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (exception: Exception) {
            callback.onWriteFailed(exception.message)
        }
    }
}
