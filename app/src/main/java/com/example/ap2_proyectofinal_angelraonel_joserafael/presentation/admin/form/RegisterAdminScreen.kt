package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.QrCodeScanner
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.components.DniScannerDialog
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAdminScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToActivation: (email: String, code: String) -> Unit = { _, _ -> },
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // Campos de texto
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }

    // Pago y Banco
    var selectedBank by remember { mutableStateOf("") }
    var expandedBankMenu by remember { mutableStateOf(false) }
    var transferNumber by remember { mutableStateOf("") }
    var depositorName by remember { mutableStateOf("") }
    var voucherUri by remember { mutableStateOf<Uri?>(null) }

    // Términos
    var termsAccepted by remember { mutableStateOf(false) }
    var showDniScanner by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val registerState by viewModel.registerState.collectAsState()

    // Selector de archivos para el Voucher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> voucherUri = uri }

    // Manejo de Estados
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Solicitud enviada. Código enviado al correo: ${state.activationCode}", Toast.LENGTH_LONG).show()

                // Intent para abrir cliente de correo con el código de activación
                try {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${state.email}")
                        putExtra(Intent.EXTRA_SUBJECT, "Código de Activación TacoBrao")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "¡Hola ${fullName}!\n\nTu registro ha sido enviado exitosamente.\n\nTu código de activación es: ${state.activationCode}\n\nConsérvalo para verificar tu cuenta."
                        )
                    }
                    context.startActivity(Intent.createChooser(emailIntent, "Enviar Código por Correo"))
                } catch (e: Exception) {
                    // Continuar si no hay app de correo por defecto
                }

                onNavigateToActivation(state.email, state.activationCode)
            }
            is RegisterState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("TacoBrao", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Text("Registrar Empresa", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                "Complete la información de su empresa, defina su PIN y adjunte el comprobante para habilitar su cuenta.",
                fontSize = 14.sp,
                color = Color(0xFF30323A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // --- TARJETA FORMULARIO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // 1. DATOS PERSONALES
                    Text("INFORMACIÓN PERSONAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF30323A))
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username, onValueChange = { username = it },
                        label = { Text("Usuario (Login)") },
                        trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        trailingIcon = { Icon(Icons.Default.Mail, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nuevo Campo: PIN / Contraseña de Acceso
                    OutlinedTextField(
                        value = pin, onValueChange = { pin = it },
                        label = { Text("PIN / Contraseña de Acceso") },
                        placeholder = { Text("Mínimo 4 dígitos") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { isPinVisible = !isPinVisible }) {
                                Icon(Icons.Default.Lock, contentDescription = "Mostrar/Ocultar PIN")
                            }
                        },
                        visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text("Teléfono de Contacto") },
                        trailingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo Cédula Dominicana
                    OutlinedTextField(
                        value = cedula,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(11)
                            cedula = when {
                                digits.length > 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 10)}-${digits.substring(10)}"
                                digits.length > 3 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
                                else -> digits
                            }
                        },
                        label = { Text("Cédula de Identidad (Dominicana)") },
                        placeholder = { Text("001-0000000-0") },
                        trailingIcon = {
                            IconButton(onClick = { showDniScanner = true }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear Cédula", tint = Color(0xFF006C49))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = Color(0xFFC6C6CD))

                    // 2. SECCIÓN DE PAGO / BANCO
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE9FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Precio Mensual", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("RD$ 2,500.00", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown Banco con .menuAnchor() para interacción perfecta
                    ExposedDropdownMenuBox(
                        expanded = expandedBankMenu,
                        onExpandedChange = { expandedBankMenu = !expandedBankMenu }
                    ) {
                        OutlinedTextField(
                            value = selectedBank,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Banco") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBankMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBankMenu,
                            onDismissRequest = { expandedBankMenu = false }
                        ) {
                            listOf("Banreservas", "Banco Popular", "Banco BHD").forEach { bank ->
                                DropdownMenuItem(
                                    text = { Text(bank) },
                                    onClick = {
                                        selectedBank = bank
                                        expandedBankMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Muestra de cuenta bancaria dinámica
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEFF4FF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFC6C6CD))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Datos de Transferencia:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = when (selectedBank) {
                                    "Banreservas" -> "Cta. Corriente: 960-123456-7"
                                    "Banco Popular" -> "Cta. Ahorros: 792-883920-1"
                                    "Banco BHD" -> "Cta. Corriente: 102-492019-3"
                                    else -> "Seleccione un banco para ver el número de cuenta"
                                },
                                fontSize = 14.sp,
                                color = Color(0xFF3980F4),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = transferNumber, onValueChange = { transferNumber = it },
                        label = { Text("Número de Transferencia") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = depositorName, onValueChange = { depositorName = it },
                        label = { Text("Nombre del Depositante") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Carga de Comprobante / Voucher
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { photoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEFF4FF),
                        border = BorderStroke(1.dp, Color(0xFF3980F4))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF3980F4))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (voucherUri != null) "Voucher adjuntado correctamente" else "Toca para subir el Comprobante (Voucher)",
                                fontSize = 12.sp,
                                color = Color(0xFF3980F4),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Aceptación de Términos
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it }
                        )
                        Text(
                            "Certifico que la información provista es verídica y corresponde a mi identidad legal en la República Dominicana.",
                            fontSize = 11.sp,
                            color = Color(0xFF30323A)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Enviar
                    Button(
                        onClick = {
                            viewModel.onEvent(
                                RegisterUiEvent.SubmitRegistration(
                                    fullName = fullName,
                                    username = username,
                                    email = email,
                                    phone = phone,
                                    cedula = cedula,
                                    bank = selectedBank,
                                    transferNum = transferNumber,
                                    depositor = depositorName,
                                    voucherUri = voucherUri,
                                    pin = pin
                                )
                            )
                        },
                        enabled = termsAccepted && registerState !is RegisterState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        if (registerState is RegisterState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrar Cuenta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    if (showDniScanner) {
        DniScannerDialog(
            onDniDetected = { detectedDni ->
                cedula = detectedDni
                showDniScanner = false
            },
            onDismiss = { showDniScanner = false }
        )
    }
}
