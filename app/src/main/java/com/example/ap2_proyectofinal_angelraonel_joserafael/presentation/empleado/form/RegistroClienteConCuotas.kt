package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.form

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.FrecuenciaPago

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroClienteConCuotas(
    onNavigateBack: () -> Unit,
    viewModel: RegistroClienteViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    // Colors adapted from the provided Tailwind config
    val surfaceColor = Color(0xFFF8F9FF)
    val onSurfaceVariant = Color(0xFF45464D)
    val outlineVariant = Color(0xFFC6C6CD)
    val primaryBlack = Color(0xFF000000)
    val secondaryGreen = Color(0xFF006C49)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nuevo Cliente", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceColor
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.onSaveCliente() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlack)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Cliente", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = surfaceColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo Picker
            ProfilePhotoPicker(
                photoPath = viewModel.profilePhotoPath,
                onPhotoPicked = { viewModel.profilePhotoPath = it }
            )

            Spacer(Modifier.height(24.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = borderStroke(outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormSectionTitle(Icons.Default.Person, "Información Personal")
                    
                    FormTextField(
                        value = viewModel.fullName,
                        onValueChange = { viewModel.fullName = it },
                        label = "Nombre Completo",
                        placeholder = "Ej. Juan Pérez",
                        icon = Icons.Default.Person
                    )

                    FormTextField(
                        value = viewModel.dni,
                        onValueChange = { viewModel.dni = it },
                        label = "Número de Identificación (DNI/ID)",
                        placeholder = "000-0000000-0",
                        icon = Icons.Default.AccountBox
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Imagen del ID (DNI)",
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IdPhotoBox(
                            modifier = Modifier.weight(1f),
                            label = "Parte Frontal",
                            photoPath = viewModel.dniFrontPhotoPath,
                            onPhotoPicked = { viewModel.dniFrontPhotoPath = it }
                        )
                        IdPhotoBox(
                            modifier = Modifier.weight(1f),
                            label = "Parte Trasera",
                            photoPath = viewModel.dniBackPhotoPath,
                            onPhotoPicked = { viewModel.dniBackPhotoPath = it }
                        )
                    }
                    Text(
                        "JPG, PNG o PDF (Máx. 5MB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = outlineVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    FormTextField(
                        value = viewModel.phone,
                        onValueChange = { viewModel.phone = it },
                        label = "Teléfono de Contacto",
                        placeholder = "+1 (555) 000-0000",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    FormTextField(
                        value = viewModel.address,
                        onValueChange = { viewModel.address = it },
                        label = "Dirección",
                        placeholder = "Calle, Número, Ciudad...",
                        icon = Icons.Default.LocationOn
                    )

                    Spacer(Modifier.height(24.dp))
                    FormSectionTitle(Icons.Default.ShoppingCart, "Detalles del Préstamo")

                    FormTextField(
                        value = viewModel.montoPrestamo,
                        onValueChange = { viewModel.montoPrestamo = it },
                        label = "Monto del Préstamo Inicial",
                        placeholder = "$ 0.00",
                        icon = Icons.Default.Add,
                        keyboardType = KeyboardType.Decimal
                    )

                    FormTextField(
                        value = viewModel.numCuotas,
                        onValueChange = { viewModel.numCuotas = it },
                        label = "Número de Cuotas",
                        placeholder = "Ej. 12",
                        icon = Icons.Default.List,
                        keyboardType = KeyboardType.Number
                    )

                    FrecuenciaDropdown(
                        selected = viewModel.frecuenciaPago,
                        onSelected = { viewModel.frecuenciaPago = it }
                    )
                }
            }
            
            Spacer(Modifier.height(100.dp)) // Extra space for bottom bar
        }
    }

    // Handle Success/Error
    if (viewModel.success) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }

    viewModel.error?.let { err ->
        AlertDialog(
            onDismissRequest = { viewModel.error = null },
            confirmButton = { TextButton(onClick = { viewModel.error = null }) { Text("OK") } },
            title = { Text("Error") },
            text = { Text(err) }
        )
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
            .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
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
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Gray)
                Text("Añadir Foto", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
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
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { onPhotoPicked(it.toString()) } }
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFBFBFE))
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
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun FormSectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF006C49), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color(0xFF45464D))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFC6C6CD),
                focusedBorderColor = Color(0xFF006C49)
            )
        )
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
        Text("Frecuencia de Pago", style = MaterialTheme.typography.labelMedium, color = Color(0xFF45464D))
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
                    unfocusedBorderColor = Color(0xFFC6C6CD),
                    focusedBorderColor = Color(0xFF006C49)
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

