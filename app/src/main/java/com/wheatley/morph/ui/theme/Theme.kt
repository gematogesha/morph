package com.wheatley.morph.ui.theme
import com.wheatley.morph.util.ui.ThemeManager
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.wheatley.morph.util.app.darken
import com.wheatley.morph.util.app.lighten

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)


@Immutable
data class ExtendedColorScheme(
    val primary: ColorFamily,
    val green: ColorFamily,
    val orange: ColorFamily,
    val mint: ColorFamily,
    val lightPurple: ColorFamily,
    val yellow: ColorFamily,
    val pink: ColorFamily,
    val bluePurple: ColorFamily,
    val lightGreen: ColorFamily
)

@Immutable
data class ColorFamily(
    val color: Color,
    val secondColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val extendedLight = ExtendedColorScheme(
    primary = ColorFamily(
        color = primary,
        secondColor = lighten(primary, 0.35f),
        colorContainer = lighten(primary, 0.85f),
        onColorContainer = darken(primary, 0.05f)
    ),
    green = ColorFamily(
        color = green,
        secondColor = lighten(green, 0.35f),
        colorContainer = lighten(green, 0.85f),
        onColorContainer = darken(green, 0.05f)
    ),
    orange = ColorFamily(
        color = orange,
        secondColor = lighten(orange, 0.35f),
        colorContainer = lighten(orange, 0.85f),
        onColorContainer = darken(orange, 0.05f)
    ),
    mint = ColorFamily(
        color = mint,
        secondColor = lighten(mint, 0.35f),
        colorContainer = lighten(mint, 0.85f),
        onColorContainer = darken(mint, 0.05f)
    ),
    lightPurple = ColorFamily(
        color = lightPurple,
        secondColor = lighten(lightPurple, 0.35f),
        colorContainer = lighten(lightPurple, 0.855f),
        onColorContainer = darken(lightPurple, 0.05f)
    ),
    yellow = ColorFamily(
        color = yellow,
        secondColor = lighten(yellow, 0.35f),
        colorContainer = lighten(yellow, 0.85f),
        onColorContainer = darken(yellow, 0.05f)
    ),
    pink = ColorFamily(
        color = pink,
        secondColor = lighten(pink, 0.35f),
        colorContainer = lighten(pink, 0.85f),
        onColorContainer = darken(pink, 0.05f)
    ),
    bluePurple = ColorFamily(
        color = bluePurple,
        secondColor = lighten(bluePurple, 0.35f),
        colorContainer = lighten(bluePurple, 0.85f),
        onColorContainer = darken(bluePurple, 0.05f)
    ),
    lightGreen = ColorFamily(
        color = lightGreen,
        secondColor = lighten(lightGreen, 0.35f),
        colorContainer = lighten(lightGreen, 0.85f),
        onColorContainer = darken(lightGreen, 0.05f)
    )
)

val extendedDark = ExtendedColorScheme(
    primary = ColorFamily(
        color = darken(primary, 0.5f),
        secondColor = darken(primary, 0.2f),
        colorContainer = darken(primary, 0.6f),
        onColorContainer = darken(primary, 0.2f)
    ),
    green = ColorFamily(
        color = darken(green, 0.5f),
        secondColor = darken(green, 0.2f),
        colorContainer = darken(green, 0.6f),
        onColorContainer = darken(green, 0.2f)
    ),
    orange = ColorFamily(
        color = darken(orange, 0.5f),
        secondColor = darken(orange, 0.2f),
        colorContainer = darken(orange, 0.6f),
        onColorContainer = darken(orange, 0.2f)
    ),
    mint= ColorFamily(
        color = darken(mint, 0.5f),
        secondColor = darken(mint, 0.2f),
        colorContainer = darken(mint, 0.6f),
        onColorContainer = darken(mint, 0.2f)
    ),
    lightPurple = ColorFamily(
        color = darken(lightPurple, 0.5f),
        secondColor = darken(lightPurple, 0.2f),
        colorContainer = darken(lightPurple, 0.6f),
        onColorContainer = darken(lightPurple, 0.2f)
    ),
    yellow = ColorFamily(
        color = darken(yellow, 0.5f),
        secondColor = darken(yellow, 0.2f),
        colorContainer = darken(yellow, 0.6f),
        onColorContainer = darken(yellow, 0.2f)
    ),
    pink = ColorFamily(
        color = darken(pink, 0.5f),
        secondColor = darken(pink, 0.2f),
        colorContainer = darken(pink, 0.6f),
        onColorContainer = darken(pink, 0.2f)
    ),
    bluePurple= ColorFamily(
        color = darken(bluePurple, 0.5f),
        secondColor = darken(bluePurple, 0.2f),
        colorContainer = darken(bluePurple, 0.6f),
        onColorContainer = darken(bluePurple, 0.2f)
    ),
    lightGreen = ColorFamily(
        color = darken(lightGreen, 0.5f),
        secondColor = darken(lightGreen, 0.2f),
        colorContainer = darken(lightGreen, 0.6f),
        onColorContainer = darken(lightGreen, 0.2f)
    )
)

val LocalExColorScheme = staticCompositionLocalOf { extendedLight }

@Composable
fun MorphTheme(
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
    val darkTheme = isDarkThemeEnabled()

    ApplySystemUi()

    val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

        darkTheme -> darkScheme
      else -> lightScheme
    }

    val extendedColorScheme = if (darkTheme) extendedDark else extendedLight

    CompositionLocalProvider(LocalExColorScheme provides extendedColorScheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MorphTypography,
            content = content
        )
    }
}

@Composable
fun isDarkThemeEnabled(): Boolean {
    return when (ThemeManager.currentTheme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
}

@Composable
fun ApplySystemUi() {
    val systemUiController = rememberSystemUiController()
    val navBarColor = MaterialTheme.colorScheme.surfaceContainer
    val darkTheme = isDarkThemeEnabled()

    DisposableEffect(systemUiController, darkTheme) {

        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )

        systemUiController.setNavigationBarColor(
            color = navBarColor,
        )

        onDispose {}
    }
}

@Composable
fun ApplySystemUiRegister() {
    val systemUiController = rememberSystemUiController()
    val darkTheme = isDarkThemeEnabled()

    DisposableEffect(systemUiController, darkTheme) {

        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = !darkTheme
        )

        onDispose {}
    }
}
