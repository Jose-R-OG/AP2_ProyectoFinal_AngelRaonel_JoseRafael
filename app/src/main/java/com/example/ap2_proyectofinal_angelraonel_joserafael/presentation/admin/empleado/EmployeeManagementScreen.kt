package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.io.File

private val Page = Color(0xFFF8F9FF)
private val Ink = Color(0xFF111318)
private val Muted = Color(0xFF30323A)
private val Green = Color(0xFF006C49)
private val Red = Color(0xFFBA1A1A)
private val Border = Color(0xFFC6C6CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    uiState: EmployeeUiState,
    onEvent: (EmployeeUiEvent) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        containerColor = Page,
        topBar = {
            TopAppBar(
                title = { Text("TaCobrao · Empleados", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Page)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(EmployeeUiEvent.OpenAddModal) }, containerColor = Color.Black, contentColor = Color.White) {
                Icon(Icons.Default.PersonAdd, "Agregar empleado")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Text("Directorio de empleados", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Busca, revisa actividad, edita credenciales y distribuye clientes.", color = Muted)
            }
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onEvent(EmployeeUiEvent.SearchChanged(it)) },
                    label = { Text("Buscar por nombre, cédula, teléfono o zona") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Stat("Total", uiState.totalAgents, Modifier.weight(1f))
                    Stat("Activos", uiState.activeAgents, Modifier.weight(1f))
                    Stat("Sin ruta", uiState.pendingRoutes, Modifier.weight(1f))
                }
            }
            if (uiState.isLoading) item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            if (!uiState.isLoading && uiState.employees.isEmpty()) item {
                Text("No se encontraron empleados.", modifier = Modifier.fillMaxWidth().padding(28.dp), color = Muted)
            }
            items(uiState.employees, key = Employee::id) { employee ->
                EmployeeCard(employee, onEvent)
            }
        }
    }

    if (uiState.isEditorOpen) EmployeeEditor(uiState, onEvent)
    uiState.selectedEmployee?.let { EmployeeDetail(it, onEvent) }
    uiState.assignmentEmployee?.let { AssignmentDialog(it, uiState.assignableClients, onEvent) }
    uiState.pendingDeactivation?.let { employee ->
        AlertDialog(
            onDismissRequest = { onEvent(EmployeeUiEvent.CancelDeactivation) },
            title = { Text("¿Desactivar empleado?") },
            text = { Text("${employee.name} conservará su historial y aparecerá como INACTIVO. Sus ${employee.clientsAssigned} cliente(s) pendientes pasarán al administrador para ser redistribuidos.") },
            confirmButton = { Button(onClick = { onEvent(EmployeeUiEvent.ConfirmDeactivation) }, colors = ButtonDefaults.buttonColors(containerColor = Red)) { Text("Sí, desactivar") } },
            dismissButton = { TextButton(onClick = { onEvent(EmployeeUiEvent.CancelDeactivation) }) { Text("Cancelar") } }
        )
    }
    (uiState.errorMessage ?: uiState.successMessage)?.let { message ->
        AlertDialog(
            onDismissRequest = { onEvent(EmployeeUiEvent.ClearMessage) },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { onEvent(EmployeeUiEvent.ClearMessage) }) { Text("Aceptar") } }
        )
    }
}

