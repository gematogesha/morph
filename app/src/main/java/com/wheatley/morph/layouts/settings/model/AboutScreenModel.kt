package com.wheatley.morph.layouts.settings.model

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.StateScreenModel
import com.wheatley.morph.util.update.UpdateChecker
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

data class AboutScreenState(
    val isLoading: Boolean = false,
    val showSheet: Boolean = false,
    val version: String = "",
    val changelog: String = "",
    val downloadLink: String = ""
)

class AboutScreenModel(
    private val context: Context
) : StateScreenModel<AboutScreenState>(AboutScreenState()) {

    fun checkForUpdate(snackbarHostState: SnackbarHostState) {
        mutableState.value = mutableState.value.copy(isLoading = true)

        screenModelScope.launch {
            UpdateChecker(context).checkVersion(
                snackbarHostState = snackbarHostState,
                onNewUpdate = { version, changelog, download ->
                    mutableState.value = mutableState.value.copy(
                        showSheet = true,
                        version = version,
                        changelog = changelog,
                        downloadLink = download
                    )
                },
                onFinish = {
                    mutableState.value = mutableState.value.copy(isLoading = false)
                }
            )
        }
    }

    fun dismissSheet() {
        mutableState.value = mutableState.value.copy(showSheet = false)
    }
}
