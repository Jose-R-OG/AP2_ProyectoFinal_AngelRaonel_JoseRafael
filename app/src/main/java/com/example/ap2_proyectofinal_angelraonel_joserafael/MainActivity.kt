package com.example.ap2_proyectofinal_angelraonel_joserafael

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.AppNavigation
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        AppNavigation(navController = navController)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        MainContent()
    }
}