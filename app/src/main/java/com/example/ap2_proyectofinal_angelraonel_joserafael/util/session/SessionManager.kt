package com.example.ap2_proyectofinal_angelraonel_joserafael.util.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_ID_KEY = longPreferencesKey("logged_in_user_id")

    val currentUserId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]?.takeIf { it != 0L }
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { prefs -> prefs[USER_ID_KEY] = userId }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs -> prefs.remove(USER_ID_KEY) }
    }
}