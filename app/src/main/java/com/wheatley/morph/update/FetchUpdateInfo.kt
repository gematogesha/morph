package com.wheatley.morph.update

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

suspend fun fetchUpdateInfo(): UpdateInfo? {
    return withContext(Dispatchers.IO) {
        try {
            val conn = URL("https://gist.githubusercontent.com/gematogesha/c80563cf26d920b0b609cea386f82583/raw/update.json")
                .openConnection() as HttpURLConnection

            conn.inputStream.bufferedReader().use {
                Json.decodeFromString<UpdateInfo>(it.readText())
            }
        } catch (e: Exception) {
            Log.e("UpdateInfo", "Failed to fetch update info", e)
            null
        }
    }
}