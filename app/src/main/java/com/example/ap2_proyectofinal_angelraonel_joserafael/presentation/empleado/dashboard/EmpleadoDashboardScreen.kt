package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.dashboard

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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar

// --- PALETA DE COLORES (Mockup TacoBraoApp) ---
private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryGreen = Color(0xFF006C49)
private val GreenBadgeBg = Color(0xFF6CF8BB).copy(alpha = 0.35f)
private val GreenBadgeText = Color(0xFF006C49)
private val LightBlueActionBg = Color(0xFFE3EEFF)
private val OnSurfaceVariant = Color(0xFF30323A)
private val OutlineVariant = Color(0xFFC6C6CD)
private val SuccessBadgeBg = Color(0xFFE8F5E9)
private val AdminBadgeBg = Color(0xFFFFF3E0)
private val AdminBadgeText = Color(0xFFE65100)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoDashboardScreen(
    onNuevoClienteClick: () -> Unit = {},
    onRealizarCobroClick: () -> Unit = {},
    onVerRutaClick: () -> Unit = {},
    onCierreCajaClick: () -> Unit = {},
    onVerTodosCobrosClick: () -> Unit = {},
    onNavigateToAdminDashboard: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onNavigateToLoans: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    viewModel: EmpleadoDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!uiState.userAvatarUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = uiState.userAvatarUrl,
                                contentDescription = "User Avatar",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, OutlineVariant, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD3E4FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = PrimaryBlack,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState.appTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = PrimaryBlack
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.BadgedBox(
                        badge = {
                            if (uiState.unreadNotifications > 0) {
                                androidx.compose.material3.Badge {
                                    Text(uiState.unreadNotifications.toString())
                                }
                            }
                        }
                    ) {
                        androidx.compose.material3.IconButton(onClick = onNavigateToNotifications) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = false,
                selectedTab = PrimaryTab.HOME,
                onHome = {},
                onClients = onNavigateToClients,
                onLoans = onNavigateToLoans,
                onRoutes = onVerRutaClick,
                onProfile = onNavigateToProfile
            )
        },
        containerColor = SurfaceColor
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SecondaryGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Indicador especial si el usuario es Admin utilizando modo empleado
                if (uiState.userRole == UserRole.ADMINISTRADOR) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .clickable { onNavigateToAdminDashboard() },
                            color = AdminBadgeBg,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AdminBadgeText.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👑 Modo Admin (Vista Empleado Activa)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AdminBadgeText
                                )
                                Text(
                                    text = "Volver a Panel Admin >",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AdminBadgeText
                                )
                            }
                        }
                    }
                }

                // Saludo y Fecha
                item {
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text(
                            text = "¡Hola, ${uiState.userName}!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B1C30)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.formattedDate,
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Resumen del día: esperado, cobrado y pendiente
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DailyAmountCard("Total a cobrar", uiState.totalToCollectToday, Icons.Default.Payments, Color(0xFF1565C0))
                        DailyAmountCard("Cobrado", uiState.totalCollectedToday, Icons.Default.CheckCircle, SecondaryGreen)
                        DailyAmountCard("Pendiente", uiState.pendingAmountToday, Icons.Default.PendingActions, Color(0xFFBA1A1A))
                    }
                }

                // Fila de 2 Tarjetas Pequeñas (Pendientes y Ruta Activa)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Pendientes
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.7f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.PendingActions,
                                    contentDescription = null,
                                    tint = Color(0xFF2979FF),
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Pendientes",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.pendingCount}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlack
                                )
                            }
                        }

                        // Ruta Activa
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.7f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AltRoute,
                                    contentDescription = null,
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Ruta Activa",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.activeRoute,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlack
                                )
                            }
                        }
                    }
                }

                // Sección Acciones Rápidas
                item {
                    Text(
                        text = "Acciones Rápidas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlack
                    )
                }

                // Grid 2x2 Acciones Rápidas
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 1. Nuevo Cliente (Negro)
                            ActionButtonLarge(
                                label = "Nuevo Cliente",
                                icon = Icons.Default.PersonAdd,
                                containerColor = PrimaryBlack,
                                contentColor = Color.White,
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canCreateClients,
                                onClick = onNuevoClienteClick
                            )

                            // 2. Realizar Cobro (Verde Oscuro)
                            ActionButtonLarge(
                                label = "Realizar Cobro",
                                icon = Icons.Default.Payments,
                                containerColor = SecondaryGreen,
                                contentColor = Color.White,
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canCollectPayments,
                                onClick = onRealizarCobroClick
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // 3. Ver Mi Ruta (Azul Suave)
                            ActionButtonLarge(
                                label = "Ver Mi Ruta",
                                icon = Icons.Default.Map,
                                containerColor = LightBlueActionBg,
                                contentColor = Color(0xFF1565C0),
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canViewRoute,
                                onClick = onVerRutaClick
                            )

                            // 4. Cierre de Caja (Azul Suave con icono Rojo)
                            ActionButtonLarge(
                                label = "Cierre de Caja",
                                icon = Icons.Default.PointOfSale,
                                containerColor = LightBlueActionBg,
                                contentColor = Color(0xFFC62828),
                                modifier = Modifier.weight(1f),
                                enabled = uiState.canCloseCash,
                                onClick = onCierreCajaClick
                            )
                        }
                    }
                }

                // Sección Cobros Recientes
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cobros Recientes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack
                        )
                        TextButton(onClick = onVerTodosCobrosClick) {
                            Text(
                                text = "Ver todo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2979FF)
                            )
                        }
                    }
                }

                // Lista de Cobros Recientes
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            if (uiState.recentCobros.isEmpty()) {
                                Text(
                                    text = "No hay cobros registrados hoy.",
                                    modifier = Modifier.padding(20.dp),
                                    fontSize = 13.sp,
                                    color = OnSurfaceVariant
                                )
                            } else {
                                uiState.recentCobros.forEachIndexed { index, cobro ->
                                    RecentCobroRow(cobro, onVerTodosCobrosClick)
                                    if (index < uiState.recentCobros.size - 1) {
                                        androidx.compose.material3.HorizontalDivider(
                                            color = OutlineVariant.copy(alpha = 0.3f),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun ActionButtonLarge(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (enabled) containerColor else Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) contentColor else OnSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) contentColor else OnSurfaceVariant
            )
            if (!enabled) Text("Sin permiso", fontSize = 10.sp, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun DailyAmountCard(label: String, value: String, icon: ImageVector, accent: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text(label, color = OnSurfaceVariant); Spacer(Modifier.height(8.dp)); Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = accent) }
            Surface(color = accent.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
                Icon(icon, null, tint = accent, modifier = Modifier.padding(12.dp).size(26.dp))
            }
        }
    }
}

@Composable
private fun RecentCobroRow(cobro: RecentCobroItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFECEFF1)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cobro.initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = cobro.clientName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlack
                )
                Text(
                    text = cobro.timeAgo,
                    fontSize = 11.sp,
                    color = OnSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = cobro.amountFormatted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = SuccessBadgeBg,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = cobro.statusText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryGreen,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
