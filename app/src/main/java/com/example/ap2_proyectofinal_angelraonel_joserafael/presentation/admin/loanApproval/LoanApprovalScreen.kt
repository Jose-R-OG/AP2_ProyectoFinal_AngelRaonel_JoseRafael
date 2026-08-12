package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApproval

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ap2_proyectofinal_angelraonel_joserafael.data.local.prestamo.PrestamoEntity
import java.util.Locale
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatusHistory
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryColor = Color(0xFF000000)
private val OnSurfaceVariant = Color(0xFF30323A)
private val SecondaryGreen = Color(0xFF006C49)
private val SecondaryContainer = Color(0xFF6CF8BB)
private val OnSecondaryContainer = Color(0xFF00714D)
private val OutlineVariant = Color(0xFFC6C6CD)
private val SurfaceContainerLowest = Color(0xFFFFFFFF)
private val SurfaceContainerLow = Color(0xFFEFF4FF)
private val SurfaceContainerHigh = Color(0xFFDCE9FF)
private val OnTertiaryContainer = Color(0xFF3980F4)
private val ErrorColor = Color(0xFFBA1A1A)

@Composable
fun LoanApprovalScreen(
    viewModel: LoanApprovalViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onHome: () -> Unit = {},
    onClients: () -> Unit = {},
    onRoutes: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanApprovalContent(
        uiState = uiState,
        onEvent = { viewModel.onEvent(it) },
        onBackClick = onBackClick,
        onHome = onHome,
        onClients = onClients,
        onRoutes = onRoutes,
        onProfile = onProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApprovalContent(
    uiState: LoanApprovalUiState,
    onEvent: (LoanApprovalUiEvent) -> Unit,
    onBackClick: () -> Unit,
    onHome: () -> Unit = {},
    onClients: () -> Unit = {},
    onRoutes: () -> Unit = {},
    onProfile: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TacoBrao", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryColor)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = true,
                selectedTab = PrimaryTab.LOANS,
                onHome = onHome,
                onClients = onClients,
                onLoans = {},
                onRoutes = onRoutes,
                onProfile = onProfile
            )
        },
        containerColor = SurfaceColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "GESTIÓN DE PRÉSTAMOS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnTertiaryContainer,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Préstamos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        }
                        Text(
                            text = "Consulta préstamos activos, rechazados y solicitudes en espera.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoanListTab.entries.forEach { tab ->
                            FilterChip(
                                selected = uiState.selectedTab == tab,
                                onClick = { onEvent(LoanApprovalUiEvent.SelectTab(tab)) },
                                label = {
                                    Text(
                                        when (tab) {
                                            LoanListTab.ACTIVOS -> "Activos"
                                            LoanListTab.RECHAZADOS -> "Rechazados"
                                            LoanListTab.EN_ESPERA -> "En espera"
                                        },
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BentoStatCard("Total pendientes", uiState.totalPendingCount.toString(), PrimaryColor, Modifier.weight(1f))
                            BentoStatCard("Volumen solicitado", String.format(Locale.US, "$ %,.0f", uiState.totalRequestedVolume), PrimaryColor, Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BentoStatCard("Interés promedio", "${uiState.avgInterestRate}%", SecondaryGreen, Modifier.weight(1f))
                            BentoStatStatCard("Estado", "OPERATIVO", OnTertiaryContainer, Modifier.weight(1f))
                        }
                    }
                }

                item {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SecondaryGreen)
                        }
                    } else if (uiState.errorMessage != null) {
                        Text(
                            uiState.errorMessage.orEmpty(),
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            color = ErrorColor
                        )
                    } else if (uiState.pendingPrestamos.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Text(
                                when (uiState.selectedTab) {
                                    LoanListTab.ACTIVOS -> "No hay préstamos activos o aprobados."
                                    LoanListTab.RECHAZADOS -> "No hay préstamos rechazados."
                                    LoanListTab.EN_ESPERA -> "No hay solicitudes pendientes de revisión."
                                },
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                color = OnSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.pendingPrestamos.forEach { request ->
                                LoanRequestCompactCard(
                                    request = request,
                                    client = uiState.clientSummaries[request.clienteId],
                                    onDetailClick = { onEvent(LoanApprovalUiEvent.SelectPrestamo(request)) },
                                    onRejectClick = { onEvent(LoanApprovalUiEvent.RejectPrestamo(request)) },
                                    onApproveClick = { onEvent(LoanApprovalUiEvent.ApprovePrestamo(request)) }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mostrando ${uiState.pendingPrestamos.size} de ${uiState.totalPendingCount} solicitudes",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            if (uiState.isDetailOpen && uiState.selectedPrestamo != null) {
                uiState.selectedPrestamo?.let { prestamo ->
                    LoanDetailModal(
                        request = prestamo,
                        client = uiState.clientSummaries[prestamo.clienteId],
                        history = uiState.historyByLoan[prestamo.id].orEmpty(),
                        onClose = { onEvent(LoanApprovalUiEvent.CloseDetail) },
                        onApprove = { onEvent(LoanApprovalUiEvent.ApprovePrestamo(prestamo)) },
                        onReject = { onEvent(LoanApprovalUiEvent.RejectPrestamo(prestamo)) }
                    )
                }
            }

            uiState.ticketParaImprimir?.let { ticketTexto ->
                AlertDialog(
                    onDismissRequest = { onEvent(LoanApprovalUiEvent.DismissTicket) },
                    title = { Text("Pagaré Generado (Térmico)", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("Ticket listo para enviar a la impresora portátil Bluetooth:")
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = ticketTexto,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = Color.Black
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { onEvent(LoanApprovalUiEvent.PrintTicket) },
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Imprimir Ticket")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { onEvent(LoanApprovalUiEvent.DismissTicket) }) {
                            Text("Cerrar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BentoStatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
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
fun BentoStatStatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    BentoStatCard(title, value, valueColor, modifier)
}

@Composable
private fun LoanRequestCompactCard(
    request: PrestamoEntity,
    client: LoanClientSummary?,
    onDetailClick: () -> Unit,
    onRejectClick: () -> Unit,
    onApproveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onDetailClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(client?.name ?: "Cliente #${request.clienteId}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Registrado por empleado #${request.empleadoId}", fontSize = 12.sp, color = OnSurfaceVariant)
                }
                Text(
                    String.format(Locale.US, "RD$ %,.2f", request.montoSolicitado),
                    fontWeight = FontWeight.Bold,
                    color = SecondaryGreen
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${request.cantidadCuotas} cuotas · ${request.frecuenciaPago.name.lowercase()}", fontSize = 12.sp)
                Text("${request.porcentajeInteres}% interés", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            request.diaPagoDescripcion?.let { Text("Día elegido: $it", fontSize = 12.sp, color = OnSurfaceVariant) }
            OutlinedButton(onClick = onDetailClick, modifier = Modifier.fillMaxWidth()) { Text("Ver expediente") }
            Text("Estado: ${request.estado.displayName()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            if (request.estado == LoanStatus.PENDIENTE_REVISION) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorColor)
                    ) { Text("Rechazar") }
                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                    ) { Text("Aprobar") }
                }
            }
        }
    }
}

@Composable
fun LoanDetailModal(
    request: PrestamoEntity,
    client: LoanClientSummary?,
    history: List<LoanStatusHistory>,
    onClose: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClose() },
            contentAlignment = Alignment.CenterEnd
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 450.dp)
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) {},
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Detalle de Solicitud", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            IconButton(onClick = onClose) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = OnSurfaceVariant)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    client?.name?.split(" ")?.mapNotNull { it.firstOrNull() }
                                        ?.joinToString("")?.take(2)?.uppercase() ?: "CL",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSecondaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(client?.name ?: "Cliente #${request.clienteId}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                Text("Cédula: ${client?.dni?.ifBlank { "No registrada" } ?: "No registrada"}", fontSize = 13.sp, color = OnSurfaceVariant)
                                Text(client?.phone?.ifBlank { "Teléfono no registrado" } ?: "Teléfono no registrado", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        client?.profilePhotoPath?.let { path ->
                            AsyncImage(
                                model = if (path.startsWith("/")) File(path) else path,
                                contentDescription = "Foto del cliente",
                                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("CONFIGURACIÓN DEL PRÉSTAMO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Monto", fontSize = 11.sp, color = OnSurfaceVariant)
                                        Text("$${String.format(Locale.US, "%,.2f", request.montoSolicitado)}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text("Tasa de Interés", fontSize = 11.sp, color = OnSurfaceVariant)
                                        Text("${request.porcentajeInteres}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Duración", fontSize = 11.sp, color = OnSurfaceVariant)
                                        Text("${request.cantidadCuotas} Cuotas", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Monto por cuota", fontSize = 11.sp, color = OnSurfaceVariant)
                                        Text("$${request.montoCuota}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("METADATOS DE APLICACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                            MetadataRow("Agente Cobrador", "Empleado #${request.empleadoId}")
                            MetadataRow(
                                "Fecha de envío",
                                java.text.SimpleDateFormat("dd/MM/yyyy", Locale("es", "DO"))
                                    .format(java.util.Date(request.fechaCreacion))
                            )
                            MetadataRow("Dirección", client?.address?.ifBlank { "No registrada" } ?: "No registrada")
                            MetadataRow("Zona", client?.zone?.ifBlank { "Sin asignar" } ?: "Sin asignar")
                            MetadataRow("Día de pago", request.diaPagoDescripcion ?: "Según frecuencia")
                            MetadataRow("Estado", request.estado.displayName())
                        }

                        if (client?.dniFrontPhotoPath != null || client?.dniBackPhotoPath != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("DOCUMENTOS DEL CLIENTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(client.dniFrontPhotoPath, client.dniBackPhotoPath).forEachIndexed { index, path ->
                                    path?.let {
                                        AsyncImage(
                                            model = if (it.startsWith("/")) File(it) else it,
                                            contentDescription = if (index == 0) "Cédula frontal" else "Cédula posterior",
                                            modifier = Modifier.weight(1f).height(110.dp).clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("HISTORIAL DE DECISIONES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                        history.sortedByDescending { it.changedAt }.forEach { item ->
                            MetadataRow(
                                item.status.displayName(),
                                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "DO")).format(Date(item.changedAt))
                            )
                            Text("Registrado por usuario #${item.changedByUserId}", fontSize = 11.sp, color = OnSurfaceVariant)
                            item.note?.let { Text(it, fontSize = 12.sp, color = OnSurfaceVariant) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("NOTAS INTERNAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("\"${request.motivoRechazo ?: "Sin observaciones"}\"", fontSize = 12.sp, color = PrimaryColor)
                            }
                        }
                    }

                    if (request.estado == LoanStatus.PENDIENTE_REVISION) Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onReject,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorColor.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Rechazar", color = ErrorColor, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onApprove,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aprobar Préstamo", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private fun LoanStatus.displayName(): String = when (this) {
    LoanStatus.BORRADOR -> "Borrador"
    LoanStatus.PENDIENTE_REVISION -> "En revisión"
    LoanStatus.APROBADO -> "Aprobado · pendiente de firma"
    LoanStatus.RECHAZADO -> "Rechazado"
    LoanStatus.ACTIVO -> "Activo"
    LoanStatus.FINALIZADO -> "Finalizado"
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = OnSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
    }
}
