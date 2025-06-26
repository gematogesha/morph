package com.wheatley.morph.layouts.home.model

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.model.StateScreenModel
import com.wheatley.morph.model.challenge.Challenge
import com.wheatley.morph.model.challenge.ChallengeDao
import com.wheatley.morph.model.challenge.ChallengeEntry
import com.wheatley.morph.model.challenge.ChallengeStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChallengeListState(
    val challengesWithCount: List<Pair<Challenge, Int>> = emptyList()
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

                challenges.map { challenge ->
                    val doneCount = grouped[challenge.id]?.count { it.done } ?: 0
                    challenge to doneCount
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
