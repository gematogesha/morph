package com.wheatley.morph.data.local.helpers

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SnackbarHelper {
    suspend fun show(snackbarHostState: SnackbarHostState, message: String) {
        withContext(Dispatchers.Main) {
            snackbarHostState.showSnackbar(message)
        }
    }
}
