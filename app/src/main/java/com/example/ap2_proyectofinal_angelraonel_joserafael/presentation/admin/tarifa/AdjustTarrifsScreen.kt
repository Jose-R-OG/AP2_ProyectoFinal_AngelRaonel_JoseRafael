package com.example.ap2_proyectofinal_angelraonel_joserafael.presentation.admin.tarifa

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val SurfaceColor = Color(0xFFF8F9FF)
private val PrimaryColor = Color(0xFF000000)
private val PrimaryContainer = Color(0xFF131B2E)
private val OnPrimaryContainer = Color(0xFF7C839B)
private val SecondaryGreen = Color(0xFF006C49)
private val SecondaryContainer = Color(0xFF6CF8BB)
private val OnSecondaryContainer = Color(0xFF00714D)
private val OnSurfaceVariant = Color(0xFF45464D)
private val OutlineVariant = Color(0xFFC6C6CD)
private val ErrorContainer = Color(0xFFFFDAD6)
private val OnErrorContainer = Color(0xFF93000A)

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
    val onSaveClick: () -> Unit = { onEvent(TariffsUiEvent.SaveTariffs) }
    Box(modifier = Modifier.fillMaxSize()) {
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
                )
            },
            containerColor = SurfaceColor
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
                            color = PrimaryColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Define y gestiona las tasas de interés aplicadas según la frecuencia de pago del préstamo. Estos valores son los estándares actuales del sistema para el crecimiento controlado del capital.",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SecondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = OnSecondaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Políticas de Interés",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Las tasas configuradas aquí se aplicarán automáticamente a todas las nuevas solicitudes de préstamo dentro del flujo de Equity Flow.",
                                fontSize = 13.sp,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tasas de Negocio Estándar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryGreen
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
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
                                onValueChange = { onEvent(TariffsUiEvent.DailyRateChanged(it)) }
                            )

                            TariffInputRow(
                                code = "Q",
                                title = "Quincenal",
                                subtitle = "Frecuencia Bi-mensual",
                                value = uiState.biweeklyRate,
                                onValueChange = { onEvent(TariffsUiEvent.BiweeklyRateChanged(it)) }
                            )

                            TariffInputRow(
                                code = "M",
                                title = "Mensual",
                                subtitle = "Frecuencia Mensual Estándar",
                                value = uiState.monthlyRate,
                                onValueChange = { onEvent(TariffsUiEvent.MonthlyRateChanged(it)) }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = OutlineVariant.copy(alpha = 0.5f))

                            TariffInputRow(
                                code = "S4",
                                title = "Semanal (4 semanas)",
                                subtitle = "Préstamo a Corto Plazo",
                                value = uiState.fourWeeksRate,
                                onValueChange = { onEvent(TariffsUiEvent.FourWeeksChanged(it)) }
                            )

                            TariffInputRow(
                                code = "S6",
                                title = "Semanal (6 semanas)",
                                subtitle = "Préstamo Intermedio",
                                value = uiState.sixWeeksRate,
                                onValueChange = { onEvent(TariffsUiEvent.SixWeeksChanged(it)) }
                            )

                            TariffInputRow(
                                code = "S12",
                                title = "Semanal (12 semanas)",
                                subtitle = "Préstamo Extendido",
                                value = uiState.twelveWeeksRate,
                                onValueChange = { onEvent(TariffsUiEvent.TwelveWeeksChanged(it)) }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "* Los cambios requieren permisos de Admin para ser efectivos globalmente.",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = OnSurfaceVariant
                            )

                            Button(
                                onClick = onSaveClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                                enabled = !uiState.isSaving
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar Cambios", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5EEFF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resumen de Rentabilidad",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryColor
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            MetricRow("Margen Neto Proyectado", uiState.projectedNetMargin, isSecondary = true)
                            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                            MetricRow("Tasa Promedio de Mercado", uiState.averageMarketRate)
                            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Factor de Riesgo (Risk Score)", fontSize = 13.sp, color = OnSurfaceVariant)
                                Surface(
                                    color = ErrorContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = uiState.riskScore,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnErrorContainer,
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
                color = SecondaryGreen,
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
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tarifario actualizado correctamente",
                        color = Color.White,
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
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
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
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = code,
                        color = OnPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryColor)
                    Text(subtitle, fontSize = 11.sp, color = OnSurfaceVariant)
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
                    fontSize = 15.sp
                ),
                trailingIcon = {
                    Text("%", fontWeight = FontWeight.Bold, color = OnSurfaceVariant, fontSize = 14.sp)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = OutlineVariant,
                    focusedBorderColor = PrimaryColor
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
        Text(label, fontSize = 13.sp, color = OnSurfaceVariant)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSecondary) SecondaryGreen else PrimaryColor
        )
    }
}