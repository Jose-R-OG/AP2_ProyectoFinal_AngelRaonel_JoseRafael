package com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthUiClient(
    private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)
    private val TAG = "GoogleAuthUiClient"

    suspend fun signIn(): String? {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("384429348767-0rog4pg4shgslcduio18kc33tmocm1gm.apps.googleusercontent.com")
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            Log.d(TAG, "Initiating CredentialManager.getCredential...")
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            Log.d(TAG, "Credential received. Type: ${result.credential.type}")
            val credential = result.credential
            if (credential is GoogleIdTokenCredential) {
                Log.d(TAG, "Sign in successful. Email: ${credential.id}")
                credential.id
            } else {
                Log.e(TAG, "Received unexpected credential type: ${credential.type}")
                null
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Sign in cancelled by user")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign in. Exception type: ${e::class.java.simpleName}", e)
            null
        }
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
    }
}
