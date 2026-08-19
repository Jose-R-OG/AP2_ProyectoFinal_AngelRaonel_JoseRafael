package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryGreen = Color(0xFF006C49)
private val GreenBadgeBg = Color(0xFF6CF8BB).copy(alpha = 0.4f)
private val GreenBadgeText = Color(0xFF00714D)
private val LightBlueButtonBg = Color(0xFFE3EEFF)
private val OnSurfaceVariant = Color(0xFF30323A)
private val OutlineVariant = Color(0xFFC6C6CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CierreCajaScreen(
    isAdmin: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onNavigateToCobros: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: CierreCajaViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Cierre de Caja",
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
                    IconButton(onClick = { viewModel.imprimirResumen(context) }) {
                        Icon(Icons.Default.Print, contentDescription = "Imprimir", tint = PrimaryBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = isAdmin,
                selectedTab = null,
                onHome = onNavigateBack,
                onClients = onNavigateToClients,
                onLoans = onNavigateToCobros,
                onRoutes = onNavigateToRoutes,
                onProfile = onNavigateToProfile
            )
        },
        containerColor = SurfaceColor,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Recaudado (Turno)",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.totalCollectedTurn,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = GreenBadgeBg,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GreenBadgeText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isTurnActive) "Turno en curso" else "Turno finalizado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenBadgeText
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cobros", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${uiState.totalCobrosCount}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Visitados", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${uiState.visitedCount}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlack
                            )
                            Text(
                                text = "/${uiState.totalTargetVisited}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Desglose por Método",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFE8F0FE),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = PrimaryBlack,
                                    modifier = Modifier.padding(10.dp).size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Efectivo", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlack)
                        }
                        Text(uiState.cashAmount, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFFE8F0FE),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = PrimaryBlack,
                                    modifier = Modifier.padding(10.dp).size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Transferencias", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlack)
                        }
                        Text(uiState.transferAmount, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (uiState.cashInHandError != null) MaterialTheme.colorScheme.error else GreenBadgeText.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = null,
                            tint = if (uiState.cashInHandError != null) MaterialTheme.colorScheme.error else PrimaryBlack,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cuadre de Efectivo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.cashInHandError != null) MaterialTheme.colorScheme.error else PrimaryBlack
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Efectivo Registrado", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(uiState.registeredCash, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Efectivo en Mano", fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = uiState.cashInHandInput,
                                onValueChange = { viewModel.onEvent(CierreCajaUiEvent.OnCashInHandChanged(it)) },
                                modifier = Modifier.width(130.dp),
                                prefix = { Text("RD$ ") },
                                singleLine = true,
                                enabled = uiState.isTurnActive,
                                isError = uiState.cashInHandError != null,
                                supportingText = uiState.cashInHandError?.let { { Text(it) } },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Diferencia", fontSize = 14.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                        Text(
                            text = uiState.differenceAmount,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.differenceAmount == "RD$ 0.00") SecondaryGreen else Color(0xFFBA1A1A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onEvent(CierreCajaUiEvent.FinalizarTurno) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                enabled = !uiState.isFinalizingTurn && uiState.isTurnActive
            ) {
                if (uiState.isFinalizingTurn) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (uiState.isTurnActive) "Finalizar Turno" else "Turno Finalizado",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { viewModel.imprimirResumen(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlueButtonBg, contentColor = PrimaryBlack),
                enabled = !uiState.isPrinting
            ) {
                if (uiState.isPrinting) {
                    CircularProgressIndicator(color = PrimaryBlack, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(20.dp), tint = PrimaryBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Imprimir Resumen", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (uiState.turnFinalizedSuccess) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(CierreCajaUiEvent.DismissSuccess)
            },
            title = { Text("Turno finalizado") },
            text = { Text("El cierre de caja fue completado correctamente.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(CierreCajaUiEvent.DismissSuccess) }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    uiState.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(CierreCajaUiEvent.ClearError) },
            title = { Text("No fue posible completar la acción") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(CierreCajaUiEvent.ClearError) }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
