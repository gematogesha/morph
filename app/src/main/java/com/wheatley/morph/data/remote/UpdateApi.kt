package com.wheatley.morph.data.remote

import com.wheatley.morph.domain.model.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class UpdateApi {

    suspend fun getLatestRelease(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.github.com/repos/gematogesha/morph/releases/latest"
            val response = URL(url).readText()
            val release = JSONObject(response)

            val tagName = release.getString("tag_name")
            val info = release.getString("body")
            val assets = release.getJSONArray("assets")
            val apkLink = (0 until assets.length())
                .map { assets.getJSONObject(it) }
                .firstOrNull { it.getString("name").endsWith(".apk") }
                ?.getString("browser_download_url") ?: return@withContext null

            UpdateInfo(
                version = tagName,
                apkUrl = apkLink,
                changelog = info
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}