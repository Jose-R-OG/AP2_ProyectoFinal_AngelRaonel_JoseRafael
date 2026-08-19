package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.QrCodeScanner
import android.net.Uri
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.components.DniScannerDialog
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.components.CameraScannerDialog
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteConCuotas(
    onNavigateBack: () -> Unit,
    viewModel: RegistroClienteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RegistroClienteContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteContent(
    uiState: RegistroClienteUiState,
    onEvent: (RegistroClienteUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var showDniScanner by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nuevo Cliente", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { onEvent(RegistroClienteUiEvent.SaveCliente) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (uiState.isExistingClient) "Solicitar nuevo préstamo" else "Guardar Cliente", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePhotoPicker(
                photoPath = uiState.profilePhotoPath,
                onPhotoPicked = { onEvent(RegistroClienteUiEvent.ProfilePhotoChanged(it)) }
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormSectionTitle(Icons.Default.Person, "Información Personal")
                    
                    FormTextField(
                        value = uiState.fullName,
                        onValueChange = { onEvent(RegistroClienteUiEvent.FullNameChanged(it)) },
                        label = "Nombre Completo",
                        placeholder = "Ej. Juan Pérez",
                        icon = Icons.Default.Person,
                        supportingText = "${uiState.fullName.length}/80 caracteres"
                    )

                    FormTextField(
                        value = uiState.dni,
                        onValueChange = { onEvent(RegistroClienteUiEvent.DniChanged(it)) },
                        label = "Número de Identificación (DNI/ID)",
                        placeholder = "000-0000000-0",
                        icon = Icons.Default.AccountBox,
                        supportingText = "${uiState.dni.length}/11 dígitos",
                        trailingIcon = {
                            IconButton(onClick = { showDniScanner = true }) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = "Escanear Cédula",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Imagen del ID (DNI)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IdPhotoBox(
                            modifier = Modifier.weight(1f),
                            label = "Parte Frontal",
                            photoPath = uiState.dniFrontPhotoPath,
                            onPhotoPicked = { onEvent(RegistroClienteUiEvent.DniFrontPhotoChanged(it)) }
                        )
                        IdPhotoBox(
                            modifier = Modifier.weight(1f),
                            label = "Parte Trasera",
                            photoPath = uiState.dniBackPhotoPath,
                            onPhotoPicked = { onEvent(RegistroClienteUiEvent.DniBackPhotoChanged(it)) }
                        )
                    }
                    Text(
                        "JPG, PNG o PDF (Máx. 5MB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    FormTextField(
                        value = uiState.phone,
                        onValueChange = { onEvent(RegistroClienteUiEvent.PhoneChanged(it)) },
                        label = "Teléfono de Contacto",
                        placeholder = "+1 (555) 000-0000",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        supportingText = "${uiState.phone.length}/10 dígitos"
                    )

                    FormTextField(
                        value = uiState.address,
                        onValueChange = { onEvent(RegistroClienteUiEvent.AddressChanged(it)) },
                        label = "Dirección",
                        placeholder = "Calle, Número, Ciudad...",
                        icon = Icons.Default.LocationOn,
                        supportingText = "${uiState.address.length}/160 caracteres"
                    )

                    Text("Zona de cobro", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Zona Norte", "Zona Sur", "Zona Este").forEach { zone ->
                            OutlinedButton(
                                onClick = { onEvent(RegistroClienteUiEvent.ZoneChanged(zone)) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (uiState.zone == zone) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                    contentColor = if (uiState.zone == zone) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) { Text(zone.removePrefix("Zona "), fontSize = 11.sp) }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    FormSectionTitle(Icons.Default.ShoppingCart, "Detalles del Préstamo")

                    FormTextField(
                        value = uiState.montoPrestamo,
                        onValueChange = { onEvent(RegistroClienteUiEvent.MontoChanged(it)) },
                        label = "Monto del Préstamo Inicial",
                        placeholder = "$ 0.00",
                        icon = Icons.Default.Add,
                        keyboardType = KeyboardType.Decimal,
                        supportingText = "${uiState.montoPrestamo.length}/10 dígitos"
                    )

                    FormTextField(
                        value = uiState.numCuotas,
                        onValueChange = { onEvent(RegistroClienteUiEvent.CuotasChanged(it)) },
                        label = "Número de Cuotas",
                        placeholder = "Ej. 12",
                        icon = Icons.Default.List,
                        keyboardType = KeyboardType.Number,
                        supportingText = "${uiState.numCuotas.length}/3 dígitos"
                    )

                    FrecuenciaDropdown(
                        selected = uiState.frecuenciaPago,
                        onSelected = { onEvent(RegistroClienteUiEvent.FrecuenciaChanged(it)) }
                    )

                    PaymentDaySelector(
                        frequency = uiState.frecuenciaPago,
                        selectedValue = uiState.diaPagoPreferido,
                        selectedDescription = uiState.diaPagoDescripcion,
                        onSelected = { value, description ->
                            onEvent(RegistroClienteUiEvent.DiaPagoChanged(value, description))
                        }
                    )

                    if (uiState.canUseCustomRate) {
                        FormTextField(
                            value = uiState.tasaPersonalizada,
                            onValueChange = { onEvent(RegistroClienteUiEvent.TasaPersonalizadaChanged(it)) },
                            label = "Tasa personalizada (opcional)",
                            placeholder = "Usar tarifa configurada",
                            icon = Icons.Default.Percent,
                            keyboardType = KeyboardType.Decimal,
                            supportingText = "0 a 100% · úsala para ampliar o renovar el préstamo",
                            imeAction = ImeAction.Done,
                            onImeAction = {
                                focusManager.clearFocus()
                                onEvent(RegistroClienteUiEvent.SaveCliente)
                            }
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(100.dp))
        }
    }

    if (showDniScanner) {
        DniScannerDialog(
            onDniDetected = { dni ->
                onEvent(RegistroClienteUiEvent.DniChanged(dni))
                showDniScanner = false
            },
            onDismiss = { showDniScanner = false }
        )
    }

    if (uiState.success) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Solicitud enviada") },
            text = { Text("El cliente y su préstamo quedaron EN REVISIÓN. Cuando el administrador apruebe o rechace, recibirás una notificación y el estado cambiará automáticamente.") },
            confirmButton = { Button(onClick = onNavigateBack) { Text("Ver clientes") } }
        )
    }

    uiState.error?.let { err ->
        AlertDialog(
            onDismissRequest = { onEvent(RegistroClienteUiEvent.ClearError) },
            confirmButton = { TextButton(onClick = { onEvent(RegistroClienteUiEvent.ClearError) }) { Text("OK") } },
            title = { Text("Error") },
            text = { Text(err) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDaySelector(
    frequency: FrecuenciaPago,
    selectedValue: Int?,
    selectedDescription: String?,
    onSelected: (Int, String) -> Unit
) {
    if (frequency == FrecuenciaPago.DIARIO) {
        Text("El pago diario iniciará el día siguiente a la activación.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    var expanded by remember { mutableStateOf(false) }
    val options = if (frequency == FrecuenciaPago.SEMANAL) {
        listOf(1 to "Domingo", 2 to "Lunes", 3 to "Martes", 4 to "Miércoles", 5 to "Jueves", 6 to "Viernes", 7 to "Sábado")
    } else {
        (1..28).map { day ->
            day to if (frequency == FrecuenciaPago.QUINCENAL) "Primera fecha: día $day (luego cada 15 días)" else "Día $day de cada mes"
        }
    }
    Column(Modifier.padding(vertical = 8.dp)) {
        Text("Día preferido de pago", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedDescription.orEmpty(),
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("El cliente elige cuándo pagar") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (value, label) ->
                    DropdownMenuItem(text = { Text(label) }, onClick = { onSelected(value, label); expanded = false })
                }
            }
        }
        Text(if (selectedValue == null) "Obligatorio para esta frecuencia" else "Seleccionado: $selectedDescription", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ProfilePhotoPicker(
    photoPath: String?,
    onPhotoPicked: (String) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onPhotoPicked(it.toString()) } }
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        contentAlignment = Alignment.Center
    ) {
        if (photoPath != null) {
            AsyncImage(
                model = photoPath,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Añadir Foto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun IdPhotoBox(
    modifier: Modifier = Modifier,
    label: String,
    photoPath: String?,
    onPhotoPicked: (String) -> Unit
) {
    var showCamera by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showCamera = true },
            contentAlignment = Alignment.Center
        ) {
            if (photoPath != null) {
                AsyncImage(
                    model = photoPath,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    if (showCamera) {
        CameraScannerDialog(
            onImageCaptured = { uri ->
                onPhotoPicked(uri.toString())
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
    }
}

@Composable
fun FormSectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) },
                onDone = {
                    focusManager.clearFocus()
                    onImeAction()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        supportingText?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 12.dp, top = 2.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrecuenciaDropdown(
    selected: FrecuenciaPago,
    onSelected: (FrecuenciaPago) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text("Frecuencia de Pago", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selected.name.lowercase().replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(20.dp)) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                FrecuenciaPago.values().forEach { frecuencia ->
                    DropdownMenuItem(
                        text = { Text(frecuencia.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            onSelected(frecuencia)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroClienteConCuotasPreview() {
    val sampleUiState = RegistroClienteUiState(
        fullName = "Juan Pérez",
        dni = "001-0000000-0",
        phone = "809-555-0199",
        address = "Calle Principal #123",
        montoPrestamo = "15000",
        numCuotas = "12",
        frecuenciaPago = FrecuenciaPago.SEMANAL
    )
    
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        RegistroClienteContent(
            uiState = sampleUiState,
            onEvent = {},
            onNavigateBack = {}
        )
    }
}
