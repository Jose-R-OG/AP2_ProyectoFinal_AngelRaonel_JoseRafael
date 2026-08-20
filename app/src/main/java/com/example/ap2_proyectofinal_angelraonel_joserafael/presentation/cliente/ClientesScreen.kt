package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.*

private const val ZONA_NORTE = "Zona Norte"
private const val ZONA_SUR = "Zona Sur"
private const val ZONA_ESTE = "Zona Este"

data class ClientesNavActions(
    val onNavigateHome: () -> Unit = {},
    val onNavigateLoans: () -> Unit = {},
    val onNavigateProfile: () -> Unit = {},
    val onNavigateRoutes: () -> Unit = {},
    val onAddCliente: () -> Unit = {},
    val onNewLoan: (Long) -> Unit = {},
    val onEdit: (Cliente) -> Unit = {},
    val onDeactivate: (Cliente) -> Unit = {},
    val onAssign: (Cliente) -> Unit = {}
)

@Composable
fun ClientesScreen(
    isAdmin: Boolean,
    actions: ClientesNavActions,
    viewModel: ClientesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ClientesContent(
        isAdmin = isAdmin,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        actions = actions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesContent(
    isAdmin: Boolean,
    uiState: ClientesUiState,
    onEvent: (ClientesUiEvent) -> Unit,
    actions: ClientesNavActions
) {
    val focusManager = LocalFocusManager.current
    val visibleClientes = uiState.filteredClientes(isAdmin)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message, uiState.editor) {
        if (uiState.editor == null) {
            uiState.message?.let { message ->
                snackbarHostState.showSnackbar(message)
                onEvent(ClientesUiEvent.MessageShown)
            }
        }
    }

    Scaffold(
        topBar = {
            ClientesTopBar()
        },
        bottomBar = {
            ClientesBottomBar(
                isAdmin = isAdmin,
                actions = actions
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = actions.onAddCliente,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar cliente")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ClientesSurface,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            ClientesHeaderSection(isAdmin, visibleClientes.count { it.isActive }, visibleClientes.count { !it.isActive }, visibleClientes.size)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { onEvent(ClientesUiEvent.SearchChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar cliente") },
                placeholder = { Text("Nombre, cédula o teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ClientesGreen)
                    }
                }
                visibleClientes.isEmpty() -> {
                    EmptyClientesState(hasSearch = uiState.searchQuery.isNotBlank())
                }
                else -> {
                    ClientesList(
                        isAdmin = isAdmin,
                        uiState = uiState,
                        visibleClientes = visibleClientes,
                        onEvent = onEvent,
                        actions = actions
                    )
                }
            }
        }
    }

    uiState.editor?.let { editor ->
        EditClienteDialog(
            editor = editor,
            isSaving = uiState.isMutating,
            onEvent = onEvent
        )
    }

    DeactivationConfirmDialog(
        pendingDeactivation = uiState.pendingDeactivation,
        isMutating = uiState.isMutating,
        onEvent = onEvent
    )

    AssignmentDialog(
        pendingAssignment = uiState.pendingAssignment,
        employeeOptions = uiState.employeeOptions,
        onEvent = onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientesTopBar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = ClientesPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                @Suppress("SpellCheckingInspection")
                Text(
                    text = "TaCobrao",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = ClientesPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ClientesSurface)
    )
}

@Composable
private fun ClientesHeaderSection(isAdmin: Boolean, activos: Int, inactivos: Int, total: Int) {
    Text(
        text = "Clientes",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = ClientesPrimary
    )
    @Suppress("SpellCheckingInspection")
    Text(
        text = "Consulta, modifica o desactiva clientes sin perder su historial.",
        fontSize = 14.sp,
        color = ClientesTextSecondary
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = if (isAdmin) {
            "$activos activo(s) · $inactivos inactivo(s)"
        } else {
            "$total cliente(s) asignado(s)"
        },
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = ClientesGreen
    )
}

@Composable
private fun ClientesList(
    isAdmin: Boolean,
    uiState: ClientesUiState,
    visibleClientes: List<Cliente>,
    onEvent: (ClientesUiEvent) -> Unit,
    actions: ClientesNavActions
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(visibleClientes, key = { it.id }) { cliente ->
            ClienteCard(
                cliente = cliente,
                enabled = !uiState.isMutating && cliente.isActive,
                isAdmin = isAdmin,
                canCreateLoans = uiState.canCreateLoans,
                actions = actions.copy(
                    onEdit = { onEvent(ClientesUiEvent.EditRequested(cliente)) },
                    onDeactivate = { onEvent(ClientesUiEvent.DeactivationRequested(cliente)) },
                    onAssign = { onEvent(ClientesUiEvent.AssignmentRequested(cliente)) }
                )
            )
        }
        item { Spacer(modifier = Modifier.height(88.dp)) }
    }
}

