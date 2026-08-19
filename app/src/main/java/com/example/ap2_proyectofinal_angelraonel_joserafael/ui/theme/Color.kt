package com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// --- Base Raw Colors (Used to define the Theme) ---
val PrimaryBlackRaw = Color(0xFF000000)
val SecondaryGreenRaw = Color(0xFF006C49)
val SurfaceColorRaw = Color(0xFFF8F9FF)
val OnSurfaceVariantRaw = Color(0xFF30323A)
val OutlineVariantRaw = Color(0xFFC6C6CD)
val ErrorColorRaw = Color(0xFFBA1A1A)
val SecondaryContainerRaw = Color(0xFF6CF8BB)
val OnSecondaryContainerRaw = Color(0xFF00714D)
val ErrorContainerRaw = Color(0xFFFFDAD6)
val OnErrorContainerRaw = Color(0xFF93000A)

// --- Shared Base Colors (Semantic - react to current theme) ---
val SurfaceColor @Composable get() = MaterialTheme.colorScheme.surface
val PrimaryBlack @Composable get() = MaterialTheme.colorScheme.onSurface
val SecondaryGreen @Composable get() = MaterialTheme.colorScheme.secondary
val SecondaryContainer @Composable get() = MaterialTheme.colorScheme.secondaryContainer
val OnSecondaryContainer @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
val OnSurfaceVariant @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
val OutlineVariant @Composable get() = MaterialTheme.colorScheme.outlineVariant
val ErrorColor @Composable get() = MaterialTheme.colorScheme.error
val ErrorContainer @Composable get() = MaterialTheme.colorScheme.errorContainer
val OnErrorContainer @Composable get() = MaterialTheme.colorScheme.onErrorContainer

// Compatibility aliases for legacy names
val PrimaryColor @Composable get() = PrimaryBlack
val Page @Composable get() = SurfaceColor
val Ink = Color(0xFF111318)
val Muted @Composable get() = OnSurfaceVariant
val Green @Composable get() = SecondaryGreen
val Red @Composable get() = ErrorColor
val Border @Composable get() = OutlineVariant

// --- Dark Mode Palette ---
val DarkBackground = Color(0xFF111318)
val DarkSurface = Color(0xFF1C1B1F)
val DarkOnSurface = Color(0xFFE2E2E6)
val DarkSurfaceVariant = Color(0xFF44474E)
val DarkOnSurfaceVariant = Color(0xFFC4C6D0)
val DarkOutline = Color(0xFF8E9099)
val DarkOutlineVariant = Color(0xFF44474E)

// --- Status and Badge Colors ---
val GreenBadgeBg @Composable get() = SecondaryContainer.copy(alpha = 0.35f)
val GreenBadgeText @Composable get() = SecondaryGreen
val SuccessBadgeBg @Composable get() = Color(0xFFE8F5E9)
val AdminBadgeBg = Color(0xFFFFF3E0)
val AdminBadgeText = Color(0xFFE65100)
val RedDanger = Color(0xFFBA1A1A)
val RedDangerBg = Color(0xFFFFDAD6)

// --- Action and Icon Colors ---
val LightBlueActionBg @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val LightBlueBadgeBg @Composable get() = SecondaryContainer
val LightBlueBadgeText @Composable get() = OnSecondaryContainer
val LightBlueIconBg @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val TealActionButtonBg = Color(0xFF67B59F)
val LightBlueButtonBg @Composable get() = MaterialTheme.colorScheme.surfaceVariant

// --- Specific Screen Colors ---
// Admin Profile Settings Screen Colors
val ProfileSurface @Composable get() = SurfaceColor
val ProfileGreen @Composable get() = SecondaryGreen
val ProfileOutline @Composable get() = OutlineVariant

// Adjust TarrifsScreen Colors
val PrimaryContainer @Composable get() = MaterialTheme.colorScheme.primaryContainer
val OnPrimaryContainer @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer

// ClientesScreen Colors
val ClientesSurface @Composable get() = SurfaceColor
val ClientesPrimary @Composable get() = PrimaryBlack
val ClientesGreen @Composable get() = SecondaryGreen
val ClientesTextSecondary @Composable get() = OnSurfaceVariant
val ClientesOutline @Composable get() = OutlineVariant
val ClientesError @Composable get() = ErrorColor

// Detalle Prestamo Colors
val PagadoBadgeBg @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFF003924) else Color(0xFF6CF8BB).copy(alpha = 0.4f)
val PagadoBadgeText @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFF6CF8BB) else Color(0xFF00714D)
val VencidoBadgeBg @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFF680003) else Color(0xFFFFDAD6)
val VencidoBadgeText @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFFFFDAD6) else Color(0xFFBA1A1A)
val PendienteBadgeBg @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFF003258) else Color(0xFFE3EEFF)
val PendienteBadgeText @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFFD1E4FF) else Color(0xFF1976D2)
val FuturoBadgeBg @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFF32353A) else Color(0xFFEEEEEE)
val FuturoBadgeText @Composable get() = if (MaterialTheme.colorScheme.surface == DarkSurface) Color(0xFFC6C6CD) else Color(0xFF616161)

// Cobros Ruta Screen
val CollectionsSurface @Composable get() = SurfaceColor
val CollectionsGreen @Composable get() = SecondaryGreen
val CollectionsOutline @Composable get() = OutlineVariant
