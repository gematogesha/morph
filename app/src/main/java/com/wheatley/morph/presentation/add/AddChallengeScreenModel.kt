package com.wheatley.morph.presentation.add

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.domain.model.Challenge
import com.wheatley.morph.domain.model.ChallengeColor
import com.wheatley.morph.domain.repository.ChallengeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

// ---------- UI STATE ----------

data class AddChallengeState(
    val isSaving: Boolean = false,
    val name: String = "",
    val emoji: String = "",
    val duration: Int = 7,
    val color: ChallengeColor = ChallengeColor.LIGHTPURPLE,
    val resetTrigger: Boolean = false
)

// ---------- ONE-TIME EVENTS ----------

sealed interface AddChallengeEvent {
    data class ShowMessage(val message: String) : AddChallengeEvent
}

// ---------- SCREEN MODEL ----------

class AddChallengeScreenModel(
    private val repository: ChallengeRepository
) : StateScreenModel<AddChallengeState>(AddChallengeState()) {

    private val scope = screenModelScope

    private val _events = MutableSharedFlow<AddChallengeEvent>()
    val events: SharedFlow<AddChallengeEvent> = _events

    private fun updateState(update: AddChallengeState.() -> AddChallengeState) {
        mutableState.value = mutableState.value.update()
    }

    fun updateName(name: String) = updateState { copy(name = name) }
    fun updateEmoji(emoji: String) = updateState { copy(emoji = emoji) }
    fun updateDuration(duration: Int) = updateState { copy(duration = duration) }
    fun updateColor(color: ChallengeColor) = updateState { copy(color = color) }

    @OptIn(UnstableApi::class)
    fun save() {
        val state = mutableState.value

        if (state.name.isBlank()) {
            scope.launch {
                _events.emit(AddChallengeEvent.ShowMessage("Не все поля заполнены"))
            }
            return
        }

        scope.launch {
            mutableState.value = state.copy(isSaving = true)

            runCatching {
                repository.addChallenge(
                    Challenge(
                        name = state.name.trim(),
                        emoji = state.emoji.trim(),
                        duration = state.duration,
                        color = state.color
                    )
                )
            }.onFailure {
                _events.emit(AddChallengeEvent.ShowMessage(it.message ?: "Не удалось добавить достижение"))
            }.onSuccess {
                mutableState.value = AddChallengeState(resetTrigger = true) // сброс формы
                _events.emit(AddChallengeEvent.ShowMessage("Достижение добавлено"))
            }
        }
    }

    fun resetHandled() {
        mutableState.value = mutableState.value.copy(resetTrigger = false)
    }
}
