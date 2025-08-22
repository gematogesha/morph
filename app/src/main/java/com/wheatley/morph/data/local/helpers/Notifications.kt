package com.wheatley.morph.data.local.helpers

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object SnackbarHelper {
    private val jobs = mutableMapOf<SnackbarHostState, Job?>()
    private val lastMessages = mutableMapOf<SnackbarHostState, String?>()

    fun show(scope: CoroutineScope, snackbarHostState: SnackbarHostState, message: String) {
        if (lastMessages[snackbarHostState] == message && jobs[snackbarHostState]?.isActive == true) return
        lastMessages[snackbarHostState] = message
        jobs[snackbarHostState]?.cancel()
        jobs[snackbarHostState] = scope.launch {
            snackbarHostState.showSnackbar(message)
            lastMessages[snackbarHostState] = null
        }
    }

    fun inDev(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        show(scope, snackbarHostState, "Данная функция находится в разработке")
    }
}
