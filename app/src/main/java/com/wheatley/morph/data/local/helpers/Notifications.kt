package com.wheatley.morph.data.local.helpers

import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarHostState
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object SnackbarHelper {
    private val jobs = mutableMapOf<SnackbarHostState, Job?>()
    private val lastMessages = mutableMapOf<SnackbarHostState, String?>()
    private val lastShownTime = mutableMapOf<SnackbarHostState, Long>()


    @OptIn(UnstableApi::class)
    fun show(scope: CoroutineScope, hostState: SnackbarHostState, message: String, cooldown: Long = 3000L) {
        val now = System.currentTimeMillis()
        val lastTime = lastShownTime[hostState] ?: 0L

        Log.d("SnackbarHelper", "show: cooldown=$cooldown last=$lastTime now=$now minus=${now - lastTime}")

        if (lastMessages[hostState] == message && (now - lastTime) < cooldown + 4000L) {
            return
        }

        lastMessages[hostState] = message
        lastShownTime[hostState] = now

        jobs[hostState]?.cancel()
        jobs[hostState] = scope.launch {
            hostState.currentSnackbarData?.dismiss()
            hostState.showSnackbar(message)
        }
    }

    fun inDev(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        show(scope, snackbarHostState, "Данная функция находится в разработке")
    }

}
