package com.multigp.racesync.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multigp.racesync.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


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

    suspend fun saveSearchRadius(radius: Double, unit: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_RADIUS_UNIT] = unit
            preferences[KEY_RADIUS_VALUE] = radius
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

    suspend fun getSearchRadius(): Pair<Double, String> {
        val preferences = context.dataStore.data.first()
        val unit = preferences[KEY_RADIUS_UNIT] ?: DEFAULT_RADIUS_UNIT
        val radius = preferences[KEY_RADIUS_VALUE] ?: DEFAULT_RADIUS_VALUE
        return Pair(radius, unit)
    }

    suspend fun saveNotificationPreference(enableNotification: Boolean){
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_PREFERENCE] = enableNotification
        }
    }

    val getNotificationPreference: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_NOTIFICATION_PREFERENCE] ?: false
        }

    suspend fun clearSession(){
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_INFO] = ""
            preferences[KEY_SESSION_ID] = ""
            preferences[KEY_RADIUS_UNIT] = DEFAULT_RADIUS_UNIT
            preferences[KEY_RADIUS_VALUE] = DEFAULT_RADIUS_VALUE
            preferences[KEY_NOTIFICATION_PREFERENCE] = false
        }
    }

    companion object {
        private val KEY_USER_INFO = stringPreferencesKey("key_user_info")
        private val KEY_SESSION_ID = stringPreferencesKey("key_session_id")
        private val KEY_RADIUS_UNIT = stringPreferencesKey("key_radius_unit")
        private val KEY_RADIUS_VALUE = doublePreferencesKey("key_radius_value")
        private val KEY_NOTIFICATION_PREFERENCE = booleanPreferencesKey("key_notification_preference")

        private const val DEFAULT_RADIUS_UNIT = "mi"
        private const val DEFAULT_RADIUS_VALUE = 100.0
    }
}
