package com.wheatley.morph.ui.theme
import ThemeManager
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
import com.wheatley.morph.components.darken
import com.wheatley.morph.components.lighten

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
    green = ColorFamily(
        color = greenLight,
        secondColor = lighten(greenLight, 0.35f),
        colorContainer = lighten(greenLight, 0.8f),
        onColorContainer = darken(greenLight, 0.05f)
    ),
    orange = ColorFamily(
        color = orangeLight,
        secondColor = lighten(orangeLight, 0.35f),
        colorContainer = lighten(orangeLight, 0.8f),
        onColorContainer = darken(orangeLight, 0.05f)
    ),
    mint = ColorFamily(
        color = mintLight,
        secondColor = lighten(mintLight, 0.35f),
        colorContainer = lighten(mintLight, 0.8f),
        onColorContainer = darken(mintLight, 0.05f)
    ),
    lightPurple = ColorFamily(
        color = lightPurpleLight,
        secondColor = lighten(lightPurpleLight, 0.35f),
        colorContainer = lighten(lightPurpleLight, 0.8f),
        onColorContainer = darken(lightPurpleLight, 0.05f)
    ),
    yellow = ColorFamily(
        color = yellowLight,
        secondColor = lighten(yellowLight, 0.35f),
        colorContainer = lighten(yellowLight, 0.8f),
        onColorContainer = darken(yellowLight, 0.05f)
    ),
    pink = ColorFamily(
        color = pinkLight,
        secondColor = lighten(pinkLight, 0.35f),
        colorContainer = lighten(pinkLight, 0.8f),
        onColorContainer = darken(pinkLight, 0.05f)
    ),
    bluePurple = ColorFamily(
        color = bluePurpleLight,
        secondColor = lighten(bluePurpleLight, 0.35f),
        colorContainer = lighten(bluePurpleLight, 0.8f),
        onColorContainer = darken(bluePurpleLight, 0.05f)
    ),
    lightGreen = ColorFamily(
        color = lightGreenLight,
        secondColor = lighten(lightGreenLight, 0.35f),
        colorContainer = lighten(lightGreenLight, 0.8f),
        onColorContainer = darken(lightGreenLight, 0.05f)
    )
)

val extendedDark = ExtendedColorScheme(
    green = ColorFamily(
        color = darken(greenLight, 0.5f),
        secondColor = darken(greenLight, 0.2f),
        colorContainer = darken(greenLight, 0.6f),
        onColorContainer = darken(greenLight, 0.2f)
    ),
    orange = ColorFamily(
        color = darken(orangeLight, 0.5f),
        secondColor = darken(orangeLight, 0.2f),
        colorContainer = darken(orangeLight, 0.6f),
        onColorContainer = darken(orangeLight, 0.2f)
    ),
    mint= ColorFamily(
        color = darken(mintLight, 0.5f),
        secondColor = darken(mintLight, 0.2f),
        colorContainer = darken(mintLight, 0.6f),
        onColorContainer = darken(mintLight, 0.2f)
    ),
    lightPurple = ColorFamily(
        color = darken(lightPurpleLight, 0.5f),
        secondColor = darken(lightPurpleLight, 0.2f),
        colorContainer = darken(lightPurpleLight, 0.6f),
        onColorContainer = darken(lightPurpleLight, 0.2f)
    ),
    yellow = ColorFamily(
        color = darken(yellowLight, 0.5f),
        secondColor = darken(yellowLight, 0.2f),
        colorContainer = darken(yellowLight, 0.6f),
        onColorContainer = darken(yellowLight, 0.2f)
    ),
    pink = ColorFamily(
        color = darken(pinkLight, 0.5f),
        secondColor = darken(pinkLight, 0.2f),
        colorContainer = darken(pinkLight, 0.6f),
        onColorContainer = darken(pinkLight, 0.2f)
    ),
    bluePurple= ColorFamily(
        color = darken(bluePurpleLight, 0.5f),
        secondColor = darken(bluePurpleLight, 0.2f),
        colorContainer = darken(bluePurpleLight, 0.6f),
        onColorContainer = darken(bluePurpleLight, 0.2f)
    ),
    lightGreen = ColorFamily(
        color = darken(lightGreenLight, 0.5f),
        secondColor = darken(lightGreenLight, 0.2f),
        colorContainer = darken(lightGreenLight, 0.6f),
        onColorContainer = darken(lightGreenLight, 0.2f)
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
    return when (ThemeManager.currentTheme.value) {
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
