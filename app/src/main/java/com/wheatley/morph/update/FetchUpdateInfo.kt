package com.wheatley.morph.update

import android.content.Context
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

fun fetchUpdateInfo(context: Context): UpdateInfo? {
    return try {
        val conn = URL("https://gist.githubusercontent.com/gematogesha/c80563cf26d920b0b609cea386f82583/raw/update.json")
            .openConnection() as HttpURLConnection
        conn.inputStream.bufferedReader().use {
            Json.decodeFromString<UpdateInfo>(it.readText())
        }
    } catch (e: Exception) {
        null
    }
}