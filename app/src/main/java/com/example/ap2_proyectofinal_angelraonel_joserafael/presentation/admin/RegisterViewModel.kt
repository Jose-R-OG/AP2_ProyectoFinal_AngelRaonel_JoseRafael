package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.adminregisterrequest.AdminRegisterRequest
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun submitRegistration(
        fullName: String, email: String, phone: String, cedula: String,
        bank: String, transferNum: String, depositor: String, voucherUri: Uri?
    ) {
        if (fullName.isBlank() || email.isBlank() || cedula.isBlank() || voucherUri == null) {
            _registerState.value = RegisterState.Error("Por favor complete los campos obligatorios y adjunte el voucher.")
            return
        }

        _registerState.value = RegisterState.Loading

        // 1. Subir imagen del Voucher a Firebase Storage
        val storageRef = storage.reference.child("vouchers/${System.currentTimeMillis()}.jpg")
        storageRef.putFile(voucherUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    saveRegistrationData(
                        fullName, email, phone, cedula,
                        bank, transferNum, depositor, downloadUrl.toString()
                    )
                }
            }
            .addOnFailureListener {
                _registerState.value = RegisterState.Error("Error al subir el comprobante de pago.")
            }
    }

    private fun saveRegistrationData(
        fullName: String, email: String, phone: String, cedula: String,
        bank: String, transferNum: String, depositor: String, voucherUrl: String
    ) {
        // Generar un código aleatorio de activación de 6 dígitos
        val activationCode = "EF-" + (100000..999999).random()

        val request = AdminRegisterRequest(
            fullName = fullName,
            email = email,
            phone = phone,
            cedula = cedula,
            selectedBank = bank,
            transferNumber = transferNum,
            depositorName = depositor,
            voucherUrl = voucherUrl,
            status = "PENDIENTE",
            activationCode = activationCode
        )

        // 2. Guardar en la colección 'admin_requests'
        db.collection("admin_requests").document(email)
            .set(request)
            .addOnSuccessListener {
                _registerState.value = RegisterState.Success
            }
            .addOnFailureListener {
                _registerState.value = RegisterState.Error("Error al registrar la solicitud.")
            }
    }
}