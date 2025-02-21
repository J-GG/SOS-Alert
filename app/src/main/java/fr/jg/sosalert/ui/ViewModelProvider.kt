package fr.jg.sosalert.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.jg.sosalert.SosAlertApplication
import fr.jg.sosalert.ui.view.ContactsViewModel
import fr.jg.sosalert.ui.view.HomeViewModel
import fr.jg.sosalert.ui.view.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ContactsViewModel(
                sosAlertApplication().container.contactRepository,
            )
        }
        initializer {
            HomeViewModel(
                sosAlertApplication().container.contactRepository,
                sosAlertApplication().container.userSettingsRepository,
                sosAlertApplication().container.smsRepository,
                sosAlertApplication().container.locationRepository,
            )
        }
        initializer {
            SettingsViewModel(
                sosAlertApplication().container.userSettingsRepository,
                sosAlertApplication().container.locationRepository,
            )
        }
    }
}

fun CreationExtras.sosAlertApplication(): SosAlertApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SosAlertApplication)
