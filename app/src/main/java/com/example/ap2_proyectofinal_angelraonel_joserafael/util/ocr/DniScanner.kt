package com.example.ap2_proyectofinal_angelraonel_joserafael.util.ocr

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class DniScanner {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    private val dniRegex = Regex("""\d{3}-?\d{7}-?\d{1}""")

    fun scanImage(
        image: InputImage,
        onSuccess: (String) -> Unit,
        onFinished: () -> Unit
    ) {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                val match = dniRegex.find(fullText)
                if (match != null) {
                    onSuccess(match.value)
                }
                onFinished()
            }
            .addOnFailureListener {
                onFinished()
            }
    }

    fun extractDni(text: String): String? {
        return dniRegex.find(text)?.value
    }
}
