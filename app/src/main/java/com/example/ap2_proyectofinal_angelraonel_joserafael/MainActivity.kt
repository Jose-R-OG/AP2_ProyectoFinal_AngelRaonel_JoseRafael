package com.example.ap2_proyectofinal_angelraonel_joserafael

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.AppNavigation
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.AppBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.session.SessionManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
                MainContent(sessionManager)
            }
        }
    }
}

@Composable
fun MainContent(sessionManager: SessionManager) {
    val navController = rememberNavController()
    val userRole by sessionManager.currentUserRole.collectAsState(initial = null)
    val isAdmin = userRole == UserRole.ADMINISTRADOR

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppBottomBar(navController = navController, isAdmin = isAdmin)
        }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation(navController = navController)
        }
    }
}
