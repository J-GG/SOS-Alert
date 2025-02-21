package fr.jg.sosalert.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import fr.jg.sosalert.data.LocationRepository
import fr.jg.sosalert.data.LocationRepositoryImpl
import fr.jg.sosalert.data.SmsRepository
import fr.jg.sosalert.data.SmsRepositoryImpl
import fr.jg.sosalert.data.UserSettingsRepository
import fr.jg.sosalert.data.UserSettingsRepositoryImpl

interface AppContainer {
    val contactRepository: ContactRepository
    val userSettingsRepository: UserSettingsRepository
    val smsRepository: SmsRepository
    val locationRepository: LocationRepository
}

private const val USER_SETTINGS_PREFERENCES_NAME = "user_settings_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_SETTINGS_PREFERENCES_NAME
)

class AppDataContainer(private val context: Context) : AppContainer {

    override val contactRepository: ContactRepository by lazy {
        ContactRepositoryImpl(AppDatabase.getDatabase(context).contactDao())
    }

    override val userSettingsRepository: UserSettingsRepository by lazy {
        UserSettingsRepositoryImpl(context.dataStore)
    }

    override val smsRepository: SmsRepository by lazy {
        SmsRepositoryImpl(context)
    }

    override val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(context)
    }
}
