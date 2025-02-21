package fr.jg.sosalert.ui.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jg.sosalert.data.ContactRepository
import fr.jg.sosalert.data.LocationRepository
import fr.jg.sosalert.data.SmsRepository
import fr.jg.sosalert.data.UserSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val contactRepository: ContactRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val smsRepository: SmsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    var totalSentAlerts by mutableIntStateOf(0)
        private set

    var isAlertSent by mutableStateOf(false)
        private set

    val isPressAndHoldToSendAlert = userSettingsRepository.isPressAndHold


    fun hasSendSmsPermission(): Boolean {
        return smsRepository.hasSendSmsPermission()
    }

    fun sendAlert() {
        Log.d("HomeViewModel", "Sending alert...")
        resetSentAlerts()

        viewModelScope.launch {
            val isSendLocation = userSettingsRepository.isSendLocation.first()
            val messageContent = userSettingsRepository.messageContent.first()

            val phoneNumbers =
                contactRepository.getAllContacts().first().map { it.phoneNumber }
            var fullMessage = messageContent
            if (isSendLocation) {
                val location = locationRepository.getLocation()
                val locationUrl = location?.let {
                    "https://maps.google.com/?q=${it.latitude},${it.longitude}"
                }
                if (locationUrl != null) {
                    fullMessage = "$messageContent $locationUrl"
                }
            }

            totalSentAlerts = smsRepository.sendSmsToList(phoneNumbers, fullMessage)
            isAlertSent = true
        }
    }

    fun resetSentAlerts() {
        isAlertSent = false
        totalSentAlerts = 0
    }
}