fun borderStroke(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroClienteConCuotasPreview() {
    MaterialTheme {
        // Vista previa estática simulando la pantalla
        RegistroClienteContentPreview()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistroClienteContentPreview() {
    val surfaceColor = Color(0xFFF8F9FF)
    val onSurfaceVariant = Color(0xFF45464D)
    val outlineVariant = Color(0xFFC6C6CD)
    val primaryBlack = Color(0xFF000000)

    // Variables de estado simuladas para el Preview
    var fullName by remember { mutableStateOf("Juan Pérez") }
    var dni by remember { mutableStateOf("001-0000000-0") }
    var phone by remember { mutableStateOf("809-555-0199") }
    var address by remember { mutableStateOf("Calle Principal #123") }
    var monto by remember { mutableStateOf("15000") }
    var cuotas by remember { mutableStateOf("12") }
    var frecuencia by remember { mutableStateOf(FrecuenciaPago.SEMANAL) } // Reemplazar con una variante real de tu Enum

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nuevo Cliente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
            )
        },
        bottomBar = {
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlack)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Cliente", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = surfaceColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfilePhotoPicker(
                photoPath = null,
                onPhotoPicked = {}
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = borderStroke(outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FormSectionTitle(Icons.Default.Person, "Información Personal")

                    FormTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Nombre Completo",
                        placeholder = "Ej. Juan Pérez",
                        icon = Icons.Default.Person
                    )

                    FormTextField(
                        value = dni,
                        onValueChange = { dni = it },
                        label = "Número de Cédula (DNI/ID)",
                        placeholder = "000-0000000-0",
                        icon = Icons.Default.AccountBox
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Imagen de la cedula (DNI/ID)",
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IdPhotoBox(
                            modifier = Modifier.weight(1f),
                            label = "Parte Frontal",
                            photoPath = null,
                            onPhotoPicked = {}
                        )
                        IdPhotoBox(
                            modifier = Modifier.weight(1f),
                            label = "Parte Trasera",
                            photoPath = null,
                            onPhotoPicked = {}
                        )
                    }
                    Text(
                        "JPG, PNG o PDF (Máx. 5MB)",
                        style = MaterialTheme.typography.bodySmall,
                        color = outlineVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    FormTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Teléfono de Contacto",
                        placeholder = "+1 (555) 000-0000",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    FormTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Dirección",
                        placeholder = "Calle, Número, Ciudad...",
                        icon = Icons.Default.LocationOn
                    )

                    Spacer(Modifier.height(24.dp))
                    FormSectionTitle(Icons.Default.ShoppingCart, "Detalles del Préstamo")

                    FormTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = "Monto del Préstamo Inicial",
                        placeholder = "$ 0.00",
                        icon = Icons.Default.Add,
                        keyboardType = KeyboardType.Decimal
                    )

                    FormTextField(
                        value = cuotas,
                        onValueChange = { cuotas = it },
                        label = "Número de Cuotas",
                        placeholder = "Ej. 12",
                        icon = Icons.Default.List,
                        keyboardType = KeyboardType.Number
                    )

                    FrecuenciaDropdown(
                        selected = frecuencia,
                        onSelected = { frecuencia = it }
                    )
                }
            }

            Spacer(Modifier.height(100.dp))
        }
    }
}