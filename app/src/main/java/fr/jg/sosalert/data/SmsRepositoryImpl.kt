package fr.jg.sosalert.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import fr.jg.sosalert.R

class SmsRepositoryImpl(private val context: Context) : SmsRepository {

    override fun hasSendSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun sendSmsToList(
        phoneNumbers: List<String>,
        message: String?,
    ): Int {
        val messageOrDefault =
            message ?: context.getString(R.string.settings_default_message_content)

        var totalSent = 0
        phoneNumbers.forEach { phoneNumber ->
            val success = sendSms(phoneNumber, messageOrDefault)
            if (success) totalSent++
        }
        return totalSent
    }

    private fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d("SmsRepository", "SMS sent to $phoneNumber")
            true
        } catch (e: Exception) {
            Log.e("SmsRepository", "Failed to send SMS to $phoneNumber: ${e.message}")
            false
        }
    }
}
