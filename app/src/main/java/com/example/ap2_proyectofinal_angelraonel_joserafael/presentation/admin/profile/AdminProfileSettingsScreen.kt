package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.ap2_proyectofinal_angelraonel_joserafael.R
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.PrimaryTab
import com.example.ap2_proyectofinal_angelraonel_joserafael.navigation.RoleBottomBar
import com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.empleado.profile.ConfirmarCerrarSesionDialog
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.ProfileGreen
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.ProfileOutline
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.ProfileSurface

@Composable
fun AdminProfileSettingsScreen(
    onLogoutSuccess: () -> Unit,
    onHome: () -> Unit,
    onClients: () -> Unit,
    onLoans: () -> Unit,
    onRoutes: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: AdminProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val profilePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.onEvent(AdminProfileUiEvent.ProfilePhotoSelected(it.toString())) }
    }
    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.onEvent(AdminProfileUiEvent.BusinessLogoSelected(it.toString())) }
    }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLogoutSuccess()
    }
    LaunchedEffect(uiState.message, uiState.showPinDialog, uiState.isEditing) {
        if (!uiState.message.isNullOrBlank() && !uiState.showPinDialog && !uiState.isEditing) {
            snackbar.showSnackbar(uiState.message.orEmpty())
            viewModel.onEvent(AdminProfileUiEvent.MessageShown)
        }
    }

    AdminProfileSettingsContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onHome = onHome,
        onClients = onClients,
        onLoans = onLoans,
        onRoutes = onRoutes,
        onBack = onBack,
        onProfilePicker = { profilePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onLogoPicker = { logoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        snackbar = snackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileSettingsContent(
    uiState: AdminProfileUiState,
    onEvent: (AdminProfileUiEvent) -> Unit,
    onHome: () -> Unit,
    onClients: () -> Unit,
    onLoans: () -> Unit,
    onRoutes: () -> Unit,
    onBack: () -> Unit = {},
    onProfilePicker: () -> Unit,
    onLogoPicker: () -> Unit,
    snackbar: SnackbarHostState
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(uiState.businessName, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            RoleBottomBar(
                isAdmin = true,
                selectedTab = PrimaryTab.PROFILE,
                onHome = onHome,
                onClients = onClients,
                onLoans = onLoans,
                onRoutes = onRoutes,
                onProfile = {}
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = ProfileSurface,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ProfileGreen)
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ProfileOutline)
                ) {
                    Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImageEditor(
                                path = uiState.profilePhotoPath,
                                fallbackIcon = Icons.Default.Person,
                                label = "Foto de perfil",
                                onClick = { onEvent(AdminProfileUiEvent.StartEdit) }
                            )
                            ImageEditor(
                                path = uiState.businessLogoPath,
                                fallbackIcon = Icons.Default.Business,
                                label = "Logotipo",
                                onClick = { onEvent(AdminProfileUiEvent.StartEdit) }
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(uiState.adminName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(uiState.adminEmail, fontSize = 13.sp, color = Color(0xFF30323A))
                        Text(uiState.adminPhone, fontSize = 13.sp, color = Color(0xFF30323A))
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onEvent(AdminProfileUiEvent.StartEdit) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Editar perfil y negocio")
                        }
                    }
                }

                FunctionalOption(Icons.Default.LockReset, "Cambiar PIN", "Actualiza tus credenciales locales") {
                    onEvent(AdminProfileUiEvent.ShowPinDialog)
                }
                FunctionalOption(Icons.Default.Notifications, "Preferencias de notificación", "Activa o desactiva los avisos dentro de la aplicación") {
                    onEvent(AdminProfileUiEvent.ShowNotifications)
                }
                FunctionalOption(Icons.Default.HelpCenter, "Ayuda", "Explicación de los módulos principales") {
                    onEvent(AdminProfileUiEvent.ShowHelp)
                }

                Button(
                    onClick = { onEvent(AdminProfileUiEvent.RequestLogout) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD6), contentColor = Color(0xFFBA1A1A))
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    if (uiState.isEditing) {
        AlertDialog(
            onDismissRequest = { onEvent(AdminProfileUiEvent.CancelEdit) },
            title = { Text("Editar perfil y negocio") },
            text = {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        },
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EditableImage(
                            path = uiState.pendingProfilePhoto,
                            label = "Foto",
                            modifier = Modifier.weight(1f)
                        ) { onProfilePicker() }
                        EditableImage(
                            path = uiState.pendingBusinessLogo,
                            label = "Logo",
                            modifier = Modifier.weight(1f)
                        ) { onLogoPicker() }
                    }
                    OutlinedTextField(
                        uiState.editName,
                        { onEvent(AdminProfileUiEvent.NameChanged(it)) },
                        label = { Text("Nombre") },
                        supportingText = { Text("${uiState.editName.length}/80") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    OutlinedTextField(
                        uiState.editEmail,
                        { onEvent(AdminProfileUiEvent.EmailChanged(it)) },
                        label = { Text("Correo") },
                        supportingText = { Text("${uiState.editEmail.length}/120") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    OutlinedTextField(
                        uiState.editPhone,
                        { onEvent(AdminProfileUiEvent.PhoneChanged(it)) },
                        label = { Text("Teléfono") },
                        supportingText = { Text("${uiState.editPhone.length}/10 dígitos") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )
                    OutlinedTextField(
                        uiState.editBusinessName,
                        { onEvent(AdminProfileUiEvent.BusinessNameChanged(it)) },
                        label = { Text("Nombre del negocio") },
                        supportingText = { Text("${uiState.editBusinessName.length}/60") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            onEvent(AdminProfileUiEvent.SaveProfile)
                        })
                    )
                    uiState.message?.let { Text(it, color = Color(0xFFBA1A1A), fontSize = 12.sp) }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(AdminProfileUiEvent.SaveProfile) },
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text("Guardar")
                }
            },
            dismissButton = { TextButton(onClick = { onEvent(AdminProfileUiEvent.CancelEdit) }) { Text("Cancelar") } }
        )
    }
    if (uiState.showLogoutConfirmation) {
        ConfirmarCerrarSesionDialog(
            onConfirm = { onEvent(AdminProfileUiEvent.ConfirmLogout) },
            onDismiss = { onEvent(AdminProfileUiEvent.CancelLogout) }
        )
    }

    if (uiState.showPinDialog) PinDialog(uiState, onEvent)
    if (uiState.showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(AdminProfileUiEvent.HideNotifications) },
            title = { Text("Notificaciones") },
            text = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Avisos de solicitudes y cobros")
                    Switch(uiState.notificationsEnabled, { onEvent(AdminProfileUiEvent.NotificationsChanged(it)) })
                }
            },
            confirmButton = { TextButton(onClick = { onEvent(AdminProfileUiEvent.HideNotifications) }) { Text("Listo") } }
        )
    }
    if (uiState.showHelpDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(AdminProfileUiEvent.HideHelp) },
            title = { Text("Ayuda rápida") },
            text = { Text("Clientes administra expedientes. Préstamos aprueba o rechaza solicitudes. Rutas muestra cobros pendientes. Ajustar tarifas define el interés usado en solicitudes nuevas.") },
            confirmButton = { TextButton(onClick = { onEvent(AdminProfileUiEvent.HideHelp) }) { Text("Entendido") } }
        )
    }
}

