package com.example.ap2_proyectofinal_angelraonel_joserafael.util.storage

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object FileStorageUtil {

    fun saveFileToInternalStorage(context: Context, uri: Uri, folderName: String): String? {
        return try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("No se pudo abrir el archivo seleccionado")
                
            val type = context.contentResolver.getType(uri)
            val extension = type?.split("/")?.last() ?: "jpg"
            val fileName = "${UUID.randomUUID()}.$extension"
            
            val folder = File(context.filesDir, folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            
            val file = File(folder, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