@Composable private fun Stat(label: String, value: Int, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Border)) {
        Column(Modifier.padding(12.dp)) { Text(label, fontSize = 12.sp, color = Muted); Text(value.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable private fun EmployeeCard(employee: Employee, onEvent: (EmployeeUiEvent) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEvent(EmployeeUiEvent.ShowDetails(employee.id)) },
        colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (employee.photoUrl != null) AsyncImage(
                    model = if (employee.photoUrl.startsWith("/")) File(employee.photoUrl) else employee.photoUrl,
                    contentDescription = "Foto de ${employee.name}", modifier = Modifier.size(52.dp).clip(CircleShape)
                ) else Box(Modifier.size(52.dp).clip(CircleShape).background(Color(0xFFDCE9FF)), contentAlignment = Alignment.Center) {
                    Text(employee.name.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("${employee.route} · ${employee.phone}", color = Muted, fontSize = 13.sp)
                }
                Text(if (employee.isActive) "ACTIVO" else "INACTIVO", color = if (employee.isActive) Green else Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text("${employee.clientsAssigned} clientes · ${employee.collectionCount} cobros registrados", color = Muted, fontSize = 13.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(onClick = { onEvent(EmployeeUiEvent.OpenEdit(employee.id)) }, modifier = Modifier.weight(1f)) { Text("Editar", fontSize = 12.sp) }
                OutlinedButton(onClick = { onEvent(EmployeeUiEvent.OpenAssignment(employee.id)) }, modifier = Modifier.weight(1f), enabled = employee.isActive) { Text("Asignar", fontSize = 12.sp) }
                TextButton(onClick = { onEvent(EmployeeUiEvent.ToggleStatus(employee.id)) }, modifier = Modifier.weight(1f)) {
                    Text(if (employee.isActive) "Desactivar" else "Activar", color = if (employee.isActive) Red else Green, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun EmployeeEditor(ui: EmployeeUiState, onEvent: (EmployeeUiEvent) -> Unit) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    val profile = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onEvent(EmployeeUiEvent.PhotoChanged(EmployeePhotoType.PROFILE, it.toString())) }
    }
    val front = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onEvent(EmployeeUiEvent.PhotoChanged(EmployeePhotoType.DNI_FRONT, it.toString())) }
    }
    val back = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onEvent(EmployeeUiEvent.PhotoChanged(EmployeePhotoType.DNI_BACK, it.toString())) }
    }

    AlertDialog(
        onDismissRequest = { onEvent(EmployeeUiEvent.CloseModal) },
        title = { Text(if (ui.editingEmployeeId == null) "Agregar empleado" else "Editar empleado") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 620.dp)
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EditorField(ui.name, { onEvent(EmployeeUiEvent.NameChanged(it)) }, "Nombre completo", "${ui.name.length}/80", KeyboardType.Text, imeAction = ImeAction.Next, focusManager = focusManager)
                EditorField(ui.username, { onEvent(EmployeeUiEvent.UsernameChanged(it)) }, "Usuario de acceso", "${ui.username.length}/24 (mínimo 4)", KeyboardType.Text, imeAction = ImeAction.Next, focusManager = focusManager)
                EditorField(ui.pin, { onEvent(EmployeeUiEvent.PinChanged(it)) }, "PIN", "${ui.pin.length}/4 dígitos", KeyboardType.NumberPassword, true, imeAction = ImeAction.Next, focusManager = focusManager)
                EditorField(ui.phone, { onEvent(EmployeeUiEvent.PhoneChanged(it)) }, "Teléfono", "${ui.phone.length}/10 dígitos", KeyboardType.Phone, imeAction = ImeAction.Next, focusManager = focusManager)
                EditorField(ui.identification, { onEvent(EmployeeUiEvent.IdentificationChanged(it)) }, "Cédula", "${ui.identification.length}/11 dígitos", KeyboardType.Number, imeAction = ImeAction.Next, focusManager = focusManager)
                EditorField(ui.address, { onEvent(EmployeeUiEvent.AddressChanged(it)) }, "Dirección donde vive", "${ui.address.length}/160", KeyboardType.Text, imeAction = ImeAction.Done, focusManager = focusManager, onDone = {
                    focusManager.clearFocus()
                    onEvent(EmployeeUiEvent.SaveEmployee)
                })
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(ui.route, {}, readOnly = true, label = { Text("Zona / ruta") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded, { expanded = false }) { ui.availableRoutes.forEach { route ->
                        DropdownMenuItem({ Text(route) }, { onEvent(EmployeeUiEvent.RouteSelected(route)); expanded = false })
                    } }
                }
                PhotoButton("Foto del empleado", ui.profilePhotoPath) { profile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                PhotoButton("Cédula · frente", ui.dniFrontPhotoPath) { front.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                PhotoButton("Cédula · reverso", ui.dniBackPhotoPath) { back.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                Text("Permisos operativos", fontWeight = FontWeight.Bold)
                PermissionSwitch("Registrar clientes", ui.canCreateClients) { onEvent(EmployeeUiEvent.PermissionChanged(EmployeePermission.CREATE_CLIENTS, it)) }
                PermissionSwitch("Realizar cobros", ui.canCollectPayments) { onEvent(EmployeeUiEvent.PermissionChanged(EmployeePermission.COLLECT_PAYMENTS, it)) }
                PermissionSwitch("Consultar su ruta", ui.canViewRoute) { onEvent(EmployeeUiEvent.PermissionChanged(EmployeePermission.VIEW_ROUTE, it)) }
                PermissionSwitch("Cerrar caja", ui.canCloseCash) { onEvent(EmployeeUiEvent.PermissionChanged(EmployeePermission.CLOSE_CASH, it)) }
                PermissionSwitch("Imprimir y compartir documentos", ui.canShareDocuments) { onEvent(EmployeeUiEvent.PermissionChanged(EmployeePermission.SHARE_DOCUMENTS, it)) }
            }
        },
        confirmButton = { Button(onClick = { onEvent(EmployeeUiEvent.SaveEmployee) }, enabled = !ui.isSaving) { Text(if (ui.isSaving) "Guardando…" else "Guardar") } },
        dismissButton = { TextButton(onClick = { onEvent(EmployeeUiEvent.CloseModal) }) { Text("Cancelar") } }
    )
}

@Composable private fun PermissionSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, modifier = Modifier.weight(1f), color = Muted)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable private fun EditorField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    support: String,
    type: KeyboardType,
    password: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    focusManager: FocusManager? = null,
    onDone: () -> Unit = {}
) {
    OutlinedTextField(
        value, onChange, label = { Text(label) }, supportingText = { Text(support) },
        keyboardOptions = KeyboardOptions(keyboardType = type, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { focusManager?.moveFocus(FocusDirection.Down) },
            onDone = {
                focusManager?.clearFocus()
                onDone()
            }
        ),
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(), singleLine = label != "Dirección donde vive"
    )
}