@Composable
private fun FunctionalOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ProfileOutline),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(26.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = Color(0xFF30323A))
            }
        }
    }
}

@Composable
private fun ImageEditor(path: String?, fallbackIcon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(84.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFFDCE9FF)).clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (!path.isNullOrBlank()) AsyncImage(path, label, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            else Icon(fallbackIcon, null, Modifier.size(44.dp))
        }
        Text(label, fontSize = 11.sp)
    }
}

@Composable
private fun EditableImage(path: String?, label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.height(92.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFDCE9FF)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!path.isNullOrBlank()) AsyncImage(path, label, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        else Text("Seleccionar $label", fontSize = 11.sp)
    }
}

@Composable
private fun PinDialog(uiState: AdminProfileUiState, onEvent: (AdminProfileUiEvent) -> Unit) {
    val focusManager = LocalFocusManager.current
    AlertDialog(
        onDismissRequest = { onEvent(AdminProfileUiEvent.HidePinDialog) },
        title = { Text("Cambiar PIN") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
            ) {
                OutlinedTextField(
                    uiState.oldPin,
                    { onEvent(AdminProfileUiEvent.OldPinChanged(it)) },
                    label = { Text("PIN actual") },
                    supportingText = { Text("${uiState.oldPin.length}/8 dígitos") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                OutlinedTextField(
                    uiState.newPin,
                    { onEvent(AdminProfileUiEvent.NewPinChanged(it)) },
                    label = { Text("PIN nuevo") },
                    supportingText = { Text("${uiState.newPin.length}/8 (mínimo 4)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                OutlinedTextField(
                    uiState.confirmPin,
                    { onEvent(AdminProfileUiEvent.ConfirmPinChanged(it)) },
                    label = { Text("Confirmar PIN") },
                    supportingText = { Text("${uiState.confirmPin.length}/8 dígitos") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        onEvent(AdminProfileUiEvent.SavePin)
                    })
                )
                uiState.message?.let { Text(it, color = Color(0xFFBA1A1A), fontSize = 12.sp) }
            }
        },
        confirmButton = { Button(onClick = { onEvent(AdminProfileUiEvent.SavePin) }) { Text("Actualizar") } },
        dismissButton = { TextButton(onClick = { onEvent(AdminProfileUiEvent.HidePinDialog) }) { Text("Cancelar") } }
    )
}

@Preview(showBackground = true)
@Composable
fun AdminProfileSettingsScreenPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        AdminProfileSettingsContent(
            uiState = AdminProfileUiState(
                adminName = "Angel Raonel",
                adminEmail = "angel@example.com",
                adminPhone = "809-555-5555",
                businessName = "TaCobrao Admin",
                isLoading = false
            ),
            onEvent = {},
            onHome = {},
            onClients = {},
            onLoans = {},
            onRoutes = {},
            onProfilePicker = {},
            onLogoPicker = {},
            snackbar = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminProfileSettingsLoadingPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        AdminProfileSettingsContent(
            uiState = AdminProfileUiState(isLoading = true),
            onEvent = {},
            onHome = {},
            onClients = {},
            onLoans = {},
            onRoutes = {},
            onProfilePicker = {},
            onLogoPicker = {},
            snackbar = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminProfileSettingsEditingPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        AdminProfileSettingsContent(
            uiState = AdminProfileUiState(
                adminName = "Angel Raonel",
                adminEmail = "angel@example.com",
                adminPhone = "809-555-5555",
                businessName = "TaCobrao Admin",
                isLoading = false,
                isEditing = true,
                editName = "Angel Raonel",
                editEmail = "angel@example.com",
                editPhone = "809-555-5555",
                editBusinessName = "TaCobrao Admin"
            ),
            onEvent = {},
            onHome = {},
            onClients = {},
            onLoans = {},
            onRoutes = {},
            onProfilePicker = {},
            onLogoPicker = {},
            snackbar = remember { SnackbarHostState() }
        )
    }
}
