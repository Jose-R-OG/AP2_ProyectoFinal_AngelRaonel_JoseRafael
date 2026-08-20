package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cobro

import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.FuturoBadgeBg
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.FuturoBadgeText
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.LightBlueBadgeBg
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.LightBlueBadgeText
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.OnSurfaceVariant
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.OutlineVariant
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.PagadoBadgeBg
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.PagadoBadgeText
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.PendienteBadgeBg
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.PendienteBadgeText
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.PrimaryBlack
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.SecondaryGreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.SurfaceColor
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.VencidoBadgeBg
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.VencidoBadgeText
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.DigitalSignaturePad
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceipt
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceiptManager
import java.util.Locale

private val TealActionButtonBg = Color(0xFF67B59F)

data class DetalleCobroCallbacks(
    val onNavigateBack: () -> Unit = {},
    val onNavigateHome: () -> Unit = {},
    val onNavigateToClients: () -> Unit = {},
    val onNavigateToLoans: () -> Unit = {},
    val onNavigateToRoutes: () -> Unit = {},
    val onNavigateToProfile: () -> Unit = {},
    val onNavigateToRegisterClient: (Long) -> Unit = {}
)

@Composable
fun DetallePrestamoCobroScreen(
    isAdmin: Boolean,
    callbacks: DetalleCobroCallbacks,
    viewModel: DetallePrestamoCobroViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DetallePrestamoCobroContent(
        isAdmin = isAdmin,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        callbacks = callbacks
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePrestamoCobroContent(
    isAdmin: Boolean,
    uiState: DetallePrestamoCobroUiState,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit,
    callbacks: DetalleCobroCallbacks
) {
    val context = LocalContext.current
    var showSignaturePad by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.generatedReceipt?.receiptNumber) {
        if (uiState.generatedReceipt != null) {
            onEvent(DetallePrestamoCobroUiEvent.PrintReceipt)
        }
    }

    Scaffold(
        topBar = {
            DetalleCobroTopBar(
                canCreateLoans = uiState.canCreateLoans,
                clientId = uiState.clientId,
                onBack = callbacks.onNavigateBack,
                onProfile = callbacks.onNavigateToProfile,
                onRegisterClient = callbacks.onNavigateToRegisterClient
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = isAdmin,
                selectedTab = null,
                onHome = callbacks.onNavigateHome,
                onClients = callbacks.onNavigateToClients,
                onLoans = callbacks.onNavigateToLoans,
                onRoutes = callbacks.onNavigateToRoutes,
                onProfile = callbacks.onNavigateToProfile
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
            item { BackToClientsHeader(callbacks.onNavigateBack) }
            item { LoanHeaderInfo(uiState) }
            item { LoanBalanceCard(uiState) }
            item { LoanInfoCardsRow(uiState) }
            item {
                PaymentActionsSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }
            item { PaymentPlanHeader(uiState.totalPlanFormatted) }
            items(uiState.cuotasList, key = { it.id }) { cuota ->
                CuotaCardRow(
                    cuota = cuota,
                    onToggleSelect = { if (uiState.isPayable) onEvent(DetallePrestamoCobroUiEvent.ToggleSelectCuota(cuota.id)) }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    ReceiptDialog(uiState.generatedReceipt, context, onEvent, { showSignaturePad = true })
    
    SignaturePadDialog(showSignaturePad, uiState.generatedReceipt, context, onEvent, { showSignaturePad = false })

    ErrorMessageDialog(uiState.errorMessage, onEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalleCobroTopBar(
    canCreateLoans: Boolean,
    clientId: Long,
    onBack: () -> Unit,
    onProfile: () -> Unit,
    onRegisterClient: (Long) -> Unit
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "TaCobraoApp",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = PrimaryBlack
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Menu, contentDescription = "Menú", tint = PrimaryBlack)
            }
        },
        actions = {
            if (canCreateLoans) {
                IconButton(onClick = { onRegisterClient(clientId) }) {
                    Icon(Icons.Default.Add, contentDescription = "Ampliar capital", tint = SecondaryGreen)
                }
            }
            IconButton(onClick = onProfile) {
                Icon(Icons.Default.AccountCircle, contentDescription = "Perfil", tint = PrimaryBlack)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
    )
}

@Composable
private fun BackToClientsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onBack() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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

@Composable
private fun LoanHeaderInfo(uiState: DetallePrestamoCobroUiState) {
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

@Composable
private fun LoanBalanceCard(uiState: DetallePrestamoCobroUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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

@Composable
private fun LoanInfoCardsRow(uiState: DetallePrestamoCobroUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoCard(
            title = "Monto Original",
            value = uiState.originalAmountFormatted,
            icon = Icons.Default.Receipt,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            title = "% Tasa de Interés",
            value = uiState.interestRateText,
            icon = Icons.Default.Percent,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InfoCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(title, fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
        }
    }
}

@Composable
private fun PaymentActionsSection(
    uiState: DetallePrestamoCobroUiState,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit
) {
    if (uiState.isPayable) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PaymentMethodSelector(uiState.paymentMethod, onEvent)
            PaymentButton(uiState, onEvent)
        }
    } else {
        UnpayableWarningCard(uiState.statusText)
    }
}

@Composable
private fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit
) {
    Column {
        Text("Método de pago", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaymentMethodButton(
                text = "Efectivo",
                selected = selectedMethod == PaymentMethod.EFECTIVO,
                modifier = Modifier.weight(1f)
            ) { onEvent(DetallePrestamoCobroUiEvent.PaymentMethodChanged(PaymentMethod.EFECTIVO)) }
            PaymentMethodButton(
                text = "Transferencia",
                selected = selectedMethod == PaymentMethod.TRANSFERENCIA,
                modifier = Modifier.weight(1f)
            ) { onEvent(DetallePrestamoCobroUiEvent.PaymentMethodChanged(PaymentMethod.TRANSFERENCIA)) }
        }
    }
}

@Composable
private fun PaymentButton(
    uiState: DetallePrestamoCobroUiState,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit
) {
    Column {
        Button(
            onClick = { onEvent(DetallePrestamoCobroUiEvent.RealizarCobroSeleccionado) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (uiState.selectedCountError != null) MaterialTheme.colorScheme.error else TealActionButtonBg),
            enabled = !uiState.isProcessingPayment
        ) {
            if (uiState.isProcessingPayment) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (uiState.selectedCount == 0) "Seleccione cuotas a pagar" else "Cobrar ${uiState.selectedCount} cuota(s)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        uiState.selectedCountError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun UnpayableWarningCard(statusText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Este préstamo está en estado $statusText. Debe ser aprobado por un administrador antes de poder realizar cobros.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PaymentPlanHeader(totalPlanFormatted: String) {
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
                text = totalPlanFormatted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LightBlueBadgeText,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ReceiptDialog(
    receipt: PaymentReceipt?,
    context: Context,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit,
    onOpenSignature: () -> Unit
) {
    receipt?.let { r ->
        AlertDialog(
            onDismissRequest = { onEvent(DetallePrestamoCobroUiEvent.DismissReceipt) },
            title = { Text(if (r.debtPaidOff) "¡Deuda saldada!" else "Comprobante generado") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (r.debtPaidOff) "Todas las cuotas fueron pagadas. Se generó la constancia de deuda saldada." else "El pago se guardó correctamente.")
                    Text("Cuota: ${r.installmentLabel}", fontWeight = FontWeight.Bold)
                    Text("Pagos restantes: ${r.remainingInstallments}")
                    Text("Saldo: ${String.format(Locale.US, "RD$ %,.2f", r.remainingBalance)}")
                    Text(if (r.signaturePath == null) "Puedes agregar la firma digital antes de reimprimir o compartir." else "Firma digital guardada.")
                }
            },
            confirmButton = {
                Button(onClick = { onEvent(DetallePrestamoCobroUiEvent.PrintReceipt) }) { Text("Imprimir") }
            },
            dismissButton = {
                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = onOpenSignature) { Text(if (r.signaturePath == null) "Firmar recibo" else "Reemplazar firma") }
                    TextButton(onClick = { PaymentReceiptManager.shareWhatsApp(context, r) }) { Text("Enviar por WhatsApp") }
                    TextButton(onClick = { onEvent(DetallePrestamoCobroUiEvent.DismissReceipt) }) { Text("Cerrar") }
                }
            }
        )
    }
}

@Composable
private fun SignaturePadDialog(
    show: Boolean,
    receipt: PaymentReceipt?,
    context: Context,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit,
    onDismiss: () -> Unit
) {
    if (show && receipt != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Firma del comprobante") },
            text = {
                DigitalSignaturePad { bitmap ->
                    val path = PaymentReceiptManager.saveSignature(context, receipt.receiptNumber, bitmap)
                    onEvent(DetallePrestamoCobroUiEvent.ReceiptSigned(path))
                    onDismiss()
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ErrorMessageDialog(
    errorMessage: String?,
    onEvent: (DetallePrestamoCobroUiEvent) -> Unit
) {
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { onEvent(DetallePrestamoCobroUiEvent.ClearError) },
            title = { Text("No fue posible completar el cobro") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { onEvent(DetallePrestamoCobroUiEvent.ClearError) }) {
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
    val isPagado = cuota.status == CuotaStatus.PAGADO
    
    val badgeBg = when (cuota.status) {
        CuotaStatus.PAGADO -> PagadoBadgeBg
        CuotaStatus.VENCIDO -> VencidoBadgeBg
        CuotaStatus.PENDIENTE -> PendienteBadgeBg
        CuotaStatus.FUTURO -> FuturoBadgeBg
    }
    val badgeText = when (cuota.status) {
        CuotaStatus.PAGADO -> "✔ Pagado"
        CuotaStatus.VENCIDO -> "⚠ Vencido"
        CuotaStatus.PENDIENTE -> "Pendiente"
        CuotaStatus.FUTURO -> "Futuro"
    }
    val badgeTextColor = when (cuota.status) {
        CuotaStatus.PAGADO -> PagadoBadgeText
        CuotaStatus.VENCIDO -> VencidoBadgeText
        CuotaStatus.PENDIENTE -> PendienteBadgeText
        CuotaStatus.FUTURO -> FuturoBadgeText
    }
    val selectionColor = if (isVencido) VencidoBadgeText else SecondaryGreen

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isPagado) { onToggleSelect() },
        shape = RoundedCornerShape(12.dp),
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
                CuotaStatusHeader(cuota.numeroCuota, badgeBg, badgeText, badgeTextColor)
                Spacer(modifier = Modifier.height(4.dp))
                CuotaDateInfo(cuota.fechaDue, cuota.atrasoDaysText)
            }

            CuotaPaymentInfo(cuota, isPagado, selectionColor, onToggleSelect)
        }
    }
}

@Composable
private fun CuotaStatusHeader(
    label: String,
    badgeBg: Color,
    badgeText: String,
    badgeTextColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlack
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(color = badgeBg, shape = RoundedCornerShape(4.dp)) {
            Text(
                text = badgeText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun CuotaDateInfo(fechaDue: String, atrasoDaysText: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = fechaDue, fontSize = 12.sp, color = OnSurfaceVariant)
        if (atrasoDaysText != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = atrasoDaysText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VencidoBadgeText
            )
        }
    }
}

@Composable
private fun CuotaPaymentInfo(
    cuota: CuotaItemState,
    isPagado: Boolean,
    selectionColor: Color,
    onToggleSelect: () -> Unit
) {
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

        if (isPagado) {
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
                    selectedColor = selectionColor,
                    unselectedColor = OutlineVariant
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetallePrestamoCobroPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        DetallePrestamoCobroContent(
            isAdmin = true,
            uiState = DetallePrestamoCobroUiState(
                prestamoCode = "#PT-1001",
                statusText = "ACTIVO",
                clientName = "Juan Pérez",
                pendingBalanceFormatted = "$5,000.00",
                percentagePaidText = "50% Pagado",
                cuotasProgressText = "5 de 10 cuotas",
                originalAmountFormatted = "$10,000.00",
                interestRateText = "10% (tasa total)",
                totalPlanFormatted = "Total: $11,000.00",
                progress = 0.5f,
                selectedCount = 1,
                isPayable = true,
                cuotasList = listOf(
                    CuotaItemState(
                        id = 1,
                        numeroCuota = "Cuota 6/10",
                        fechaDue = "15 Oct 2023",
                        montoFormatted = "$1,100.00",
                        status = CuotaStatus.PENDIENTE,
                        isSelected = true
                    ),
                    CuotaItemState(
                        id = 2,
                        numeroCuota = "Cuota 7/10",
                        fechaDue = "15 Nov 2023",
                        montoFormatted = "$1,100.00",
                        status = CuotaStatus.FUTURO
                    )
                )
            ),
            onEvent = {},
            callbacks = DetalleCobroCallbacks()
        )
    }
}
