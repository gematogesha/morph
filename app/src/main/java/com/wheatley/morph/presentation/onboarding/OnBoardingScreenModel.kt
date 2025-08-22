package com.wheatley.morph.presentation.onboarding

import android.content.Context
import android.net.Uri
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.wheatley.morph.data.local.prefs.User
import com.wheatley.morph.presentation.DashboardScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

data class OnBoardingScreenState(
    val name: String = "",
    val image: Uri? = null,
    val step: Int = 0,
)

sealed interface OnBoardingEvent {
    data class ShowMessage(val message: String) : OnBoardingEvent
    data object SavedSuccessfully : OnBoardingEvent
}

class OnBoardingScreenModel(
    private val context: Context
) : StateScreenModel<OnBoardingScreenState>(OnBoardingScreenState()) {

    private val _events = MutableSharedFlow<OnBoardingEvent>()
    val events: SharedFlow<OnBoardingEvent> = _events

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

    fun save() {
        val state = mutableState.value
        screenModelScope.launch {
            try {
                User.saveUser(context, state.name, state.image?.toString())
                _events.emit(OnBoardingEvent.SavedSuccessfully)
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(OnBoardingEvent.ShowMessage("Произошла ошибка сохранения"))
            }
        }
    }

    fun exit(navigator: Navigator?) {
        navigator?.replace(DashboardScreen())
    }

    companion object {
        private const val MAX_STEPS = 4
    }
}