@Composable private fun PhotoButton(label: String, path: String?, onClick: () -> Unit) {
    OutlinedButton(onClick, Modifier.fillMaxWidth()) {
        Icon(if (path == null) Icons.Default.AddAPhoto else Icons.Default.CheckCircle, null)
        Spacer(Modifier.width(8.dp)); Text(if (path == null) "$label (obligatoria)" else "$label cargada")
    }
}

@Composable private fun EmployeeDetail(employee: Employee, onEvent: (EmployeeUiEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(EmployeeUiEvent.CloseDetails) },
        title = { Text(employee.name) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(if (employee.isActive) "ACTIVO" else "DESACTIVADO", color = if (employee.isActive) Green else Red, fontWeight = FontWeight.Bold)
            DetailLine("Usuario", employee.username); DetailLine("Cédula", employee.identification)
            DetailLine("Teléfono", employee.phone); DetailLine("Dirección", employee.address)
            DetailLine("Ruta", employee.route); DetailLine("Clientes", employee.clientsAssigned.toString())
            DetailLine("Actividad de cobros", employee.collectionCount.toString())
            Text("Credenciales: ${if (employee.dniFrontPhotoPath != null && employee.dniBackPhotoPath != null) "completas" else "incompletas"}", color = Muted)
            Text("Permisos", fontWeight = FontWeight.Bold)
            Text(listOfNotNull(
                "Clientes".takeIf { employee.canCreateClients },
                "Cobros".takeIf { employee.canCollectPayments },
                "Ruta".takeIf { employee.canViewRoute },
                "Cierre".takeIf { employee.canCloseCash },
                "Documentos".takeIf { employee.canShareDocuments }
            ).joinToString(" · ").ifBlank { "Sin permisos operativos" }, color = Muted, fontSize = 12.sp)
            Text("Actividad reciente", fontWeight = FontWeight.Bold)
            if (employee.recentActivity.isEmpty()) Text("Sin cobros registrados.", color = Muted)
            employee.recentActivity.forEach { Text("• $it", fontSize = 12.sp, color = Muted) }
        } },
        confirmButton = { Button(onClick = { onEvent(EmployeeUiEvent.CloseDetails); onEvent(EmployeeUiEvent.OpenEdit(employee.id)) }) { Text("Editar información") } },
        dismissButton = { TextButton(onClick = { onEvent(EmployeeUiEvent.CloseDetails) }) { Text("Cerrar") } }
    )
}

@Composable private fun DetailLine(label: String, value: String) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, color = Muted); Text(value.ifBlank { "No registrado" }, fontWeight = FontWeight.SemiBold) } }

@Composable private fun AssignmentDialog(employee: Employee, clients: List<AssignableClient>, onEvent: (EmployeeUiEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(EmployeeUiEvent.CloseAssignment) },
        title = { Text("Asignar cliente a ${employee.name}") },
        text = { LazyColumn(Modifier.heightIn(max = 440.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (clients.isEmpty()) item { Text("No hay clientes activos disponibles.") }
            items(clients, key = AssignableClient::id) { client ->
                Card(
                    Modifier.fillMaxWidth().clickable(enabled = client.currentEmployeeId != employee.id.toLong()) { onEvent(EmployeeUiEvent.AssignClient(client.id)) },
                    colors = CardDefaults.cardColors(containerColor = if (client.currentEmployeeId == employee.id.toLong()) Color(0xFFE5F7EF) else Color.White),
                    border = BorderStroke(1.dp, Border)
                ) { Column(Modifier.padding(12.dp)) {
                    Text(client.name, fontWeight = FontWeight.Bold); Text(client.zone, color = Muted, fontSize = 12.sp)
                    Text(if (client.currentEmployeeId == employee.id.toLong()) "Ya asignado" else "Tocar para asignar", color = if (client.currentEmployeeId == employee.id.toLong()) Green else Muted, fontSize = 12.sp)
                } }
            }
        } },
        confirmButton = { TextButton(onClick = { onEvent(EmployeeUiEvent.CloseAssignment) }) { Text("Cerrar") } }
    )
}
