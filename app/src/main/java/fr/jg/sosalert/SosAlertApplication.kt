package fr.jg.sosalert

import android.app.Application
import fr.jg.sosalert.data.AppContainer
import fr.jg.sosalert.data.AppDataContainer


class SosAlertApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}