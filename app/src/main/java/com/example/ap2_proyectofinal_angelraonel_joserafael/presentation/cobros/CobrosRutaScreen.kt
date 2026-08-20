package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.cobros

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ReportProblem
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
import androidx.compose.ui.tooling.preview.Preview
import java.math.BigDecimal

private const val ZONA_NORTE = "Zona Norte"
private const val ZONA_SUR = "Zona Sur"
private const val ZONA_ESTE = "Zona Este"
private const val TODAS = "Todas"

data class CobrosRutaCallbacks(
    val onBack: () -> Unit = {},
    val onOpenLoan: (Long) -> Unit = {},
    val onHome: () -> Unit = {},
    val onClients: () -> Unit = {},
    val onLoans: () -> Unit = {},
    val onRoutes: () -> Unit = {},
    val onProfile: () -> Unit = {},
    val onSearchChanged: (String) -> Unit = {},
    val onZoneFilterChanged: (String) -> Unit = {},
    val onActivateWithSignedContract: (Long) -> Unit = {},
    val onPrintContract: (Long) -> Unit = {},
    val onShareContractWhatsApp: (Long) -> Unit = {},
    val onDismissMessage: () -> Unit = {}
)

@Composable
fun CobrosRutaScreen(
    isAdmin: Boolean,
    routeOnly: Boolean,
    callbacks: CobrosRutaCallbacks,
    viewModel: CobrosRutaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var loanForSignedContract by remember { mutableStateOf<Long?>(null) }
    val signedContractPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val loanId = loanForSignedContract
        if (uri != null && loanId != null) viewModel.activateWithSignedContract(loanId, uri.toString())
        loanForSignedContract = null
    }

    val finalCallbacks = callbacks.copy(
        onSearchChanged = viewModel::onSearchChanged,
        onZoneFilterChanged = viewModel::onZoneFilterChanged,
        onActivateWithSignedContract = { loanId ->
            loanForSignedContract = loanId
            signedContractPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onPrintContract = viewModel::printContract,
        onShareContractWhatsApp = viewModel::shareContractWhatsApp,
        onDismissMessage = viewModel::dismissMessage
    )

    CobrosRutaContent(
        uiState = uiState,
        isAdmin = isAdmin,
        routeOnly = routeOnly,
        callbacks = finalCallbacks
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CobrosRutaContent(
    uiState: CobrosRutaUiState,
    isAdmin: Boolean,
    routeOnly: Boolean,
    callbacks: CobrosRutaCallbacks
) {
    val focusManager = LocalFocusManager.current
    val visibleItems = uiState.visibleItems(routeOnly)
    val permissionDenied = (!isAdmin && routeOnly && !uiState.canViewRoute) || (!isAdmin && !routeOnly && !uiState.canCollectPayments)

    Scaffold(
        topBar = {
            CobrosTopBar(routeOnly, isAdmin, uiState.activeRoute, callbacks.onBack)
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = isAdmin,
                selectedTab = if (routeOnly) PrimaryTab.ROUTES else PrimaryTab.LOANS,
                onHome = callbacks.onHome,
                onClients = callbacks.onClients,
                onLoans = callbacks.onLoans,
                onRoutes = callbacks.onRoutes,
                onProfile = callbacks.onProfile
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
            CobrosHeaderSection(routeOnly, isAdmin)
            
            if (isAdmin) {
                ZoneFilterSection(uiState.zoneFilter, callbacks.onZoneFilterChanged)
            }
            
            Spacer(Modifier.height(12.dp))
            
            SearchSection(uiState.searchQuery, callbacks.onSearchChanged, focusManager::clearFocus)
            
            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> LoadingBox()
                permissionDenied -> PermissionDeniedBox()
                uiState.errorMessage != null -> ErrorBox(uiState.errorMessage)
                visibleItems.isEmpty() -> EmptyCollections(routeOnly)
                else -> CobrosList(visibleItems, isAdmin, routeOnly, callbacks)
            }
        }
    }

    FallbackContractDialog(uiState, callbacks)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CobrosTopBar(routeOnly: Boolean, isAdmin: Boolean, activeRoute: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    if (routeOnly) "Ruta de cobro" else "Realizar cobro",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (routeOnly && !isAdmin) {
                    Text(activeRoute, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
}

@Composable
private fun CobrosHeaderSection(routeOnly: Boolean, isAdmin: Boolean) {
    Text(
        if (routeOnly) "Ruta organizada por zona y por la fecha de cobro más próxima" else
            if (isAdmin) "Selecciona cualquier cliente con préstamo activo" else
                "Clientes y préstamos asignados a tu usuario",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 13.sp
    )
}

@Composable
private fun ZoneFilterSection(zoneFilter: String, onZoneFilterChanged: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(TODAS, ZONA_NORTE, ZONA_SUR, ZONA_ESTE).forEach { zone ->
            FilterChip(
                selected = zoneFilter == zone,
                onClick = { onZoneFilterChanged(zone) },
                label = { Text(if (zone == TODAS) zone else zone.removePrefix("Zona "), fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SearchSection(query: String, onSearchChanged: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onSearchChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar cliente, teléfono o dirección") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() })
    )
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CollectionsGreen)
    }
}

@Composable
private fun PermissionDeniedBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("El administrador no te ha concedido permiso para esta función.", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun ErrorBox(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun CobrosList(
    items: List<CollectionClientItem>,
    isAdmin: Boolean,
    routeOnly: Boolean,
    callbacks: CobrosRutaCallbacks
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (isAdmin && routeOnly) {
            items.groupBy { it.zone.ifBlank { "Sin zona" } }.forEach { (zone, zoneItems) ->
                item(key = "zone-$zone") {
                    Text(zone, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CollectionsGreen, modifier = Modifier.padding(top = 6.dp))
                }
                items(zoneItems, key = { it.loanId }) { item ->
                    CollectionClientCard(item, isAdmin, callbacks)
                }
            }
        } else {
            items(items, key = { it.loanId }) { item ->
                CollectionClientCard(item, isAdmin, callbacks)
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun FallbackContractDialog(uiState: CobrosRutaUiState, callbacks: CobrosRutaCallbacks) {
    uiState.contractShareFallbackLoanId?.let { loanId ->
        AlertDialog(
            onDismissRequest = callbacks.onDismissMessage,
            title = { Text("Impresora no disponible") },
            text = { Text(uiState.message.orEmpty()) },
            confirmButton = {
                Button(onClick = { callbacks.onShareContractWhatsApp(loanId) }, enabled = uiState.canShareDocuments) {
                    Text("Enviar por WhatsApp")
                }
            },
            dismissButton = { TextButton(onClick = callbacks.onDismissMessage) { Text("Cerrar") } }
        )
    }
}

@Composable
private fun CollectionClientCard(
    item: CollectionClientItem,
    isAdmin: Boolean,
    callbacks: CobrosRutaCallbacks
) {
    val borderStroke = BorderStroke(1.dp, if (item.isDue) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else CollectionsOutline)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = borderStroke
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CollectionCardHeader(item, isAdmin)
            CollectionCardContactInfo(item)
            Text(item.zone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            item.rejectionReason?.let { Text("Motivo: $it", color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
            CollectionCardFooter(item, isAdmin, callbacks)
        }
    }
}

@Composable
private fun CollectionCardHeader(item: CollectionClientItem, isAdmin: Boolean) {
    val statusText = when (item.status) {
        LoanStatus.PENDIENTE_REVISION -> "EN REVISIÓN"
        LoanStatus.RECHAZADO -> "RECHAZADO"
        LoanStatus.APROBADO -> "APROBADO"
        LoanStatus.ACTIVO -> if (item.isDue) "${item.dueCount} pendiente(s)" else "ACTIVO"
        else -> item.status.name
    }
    val statusColor = if (item.isDue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val statusBg = if (item.isDue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)

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
            if (isAdmin) Text("Asignado al empleado #${item.employeeId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            color = statusBg,
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                statusText,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }
    }
}

@Composable
private fun CollectionCardContactInfo(item: CollectionClientItem) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(6.dp))
            Text(item.phone, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(6.dp))
            Text(item.address.ifBlank { "Dirección no registrada" }, fontSize = 12.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun CollectionCardFooter(
    item: CollectionClientItem,
    isAdmin: Boolean,
    callbacks: CobrosRutaCallbacks
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Saldo pendiente", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(item.pendingBalanceFormatted, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Próxima: ${item.nextDueText}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        CollectionCardActions(item, callbacks)
    }
}

@Composable
private fun CollectionCardActions(
    item: CollectionClientItem,
    callbacks: CobrosRutaCallbacks
) {
    when (item.status) {
        LoanStatus.ACTIVO -> Button(
            onClick = { callbacks.onOpenLoan(item.loanId) },
            colors = ButtonDefaults.buttonColors(containerColor = CollectionsGreen),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Payments, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Cobrar")
        }
        LoanStatus.APROBADO -> Column(horizontalAlignment = Alignment.End) {
            OutlinedButton(onClick = { callbacks.onPrintContract(item.loanId) }) { Text("Imprimir contrato", fontSize = 11.sp) }
            Button(onClick = { callbacks.onActivateWithSignedContract(item.loanId) }, colors = ButtonDefaults.buttonColors(containerColor = CollectionsGreen)) { Text("Subir firmado", fontSize = 11.sp) }
        }
        else -> Text("Sin cobro disponible", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CobrosRutaScreenPreview() {
    val sampleItems = listOf(
        CollectionClientItem(
            loanId = 1L, clientId = 101L, clientName = "Juan Pérez", clientDni = "001-0000000-0",
            phone = "809-555-0101", address = "Calle A #123", zone = ZONA_NORTE,
            photoPath = null, employeeId = 5L, status = LoanStatus.ACTIVO,
            amount = BigDecimal("10000"), rate = BigDecimal("10"), installmentCount = 10,
            installmentAmount = BigDecimal("1100"), dueCount = 0, pendingBalance = BigDecimal("5000"),
            pendingBalanceFormatted = "RD$ 5,000.00", nextDueText = "Hoy", nextDueAt = null,
            paymentDay = "Lunes", isDue = false
        ),
        CollectionClientItem(
            loanId = 2L, clientId = 102L, clientName = "María López", clientDni = "002-0000000-0",
            phone = "829-555-0202", address = "Calle B #456", zone = ZONA_SUR,
            photoPath = null, employeeId = 5L, status = LoanStatus.ACTIVO,
            amount = BigDecimal("20000"), rate = BigDecimal("15"), installmentCount = 12,
            installmentAmount = BigDecimal("2000"), dueCount = 2, pendingBalance = BigDecimal("15000"),
            pendingBalanceFormatted = "RD$ 15,000.00", nextDueText = "Ayer", nextDueAt = null,
            paymentDay = "Martes", isDue = true
        )
    )
    val sampleUiState = CobrosRutaUiState(
        items = sampleItems,
        isLoading = false,
        activeRoute = ZONA_NORTE
    )
    
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        CobrosRutaContent(
            uiState = sampleUiState,
            isAdmin = true,
            routeOnly = true,
            callbacks = CobrosRutaCallbacks()
        )
    }
}
