package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Tokens según la paleta HTML
private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryColor = Color(0xFF000000)
private val OnSurfaceColor = Color(0xFF0B1C30)
private val OnSurfaceVariant = Color(0xFF45464D)
private val OutlineVariant = Color(0xFFC6C6CD)
private val SurfaceContainerLowest = Color(0xFFFFFFFF)
private val SurfaceContainerLow = Color(0xFFEFF4FF)
private val SurfaceVariant = Color(0xFFD3E4FE)
private val SecondaryColor = Color(0xFF006C49)
private val SecondaryContainer = Color(0xFF6CF8BB)
private val PrimaryContainer = Color(0xFF131B2E)
private val OnPrimaryContainer = Color(0xFF7C839B)
private val ErrorColor = Color(0xFFBA1A1A)
private val ErrorContainer = Color(0xFFFFDAD6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileSettingsScreen(
    uiState: AdminProfileUiState = AdminProfileUiState(),
    onChangePasswordClick: () -> Unit = {},
    onSecuritySettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = PrimaryColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Equity Flow",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = PrimaryColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = PrimaryColor
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Admin Avatar",
                            tint = OnSurfaceColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        containerColor = SurfaceColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Header de Perfil
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(80.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(SurfaceVariant)
                                .border(2.dp, SurfaceContainerLow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = OnSurfaceColor
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(PrimaryColor)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar Perfil",
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = uiState.adminName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceColor
                        )
                        Text(
                            text = uiState.adminEmail,
                            fontSize = 13.sp,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            BadgeChip(
                                text = uiState.roleBadge,
                                containerColor = SecondaryContainer.copy(alpha = 0.4f),
                                contentColor = SecondaryColor
                            )
                            BadgeChip(
                                text = uiState.locationBadge,
                                containerColor = PrimaryContainer.copy(alpha = 0.1f),
                                contentColor = OnPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Sección: Security & Account
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Security & Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                SettingOptionTile(
                    icon = Icons.Default.LockReset,
                    title = "Change Password",
                    subtitle = "Update your account credentials",
                    onClick = onChangePasswordClick
                )

                SettingOptionTile(
                    icon = Icons.Default.Person,
                    title = "Security Settings",
                    subtitle = if (uiState.isTwoFactorEnabled) "Two-Factor Authentication is Enabled" else "2FA is Disabled",
                    subtitleHighlight = if (uiState.isTwoFactorEnabled) "Enabled" else null,
                    highlightColor = SecondaryColor,
                    onClick = onSecuritySettingsClick
                )

                SettingOptionTile(
                    icon = Icons.Default.Notifications,
                    title = "Notification Preferences",
                    subtitle = "Email, Push and SMS alerts",
                    onClick = onNotificationsClick
                )
            }

            // Sección: Support
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Support",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                SettingOptionTile(
                    icon = Icons.Default.HelpCenter,
                    title = "Help & Support",
                    subtitle = "Documentation and customer care",
                    trailingIcon = Icons.Default.OpenInNew,
                    onClick = onHelpSupportClick
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logout & Versión
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorContainer.copy(alpha = 0.5f),
                        contentColor = ErrorColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorColor.copy(alpha = 0.2f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Logout from Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Text(
                    text = "Equity Flow ${uiState.appVersion}",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BadgeChip(text: String, containerColor: Color, contentColor: Color) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Text(
            text = text.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun SettingOptionTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    subtitleHighlight: String? = null,
    highlightColor: Color = SecondaryColor,
    trailingIcon: ImageVector = Icons.Default.ChevronRight,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceColor
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}