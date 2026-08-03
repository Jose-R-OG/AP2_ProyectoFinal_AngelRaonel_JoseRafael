package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.empleado

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryColor = Color(0xFF000000)
private val PrimaryContainer = Color(0xFF131B2E)
private val OnPrimaryContainer = Color(0xFF7C839B)
private val SecondaryGreen = Color(0xFF006C49)
private val OnSurfaceVariant = Color(0xFF45464D)
private val OutlineVariant = Color(0xFFC6C6CD)
private val SurfaceContainerLowest = Color(0xFFFFFFFF)
private val SurfaceVariant = Color(0xFFD3E4FE)
private val OnTertiaryVariant = Color(0xFF004395)
private val ErrorColor = Color(0xFFBA1A1A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeManagementScreen(
    uiState: EmployeeUiState = EmployeeUiState(),
    onOpenModal: () -> Unit = {},
    onCloseModal: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onPhoneChange: (String) -> Unit = {},
    onRouteSelected: (String) -> Unit = {},
    onSaveEmployee: () -> Unit = {},
    onToggleStatus: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Equity Flow", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryColor)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = OnSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        containerColor = SurfaceColor
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Directorio de Empleados", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text("Gestiona los cobradores de campo y sus rutas asignadas.", fontSize = 14.sp, color = OnSurfaceVariant)
                        }
                        Button(
                            onClick = onOpenModal,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nuevo Empleado")
                        }
                    }
                }
            }

            // Tarjetas de Métricas (Dashboard Stats)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard("Total Agentes", uiState.totalAgents.toString(), PrimaryColor, Modifier.weight(1f))
                    MetricCard("Activos Ahora", uiState.activeAgents.toString(), SecondaryGreen, Modifier.weight(1f))
                    MetricCard("Rutas Pendientes", uiState.pendingRoutes.toString(), OnTertiaryVariant, Modifier.weight(1f))
                    MetricCard("Alertas", uiState.alertsCount.toString(), ErrorColor, Modifier.weight(1f))
                }
            }

            // Lista de Empleados
            items(uiState.employees, key = { it.id }) { employee ->
                EmployeeCard(
                    employee = employee,
                    onToggleStatus = { onToggleStatus(employee.id) }
                )
            }

            // Botón/Card para Asignar Nuevo Empleado
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { onOpenModal() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = OutlineVariant, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Asignar nuevo agente\npara expandir cobertura.",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Modal para Agregar Empleado
    if (uiState.isAddModalOpen) {
        AddEmployeeDialog(
            uiState = uiState,
            onCloseModal = onCloseModal,
            onNameChange = onNameChange,
            onPhoneChange = onPhoneChange,
            onRouteSelected = onRouteSelected,
            onSaveEmployee = onSaveEmployee
        )
    }
}

@Composable
private fun MetricCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
private fun EmployeeCard(
    employee: Employee,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = employee.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(employee.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PrimaryColor)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (employee.isActive) SecondaryGreen else OnSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (employee.isActive) "ACTIVO" else "INACTIVO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (employee.isActive) SecondaryGreen else OnSurfaceVariant
                            )
                        }
                    }
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = OnSurfaceVariant)
                }
            }

            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Teléfono", fontSize = 12.sp, color = OnSurfaceVariant)
                }
                Text(employee.phone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryColor)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ruta", fontSize = 12.sp, color = OnSurfaceVariant)
                }
                Surface(
                    color = SurfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = employee.route.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnTertiaryVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(employee.clientsAssigned.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clientes Asignados", fontSize = 11.sp, color = OnSurfaceVariant)
                }

                TextButton(onClick = onToggleStatus) {
                    Text(
                        text = if (employee.isActive) "Desactivar" else "Activar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (employee.isActive) ErrorColor else SecondaryGreen
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEmployeeDialog(
    uiState: EmployeeUiState,
    onCloseModal: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRouteSelected: (String) -> Unit,
    onSaveEmployee: () -> Unit
) {
    var expandedDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onCloseModal) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Agregar Empleado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    IconButton(onClick = onCloseModal) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                OutlinedTextField(
                    value = uiState.newEmployeeName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre Completo") },
                    placeholder = { Text("Ej. Juan Pérez") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.newEmployeePhone,
                    onValueChange = onPhoneChange,
                    label = { Text("Número de Teléfono") },
                    placeholder = { Text("809-000-0000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown }
                ) {
                    OutlinedTextField(
                        value = uiState.newEmployeeRoute.ifBlank { "Seleccionar Ruta" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Asignar Ruta") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        uiState.availableRoutes.forEach { route ->
                            DropdownMenuItem(
                                text = { Text(route) },
                                onClick = {
                                    onRouteSelected(route)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCloseModal,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Cancelar", color = PrimaryColor)
                    }

                    Button(
                        onClick = onSaveEmployee,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}