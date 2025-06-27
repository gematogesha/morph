package com.wheatley.morph.presentation.home.model

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.model.StateScreenModel
import com.wheatley.morph.model.challenge.Challenge
import com.wheatley.morph.model.challenge.ChallengeDao
import com.wheatley.morph.model.challenge.ChallengeEntry
import com.wheatley.morph.model.challenge.ChallengeStatus
import com.wheatley.morph.util.app.isSameDay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

data class ChallengeListState(
    val challenges: List<Triple<Challenge, Int, Boolean>> = emptyList()
)

class ChallengeListScreenModel(
    private val dao: ChallengeDao,
    private val status: ChallengeStatus
) : StateScreenModel<ChallengeListState>(ChallengeListState()) {

    init {
        load()
    }

    private fun load() {
        screenModelScope.launch {
            combine(
                dao.getChallengesByStatus(status),
                dao.getAllEntries()
            ) { challenges, entries ->
                val grouped = entries.groupBy { it.challengeId }
                val today = Date()

                challenges.map { challenge ->
                    val challengeEntries = grouped[challenge.id].orEmpty()
                    val doneCount = challengeEntries.count { it.done }

                    val todayDone = challengeEntries
                        .filter { it.date.isSameDay(today) }
                        .maxByOrNull { it.date }
                        ?.done == true

                    Triple(challenge, doneCount, todayDone)
                }
            }.collect { result ->
                mutableState.value = ChallengeListState(result)
            }
        }
    }

    fun toggleDone(challengeId: Long, date: java.util.Date, done: Boolean) {
        screenModelScope.launch {
            dao.upsertEntry(ChallengeEntry(challengeId = challengeId, date = date, done = done))
        }
    }
}
