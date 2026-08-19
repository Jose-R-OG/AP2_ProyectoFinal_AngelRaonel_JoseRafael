package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme.AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme

@Composable
fun AdjustTariffsScreen(
    viewModel: TariffsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdjustTariffsContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustTariffsContent(
    uiState: TariffsUiState = TariffsUiState(),
    onEvent: (TariffsUiEvent) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val onSaveClick: () -> Unit = { onEvent(TariffsUiEvent.SaveTariffs) }
    
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryGreen = MaterialTheme.colorScheme.secondary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TaCobrao",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = onSurface
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
                )
            },
            containerColor = surfaceColor,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Tarifario del Negocio",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Define y gestiona las tasas de interés aplicadas según la frecuencia de pago del préstamo. Estos valores son los estándares actuales del sistema para el crecimiento controlado del capital.",
                            fontSize = 14.sp,
                            color = onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Políticas de Interés",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Las tasas configuradas aquí se aplicarán automáticamente a todas las nuevas solicitudes de préstamo de TaCobrao.",
                                fontSize = 13.sp,
                                color = onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = secondaryGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tasas de Negocio Estándar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryGreen
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TariffInputRow(
                                code = "D",
                                title = "Diario",
                                subtitle = "Frecuencia Diaria",
                                value = uiState.dailyRate,
                                onValueChange = { onEvent(TariffsUiEvent.DailyRateChanged(it)) },
                                focusManager = focusManager,
                                imeAction = ImeAction.Next
                            )

                            TariffInputRow(
                                code = "Q",
                                title = "Quincenal",
                                subtitle = "Frecuencia Bi-mensual",
                                value = uiState.biweeklyRate,
                                onValueChange = { onEvent(TariffsUiEvent.BiweeklyRateChanged(it)) },
                                focusManager = focusManager,
                                imeAction = ImeAction.Next
                            )

                            TariffInputRow(
                                code = "M",
                                title = "Mensual",
                                subtitle = "Frecuencia Mensual Estándar",
                                value = uiState.monthlyRate,
                                onValueChange = { onEvent(TariffsUiEvent.MonthlyRateChanged(it)) },
                                focusManager = focusManager,
                                imeAction = ImeAction.Next
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = outlineVariant.copy(alpha = 0.5f))

                            TariffInputRow(
                                code = "S4",
                                title = "Semanal (4 semanas)",
                                subtitle = "Préstamo a Corto Plazo",
                                value = uiState.fourWeeksRate,
                                onValueChange = { onEvent(TariffsUiEvent.FourWeeksChanged(it)) },
                                focusManager = focusManager,
                                imeAction = ImeAction.Next
                            )

                            TariffInputRow(
                                code = "S6",
                                title = "Semanal (6 semanas)",
                                subtitle = "Préstamo Intermedio",
                                value = uiState.sixWeeksRate,
                                onValueChange = { onEvent(TariffsUiEvent.SixWeeksChanged(it)) },
                                focusManager = focusManager,
                                imeAction = ImeAction.Next
                            )

                            TariffInputRow(
                                code = "S12",
                                title = "Semanal (12 semanas)",
                                subtitle = "Préstamo Extendido",
                                value = uiState.twelveWeeksRate,
                                onValueChange = { onEvent(TariffsUiEvent.TwelveWeeksChanged(it)) },
                                focusManager = focusManager,
                                imeAction = ImeAction.Done,
                                onDone = {
                                    focusManager.clearFocus()
                                    onSaveClick()
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "* Los cambios requieren permisos de Admin para ser efectivos globalmente.",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = onSurfaceVariant
                            )

                            uiState.errorMessage?.let { message ->
                                Text(message, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }

                            Button(
                                onClick = onSaveClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar Cambios", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resumen de Rentabilidad",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            MetricRow("Margen Neto Proyectado", uiState.projectedNetMargin, isSecondary = true)
                            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                            MetricRow("Tasa Promedio Configurada", uiState.averageMarketRate)
                            HorizontalDivider(color = outlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Factor de Riesgo (Risk Score)", fontSize = 13.sp, color = onSurfaceVariant)
                                Surface(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = uiState.riskScore,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        AnimatedVisibility(
            visible = uiState.showSuccessToast,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Surface(
                color = secondaryGreen,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tarifario actualizado correctamente",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun TariffInputRow(
    code: String,
    title: String,
    subtitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager? = null,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = code,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.width(90.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                trailingIcon = {
                    Text("%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager?.moveFocus(FocusDirection.Down) },
                    onDone = {
                        focusManager?.clearFocus()
                        onDone()
                    }
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, isSecondary: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSecondary) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdjustTariffsScreenPreview() {
    AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme {
        AdjustTariffsContent(
            uiState = TariffsUiState(
                dailyRate = "5",
                biweeklyRate = "10",
                monthlyRate = "15",
                fourWeeksRate = "10",
                sixWeeksRate = "15",
                twelveWeeksRate = "25",
                projectedNetMargin = "8.6%",
                averageMarketRate = "13.3%",
                riskScore = "BAJO"
            )
        )
    }
}
