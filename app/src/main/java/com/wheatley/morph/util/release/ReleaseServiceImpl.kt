package com.wheatley.morph.util.release

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlinx.serialization.json.Json
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ReleaseServiceImpl(
    private val json: Json,
) : ReleaseService {

    override suspend fun latest(arguments: GetApplicationRelease.Arguments): Release? {
        return try {
            val url = URL("https://api.github.com/repos/${arguments.repository}/releases/latest")
            val connection = withContext(Dispatchers.IO) {
                url.openConnection()
            } as HttpsURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github+json")
            connection.setRequestProperty("User-Agent", "Morph-App")

            connection.inputStream.bufferedReader().use {
                val text = it.readText()
                val release = json.decodeFromString(GithubRelease.serializer(), text)

                val downloadLink = getDownloadLink(release = release, isFoss = arguments.isFoss) ?: return null

                Release(
                    version = release.version,
                    info = release.info.replace(gitHubUsernameMentionRegex) { mention ->
                        "[${mention.value}](https://github.com/${mention.value.substring(1)})"
                    },
                    releaseLink = release.releaseLink,
                    downloadLink = downloadLink,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getDownloadLink(release: GithubRelease, isFoss: Boolean): String? {
        val map = release.assets.associate { asset ->
            BUILD_TYPES.find { "-$it" in asset.name } to asset.downloadLink
        }

        return if (!isFoss) {
            map[Build.SUPPORTED_ABIS[0]] ?: map[null]
        } else {
            map[FOSS]
        }
    }

    companion object {
        private const val FOSS = "foss"
        private val BUILD_TYPES = listOf(FOSS, "arm64-v8a", "armeabi-v7a", "x86_64", "x86")

        /**
         * Regular expression that matches a mention to a valid GitHub username, like it's
         * done in GitHub Flavored Markdown. It follows these constraints:
         *
         * - Alphanumeric with single hyphens (no consecutive hyphens)
         * - Cannot begin or end with a hyphen
         * - Max length of 39 characters
         *
         * Reference: https://stackoverflow.com/a/30281147
         */
        private val gitHubUsernameMentionRegex = """\B@([a-z0-9](?:-(?=[a-z0-9])|[a-z0-9]){0,38}(?<=[a-z0-9]))"""
            .toRegex(RegexOption.IGNORE_CASE)
    }
}
