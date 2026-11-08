package it.lucf15.ui.theme

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.lucf15.ui.scroll.CupertinoOverscrollFactory

@Immutable
internal data class ReaderColors(
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outlineVariant: Color,
    val glassTint: Color,
    val dark: Boolean,
)

@Immutable
internal class ReaderTypography {
    val headlineLarge =
        TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = (-1).sp,
        )
    val headlineMedium =
        TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            lineHeight = 35.sp,
            letterSpacing = (-.6).sp,
        )
    val titleLarge =
        TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-.3).sp,
        )
    val titleMedium =
        TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 23.sp,
            letterSpacing = (-.2).sp,
        )
    val bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 30.sp)
    val bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp)
    val labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp)
    val labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp)
}

private val LightColors =
    ReaderColors(
        Color(0xFF0879ED),
        Color.White,
        Color(0xFFF0F1F2),
        Color.White,
        Color(0xFF17191C),
        Color(0xFF676C74),
        Color(0xFFDEE0E4),
        Color(0xD9E0E3E7),
        false,
    )
private val DarkColors =
    ReaderColors(
        Color(0xFF83B8FF),
        Color(0xFF102339),
        Color(0xFF111316),
        Color(0xFF25282D),
        Color(0xFFF2F2F4),
        Color(0xFFB4B8C0),
        Color(0xFF41454C),
        Color(0xE6414650),
        true,
    )
private val LocalReaderColors = staticCompositionLocalOf { LightColors }
private val LocalReaderTypography = staticCompositionLocalOf { ReaderTypography() }
internal val LocalReaderInk = compositionLocalOf { Color.Unspecified }

internal object ReaderTheme {
    val colors: ReaderColors
        @Composable get() = LocalReaderColors.current

    val typography: ReaderTypography
        @Composable get() = LocalReaderTypography.current
}

@Composable
internal fun ReaderTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    val density = LocalDensity.current
    val overscroll = remember(density) { CupertinoOverscrollFactory(density) }
    val selection =
        remember(colors) { TextSelectionColors(colors.primary, colors.primary.copy(alpha = .3f)) }
    CompositionLocalProvider(
        LocalReaderColors provides colors,
        LocalReaderInk provides colors.onSurface,
        LocalOverscrollFactory provides overscroll,
        LocalTextSelectionColors provides selection,
        content = content,
    )
}
