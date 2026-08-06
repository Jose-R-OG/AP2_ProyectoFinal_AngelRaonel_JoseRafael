package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.cierre

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryGreen = Color(0xFF006C49)
private val GreenBadgeBg = Color(0xFF6CF8BB).copy(alpha = 0.4f)
private val GreenBadgeText = Color(0xFF00714D)
private val LightBlueButtonBg = Color(0xFFE3EEFF)
private val OnSurfaceVariant = Color(0xFF45464D)
private val OutlineVariant = Color(0xFFC6C6CD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CierreCajaScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CierreCajaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(3) }

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
                    IconButton(onClick = { viewModel.onEvent(CierreCajaUiEvent.ImprimirResumen) }) {
                        Icon(Icons.Default.Print, contentDescription = "Imprimir", tint = PrimaryBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontSize = 11.sp) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; onNavigateBack() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Group, contentDescription = "Clientes") },
                    label = { Text("Clientes", fontSize = 11.sp) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Payments, contentDescription = "Cobros") },
                    label = { Text("Cobros", fontSize = 11.sp) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PointOfSale, contentDescription = "Cierre") },
                    label = { Text("Cierre", fontSize = 11.sp) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        },
        containerColor = SurfaceColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Total Recaudado (Turno)
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

            // Fila de 2 Cards: Cobros y Visitados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card Cobros
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

                // Card Visitados
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

                    // Efectivo
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

                    // Transferencias
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

            // Card 4: Cuadre de Efectivo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenBadgeText.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = null,
                            tint = PrimaryBlack,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cuadre de Efectivo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack
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
                            Text(uiState.cashInHand, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
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
                            color = SecondaryGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón 1: Finalizar Turno (Verde Oscuro)
            Button(
                onClick = { viewModel.onEvent(CierreCajaUiEvent.FinalizarTurno) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen),
                enabled = !uiState.isFinalizingTurn
            ) {
                if (uiState.isFinalizingTurn) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finalizar Turno", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Botón 2: Imprimir Resumen (Azul Suave)
            Button(
                onClick = { viewModel.onEvent(CierreCajaUiEvent.ImprimirResumen) },
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
}
