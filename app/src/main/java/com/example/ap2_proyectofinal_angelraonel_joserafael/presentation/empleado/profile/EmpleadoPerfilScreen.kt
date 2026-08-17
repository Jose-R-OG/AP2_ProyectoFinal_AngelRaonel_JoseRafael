package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryBlack = Color(0xFF000000)
private val SecondaryGreen = Color(0xFF006C49)
private val LightBlueIconBg = Color(0xFFEFF4FF)
private val LightBlueBadgeBg = Color(0xFFEFF4FF)
private val OnSurfaceVariant = Color(0xFF30323A)
private val OutlineVariant = Color(0xFFC6C6CD)
private val RedDanger = Color(0xFFC62828)
private val RedDangerBg = Color(0xFFFFEBEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoPerfilScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToClients: () -> Unit = {},
    onNavigateToCobros: () -> Unit = {},
    onNavigateToRoutes: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: EmpleadoPerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showHelpDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
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
            RoleBottomBar(
                isAdmin = false,
                selectedTab = PrimaryTab.PROFILE,
                onHome = onNavigateBack,
                onClients = onNavigateToClients,
                onLoans = onNavigateToCobros,
                onRoutes = onNavigateToRoutes,
                onProfile = {}
            )
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

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileDetailTile(
                    icon = Icons.Default.Badge,
                    label = "ID del Agente",
                    value = uiState.agentId
                )

                ProfileDetailTile(
                    icon = Icons.Default.Mail,
                    label = "Email",
                    value = uiState.email
                )

                ProfileDetailTile(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = uiState.phone
                )
            }

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
                                .clickable { showHelpDialog = true }
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
                                .clickable { showReportDialog = true }
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

    if (uiState.showLogoutDialog) {
        ConfirmarCerrarSesionDialog(
            onConfirm = { viewModel.onEvent(EmpleadoPerfilUiEvent.ConfirmLogout) },
            onDismiss = { viewModel.onEvent(EmpleadoPerfilUiEvent.DismissLogoutDialog) }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Ayuda rápida") },
            text = {
                Text("Usa Cobros para seleccionar clientes asignados, Rutas para ver vencimientos del día, Clientes para consultar expedientes y Cierre para cuadrar e imprimir el turno.")
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text("Entendido") }
            }
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Reportar un problema") },
            text = { Text("Se abrirá el menú de compartir con una plantilla para describir el problema y enviarla por el canal que prefieras.") },
            confirmButton = {
                Button(onClick = {
                    val reportIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Reporte de problema - TaCobraoApp")
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Usuario: ${uiState.name} (${uiState.agentId})\nRuta: ${uiState.activeRouteText}\n\nDescripción del problema:\n"
                        )
                    }
                    context.startActivity(Intent.createChooser(reportIntent, "Compartir reporte"))
                    showReportDialog = false
                }) { Text("Compartir reporte") }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("Cancelar") }
            }
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
