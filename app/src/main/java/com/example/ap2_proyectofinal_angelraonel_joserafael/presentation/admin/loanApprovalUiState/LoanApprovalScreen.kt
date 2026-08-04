package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.loanApprovalUiState

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

// Paleta Equity Flow
private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryColor = Color(0xFF000000)
private val OnSurfaceVariant = Color(0xFF45464D)
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
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoanApprovalContent(
        uiState = uiState,
        onEvent = { viewModel.onEvent(it) },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApprovalContent(
    uiState: LoanApprovalUiState,
    onEvent: (LoanApprovalUiEvent) -> Unit,
    onBackClick: () -> Unit
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
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
                // Header
                item {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = "COLA DE REVISIÓN",
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
                            Text("Solicitudes Pendientes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Filtros", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Exportar CSV", fontSize = 12.sp)
                                }
                            }
                        }
                        Text(
                            text = "Revisa y modera las solicitudes de préstamo enviadas por los agentes de campo.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Bento Dashboard Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BentoStatCard("Total Pendientes", uiState.totalPendingCount.toString(), PrimaryColor, Modifier.weight(1f))
                        BentoStatCard("Vol. Solicitado", String.format(Locale.US, "$ %,.0f", uiState.totalRequestedVolume), PrimaryColor, Modifier.weight(1f))
                        BentoStatCard("Interes Prom.", "${uiState.avgInterestRate}%", SecondaryGreen, Modifier.weight(1f))
                        BentoStatCard("Estado Sistema", "OK", OnTertiaryContainer, Modifier.weight(1f))
                    }
                }

                // Table / Card List Container
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainerLow)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("CLIENTE Y EMPLEADO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, modifier = Modifier.weight(2f))
                                Text("MONTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, modifier = Modifier.weight(1f))
                                Text("TÉRMINOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, modifier = Modifier.weight(1f))
                                Text("ACCIONES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, modifier = Modifier.weight(1.5f))
                            }

                            uiState.pendingPrestamos.forEachIndexed { index, request ->
                                LoanRequestRow(
                                    request = request,
                                    onDetailClick = { onEvent(LoanApprovalUiEvent.SelectPrestamo(request)) },
                                    onRejectClick = { onEvent(LoanApprovalUiEvent.RejectPrestamo(request)) },
                                    onApproveClick = { onEvent(LoanApprovalUiEvent.ApprovePrestamo(request)) }
                                )
                                if (index < uiState.pendingPrestamos.lastIndex) {
                                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }

                // Footer Pagination
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
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            }
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(32.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("1", fontSize = 12.sp)
                            }
                            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }

            // MODAL DETALLE DE SOLICITUD
            if (uiState.isDetailOpen && uiState.selectedPrestamo != null) {
                uiState.selectedPrestamo?.let { prestamo ->
                    LoanDetailModal(
                        request = prestamo,
                        onClose = { onEvent(LoanApprovalUiEvent.CloseDetail) },
                        onApprove = { onEvent(LoanApprovalUiEvent.ApprovePrestamo(prestamo)) },
                        onReject = { onEvent(LoanApprovalUiEvent.RejectPrestamo(prestamo)) }
                    )
                }
            }

            // Diálogo emergente de vista previa del Ticket Térmico
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
fun LoanRequestRow(
    request: PrestamoEntity,
    onDetailClick: () -> Unit,
    onRejectClick: () -> Unit,
    onApproveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SecondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CL",
                    color = OnSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Cliente #${request.clienteId}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryColor)
                Text("Enviado por: #${request.empleadoId}", fontSize = 12.sp, color = OnSurfaceVariant)
            }
        }

        Text(
            text = "$${String.format(Locale.US, "%,.2f", request.montoSolicitado)}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = PrimaryColor,
            modifier = Modifier.weight(1f)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text("${request.cantidadCuotas} Cuotas", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("@ ${request.porcentajeInteres}%", fontSize = 12.sp, color = SecondaryGreen, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.weight(1.5f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onDetailClick,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = SurfaceContainerHigh),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Detalles", fontSize = 11.sp, color = PrimaryColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = onRejectClick,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Rechazar", fontSize = 11.sp, color = ErrorColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = onApproveClick,
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Aprobar", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun LoanDetailModal(
    request: PrestamoEntity,
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
                    Column {
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
                                Text("CL", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OnSecondaryContainer)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Cliente #${request.clienteId}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                Text("Score Crediticio: ", fontSize = 13.sp, color = OnSurfaceVariant)
                                Text("850 (EXCELENTE)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

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
                                    Column {
                                        Text("Pago Mensual Estimado", fontSize = 11.sp, color = OnSurfaceVariant)
                                        Text("$${request.montoCuota}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("METADATOS DE APLICACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant)
                            MetadataRow("Agente Cobrador", "Empleado #${request.empleadoId}")
                            MetadataRow("Fecha de Envío", "Reciente")
                            MetadataRow("Nivel de Riesgo", "BAJO")
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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