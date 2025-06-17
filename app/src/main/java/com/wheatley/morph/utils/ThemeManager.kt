import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

object ThemeManager {
    private const val PREFS_NAME = "app_preferences"
    private const val THEME_KEY = "theme"

    var currentTheme = mutableStateOf("system")

    fun loadTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentTheme.value = sharedPreferences.getString(THEME_KEY, "system") ?: "system"
    }

    fun saveTheme(context: Context, theme: String) {
        currentTheme.value = theme
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putString(THEME_KEY, theme) }
    }
}
