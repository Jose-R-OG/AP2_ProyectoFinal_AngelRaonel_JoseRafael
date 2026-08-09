package com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint

@Composable
fun DigitalSignaturePad(onSaved: (Bitmap) -> Unit) {
    val points = remember { mutableStateListOf<Offset?>() }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Firma digital del cliente")
        Canvas(
            modifier = Modifier.fillMaxWidth().height(150.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFC6C6CD))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { points.add(it) },
                        onDragEnd = { points.add(null) },
                        onDragCancel = { points.add(null) }
                    ) { change, _ -> change.consume(); points.add(change.position) }
                }
        ) {
            var previous: Offset? = null
            points.forEach { point ->
                if (point == null) previous = null
                else {
                    previous?.let { drawLine(Color.Black, it, point, 4f, StrokeCap.Round) }
                    previous = point
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { points.clear() }, modifier = Modifier.weight(1f)) { Text("Limpiar") }
            Button(
                onClick = { onSaved(renderSignature(points)) },
                enabled = points.any { it != null },
                modifier = Modifier.weight(1f)
            ) { Text("Guardar firma") }
        }
    }
}

private fun renderSignature(points: List<Offset?>): Bitmap {
    val bitmap = Bitmap.createBitmap(900, 300, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 7f
        strokeCap = AndroidPaint.Cap.ROUND
    }
    var previous: Offset? = null
    points.forEach { point ->
        if (point == null) previous = null
        else {
            previous?.let { canvas.drawLine(it.x * 2f, it.y * 2f, point.x * 2f, point.y * 2f, paint) }
            previous = point
        }
    }
    return bitmap
}
