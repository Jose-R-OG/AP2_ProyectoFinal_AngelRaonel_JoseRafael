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

import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ap2_proyectofinal_angelraonel_joserafael.domain.model.UserRole

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_ID_KEY = longPreferencesKey("logged_in_user_id")
    private val USER_ROLE_KEY = stringPreferencesKey("logged_in_user_role")

    val currentUserId: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID_KEY]?.takeIf { it != 0L }
    }

    val currentUserRole: Flow<UserRole?> = context.dataStore.data.map { prefs ->
        prefs[USER_ROLE_KEY]?.let { roleName ->
            try { UserRole.valueOf(roleName) } catch (e: Exception) { null }
        }
    }

    suspend fun saveSession(userId: Long, role: UserRole) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
            prefs[USER_ROLE_KEY] = role.name
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
            prefs.remove(USER_ROLE_KEY)
        }
    }
}
