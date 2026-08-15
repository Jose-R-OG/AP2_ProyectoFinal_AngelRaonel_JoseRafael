package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.Cliente
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar

private val ClientesSurface = Color(0xFFF8F9FF)
private val ClientesPrimary = Color(0xFF000000)
private val ClientesGreen = Color(0xFF006C49)
private val ClientesTextSecondary = Color(0xFF30323A)
private val ClientesOutline = Color(0xFFC6C6CD)
private val ClientesError = Color(0xFFBA1A1A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    isAdmin: Boolean,
    onNavigateHome: () -> Unit,
    onNavigateLoans: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateRoutes: () -> Unit,
    onAddCliente: () -> Unit,
    onNewLoan: (Long) -> Unit = {},
    viewModel: ClientesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visibleClientes = uiState.filteredClientes(isAdmin)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message, uiState.editor) {
        if (uiState.editor == null) {
            uiState.message?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.onEvent(ClientesUiEvent.MessageShown)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = ClientesPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
        },
        bottomBar = {
            ClientesBottomBar(
                isAdmin = isAdmin,
                onNavigateHome = onNavigateHome,
                onNavigateLoans = onNavigateLoans,
                onNavigateProfile = onNavigateProfile,
                onNavigateRoutes = onNavigateRoutes
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCliente,
                containerColor = ClientesPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar cliente")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ClientesSurface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Clientes",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ClientesPrimary
            )
            Text(
                text = "Consulta, modifica o desactiva clientes sin perder su historial.",
                fontSize = 14.sp,
                color = ClientesTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onEvent(ClientesUiEvent.SearchChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar cliente") },
                placeholder = { Text("Nombre, cédula o teléfono") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isAdmin) {
                    "${visibleClientes.count { it.isActive }} activo(s) · ${visibleClientes.count { !it.isActive }} inactivo(s)"
                } else {
                    "${visibleClientes.size} cliente(s) asignado(s)"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ClientesGreen
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
                                onEdit = {
                                    viewModel.onEvent(ClientesUiEvent.EditRequested(cliente))
                                },
                                onDeactivate = {
                                    viewModel.onEvent(ClientesUiEvent.DeactivationRequested(cliente))
                                },
                                onAssign = {
                                    viewModel.onEvent(ClientesUiEvent.AssignmentRequested(cliente))
                                },
                                onNewLoan = { onNewLoan(cliente.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(88.dp)) }
                    }
                }
            }
        }
    }

    uiState.editor?.let { editor ->
        EditClienteDialog(
            editor = editor,
            isSaving = uiState.isMutating,
            errorMessage = uiState.message,
            onEvent = viewModel::onEvent
        )
    }

    uiState.pendingDeactivation?.let { cliente ->
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(ClientesUiEvent.DismissDeactivation)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.PersonOff,
                    contentDescription = null,
                    tint = ClientesError
                )
            },
            title = { Text("Desactivar cliente") },
            text = {
                Text(
                    "¿Deseas desactivar a ${cliente.fullName}? No se borrará su información ni su historial. " +
                        "La acción se bloqueará si tiene solicitudes, deudas o préstamos activos."
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(ClientesUiEvent.ConfirmDeactivation) },
                    enabled = !uiState.isMutating,
                    colors = ButtonDefaults.buttonColors(containerColor = ClientesError)
                ) {
                    if (uiState.isMutating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Desactivar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(ClientesUiEvent.DismissDeactivation) },
                    enabled = !uiState.isMutating
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    uiState.pendingAssignment?.let { cliente ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ClientesUiEvent.DismissAssignment) },
            title = { Text("Asignar ${cliente.fullName}") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.employeeOptions.isEmpty()) item { Text("No hay empleados activos disponibles.") }
                    items(uiState.employeeOptions, key = { it.id }) { employee ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { viewModel.onEvent(ClientesUiEvent.AssignToEmployee(employee.id)) },
                            border = BorderStroke(1.dp, ClientesOutline),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(employee.name, fontWeight = FontWeight.Bold)
                                Text(employee.route, color = ClientesTextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { viewModel.onEvent(ClientesUiEvent.DismissAssignment) }) { Text("Cerrar") } }
        )
    }
}

