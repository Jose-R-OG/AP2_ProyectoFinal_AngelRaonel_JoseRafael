package com.example.ap2_proyectofinal_angelraonel_joserafael.util.mail

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSenderUtil {
    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "465"

    suspend fun sendActivationCode(
        recipientEmail: String,
        activationCode: String,
        senderEmail: String,
        appPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (senderEmail.isBlank() || appPassword.isBlank()) {
            Log.e("EmailSender", "Credenciales de correo no configuradas")
            return@withContext Result.failure(Exception("Credenciales de correo no configuradas. Por favor agregue email.sender y email.password en local.properties"))
        }

        val props = Properties().apply {
            put("mail.smtp.host", SMTP_HOST)
            put("mail.smtp.socketFactory.port", SMTP_PORT)
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", SMTP_PORT)
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, appPassword)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(senderEmail, "TaCobrao App"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                subject = "Código de Activación - TacoBraoApp"
                setText("""
                    ¡Hola!
                    
                    Gracias por registrar tu empresa en TaCobraoApp.
                    Tu código de activación es: $activationCode
                    
                    Ingresa este código en la aplicación para completar tu registro.
                    
                    Si no solicitaste este código, puedes ignorar este mensaje.
                """.trimIndent())
            }

            Transport.send(message)
            Log.d("EmailSender", "Correo enviado exitosamente a $recipientEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("EmailSender", "Error al enviar correo a $recipientEmail", e)
            Result.failure(e)
        }
    }
}
