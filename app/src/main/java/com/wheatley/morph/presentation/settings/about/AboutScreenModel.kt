// AboutScreenModel.kt
package com.wheatley.morph.presentation.settings.about

import androidx.compose.material3.SnackbarHostState
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wheatley.morph.core.app.UpdateManager
import com.wheatley.morph.data.local.helpers.SnackbarHelper
import com.wheatley.morph.domain.model.UpdateInfo

data class AboutScreenState(
    val isLoading: Boolean = false,
    val showSheet: Boolean = false,
    val version: String = "",
    val changelog: String = "",
    val downloadLink: String = ""
)

class AboutScreenModel : StateScreenModel<AboutScreenState>(AboutScreenState()) {

    fun checkUpdate(snackbarHostState: SnackbarHostState, updateManager: UpdateManager) {
        val scope = screenModelScope
        mutableState.value = mutableState.value.copy(isLoading = true)
        updateManager.checkForUpdate(
            onUpdateAvailable = { update ->
                mutableState.value = mutableState.value.copy(
                    isLoading = false,
                    showSheet = true,
                    version = update.version,
                    downloadLink = update.apkUrl,
                    changelog = update.changelog
                )
            },
            onNoUpdate = {
                mutableState.value = mutableState.value.copy(isLoading = false)
                SnackbarHelper.show(scope, snackbarHostState, "Вы используете актуальную версию")
            },
            onError = {
                mutableState.value = mutableState.value.copy(isLoading = false)
                SnackbarHelper.show(scope, snackbarHostState, "Не удалось проверить обновление")
            }
        )
    }

    fun dismissSheet() {
        mutableState.value = mutableState.value.copy(showSheet = false)
    }
}
