package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

@Composable
fun LoginScreen(
    onNavigateToAdminHome: () -> Unit,
    onNavigateToEmpleadoHome: () -> Unit,
    onNavigateToRegisterAdmin: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val surfaceColor = Color(0xFFF8F9FF)
    val primaryBlack = Color(0xFF000000)
    val primaryGreen = Color(0xFF006C49)
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiState) {
        when (val state = viewModel.uiState) {
            is LoginUiState.Success -> {
                if (state.user.role == UserRole.ADMINISTRADOR) {
                    onNavigateToAdminHome()
                } else {
                    onNavigateToEmpleadoHome()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color(0xFFE8F5E9)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Bienvenido a TacoBraoApp!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )

            Text(
                text = "Ingresa tus credenciales para acceder al sistema",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF76777D),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Column {
                        Text(
                            text = "USUARIO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF30323A)
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = viewModel.username,
                            onValueChange = { viewModel.onEvent(LoginUiEvent.OnUsernameChanged(it)) },
                            placeholder = { Text("Ej. jperez") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF76777D))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFC6C6CD),
                                focusedBorderColor = primaryGreen
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "PIN DE ACCESO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF30323A)
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = viewModel.pin,
                            onValueChange = { viewModel.onEvent(LoginUiEvent.OnPinChanged(it)) },
                            placeholder = { Text("••••") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF76777D))
                            },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.onEvent(LoginUiEvent.TogglePinVisibility) }) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Mostrar/Ocultar PIN"
                                    )
                                }
                            },
                            visualTransformation = if (viewModel.isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFC6C6CD),
                                focusedBorderColor = primaryGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.onEvent(LoginUiEvent.SubmitLogin) },
                        enabled = viewModel.uiState !is LoginUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlack)
                    ) {
                        if (viewModel.uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.signInWithGoogle(context) },
                        enabled = viewModel.uiState !is LoginUiState.Loading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column{
                            Text("Continuar con Google", fontWeight = FontWeight.Bold, color = primaryGreen)
                        }

                    }


                    if (viewModel.canRegisterAdmin) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "¿Es la primera vez en este dispositivo?",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF30323A))
                            )
                            TextButton(onClick = { onNavigateToRegisterAdmin() }) {
                                Text(
                                    text = "Crear administrador",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = primaryGreen
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (viewModel.uiState is LoginUiState.Error) {
        val errorMessage = (viewModel.uiState as LoginUiState.Error).message
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(LoginUiEvent.ClearError) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(LoginUiEvent.ClearError) }) {
                    Text("OK", color = primaryGreen)
                }
            },
            title = { Text("Error de Autenticación") },
            text = { Text(errorMessage) }
        )
    }
}
