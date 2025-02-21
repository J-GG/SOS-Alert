package fr.jg.sosalert.data

import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    val isPressAndHold: Flow<Boolean>
    val isSendLocation: Flow<Boolean>
    val messageContent: Flow<String?>

    suspend fun saveIsPressAndHold(isPressAndHold: Boolean)

    suspend fun saveIsSendLocation(isSendLocation: Boolean)

    suspend fun saveMessageContent(messageContent: String)
}