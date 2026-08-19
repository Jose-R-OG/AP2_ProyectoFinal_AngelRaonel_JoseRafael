package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode

@Composable
fun LoginScreen(
    onNavigateToAdminHome: () -> Unit,
    onNavigateToEmpleadoHome: () -> Unit,
    onNavigateToRegisterAdmin: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.loginStatus) {
        when (val status = uiState.loginStatus) {
            is LoginStatus.Success -> {
                if (status.user.role == UserRole.ADMINISTRADOR) {
                    onNavigateToAdminHome()
                } else {
                    onNavigateToEmpleadoHome()
                }
            }
            else -> {}
        }
    }

    LoginContent(
        uiState = uiState,
        currentThemeMode = currentThemeMode,
        onEvent = viewModel::onEvent,
        onSignInWithGoogle = { viewModel.signInWithGoogle(context) },
        onNavigateToRegisterAdmin = onNavigateToRegisterAdmin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    uiState: LoginState,
    currentThemeMode: ThemeMode,
    onEvent: (LoginUiEvent) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onNavigateToRegisterAdmin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    if (uiState.loginStatus is LoginStatus.Loading) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Theme switcher icon
            IconButton(
                onClick = { onEvent(LoginUiEvent.ShowThemeDialog) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Cambiar tema",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Column(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Bienvenido a TaCobrao!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Text(
                    text = "Ingresa tus credenciales para acceder al sistema",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.username,
                                onValueChange = { onEvent(LoginUiEvent.OnUsernameChanged(it)) },
                                placeholder = { Text("Ej. jperez") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = uiState.usernameError != null,
                                supportingText = uiState.usernameError?.let { { Text(it) } },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Column {
                            Text(
                                text = "PIN DE ACCESO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = uiState.pin,
                                onValueChange = { onEvent(LoginUiEvent.OnPinChanged(it)) },
                                placeholder = { Text("••••") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { onEvent(LoginUiEvent.TogglePinVisibility) }) {
                                        Icon(
                                            imageVector = if (uiState.isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Mostrar/Ocultar PIN"
                                        )
                                    }
                                },
                                visualTransformation = if (uiState.isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                isError = uiState.pinError != null,
                                supportingText = uiState.pinError?.let { { Text(it) } },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        onEvent(LoginUiEvent.SubmitLogin)
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { onEvent(LoginUiEvent.SubmitLogin) },
                            enabled = uiState.loginStatus !is LoginStatus.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (uiState.loginStatus is LoginStatus.Loading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Iniciar Sesión",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { onSignInWithGoogle() },
                            enabled = uiState.loginStatus !is LoginStatus.Loading,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continuar con Google", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }


                        if (uiState.canRegisterAdmin) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "¿Es la primera vez en este dispositivo?",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    textAlign = TextAlign.Center
                                )
                                TextButton(onClick = { onNavigateToRegisterAdmin() }) {
                                    Text(
                                        text = "Crear administrador",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showThemeDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(LoginUiEvent.HideThemeDialog) },
            title = { Text("Tema de la aplicación") },
            text = {
                Column(Modifier.selectableGroup()) {
                    ThemeOption("Claro", currentThemeMode == ThemeMode.LIGHT) {
                        onEvent(LoginUiEvent.ThemeModeChanged(ThemeMode.LIGHT))
                    }
                    ThemeOption("Oscuro", currentThemeMode == ThemeMode.DARK) {
                        onEvent(LoginUiEvent.ThemeModeChanged(ThemeMode.DARK))
                    }
                    ThemeOption("Usar ajuste del sistema", currentThemeMode == ThemeMode.SYSTEM) {
                        onEvent(LoginUiEvent.ThemeModeChanged(ThemeMode.SYSTEM))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { onEvent(LoginUiEvent.HideThemeDialog) }) { Text("Cerrar") } }
        )
    }

    if (uiState.loginStatus is LoginStatus.Error) {
        val errorMessage = (uiState.loginStatus as LoginStatus.Error).message
        AlertDialog(
            onDismissRequest = { onEvent(LoginUiEvent.ClearError) },
            confirmButton = {
                TextButton(onClick = { onEvent(LoginUiEvent.ClearError) }) {
                    Text("OK", color = MaterialTheme.colorScheme.secondary)
                }
            },
            title = { Text("Error de Autenticación") },
            text = { Text(errorMessage) }
        )
    }
}

@Composable
private fun ThemeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(16.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 37)
@Composable
fun LoginScreenPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        LoginContent(
            uiState = LoginState(),
            currentThemeMode = ThemeMode.SYSTEM,
            onEvent = {},
            onSignInWithGoogle = {},
            onNavigateToRegisterAdmin = {}
        )
    }
}
