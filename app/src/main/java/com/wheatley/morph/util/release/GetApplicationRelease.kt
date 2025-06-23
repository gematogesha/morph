package com.wheatley.morph.util.release

import android.content.Context
import java.time.Instant
import java.time.temporal.ChronoUnit
import androidx.core.content.edit

class GetApplicationRelease(
    private val context: Context,
    private val service: ReleaseService
) {

    private val prefs by lazy {
        context.getSharedPreferences("release_prefs", Context.MODE_PRIVATE)
    }

    private fun getLastChecked(): Long {
        return prefs.getLong("last_app_check", 0)
    }

    private fun setLastChecked(time: Long) {
        prefs.edit { putLong("last_app_check", time) }
    }
    suspend fun await(arguments: Arguments): Result {
        val now = Instant.now()
        val lastCheckTime = Instant.ofEpochMilli(getLastChecked())
        val nextCheckTime = lastCheckTime.plus(3, ChronoUnit.DAYS)

        if (!arguments.forceCheck && now.isBefore(nextCheckTime)) {
            return Result.NoNewUpdate
        }

        val release = service.latest(arguments) ?: return Result.NoNewUpdate

        setLastChecked(now.toEpochMilli())

        // Check if latest version is different from current version
        val isNewVersion = isNewVersion(
            arguments.isPreview,
            arguments.commitCount,
            arguments.versionName,
            release.version,
        )
        return when {
            isNewVersion -> Result.NewUpdate(release)
            else -> Result.NoNewUpdate
        }
    }

    private fun isNewVersion(
        isPreview: Boolean,
        commitCount: Int,
        versionName: String,
        versionTag: String,
    ): Boolean {
        // Removes prefixes like "r" or "v"
        val newVersion = versionTag.replace("[^\\d.]".toRegex(), "")
        return if (isPreview) {
            newVersion.toInt() > commitCount
        } else {
            val oldVersion = versionName.replace("[^\\d.]".toRegex(), "")

            val newSemVer = newVersion.split(".").map { it.toInt() }
            val oldSemVer = oldVersion.split(".").map { it.toInt() }

            oldSemVer.mapIndexed { index, i ->
                if (newSemVer[index] > i) {
                    return true
                }
            }

            false
        }
    }

    data class Arguments(
        val isFoss: Boolean,
        val isPreview: Boolean,
        val commitCount: Int,
        val versionName: String,
        val repository: String,
        val forceCheck: Boolean = false,
    )

    sealed interface Result {
        data class NewUpdate(val release: Release) : Result
        data object NoNewUpdate : Result
        data object OsTooOld : Result
    }
}
