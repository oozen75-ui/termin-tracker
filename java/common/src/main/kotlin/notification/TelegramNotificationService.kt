package com.termintracker.notification

import com.termintracker.model.search.NotificationType
import com.termintracker.model.search.NotificationSettings
import com.termintracker.model.search.AppointmentSearch
import com.termintracker.model.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TelegramNotificationService(
    private val botToken: String? = null
) : BaseNotificationService() {

    private val baseUrl = "https://api.telegram.org/bot"

    override suspend fun sendNotification(
        recipient: String,
        subject: String,
        content: String
    ): NotificationResult = withContext(Dispatchers.IO) {
        try {
            if (botToken.isNullOrBlank()) {
                return@withContext NotificationResult(
                    false,
                    NotificationType.TELEGRAM,
                    "Telegram bot token not configured"
                )
            }

            // TODO: Implement actual Telegram API call
            println("Sending Telegram message to chat: $recipient")
            println("Message: $content")

            NotificationResult(true, NotificationType.TELEGRAM)
        } catch (e: Exception) {
            NotificationResult(false, NotificationType.TELEGRAM, e.message)
        }
    }

    override suspend fun sendAppointmentFoundNotification(
        settings: NotificationSettings,
        search: AppointmentSearch,
        result: SearchResult
    ): List<NotificationResult> {
        val results = mutableListOf<NotificationResult>()

        if (settings.telegramEnabled) {
            settings.telegramChatId?.let { chatId ->
                val content = createAppointmentNotificationContent(search, result)
                results.add(sendNotification(chatId, content.subject, content.body))
            }
        }

        return results
    }

    override suspend fun isConfigured(): Boolean {
        return !botToken.isNullOrBlank()
    }

    suspend fun getUpdates(): List<String> = withContext(Dispatchers.IO) {
        try {
            if (botToken.isNullOrBlank()) return@withContext emptyList<String>()

            // TODO: Implement actual Telegram getUpdates API call
            println("Getting updates from Telegram...")

            emptyList<String>()
        } catch (e: Exception) {
            emptyList<String>()
        }
    }
}
