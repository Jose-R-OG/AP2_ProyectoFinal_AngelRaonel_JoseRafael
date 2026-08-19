package com.example.ap2_proyectofinal_angelraonel_joserafael.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.SettingsManager
import com.example.ap2_proyectofinal_angelraonel_joserafael.util.settings.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = SecondaryGreenRaw,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlackRaw,
    secondary = SecondaryGreenRaw,
    tertiary = Pink40,
    surface = SurfaceColorRaw,
    onSurface = PrimaryBlackRaw,
    onSurfaceVariant = OnSurfaceVariantRaw,
    secondaryContainer = SecondaryContainerRaw,
    onSecondaryContainer = OnSecondaryContainerRaw,
    outlineVariant = OutlineVariantRaw,
    error = ErrorColorRaw,
    errorContainer = ErrorContainerRaw,
    onErrorContainer = OnErrorContainerRaw
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    settingsManager: SettingsManager
) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = settingsManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM
    )
    val dynamicColor: StateFlow<Boolean> = settingsManager.dynamicColor.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )
}

@Composable
fun AP2_ProyectoFinal_AngelRaonel_JoseRafaelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val themeMode: ThemeMode
    val isDynamicEnabled: Boolean

    if (LocalInspectionMode.current) {
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT
        isDynamicEnabled = dynamicColor
    } else {
        val themeViewModel: ThemeViewModel = hiltViewModel()
        themeMode = themeViewModel.themeMode.collectAsState().value
        isDynamicEnabled = themeViewModel.dynamicColor.collectAsState().value
    }

    val actualDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        isDynamicEnabled && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (actualDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        actualDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
