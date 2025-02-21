package fr.jg.sosalert.data

interface SmsRepository {
    fun hasSendSmsPermission(): Boolean

    suspend fun sendSmsToList(
        phoneNumbers: List<String>,
        message: String?,
    ): Int
}
