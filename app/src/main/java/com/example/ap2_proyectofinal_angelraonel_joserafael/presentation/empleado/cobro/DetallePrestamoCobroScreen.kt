package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceiptManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.DigitalSignaturePad

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryGreen = Color(0xFF006C49)
private val TealActionButtonBg = Color(0xFF67B59F)
private val LightBlueBadgeBg = Color(0xFFDCE9FF)
private val LightBlueBadgeText = Color(0xFF1565C0)
private val OnSurfaceVariant = Color(0xFF30323A)
private val OutlineVariant = Color(0xFFC6C6CD)

private val PagadoBadgeBg = Color(0xFF6CF8BB).copy(alpha = 0.4f)
private val PagadoBadgeText = Color(0xFF00714D)
private val VencidoBadgeBg = Color(0xFFFFDAD6)
private val VencidoBadgeText = Color(0xFFBA1A1A)
private val PendienteBadgeBg = Color(0xFFE3EEFF)
private val PendienteBadgeText = Color(0xFF1976D2)
private val FuturoBadgeBg = Color(0xFFEEEEEE)
private val FuturoBadgeText = Color(0xFF616161)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePrestamoCobroScreen(
    isAdmin: Boolean,
    onNavigateBack: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onNavigateToLoans: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRegisterClient: (Long) -> Unit = {},
    viewModel: DetallePrestamoCobroViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSignaturePad by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.generatedReceipt?.receiptNumber) {
        if (uiState.generatedReceipt != null) {
            viewModel.onEvent(DetallePrestamoCobroUiEvent.PrintReceipt)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "TacoBraoApp",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = PrimaryBlack
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = PrimaryBlack)
                    }
                },
                actions = {
                    if (uiState.canCreateLoans) {
                        IconButton(onClick = { onNavigateToRegisterClient(uiState.clientId) }) {
                            Icon(Icons.Default.Add, contentDescription = "Ampliar capital", tint = SecondaryGreen)
                        }
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = PrimaryBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = isAdmin,
                selectedTab = null,
                onHome = onNavigateHome,
                onClients = onNavigateToClients,
                onLoans = onNavigateToLoans,
                onRoutes = onNavigateToRoutes,
                onProfile = onNavigateToProfile
            )
        },
        containerColor = SurfaceColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subheader: Volver a Clientes
            item {
                Row(
                    modifier = Modifier
                        .clickable { onNavigateBack() }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Volver a Clientes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Título de Préstamo y Cliente
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Préstamo ${uiState.prestamoCode}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack
                        )
                        Surface(
                            color = LightBlueBadgeBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = uiState.statusText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightBlueBadgeText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Cliente: ${uiState.clientName}",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Card 1: Saldo Pendiente
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Saldo Pendiente",
                            fontSize = 13.sp,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.pendingBalanceFormatted,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.percentagePaidText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryGreen
                            )
                            Text(
                                text = uiState.cuotasProgressText,
                                fontSize = 12.sp,
                                color = OnSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { uiState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = SecondaryGreen,
                            trackColor = Color(0xFFE0E0E0)
                        )
                    }
                }
            }

            // Fila 2 Cards: Monto Original y Tasa de Interés
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card Monto Original
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Monto Original", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(uiState.originalAmountFormatted, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                        }
                    }

                    // Card Tasa de Interés
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Percent, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("% Tasa de Interés", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(uiState.interestRateText, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                        }
                    }
                }
            }

            item {
                Column {
                    Text("Método de pago", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethodButton(
                            text = "Efectivo",
                            selected = uiState.paymentMethod == PaymentMethod.EFECTIVO,
                            modifier = Modifier.weight(1f)
                        ) { viewModel.onEvent(DetallePrestamoCobroUiEvent.PaymentMethodChanged(PaymentMethod.EFECTIVO)) }
                        PaymentMethodButton(
                            text = "Transferencia",
                            selected = uiState.paymentMethod == PaymentMethod.TRANSFERENCIA,
                            modifier = Modifier.weight(1f)
                        ) { viewModel.onEvent(DetallePrestamoCobroUiEvent.PaymentMethodChanged(PaymentMethod.TRANSFERENCIA)) }
                    }
                }
            }

            // Botón Realizar Cobro Seleccionado
            item {
                Button(
                    onClick = { viewModel.onEvent(DetallePrestamoCobroUiEvent.RealizarCobroSeleccionado) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealActionButtonBg),
                    enabled = !uiState.isProcessingPayment && uiState.selectedCount > 0
                ) {
                    if (uiState.isProcessingPayment) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (uiState.selectedCount == 0) "Seleccione cuotas a pagar" else "Cobrar ${uiState.selectedCount} cuota(s)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Header Plan de Pagos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Plan de Pagos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                    Surface(
                        color = LightBlueBadgeBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = uiState.totalPlanFormatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightBlueBadgeText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Lista de Cuotas
            items(uiState.cuotasList, key = { it.id }) { cuota ->
                CuotaCardRow(
                    cuota = cuota,
                    onToggleSelect = { viewModel.onEvent(DetallePrestamoCobroUiEvent.ToggleSelectCuota(cuota.id)) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    uiState.generatedReceipt?.let { receipt ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(DetallePrestamoCobroUiEvent.DismissReceipt) },
            title = { Text(if (receipt.debtPaidOff) "¡Deuda saldada!" else "Comprobante generado") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(if (receipt.debtPaidOff) "Todas las cuotas fueron pagadas. Se generó la constancia de deuda saldada." else "El pago se guardó correctamente.")
                Text("Cuota: ${receipt.installmentLabel}", fontWeight = FontWeight.Bold)
                Text("Pagos restantes: ${receipt.remainingInstallments}")
                Text("Saldo: ${String.format(java.util.Locale.US, "RD$ %,.2f", receipt.remainingBalance)}")
                Text(if (receipt.signaturePath == null) "Puedes agregar la firma digital antes de reimprimir o compartir." else "Firma digital guardada.")
            } },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(DetallePrestamoCobroUiEvent.PrintReceipt) }) { Text("Imprimir") }
            },
            dismissButton = {
                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = { showSignaturePad = true }) { Text(if (receipt.signaturePath == null) "Firmar recibo" else "Reemplazar firma") }
                    TextButton(onClick = { PaymentReceiptManager.shareWhatsApp(context, receipt) }) { Text("Enviar por WhatsApp") }
                    TextButton(onClick = { viewModel.onEvent(DetallePrestamoCobroUiEvent.DismissReceipt) }) { Text("Cerrar") }
                }
            }
        )
    }
    if (showSignaturePad && uiState.generatedReceipt != null) {
        val receipt = uiState.generatedReceipt!!
        AlertDialog(
            onDismissRequest = { showSignaturePad = false },
            title = { Text("Firma del comprobante") },
            text = {
                DigitalSignaturePad { bitmap ->
                    val path = PaymentReceiptManager.saveSignature(context, receipt.receiptNumber, bitmap)
                    viewModel.onEvent(DetallePrestamoCobroUiEvent.ReceiptSigned(path))
                    showSignaturePad = false
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showSignaturePad = false }) { Text("Cancelar") } }
        )
    }
    uiState.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(DetallePrestamoCobroUiEvent.ClearError) },
            title = { Text("No fue posible completar el cobro") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(DetallePrestamoCobroUiEvent.ClearError) }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun PaymentMethodButton(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
        ) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(text) }
    }
}

