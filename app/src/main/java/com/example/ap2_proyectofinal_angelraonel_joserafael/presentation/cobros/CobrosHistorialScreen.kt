package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.PaymentMethod
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceipt
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.receipt.PaymentReceiptManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobrosHistorialScreen(
    onBack: () -> Unit,
    viewModel: CobrosHistorialViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selected by remember { mutableStateOf<CobroHistoryItem?>(null) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cobros recientes", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
            uiState.errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(uiState.errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
            }
            uiState.items.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Todavía no se han registrado cobros.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selected = item },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (item.method == PaymentMethod.EFECTIVO) Icons.Default.Payments else Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(item.clientName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Préstamo #${item.loanId} · ${item.method.name.lowercase()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (uiState.isAdmin) Text("Cobrado por ${item.employeeName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(item.dateTime, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(item.amount, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
    selected?.let { item ->
        val receipt = PaymentReceipt(
            receiptNumber = "TB-${item.loanId}-${item.timestamp.toString().takeLast(8)}",
            transactionId = item.id,
            loanId = item.loanId,
            clientName = item.clientName,
            clientDni = item.clientDni,
            employeeName = item.employeeName,
            amount = item.amountValue,
            paymentMethod = item.method.name,
            paidAt = item.timestamp,
            note = item.note,
            installmentLabel = item.installmentLabel,
            totalInstallments = item.totalInstallments,
            remainingInstallments = item.remainingInstallments,
            remainingBalance = item.remainingBalance,
            debtPaidOff = item.debtPaidOff
        )
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("Detalle del cobro #${item.id}", color = MaterialTheme.colorScheme.onSurface) },
            text = { Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.clientName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Préstamo #${item.loanId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Monto: ${item.amount}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Método: ${item.method.name.lowercase()}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Cobrado por: ${item.employeeName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(item.dateTime, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(item.note, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Cuota: ${item.installmentLabel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Pagos restantes: ${item.remainingInstallments}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } },
            confirmButton = { Button(onClick = { viewModel.imprimir(receipt) }) { Text("Reimprimir") } },
            dismissButton = { Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = { PaymentReceiptManager.shareWhatsApp(context, receipt) }) { Text("Enviar WhatsApp") }
                TextButton(onClick = { selected = null }) { Text("Cerrar") }
            } }
        )
    }
}
