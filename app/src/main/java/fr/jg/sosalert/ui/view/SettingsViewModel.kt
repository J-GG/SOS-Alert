package fr.jg.sosalert.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jg.sosalert.data.LocationRepository
import fr.jg.sosalert.data.UserSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isPressAndHold: Boolean,
    val isSendLocation: Boolean,
    val messageContent: String
)

class SettingsViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState?> = combine(
        userSettingsRepository.isPressAndHold,
        userSettingsRepository.isSendLocation,
        userSettingsRepository.messageContent
    ) { isPressAndHold, isSendLocation, messageContent ->
        SettingsUiState(
            isPressAndHold = isPressAndHold,
            isSendLocation = isSendLocation,
            messageContent = messageContent ?: ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

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
