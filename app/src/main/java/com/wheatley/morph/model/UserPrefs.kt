package com.wheatley.morph.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object UserPrefs {
    private val Context.dataStore by preferencesDataStore("user_prefs")

    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_PHOTO = stringPreferencesKey("user_photo")

    suspend fun isRegistered(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        return prefs[USER_NAME]?.isNotBlank() == true
    }

    suspend fun saveUser(context: Context, name: String, photoUri: String?) {
        context.dataStore.edit {
            it[USER_NAME] = name
            if (photoUri != null) it[USER_PHOTO] = photoUri
        }
    }

    fun getUserNameFlow(context: Context) = context.dataStore.data.map { it[USER_NAME] }
    fun getUserPhotoFlow(context: Context) = context.dataStore.data.map { it[USER_PHOTO] }
}