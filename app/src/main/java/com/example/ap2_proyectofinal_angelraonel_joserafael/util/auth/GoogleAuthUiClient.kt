package com.example.ap2_proyectofinal_angelraonel_joserafael.util.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.User
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class GoogleAuthUiClient(
    private val context: Context
) {
    // Lazy initialization to avoid calling Google services unless needed
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    /**
     * Inicia el flujo de Google Sign-In solo cuando el usuario lo solicita explícitamente.
     */
    suspend fun signIn(): Result<User> {
        return try {
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("384429348767-0rog4pg4shgslcduio18kc33tmocm1gm.apps.googleusercontent.com")
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val user = User(
                        id = 0L,
                        nombreCompleto = firebaseUser.displayName ?: "Usuario Google",
                        username = firebaseUser.email ?: "google_user",
                        identificacion = firebaseUser.uid,
                        telefono = firebaseUser.phoneNumber ?: "S/D",
                        pin = "0000", // PIN por defecto para usuarios Google
                        role = UserRole.ADMINISTRADOR,
                        isActive = true,
                        email = firebaseUser.email
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("Error al obtener perfil de Firebase"))
                }
            } else {
                Result.failure(Exception("Tipo de credencial no soportado"))
            }
        } catch (e: NoCredentialException) {
            Result.failure(Exception("No se encontraron cuentas de Google en este dispositivo."))
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Error de autenticación de Google: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSignedInUser(): User? {
        val firebaseUser = try { firebaseAuth.currentUser } catch (e: Exception) { null } ?: return null
        return User(
            id = 0L,
            nombreCompleto = firebaseUser.displayName ?: "Usuario Google",
            username = firebaseUser.email ?: "",
            identificacion = firebaseUser.uid,
            telefono = firebaseUser.phoneNumber ?: "S/D",
            pin = "0000",
            role = UserRole.ADMINISTRADOR,
            isActive = true,
            email = firebaseUser.email
        )
    }

    suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
        } catch (e: Exception) {
            // Ignorar silente
        }
    }
}
