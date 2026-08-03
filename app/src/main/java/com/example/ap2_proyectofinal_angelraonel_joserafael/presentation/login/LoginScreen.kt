package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onNavigateToEmployeeHome: () -> Unit,
    onNavigateToAdminHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberDevice by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    // Escuchar cambios de estado para redirigir según el ROL
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                val role = (loginState as LoginState.Success).role
                if (role == "ADMINISTRADOR") {
                    onNavigateToAdminHome()
                } else {
                    onNavigateToEmployeeHome()
                }
            }
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF)) // bg-surface
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER / LOGO ---
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color(0xFFEFF4FF),
                border = BorderStroke(1.dp, Color(0xFFC6C6CD))
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox, // Cambiar por tu logo vectorial
                    contentDescription = "Logo",
                    tint = Color(0xFF131B2E),
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Acceso al Sistema",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                )
            )

            Text(
                text = "Ingrese sus credenciales para continuar",
                style = TextStyle(fontSize = 14.sp, color = Color(0xFF45464D)),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // --- TARJETA DEL FORMULARIO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Campo Correo
                    Text(
                        text = "CORREO ELECTRÓNICO",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF45464D))
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("nombre@empresa.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF76777D)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Campo Contraseña
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTRASEÑA",
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF45464D))
                        )
                        TextButton(onClick = { /* TODO: Olvidé contraseña */ }) {
                            Text("¿Olvidaste tu contraseña?", fontSize = 12.sp, color = Color(0xFF3980F4))
                        }
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF76777D)) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    // Recordar este dispositivo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Checkbox(
                            checked = rememberDevice,
                            onCheckedChange = { rememberDevice = it }
                        )
                        Text("Recordar este dispositivo", fontSize = 14.sp, color = Color(0xFF45464D))
                    }

                    // Botón Iniciar Sesión
                    Button(
                        onClick = { viewModel.loginWithEmail(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000))
                    ) {
                        Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }

                    Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color(0xFFC6C6CD))

                    // Botón Crear Cuenta
                    Text(
                        text = "¿Es nuevo en la plataforma?",
                        fontSize = 14.sp,
                        color = Color(0xFF45464D),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF3980F4))
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF3980F4))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Cuenta", color = Color(0xFF3980F4), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}