package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryGreen = Color(0xFF006C49)
private val LightBlueIconBg = Color(0xFFEFF4FF)
private val LightBlueBadgeBg = Color(0xFFEFF4FF)
private val OnSurfaceVariant = Color(0xFF45464D)
private val OutlineVariant = Color(0xFFC6C6CD)
private val RedDanger = Color(0xFFC62828)
private val RedDangerBg = Color(0xFFFFEBEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoPerfilScreen(
    onNavigateBack: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: EmpleadoPerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(3) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = PrimaryBlack,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mi Perfil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = PrimaryBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; onNavigateBack() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AltRoute, contentDescription = "Routes") },
                    label = { Text("Routes", fontSize = 11.sp) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Group, contentDescription = "Clients") },
                    label = { Text("Clients", fontSize = 11.sp) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp) },
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Cabecera de Perfil
            Box(modifier = Modifier.size(100.dp)) {
                if (!uiState.avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = uiState.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFD3E4FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryBlack,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verificado",
                        tint = SecondaryGreen,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Nombre y Rol
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1C30)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiState.roleTitle,
                    fontSize = 14.sp,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = LightBlueBadgeBg,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "🛣️ ${uiState.activeRouteText}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Datos del Agente (3 Cards)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Card ID
                ProfileDetailTile(
                    icon = Icons.Default.Badge,
                    label = "ID del Agente",
                    value = uiState.agentId
                )

                // Card Email
                ProfileDetailTile(
                    icon = Icons.Default.Mail,
                    label = "Email",
                    value = uiState.email
                )

                // Card Teléfono
                ProfileDetailTile(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = uiState.phone
                )
            }

            // Sección Preferencias
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "PREFERENCIAS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Nightlight, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Tema Oscuro", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlack)
                        }
                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { viewModel.onEvent(EmpleadoPerfilUiEvent.ToggleDarkMode(it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SecondaryGreen
                            )
                        )
                    }
                }
            }

            // Sección Soporte
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SOPORTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Ayuda", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlack)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
                        }

                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Reportar Problema", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlack)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón Cerrar Sesión (Rojo suave)
            Button(
                onClick = { viewModel.onEvent(EmpleadoPerfilUiEvent.ShowLogoutDialog) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedDangerBg, contentColor = RedDanger),
                border = androidx.compose.foundation.BorderStroke(1.dp, RedDanger.copy(alpha = 0.3f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = RedDanger, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = RedDanger)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal de Confirmación de Cierre de Sesión
    if (uiState.showLogoutDialog) {
        ConfirmarCerrarSesionDialog(
            onConfirm = { viewModel.onEvent(EmpleadoPerfilUiEvent.ConfirmLogout) },
            onDismiss = { viewModel.onEvent(EmpleadoPerfilUiEvent.DismissLogoutDialog) }
        )
    }
}

@Composable
private fun ProfileDetailTile(
    icon: ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = LightBlueIconBg,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBlack,
                    modifier = Modifier.padding(10.dp).size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(label, fontSize = 11.sp, color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
            }
        }
    }
}
