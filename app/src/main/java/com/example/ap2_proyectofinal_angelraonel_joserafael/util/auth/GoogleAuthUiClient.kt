package com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.SecureRandom
import android.util.Base64
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

class GoogleAuthUiClient(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    private fun generateNonce(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING)
    }

    suspend fun signIn(): String? {
        val googleIdOption = GetSignInWithGoogleOption.Builder(
            serverClientId = "384429348767-0rog4pg4shgslcduio18kc33tmocm1gm.apps.googleusercontent.com"
        ).setNonce(generateNonce())
        .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                credential.id
            } else if (credential is CustomCredential) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    googleIdTokenCredential.id
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        } catch (e: GetCredentialCancellationException) {
            null
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
    }
}