@Composable
private fun CuotaCardRow(
    cuota: CuotaItemState,
    onToggleSelect: () -> Unit
) {
    val isVencido = cuota.status == CuotaStatus.VENCIDO

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = cuota.status != CuotaStatus.PAGADO) { onToggleSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isVencido) VencidoBadgeText else OutlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cuota.numeroCuota,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = when (cuota.status) {
                            CuotaStatus.PAGADO -> PagadoBadgeBg
                            CuotaStatus.VENCIDO -> VencidoBadgeBg
                            CuotaStatus.PENDIENTE -> PendienteBadgeBg
                            CuotaStatus.FUTURO -> FuturoBadgeBg
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = when (cuota.status) {
                                CuotaStatus.PAGADO -> "✔ Pagado"
                                CuotaStatus.VENCIDO -> "⚠ Vencido"
                                CuotaStatus.PENDIENTE -> "Pendiente"
                                CuotaStatus.FUTURO -> "Futuro"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (cuota.status) {
                                CuotaStatus.PAGADO -> PagadoBadgeText
                                CuotaStatus.VENCIDO -> VencidoBadgeText
                                CuotaStatus.PENDIENTE -> PendienteBadgeText
                                CuotaStatus.FUTURO -> FuturoBadgeText
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cuota.fechaDue,
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                    if (cuota.atrasoDaysText != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = cuota.atrasoDaysText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VencidoBadgeText
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = cuota.montoFormatted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                    if (cuota.moraText != null) {
                        Text(
                            text = cuota.moraText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VencidoBadgeText
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))

                if (cuota.status == CuotaStatus.PAGADO) {
                    Icon(
                        imageVector = Icons.Default.RemoveCircleOutline,
                        contentDescription = "Pagado",
                        tint = OutlineVariant,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    RadioButton(
                        selected = cuota.isSelected,
                        onClick = { onToggleSelect() },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = if (isVencido) VencidoBadgeText else SecondaryGreen,
                            unselectedColor = OutlineVariant
                        )
                    )
                }
            }
        }
    }
}
