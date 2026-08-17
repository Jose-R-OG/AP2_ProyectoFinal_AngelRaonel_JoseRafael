package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.form

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.components.DniScannerDialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAdminScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToActivation: (email: String, code: String) -> Unit = { _, _ -> },
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.registerState) {
        when (val state = uiState.registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Solicitud enviada. Código enviado al correo: ${state.activationCode}", Toast.LENGTH_LONG).show()

                try {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${state.email}")
                        putExtra(Intent.EXTRA_SUBJECT, "Código de Activación TaCobrao")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "¡Hola ${uiState.fullName}!\n\nTu registro ha sido enviado exitosamente.\n\nTu código de activación es: ${state.activationCode}\n\nConsérvalo para verificar tu cuenta."
                        )
                    }
                    context.startActivity(Intent.createChooser(emailIntent, "Enviar Código por Correo"))
                } catch (e: Exception) {
                }

                onNavigateToActivation(state.email, state.activationCode)
            }
            is RegisterState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    RegisterAdminContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAdminContent(
    uiState: RegisterUiState,
    onEvent: (RegisterUiEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onEvent(RegisterUiEvent.VoucherUriChanged(uri)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("TaCobrao", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Text("Registrar Empresa", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                "Complete la información de su empresa, defina su PIN y adjunte el comprobante para habilitar su cuenta.",
                fontSize = 14.sp,
                color = Color(0xFF30323A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Text("INFORMACIÓN PERSONAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF30323A))
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.fullName, onValueChange = { onEvent(RegisterUiEvent.FullNameChanged(it)) },
                        label = { Text("Nombre Completo") },
                        trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.username, onValueChange = { onEvent(RegisterUiEvent.UsernameChanged(it)) },
                        label = { Text("Usuario (Login)") },
                        trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.email, onValueChange = { onEvent(RegisterUiEvent.EmailChanged(it)) },
                        label = { Text("Correo Electrónico") },
                        trailingIcon = { Icon(Icons.Default.Mail, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.pin, onValueChange = { onEvent(RegisterUiEvent.PinChanged(it)) },
                        label = { Text("PIN / Contraseña de Acceso") },
                        placeholder = { Text("Mínimo 4 dígitos") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { onEvent(RegisterUiEvent.TogglePinVisibility) }) {
                                Icon(Icons.Default.Lock, contentDescription = "Mostrar/Ocultar PIN")
                            }
                        },
                        visualTransformation = if (uiState.isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.phone, onValueChange = { onEvent(RegisterUiEvent.PhoneChanged(it)) },
                        label = { Text("Teléfono de Contacto") },
                        trailingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.cedula,
                        onValueChange = { onEvent(RegisterUiEvent.CedulaChanged(it)) },
                        label = { Text("Cédula de Identidad (Dominicana)") },
                        placeholder = { Text("001-0000000-0") },
                        trailingIcon = {
                            IconButton(onClick = { onEvent(RegisterUiEvent.ShowDniScannerChanged(true)) }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear Cédula", tint = Color(0xFF006C49))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = Color(0xFFC6C6CD))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE9FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            @Suppress("SpellCheckingInspection")
                            Text("Precio Mensual", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("RD$ 2,500.00", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = uiState.expandedBankMenu,
                        onExpandedChange = { onEvent(RegisterUiEvent.ToggleBankMenu) }
                    ) {
                        OutlinedTextField(
                            value = uiState.selectedBank,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Banco") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedBankMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = uiState.expandedBankMenu,
                            onDismissRequest = { onEvent(RegisterUiEvent.ToggleBankMenu) }
                        ) {
                            listOf("Banreservas", "Banco Popular", "Banco BHD").forEach { bank ->
                                DropdownMenuItem(
                                    text = { Text(bank) },
                                    onClick = {
                                        onEvent(RegisterUiEvent.BankSelected(bank))
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEFF4FF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFC6C6CD))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Datos de Transferencia:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = when (uiState.selectedBank) {
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
                        value = uiState.transferNumber, onValueChange = { onEvent(RegisterUiEvent.TransferNumberChanged(it)) },
                        label = { Text("Número de Transferencia") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.depositorName, onValueChange = { onEvent(RegisterUiEvent.DepositorNameChanged(it)) },
                        label = { Text("Nombre del Depositante") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            onEvent(RegisterUiEvent.SubmitRegistration)
                        })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                                text = if (uiState.voucherUri != null) "Voucher adjuntado correctamente" else "Toca para subir el Comprobante (Voucher)",
                                fontSize = 12.sp,
                                color = Color(0xFF3980F4),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.termsAccepted,
                            onCheckedChange = { onEvent(RegisterUiEvent.TermsAcceptedChanged(it)) }
                        )
                        Text(
                            "Certifico que la información provista es verídica y corresponde a mi identidad legal en la República Dominicana.",
                            fontSize = 11.sp,
                            color = Color(0xFF30323A)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onEvent(RegisterUiEvent.SubmitRegistration)
                        },
                        enabled = uiState.termsAccepted && uiState.registerState !is RegisterState.Loading,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        if (uiState.registerState is RegisterState.Loading) {
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

    if (uiState.showDniScanner) {
        DniScannerDialog(
            onDniDetected = { detectedDni ->
                onEvent(RegisterUiEvent.CedulaChanged(detectedDni))
                onEvent(RegisterUiEvent.ShowDniScannerChanged(false))
            },
            onDismiss = { onEvent(RegisterUiEvent.ShowDniScannerChanged(false)) }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterAdminPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        RegisterAdminContent(
            uiState = RegisterUiState(
                fullName = "Juan Pérez",
                email = "juan.perez@example.com",
                phone = "809-555-1234",
                cedula = "402-1234567-8"
            ),
            onEvent = {}
        )
    }
}
