package fr.jg.sosalert.ui.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.jg.sosalert.data.SendAlertUseCase
import fr.jg.sosalert.data.SmsRepository
import fr.jg.sosalert.data.UserSettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userSettingsRepository: UserSettingsRepository,
    private val smsRepository: SmsRepository,
    private val sendAlertUseCase: SendAlertUseCase
) : ViewModel() {

    private val _alertSentEvent = Channel<Int>(Channel.BUFFERED)
    val alertSentEvent = _alertSentEvent.receiveAsFlow()

    val isPressAndHoldToSendAlert = userSettingsRepository.isPressAndHold

    fun hasSendSmsPermission(): Boolean {
        return smsRepository.hasSendSmsPermission()
    }

    fun sendAlert() {
        Log.d("HomeViewModel", "Sending alert...")

        viewModelScope.launch {
            val notifiedCount = sendAlertUseCase.execute()
            _alertSentEvent.send(notifiedCount)
        }
    }
}
