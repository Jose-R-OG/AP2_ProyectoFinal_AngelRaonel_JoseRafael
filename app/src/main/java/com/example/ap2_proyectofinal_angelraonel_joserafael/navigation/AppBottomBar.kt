package com.example.ap2_proyectofinal_angelraonel_joserafael.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

enum class PrimaryTab { HOME, CLIENTS, LOANS, ROUTES, PROFILE }

private data class BottomDestination(
    val tab: PrimaryTab,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun RoleBottomBar(
    isAdmin: Boolean,
    selectedTab: PrimaryTab?,
    onHome: () -> Unit,
    onClients: () -> Unit,
    onLoans: () -> Unit,
    onRoutes: () -> Unit,
    onProfile: () -> Unit
) {
    val items = listOf(
        BottomDestination(PrimaryTab.HOME, "Inicio", Icons.Default.Home, onHome),
        BottomDestination(PrimaryTab.CLIENTS, "Clientes", Icons.Default.Group, onClients),
        BottomDestination(
            PrimaryTab.LOANS,
            if (isAdmin) "Préstamos" else "Cobros",
            Icons.Default.Payments,
            onLoans
        ),
        BottomDestination(PrimaryTab.ROUTES, "Rutas", Icons.Default.AltRoute, onRoutes),
        BottomDestination(PrimaryTab.PROFILE, "Perfil", Icons.Default.Person, onProfile)
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = selectedTab == item.tab,
                onClick = item.onClick,
                alwaysShowLabel = true
            )
        }
    }
}
