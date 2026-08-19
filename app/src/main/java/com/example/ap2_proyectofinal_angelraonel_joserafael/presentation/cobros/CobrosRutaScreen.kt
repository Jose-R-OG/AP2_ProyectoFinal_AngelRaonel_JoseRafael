package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.LoanStatus
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.*

private val CollectionsSurface @Composable get() = MaterialTheme.colorScheme.surface
private val CollectionsGreen @Composable get() = MaterialTheme.colorScheme.secondary
private val CollectionsOutline @Composable get() = MaterialTheme.colorScheme.outlineVariant
private val CollectionsTextSecondary @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobrosRutaScreen(
    isAdmin: Boolean,
    routeOnly: Boolean,
    onBack: () -> Unit,
    onOpenLoan: (Long) -> Unit,
    onHome: () -> Unit,
    onClients: () -> Unit,
    onLoans: () -> Unit,
    onRoutes: () -> Unit,
    onProfile: () -> Unit,
    viewModel: CobrosRutaViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val visibleItems = uiState.visibleItems(routeOnly)
    val permissionDenied = (!isAdmin && routeOnly && !uiState.canViewRoute) || (!isAdmin && !routeOnly && !uiState.canCollectPayments)
    var loanForSignedContract by remember { mutableStateOf<Long?>(null) }
    val signedContractPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val loanId = loanForSignedContract
        if (uri != null && loanId != null) viewModel.activateWithSignedContract(loanId, uri.toString())
        loanForSignedContract = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (routeOnly) "Ruta de cobro" else "Realizar cobro",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (routeOnly && !isAdmin) {
                            Text(uiState.activeRoute, fontSize = 11.sp, color = CollectionsTextSecondary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CollectionsSurface)
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = isAdmin,
                selectedTab = if (routeOnly) PrimaryTab.ROUTES else PrimaryTab.LOANS,
                onHome = onHome,
                onClients = onClients,
                onLoans = onLoans,
                onRoutes = onRoutes,
                onProfile = onProfile
            )
        },
        containerColor = CollectionsSurface,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                if (routeOnly) "Ruta organizada por zona y por la fecha de cobro más próxima" else
                    if (isAdmin) "Selecciona cualquier cliente con préstamo activo" else
                        "Clientes y préstamos asignados a tu usuario",
                color = CollectionsTextSecondary,
                fontSize = 13.sp
            )
            if (isAdmin) {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Todas", "Zona Norte", "Zona Sur", "Zona Este").forEach { zone ->
                        FilterChip(
                            selected = uiState.zoneFilter == zone,
                            onClick = { viewModel.onZoneFilterChanged(zone) },
                            label = { Text(if (zone == "Todas") zone else zone.removePrefix("Zona "), fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar cliente, teléfono o dirección") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
            )
            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CollectionsGreen)
                }
                permissionDenied -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("El administrador no te ha concedido permiso para esta función.", color = MaterialTheme.colorScheme.error)
                }
                uiState.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
                }
                visibleItems.isEmpty() -> EmptyCollections(routeOnly)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isAdmin && routeOnly) {
                        visibleItems.groupBy { it.zone.ifBlank { "Sin zona" } }.forEach { (zone, zoneItems) ->
                            item(key = "zone-$zone") {
                                Text(zone, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CollectionsGreen, modifier = Modifier.padding(top = 6.dp))
                            }
                            items(zoneItems, key = { it.loanId }) { item ->
                                CollectionClientCard(item, isAdmin, { onOpenLoan(item.loanId) }, { viewModel.printContract(item.loanId) }) {
                                    loanForSignedContract = item.loanId
                                    signedContractPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            }
                        }
                    } else {
                        items(visibleItems, key = { it.loanId }) { item ->
                            CollectionClientCard(item, isAdmin, { onOpenLoan(item.loanId) }, { viewModel.printContract(item.loanId) }) {
                                loanForSignedContract = item.loanId
                                signedContractPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }

    uiState.contractShareFallbackLoanId?.let { loanId ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text("Impresora no disponible") },
            text = { Text(uiState.message.orEmpty()) },
            confirmButton = {
                Button(onClick = { viewModel.shareContractWhatsApp(loanId) }, enabled = uiState.canShareDocuments) {
                    Text("Enviar por WhatsApp")
                }
            },
            dismissButton = { TextButton(onClick = viewModel::dismissMessage) { Text("Conservar en pantalla") } }
        )
    }
}

@Composable
private fun CollectionClientCard(
    item: CollectionClientItem,
    isAdmin: Boolean,
    onCollect: () -> Unit,
    onPrintContract: () -> Unit,
    onUploadSigned: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (item.isDue) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else CollectionsOutline)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!item.photoPath.isNullOrBlank()) {
                    AsyncImage(
                        model = item.photoPath,
                        contentDescription = item.clientName,
                        modifier = Modifier.size(52.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.clientName.take(2).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.clientName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                    Text("Préstamo #${item.loanId}", fontSize = 12.sp, color = CollectionsGreen)
                    if (isAdmin) Text("Asignado al empleado #${item.employeeId}", fontSize = 11.sp, color = CollectionsTextSecondary)
                }
                Surface(
                    color = if (item.isDue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        when (item.status) {
                            LoanStatus.PENDIENTE_REVISION -> "EN REVISIÓN"
                            LoanStatus.RECHAZADO -> "RECHAZADO"
                            LoanStatus.APROBADO -> "APROBADO"
                            LoanStatus.ACTIVO -> if (item.isDue) "${item.dueCount} pendiente(s)" else "ACTIVO"
                            else -> item.status.name
                        },
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, Modifier.size(16.dp), tint = CollectionsTextSecondary)
                Spacer(Modifier.width(6.dp))
                Text(item.phone, fontSize = 12.sp, color = CollectionsTextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), tint = CollectionsTextSecondary)
                Spacer(Modifier.width(6.dp))
                Text(item.address.ifBlank { "Dirección no registrada" }, fontSize = 12.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
            }
            Text(item.zone, fontSize = 12.sp, color = CollectionsTextSecondary, fontWeight = FontWeight.SemiBold)
            item.rejectionReason?.let { Text("Motivo: $it", color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Saldo pendiente", fontSize = 10.sp, color = CollectionsTextSecondary)
                    Text(item.pendingBalanceFormatted, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Próxima: ${item.nextDueText}", fontSize = 10.sp, color = CollectionsTextSecondary)
                }
                when (item.status) {
                    LoanStatus.ACTIVO -> Button(
                        onClick = onCollect,
                        colors = ButtonDefaults.buttonColors(containerColor = CollectionsGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Payments, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Cobrar")
                    }
                    LoanStatus.APROBADO -> Column(horizontalAlignment = Alignment.End) {
                        OutlinedButton(onClick = onPrintContract) { Text("Imprimir contrato", fontSize = 11.sp) }
                        Button(onClick = onUploadSigned, colors = ButtonDefaults.buttonColors(containerColor = CollectionsGreen)) { Text("Subir firmado", fontSize = 11.sp) }
                    }
                    else -> Text("Sin cobro disponible", color = CollectionsTextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun EmptyCollections(routeOnly: Boolean) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Map, null, Modifier.size(54.dp), tint = CollectionsOutline)
            Spacer(Modifier.height(8.dp))
            Text(
                if (routeOnly) "No hay cobros pendientes en la ruta" else "No hay clientes con préstamos activos",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