@Composable
private fun DeactivationConfirmDialog(
    pendingDeactivation: Cliente?,
    isMutating: Boolean,
    onEvent: (ClientesUiEvent) -> Unit
) {
    pendingDeactivation?.let { cliente ->
        AlertDialog(
            onDismissRequest = { onEvent(ClientesUiEvent.DismissDeactivation) },
            icon = { Icon(Icons.Outlined.PersonOff, null, tint = ClientesError) },
            title = { Text("Desactivar cliente") },
            text = {
                Text(
                    "¿Deseas desactivar a ${cliente.fullName}? No se borrará su información ni su historial. " +
                        "La acción se bloqueará si tiene solicitudes, deudas o préstamos activos."
                )
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(ClientesUiEvent.ConfirmDeactivation) },
                    enabled = !isMutating,
                    colors = ButtonDefaults.buttonColors(containerColor = ClientesError)
                ) {
                    if (isMutating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Desactivar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(ClientesUiEvent.DismissDeactivation) }, enabled = !isMutating) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun AssignmentDialog(
    pendingAssignment: Cliente?,
    employeeOptions: List<ClientEmployeeOption>,
    onEvent: (ClientesUiEvent) -> Unit
) {
    pendingAssignment?.let { cliente ->
        AlertDialog(
            onDismissRequest = { onEvent(ClientesUiEvent.DismissAssignment) },
            title = { Text("Asignar ${cliente.fullName}") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (employeeOptions.isEmpty()) item { Text("No hay empleados activos disponibles.") }
                    items(employeeOptions, key = { it.id }) { employee ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEvent(ClientesUiEvent.AssignToEmployee(employee.id)) },
                            border = BorderStroke(1.dp, ClientesOutline)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(employee.name, fontWeight = FontWeight.Bold)
                                Text(employee.route, color = ClientesTextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { onEvent(ClientesUiEvent.DismissAssignment) }) { Text("Cerrar") } }
        )
    }
}

@Composable
private fun ClientesBottomBar(
    isAdmin: Boolean,
    actions: ClientesNavActions
) {
    RoleBottomBar(
        isAdmin = isAdmin,
        selectedTab = PrimaryTab.CLIENTS,
        onHome = actions.onNavigateHome,
        onClients = {},
        onLoans = actions.onNavigateLoans,
        onRoutes = actions.onNavigateRoutes,
        onProfile = actions.onNavigateProfile
    )
}

@Composable
private fun ClienteCard(
    cliente: Cliente,
    enabled: Boolean,
    isAdmin: Boolean,
    canCreateLoans: Boolean,
    actions: ClientesNavActions
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ClientesOutline.copy(alpha = 0.75f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ClienteCardHeader(cliente)
            Spacer(modifier = Modifier.height(14.dp))
            ClienteCardBody(cliente)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ClientesOutline.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(12.dp))
            ClienteCardActions(cliente, enabled, isAdmin, canCreateLoans, actions)
        }
    }
}

