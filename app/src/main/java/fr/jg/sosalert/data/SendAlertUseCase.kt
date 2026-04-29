package fr.jg.sosalert.data

import kotlinx.coroutines.flow.first

class SendAlertUseCase(
    private val contactRepository: ContactRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val smsRepository: SmsRepository,
    private val locationRepository: LocationRepository
) {
    suspend fun execute(): Int {
        val phoneNumbers = contactRepository.getAllContacts().first().map { it.phoneNumber }
        val messageContent = userSettingsRepository.messageContent.first()
        val isSendLocation = userSettingsRepository.isSendLocation.first()

        var fullMessage = messageContent ?: ""
        if (isSendLocation) {
            val location = locationRepository.getLocation()
            val locationUrl = location?.let {
                "https://maps.google.com/?q=${it.latitude},${it.longitude}"
            }
            if (locationUrl != null) {
                fullMessage = "$fullMessage $locationUrl"
            }
        }

        smsRepository.sendSmsToList(phoneNumbers, fullMessage)
        return phoneNumbers.size
    }
}
