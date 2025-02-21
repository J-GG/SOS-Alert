package fr.jg.sosalert.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jg.sosalert.data.LocationRepository
import fr.jg.sosalert.data.UserSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val isPressAndHold = userSettingsRepository.isPressAndHold.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = runBlocking { userSettingsRepository.isPressAndHold.first() }
    )

    val isSendLocation = userSettingsRepository.isSendLocation.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = runBlocking { userSettingsRepository.isSendLocation.first() }
    )

    val messageContent = runBlocking { userSettingsRepository.messageContent.first() }

    fun hasLocationPermission(): Boolean {
        return locationRepository.hasLocationPermission()
    }

    fun updateIsPressAndHold(isPressAndHold: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.saveIsPressAndHold(isPressAndHold)
        }
    }

    fun updateIsSendLocation(isSendLocation: Boolean) {
        viewModelScope.launch {
            userSettingsRepository.saveIsSendLocation(isSendLocation)
        }
    }

    fun updateMessageContent(messageContent: String) {
        viewModelScope.launch {
            userSettingsRepository.saveMessageContent(messageContent)
        }
    }

    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            userSettingsRepository.saveIsSendLocation(true)
        }
    }
}