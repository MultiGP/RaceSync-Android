package com.multigp.racesync.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multigp.racesync.domain.model.UserInfo
import kotlinx.coroutines.flow.first


class DataStoreManager(val context: Context) {
    private val Context.dataStore by preferencesDataStore("racesynce_preferences")

    suspend fun saveUserInfo(userInfo: UserInfo) {
        val json = Gson().toJson(userInfo)
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_INFO] = json
        }
    }

    suspend fun saveSessionId(sessionId: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SESSION_ID] = sessionId
        }
    }

    suspend fun getUserInfo(): UserInfo? {
        val preferences = context.dataStore.data.first()
        val json = preferences[KEY_USER_INFO]
        return Gson().fromJson(json, object : TypeToken<UserInfo>() {}.type)
    }

    suspend fun getSessionId(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[KEY_SESSION_ID]
    }


    companion object {
        private val KEY_USER_INFO = stringPreferencesKey("key_user_info")
        private val KEY_SESSION_ID = stringPreferencesKey("key_session_id")
    }
}
