package com.wheatley.morph.util.update

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import com.wheatley.morph.BuildConfig
import com.wheatley.morph.R
import com.wheatley.morph.util.release.GetApplicationRelease
import com.wheatley.morph.util.release.ReleaseServiceImpl
import com.wheatley.morph.util.system.isPreviewBuildType
import com.wheatley.morph.util.system.notification.SnackbarHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class UpdateChecker(private val context: Context) {

    private val getApplicationRelease = GetApplicationRelease(
        context,
        ReleaseServiceImpl(json = Json { ignoreUnknownKeys = true })
    )

    private suspend fun checkForUpdate(
        forceCheck: Boolean = false
    ): GetApplicationRelease.Result {
        return withContext(Dispatchers.IO) {
            getApplicationRelease.await(
                GetApplicationRelease.Arguments(
                    isFoss = false,
                    isPreview = false,
                    commitCount = BuildConfig.COMMIT_COUNT.toIntOrNull() ?: 0,
                    versionName = BuildConfig.VERSION_NAME,
                    repository = GITHUB_REPO,
                    forceCheck = forceCheck,
                )
            )
        }
    }

    suspend fun checkVersion(
        snackbarHostState: SnackbarHostState,
        onNewUpdate: (String, String, String) -> Unit,
        onFinish: () -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            try {
                when (val result = checkForUpdate(forceCheck = true)) {
                    is GetApplicationRelease.Result.NewUpdate -> {
                        withContext(Dispatchers.Main) {
                            onNewUpdate(
                                result.release.version,
                                result.release.info,
                                result.release.releaseLink,
                            )
                        }
                    }
                    is GetApplicationRelease.Result.NoNewUpdate -> {
                        SnackbarHelper.show(snackbarHostState, context.getString(R.string.update_check_no_new_updates))
                    }
                    is GetApplicationRelease.Result.OsTooOld -> {
                        SnackbarHelper.show(snackbarHostState, context.getString(R.string.update_check_eol))
                    }
                }
            } catch (e: Exception) {
                SnackbarHelper.show(snackbarHostState, e.message ?: "Ошибка проверки обновления")
            } finally {
                onFinish()
            }
        }
    }

}

val GITHUB_REPO: String by lazy {
    if (isPreviewBuildType) {
        //"gematogesha/morph-preview"
        "gematogesha/morph"
    } else {
        "gematogesha/morph"
    }
}

val RELEASE_TAG: String by lazy {
    if (isPreviewBuildType) {
        "r${BuildConfig.COMMIT_COUNT}"
    } else {
        "v${BuildConfig.VERSION_NAME}"
    }
}

val RELEASE_URL = "https://github.com/$GITHUB_REPO/releases/tag/$RELEASE_TAG"
