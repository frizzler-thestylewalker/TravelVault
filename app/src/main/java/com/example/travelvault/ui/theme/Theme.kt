// com.example.travelvault.ui.theme/Theme.kt
package com.example.travelvault.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- THIS IS OUR NEW CUSTOM DARK PALETTE ---
private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryAccent,      // Buttons, highlights
    secondary = AppPrimaryAccent,    // (We'll use the same for now)
    tertiary = AppPrimaryAccent,     // (And again)
    background = AppDarkBackground,  // App background
    surface = AppDarkSurface,        // Card backgrounds
    onPrimary = Color.Black,         // Text on buttons
    onSecondary = Color.Black,       // Text on secondary elements
    onTertiary = Color.Black,        // Text on tertiary elements
    onBackground = AppText,          // Primary text on background
    onSurface = AppText              // Primary text on cards
)

// The light theme (we won't be using this, but it's good to have)
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    /* Other default light colors */
)

@Composable
fun TravelVaultTheme(
    // --- THIS IS THE KEY CHANGE ---
    // We are forcing darkTheme = true to always get the premium dark look
    darkTheme: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // This line now always selects our custom DarkColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Set status bar color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // This is from your Type.kt file
        content = content
    )
}