package fr.jg.sosalert

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import fr.jg.sosalert.data.AppContainer
import fr.jg.sosalert.data.AppDataContainer

class SosAlertApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            ALERT_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val ALERT_CHANNEL_ID = "sos_alert_channel"
    }
}
