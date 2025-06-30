package com.wheatley.morph.presentation.onboarding.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.wheatley.morph.model.user.UserPrefs
import com.wheatley.morph.presentation.DashboardScreen
import com.wheatley.morph.presentation.MainActivity
import com.wheatley.morph.presentation.onboarding.OnBoardingFinalScreen
import com.wheatley.morph.util.system.notification.SnackbarHelper
import kotlinx.coroutines.launch

data class OnBoardingScreenState(
    val name: String = "",
    var image: Uri? = null,
    val step: Int = 0,
)

class OnBoardingScreenModel(
    val context: Context
) : StateScreenModel<OnBoardingScreenState>(OnBoardingScreenState()) {

    val isNameValid: Boolean
        get() = mutableState.value.name.isNotBlank()

    val isImageValid: Boolean
        get() = mutableState.value.image != null

    fun updateName(name: String) {
        mutableState.value = mutableState.value.copy(name = name)
    }

    fun updateImage(image: Uri) {
        mutableState.value = mutableState.value.copy(image = image)
    }

    fun nextStep() {
        mutableState.value = mutableState.value.copy(
            step = (mutableState.value.step + 1).coerceAtMost(MAX_STEPS)
        )
    }

    fun previousStep() {
        mutableState.value = mutableState.value.copy(
            step = (mutableState.value.step - 1).coerceAtLeast(0)
        )
    }

    fun save(snackbarHostState: SnackbarHostState, onSuccess: () -> Unit) {
        screenModelScope.launch {
            try {
                val state = mutableState.value
                UserPrefs.saveUser(context, state.name, state.image?.toString())
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                SnackbarHelper.show(snackbarHostState, "Произошла ошибка сохранения")
            }
        }
    }

    fun exit(navigator: Navigator) {
        screenModelScope.launch {
            try {
                navigator.replaceAll(DashboardScreen())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val MAX_STEPS = 4
    }
}