@Composable
private fun ClienteCardHeader(cliente: Cliente) {
    val statusText = if (cliente.isActive) "ACTIVO" else "INACTIVO"
    val statusColor = if (cliente.isActive) ClientesGreen else ClientesTextSecondary
    val statusBg = if (cliente.isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant

    Row(verticalAlignment = Alignment.CenterVertically) {
        ClienteAvatar(cliente)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = cliente.fullName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ClientesPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Cliente #${cliente.id}",
                fontSize = 12.sp,
                color = ClientesGreen
            )
        }
        Surface(color = statusBg, shape = RoundedCornerShape(20.dp)) {
            Text(
                text = statusText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun ClienteCardBody(cliente: Cliente) {
    Column {
        ClienteInfoRow(Icons.Outlined.Badge, "Cédula", cliente.dni)
        ClienteInfoRow(Icons.Outlined.Phone, "Teléfono", cliente.phone)
        ClienteInfoRow(Icons.Outlined.LocationOn, "Dirección", cliente.address.ifBlank { "No registrada" })
        ClienteInfoRow(Icons.Outlined.LocationOn, "Zona", cliente.zone)
    }
}

@Composable
private fun ClienteCardActions(
    cliente: Cliente,
    enabled: Boolean,
    isAdmin: Boolean,
    canCreateLoans: Boolean,
    actions: ClientesNavActions
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { actions.onEdit(cliente) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Modificar")
            }
            OutlinedButton(
                onClick = { actions.onDeactivate(cliente) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ClientesError),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ClientesError)
            ) {
                val icon = if (cliente.isActive) Icons.Outlined.PersonOff else Icons.Default.Add
                val label = if (cliente.isActive) "Desactivar" else "Inactivo"
                Icon(icon, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(label)
            }
        }
        if (cliente.isActive && (isAdmin || canCreateLoans)) {
            if (isAdmin) {
                OutlinedButton(onClick = { actions.onAssign(cliente) }, modifier = Modifier.fillMaxWidth(), enabled = enabled) {
                    Icon(Icons.Default.Group, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("Asignar a empleado")
                }
            }
            Button(
                onClick = { actions.onNewLoan(cliente.id) },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(containerColor = ClientesGreen)
            ) {
                Icon(Icons.Default.Payments, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp)); Text("Nuevo préstamo / ampliar capital")
            }
        }
    }
}

@Composable
private fun ClienteAvatar(cliente: Cliente) {
    val modifier = Modifier
        .size(54.dp)
        .clip(CircleShape)
        .border(1.dp, ClientesOutline, CircleShape)

    if (!cliente.profilePhotoPath.isNullOrBlank()) {
        AsyncImage(
            model = cliente.profilePhotoPath,
            contentDescription = "Foto de ${cliente.fullName}",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cliente.initials(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ClienteInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ClientesTextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClientesTextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = ClientesTextSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EmptyClientesState(hasSearch: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = ClientesOutline
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (hasSearch) "No se encontraron coincidencias" else "Aún no hay clientes activos",
                fontWeight = FontWeight.Bold,
                color = ClientesPrimary
            )
            Text(
                text = if (hasSearch) "Prueba con otro nombre, cédula o teléfono." else "Usa el botón + para registrar el primero.",
                fontSize = 13.sp,
                color = ClientesTextSecondary
            )
        }
    }
}

@Composable
private fun EditClienteDialog(
    editor: ClienteEditorState,
    isSaving: Boolean,
    onEvent: (ClientesUiEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    AlertDialog(
        onDismissRequest = { onEvent(ClientesUiEvent.DismissEditor) },
        title = { Text("Modificar cliente") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = editor.fullName,
                    onValueChange = { onEvent(ClientesUiEvent.EditorNameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    isError = editor.fullNameError != null,
                    supportingText = editor.fullNameError?.let { { Text(it) } } ?: { Text("${editor.fullName.length}/80 caracteres") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                OutlinedTextField(
                    value = editor.dni,
                    onValueChange = { onEvent(ClientesUiEvent.EditorDniChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cédula") },
                    isError = editor.dniError != null,
                    supportingText = editor.dniError?.let { { Text(it) } } ?: { Text("${editor.dni.length}/11 dígitos") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                OutlinedTextField(
                    value = editor.phone,
                    onValueChange = { onEvent(ClientesUiEvent.EditorPhoneChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teléfono") },
                    isError = editor.phoneError != null,
                    supportingText = editor.phoneError?.let { { Text(it) } } ?: { Text("${editor.phone.length}/10 dígitos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editor.address,
                    onValueChange = { onEvent(ClientesUiEvent.EditorAddressChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Dirección") },
                    isError = editor.addressError != null,
                    supportingText = editor.addressError?.let { { Text(it) } } ?: { Text("${editor.address.length}/160 caracteres") },
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onEvent(ClientesUiEvent.SaveEdit)
                    })
                )
                Text("Zona de cobro", fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(ZONA_NORTE, ZONA_SUR, ZONA_ESTE).forEach { zone ->
                        OutlinedButton(
                            onClick = { onEvent(ClientesUiEvent.EditorZoneChanged(zone)) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (editor.zone == zone) Color(0xFFE5F7EF) else Color.Transparent
                            )
                        ) { Text(zone.removePrefix("Zona "), fontSize = 11.sp) }
                    }
                }
                Text(
                    text = "Las fotos y documentos actuales se conservarán.",
                    fontSize = 12.sp,
                    color = ClientesTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onEvent(ClientesUiEvent.SaveEdit) },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = ClientesPrimary)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar cambios")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(ClientesUiEvent.DismissEditor) },
                enabled = !isSaving
            ) {
                Text("Cancelar")
            }
        }
    )
}

private fun Cliente.initials(): String = fullName
    .split(" ")
    .mapNotNull { it.firstOrNull()?.toString() }
    .take(2)
    .joinToString("")
    .uppercase()
    .ifBlank { "C" }

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClientesScreenPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        ClientesContent(
            isAdmin = true,
            uiState = ClientesUiState(
                clientes = listOf(
                    Cliente(1, "Juan Perez", "12345678901", "8095551234", "Calle A", ZONA_NORTE),
                    Cliente(2, "Maria Garcia", "09876543210", "8095555678", "Calle B", ZONA_SUR, isActive = false)
                ),
                isLoading = false
            ),
            onEvent = {},
            actions = ClientesNavActions()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ClientesScreenNonAdminPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        ClientesContent(
            isAdmin = false,
            uiState = ClientesUiState(
                clientes = listOf(
                    Cliente(1, "Juan Perez", "12345678901", "8095551234", "Calle A", ZONA_NORTE),
                    Cliente(2, "Maria Garcia", "09876543210", "8095555678", "Calle B", ZONA_SUR, isActive = false)
                ),
                assignedClientIds = setOf(1),
                isLoading = false
            ),
            onEvent = {},
            actions = ClientesNavActions()
        )
    }
}
