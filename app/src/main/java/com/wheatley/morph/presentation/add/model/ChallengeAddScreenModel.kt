package com.wheatley.morph.presentation.add.model

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.model.challenge.Challenge
import com.wheatley.morph.model.challenge.ChallengeColor
import com.wheatley.morph.model.challenge.repository.ChallengeRepository
import com.wheatley.morph.util.system.notification.SnackbarHelper
import kotlinx.coroutines.launch

data class ChallengeAddState(
    val isSaving: Boolean = false,
    val name: String = "",
    val emoji: String = "",
    val duration: Int = 7,
    val color: ChallengeColor = ChallengeColor.LIGHTPURPLE,
    val resetTrigger: Boolean = false
)

class ChallengeAddScreenModel(
    private val repository: ChallengeRepository
) : StateScreenModel<ChallengeAddState>(ChallengeAddState()) {

    fun updateName(name: String) {
        mutableState.value = mutableState.value.copy(name = name)
    }

    fun updateEmoji(emoji: String) {
        mutableState.value = mutableState.value.copy(emoji = emoji)
    }

    fun updateDuration(duration: Int) {
        mutableState.value = mutableState.value.copy(duration = duration)
    }

    fun updateColor(color: ChallengeColor) {
        mutableState.value = mutableState.value.copy(color = color)
    }

    fun save(snackbarHostState: SnackbarHostState) {
        val state = mutableState.value

        if (state.name.isBlank()) {
            screenModelScope.launch {
                SnackbarHelper.show(snackbarHostState, "Не все поля заполнены")
            }
            return
        }

        screenModelScope.launch {
            mutableState.value = state.copy(isSaving = true)

            repository.addChallenge(
                Challenge(
                    name = state.name.trim(),
                    emoji = state.emoji.trim(),
                    duration = state.duration,
                    color = state.color
                ),
            )

            mutableState.value = ChallengeAddState(resetTrigger = true) // сброс формы
            SnackbarHelper.show(snackbarHostState, "Достижение добавлено")
        }
    }

    fun resetHandled() {
        mutableState.value = mutableState.value.copy(resetTrigger = false)
    }
}