@Composable
private fun ClientesBottomBar(
    isAdmin: Boolean,
    onNavigateHome: () -> Unit,
    onNavigateLoans: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateRoutes: () -> Unit
) {
    RoleBottomBar(
        isAdmin = isAdmin,
        selectedTab = PrimaryTab.CLIENTS,
        onHome = onNavigateHome,
        onClients = {},
        onLoans = onNavigateLoans,
        onRoutes = onNavigateRoutes,
        onProfile = onNavigateProfile
    )
}

@Composable
private fun ClienteCard(
    cliente: Cliente,
    enabled: Boolean,
    isAdmin: Boolean,
    canCreateLoans: Boolean = false,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
    onAssign: () -> Unit,
    onNewLoan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ClientesOutline.copy(alpha = 0.75f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                Text(
                    text = if (cliente.isActive) "ACTIVO" else "INACTIVO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (cliente.isActive) ClientesGreen else ClientesTextSecondary,
                    modifier = Modifier
                        .background(
                            if (cliente.isActive) Color(0xFFE8F5E9) else Color(0xFFECEFF1),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            ClienteInfoRow(Icons.Outlined.Badge, "Cédula", cliente.dni)
            ClienteInfoRow(Icons.Outlined.Phone, "Teléfono", cliente.phone)
            ClienteInfoRow(
                Icons.Outlined.LocationOn,
                "Dirección",
                cliente.address.ifBlank { "No registrada" }
            )
            ClienteInfoRow(Icons.Outlined.LocationOn, "Zona", cliente.zone)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ClientesOutline.copy(alpha = 0.45f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Modificar")
                }
                OutlinedButton(
                    onClick = onDeactivate,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ClientesError),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ClientesError)
                ) {
                    Icon(Icons.Outlined.PersonOff, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (cliente.isActive) "Desactivar" else "Inactivo")
                }
            }
            if (cliente.isActive && (isAdmin || canCreateLoans)) {
                if (isAdmin) {
                    OutlinedButton(onClick = onAssign, modifier = Modifier.fillMaxWidth(), enabled = enabled) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp)); Text("Asignar a empleado")
                    }
                }
                Button(
                    onClick = onNewLoan,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    colors = ButtonDefaults.buttonColors(containerColor = ClientesGreen)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp)); Text("Nuevo préstamo / ampliar capital")
                }
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
            modifier = modifier.background(Color(0xFFD3E4FE)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cliente.initials(),
                fontWeight = FontWeight.Bold,
                color = ClientesPrimary
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
    errorMessage: String?,
    onEvent: (ClientesUiEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onEvent(ClientesUiEvent.DismissEditor) },
        title = { Text("Modificar cliente") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = editor.fullName,
                    onValueChange = { onEvent(ClientesUiEvent.EditorNameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    supportingText = { Text("${editor.fullName.length}/80 caracteres") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = editor.dni,
                    onValueChange = { onEvent(ClientesUiEvent.EditorDniChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Cédula") },
                    supportingText = { Text("${editor.dni.length}/11 dígitos") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = editor.phone,
                    onValueChange = { onEvent(ClientesUiEvent.EditorPhoneChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Teléfono") },
                    supportingText = { Text("${editor.phone.length}/10 dígitos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
                OutlinedTextField(
                    value = editor.address,
                    onValueChange = { onEvent(ClientesUiEvent.EditorAddressChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Dirección") },
                    supportingText = { Text("${editor.address.length}/160 caracteres") },
                    minLines = 2,
                    maxLines = 3
                )
                Text("Zona de cobro", fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Zona Norte", "Zona Sur", "Zona Este").forEach { zone ->
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
                errorMessage?.let { message ->
                    Text(
                        text = message,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ClientesError
                    )
                }
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
                        strokeWidth = 2.dp,
                        color = Color.White
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
