package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onAddEmployee: () -> Unit = {},
    onNuevoCliente: () -> Unit = {},
    onRealizarCobro: () -> Unit = {},
    onAdjustTariffs: () -> Unit = {},
    onViewAllMovements: () -> Unit = {},
    onNavigateToLoans: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AdminDashboardContent(
        uiState = uiState,
        onAddEmployee = onAddEmployee,
        onNuevoCliente = onNuevoCliente,
        onRealizarCobro = onRealizarCobro,
        onAdjustTariffs = onAdjustTariffs,
        onViewAllMovements = onViewAllMovements,
        onNavigateToLoans = onNavigateToLoans,
        onNavigateToClients = onNavigateToClients,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToRoutes = onNavigateToRoutes,
        onNavigateToNotifications = onNavigateToNotifications
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    uiState: AdminDashboardUiState,
    onAddEmployee: () -> Unit = {},
    onNuevoCliente: () -> Unit = {},
    onRealizarCobro: () -> Unit = {},
    onAdjustTariffs: () -> Unit = {},
    onViewAllMovements: () -> Unit = {},
    onNavigateToLoans: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!uiState.businessLogoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = uiState.businessLogoUrl,
                                contentDescription = "Logotipo del negocio",
                                modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.businessName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = colorScheme.primary
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (uiState.unreadNotifications > 0) {
                                Badge { Text(uiState.unreadNotifications.toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                        }
                    }
                    if (!uiState.adminAvatarUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = uiState.adminAvatarUrl,
                            contentDescription = "Admin Avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.dp, colorScheme.outlineVariant, CircleShape)
                                .clickable(onClick = onNavigateToProfile),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorScheme.outlineVariant)
                                .clickable(onClick = onNavigateToProfile),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Admin Avatar",
                                tint = colorScheme.surface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.surface)
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = true,
                selectedTab = PrimaryTab.HOME,
                onHome = {},
                onClients = onNavigateToClients,
                onLoans = onNavigateToLoans,
                onRoutes = onNavigateToRoutes,
                onProfile = onNavigateToProfile
            )
        },
        containerColor = colorScheme.surface
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.secondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Dashboard Overview",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                        Text(
                            text = "Resumen de operaciones en tiempo real.",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    MetricCard(
                        title = "Total Cobrado Hoy",
                        value = uiState.totalCollectedToday,
                        badge = uiState.collectedPercentage,
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = colorScheme.secondary
                    )
                }

                item {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        MetricCardSmall(
                            title = "Capital en la calle", value = uiState.capitalInStreet,
                            subtitle = "Aprobado/activo", icon = Icons.Default.Payments
                        )
                        MetricCardSmall(
                            title = "Cartera por cobrar", value = uiState.outstandingPortfolio,
                            subtitle = "Saldo pendiente", icon = Icons.Default.AccountBalanceWallet
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            MetricCardSmall(
                                title = "Empleados Activos",
                                value = "${uiState.activeEmployees}",
                                subtitle = "/ ${uiState.totalEmployees} total",
                                icon = Icons.Default.Badge
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            MetricCardSmall(
                                title = "Aprobaciones Pendientes",
                                value = String.format("%02d", uiState.pendingApprovals),
                                subtitle = "",
                                icon = Icons.Default.PendingActions,
                                isError = uiState.pendingApprovals > 0
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Acciones Rápidas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton("Nuevo Empleado", Icons.Default.PersonAdd, Modifier.weight(1f), onAddEmployee)
                        QuickActionButton("Ajustar Tarifas", Icons.Default.PriceChange, Modifier.weight(1f), onAdjustTariffs)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton("Nuevo Cliente", Icons.Default.PersonAddAlt, Modifier.weight(1f), onNuevoCliente)
                        QuickActionButton("Modo Cobrador", Icons.Default.Payments, Modifier.weight(1f), onRealizarCobro)
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Movimientos Recientes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colorScheme.primary
                        )
                        if (uiState.recentMovements.isNotEmpty()) {
                            TextButton(onClick = onViewAllMovements) {
                                Text("Ver todo", color = colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }

                if (uiState.recentMovements.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No hay movimientos registrados hoy.",
                                    fontSize = 14.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.recentMovements, key = { it.id }) { item ->
                        MovementRow(item, onViewAllMovements)
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}


@Composable
fun MetricCard(title: String, value: String, badge: String, icon: ImageVector, iconTint: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
                if (badge.isNotEmpty()) {
                    Text(
                        text = badge,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.secondary,
                        modifier = Modifier
                            .background(colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
        }
    }
}

@Composable
fun MetricCardSmall(title: String, value: String, subtitle: String, icon: ImageVector, isError: Boolean = false) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isError) colorScheme.error else colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isError) colorScheme.error else colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.primary, maxLines = 1)
            if (subtitle.isNotEmpty()) Text(subtitle, fontSize = 11.sp, color = colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(label, fontSize = 11.sp, color = colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun MovementRow(item: MovementItem, onClick: () -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isAlert) colorScheme.errorContainer else colorScheme.secondaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isAlert) Icons.Default.ReportProblem else Icons.Default.Payments,
                        contentDescription = null,
                        tint = if (item.isAlert) colorScheme.onErrorContainer else colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.subtitle, fontSize = 12.sp, color = colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.amountOrStatus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (item.isAlert) colorScheme.error else colorScheme.secondary
                )
                Text(item.time, fontSize = 10.sp, color = colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardScreenPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        AdminDashboardContent(
            uiState = AdminDashboardUiState(
                businessName = "TaCobrao",
                totalCollectedToday = "RD$ 15,200.00",
                capitalInStreet = "RD$ 250,000.00",
                outstandingPortfolio = "RD$ 45,000.00",
                collectedPercentage = "+12%",
                activeEmployees = 3,
                totalEmployees = 5,
                pendingApprovals = 2,
                recentMovements = listOf(
                    MovementItem("1", "Cobro - Juan Perez", "Ruta A", "RD$ 500.00", "10:30 AM"),
                    MovementItem("2", "Nuevo Préstamo", "Maria Lopez", "RD$ 5,000.00", "09:45 AM", isAlert = true)
                )
            )
        )
    }
}
