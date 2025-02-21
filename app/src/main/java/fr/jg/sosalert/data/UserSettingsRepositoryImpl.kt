package fr.jg.sosalert.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserSettingsRepository {
    private companion object {
        const val TAG = "UserSettingsRepository"
        val IS_PRESS_AND_HOLD = booleanPreferencesKey("press_and_hold")
        val IS_SEND_LOCATION = booleanPreferencesKey("send_location")
        val MESSAGE_CONTENT = stringPreferencesKey("message_content")
    }

    override val isPressAndHold: Flow<Boolean> = dataStore.data
        .catch {
            Log.e(TAG, "Error reading 'isPressAndHold' from preferences.", it)
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[IS_PRESS_AND_HOLD] ?: true
        }

    override val isSendLocation: Flow<Boolean> = dataStore.data
        .catch {
            Log.e(TAG, "Error reading 'isSendLocation' from preferences.", it)
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[IS_SEND_LOCATION] ?: false
        }

    override val messageContent: Flow<String?> = dataStore.data
        .catch {
            Log.e(TAG, "Error reading 'messageContent' from preferences.", it)
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[MESSAGE_CONTENT]
        }

    override suspend fun saveIsPressAndHold(isPressAndHold: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PRESS_AND_HOLD] = isPressAndHold
        }
    }

    override suspend fun saveIsSendLocation(isSendLocation: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_SEND_LOCATION] = isSendLocation
        }
    }

    override suspend fun saveMessageContent(messageContent: String) {
        dataStore.edit { preferences ->
            preferences[MESSAGE_CONTENT] = messageContent
        }
    }
}