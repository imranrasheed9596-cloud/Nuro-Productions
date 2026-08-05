package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.ui.viewmodel.AppDesignTheme

private val ObsidianGlassColorScheme = darkColorScheme(
    primary = GlassPrimaryDark,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFF8FAFC),
    secondary = SoftSilverLight,
    onSecondary = Color(0xFF0F172A),
    background = Color(0xFF07080B),
    onBackground = TextPrimaryDark,
    surface = Color(0xFF111319),
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0x33FFFFFF),
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderHairlineDark,
    error = StateErrorDark
)

private val EmeraldGoldColorScheme = darkColorScheme(
    primary = Color(0xFFD4AF37), // Soft Gold
    onPrimary = Color(0xFF051D16),
    primaryContainer = Color(0xFF0C2B22),
    onPrimaryContainer = Color(0xFFF0FDF4),
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF051D16),
    background = Color(0xFF051D16),
    onBackground = Color(0xFFF0FDF4),
    surface = Color(0xFF0C2B22),
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = Color(0x28D4AF37),
    onSurfaceVariant = Color(0xFFA7F3D0),
    outline = Color(0x33D4AF37),
    error = StateErrorDark
)

private val PearlLightColorScheme = lightColorScheme(
    primary = GlassPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = SoftSilverDark,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0x14000000),
    onSurfaceVariant = Color(0xFF475569),
    outline = BorderHairlineLight,
    error = StateErrorLight
)

private val RoyalSapphireColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8), // Ice Blue
    onPrimary = Color(0xFF080E21),
    primaryContainer = Color(0xFF111A38),
    onPrimaryContainer = Color(0xFFF0F6FF),
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF080E21),
    background = Color(0xFF080E21),
    onBackground = Color(0xFFF0F6FF),
    surface = Color(0xFF111A38),
    onSurface = Color(0xFFF0F6FF),
    surfaceVariant = Color(0x2838BDF8),
    onSurfaceVariant = Color(0xFFBAE6FD),
    outline = Color(0x3338BDF8),
    error = StateErrorDark
)

private val MidnightOledColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8), // Indigo Glow
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0A0A0E),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF0D0D12),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0x28818CF8),
    onSurfaceVariant = Color(0xFFC7D2FE),
    outline = Color(0x33818CF8),
    error = StateErrorDark
)

@Composable
fun NuraTheme(
    appTheme: AppDesignTheme = AppDesignTheme.OBSIDIAN_GLASS,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (appTheme) {
        AppDesignTheme.OBSIDIAN_GLASS -> ObsidianGlassColorScheme
        AppDesignTheme.EMERALD_GOLD -> EmeraldGoldColorScheme
        AppDesignTheme.PEARL_LIGHT -> PearlLightColorScheme
        AppDesignTheme.ROYAL_SAPPHIRE -> RoyalSapphireColorScheme
        AppDesignTheme.MIDNIGHT_OLED -> MidnightOledColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NuraTheme(appTheme = AppDesignTheme.OBSIDIAN_GLASS, content = content)
}
