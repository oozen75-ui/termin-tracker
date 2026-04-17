package com.termintracker.notification

import com.termintracker.model.search.NotificationType
import com.termintracker.model.search.NotificationSettings
import com.termintracker.model.search.AppointmentSearch
import com.termintracker.model.search.SearchResult
import com.termintracker.model.search.getDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface NotificationService {
    suspend fun sendNotification(
        recipient: String,
        subject: String,
        content: String
    ): NotificationResult
    
    suspend fun sendAppointmentFoundNotification(
        settings: NotificationSettings,
        search: AppointmentSearch,
        result: SearchResult
    ): List<NotificationResult>
    
    suspend fun isConfigured(): Boolean
}

data class NotificationResult(
    val success: Boolean,
    val type: NotificationType,
    val errorMessage: String? = null
)

abstract class BaseNotificationService : NotificationService {
    
    protected open fun createAppointmentNotificationContent(
        search: AppointmentSearch,
        result: SearchResult
    ): NotificationContent {
        val dateStr = result.appointmentDate.toString()
        val timeStr = result.appointmentTime?.toString() ?: "Uhrzeit nicht angegeben"
        
        val subject = buildString {
            append("Termin gefunden: ")
            append(search.appointmentCategory.getDisplayName())
            append(" am ")
            append(dateStr)
        }
        
        val body = buildString {
            appendLine("Gute Nachrichten! Wir haben einen Termin für Sie gefunden.")
            appendLine()
            appendLine("Suchkriterien:")
            appendLine("- Kategorie: ${search.appointmentCategory.getDisplayName()}")
            appendLine("- Zeitraum: ${search.dateRange.startDate} bis ${search.dateRange.endDate}")
            appendLine()
            appendLine("Gefundener Termin:")
            appendLine("- Datum: $dateStr")
            appendLine("- Uhrzeit: $timeStr")
            result.doctorName?.let { appendLine("- Arzt: $it") }
            result.clinicName?.let { appendLine("- Fachrichtung: $it") }
            result.address?.let { appendLine("- Adresse: $it") }
            result.phoneNumber?.let { appendLine("- Telefon: $it") }
            appendLine()
            result.bookingUrl?.let {
                appendLine("Online buchen: $it")
            }
            appendLine()
            appendLine("Quelle: ${result.sourceName}")
        }
        
        return NotificationContent(subject, body)
    }
    
    data class NotificationContent(
        val subject: String,
        val body: String
    )
}

class CompositeNotificationService(
    private val emailService: NotificationService? = null,
    private val telegramService: NotificationService? = null,
    private val desktopService: NotificationService? = null
) : BaseNotificationService() {
    
    override suspend fun sendNotification(
        recipient: String,
        subject: String,
        content: String
    ): NotificationResult {
        // This method sends to all configured services
        val results = mutableListOf<NotificationResult>()
        
        emailService?.let {
            if (it.isConfigured()) {
                results.add(it.sendNotification(recipient, subject, content))
            }
        }
        
        telegramService?.let {
            if (it.isConfigured()) {
                results.add(it.sendNotification(recipient, subject, content))
            }
        }
        
        desktopService?.let {
            if (it.isConfigured()) {
                results.add(it.sendNotification(recipient, subject, content))
            }
        }
        
        // Return the first successful result or the last failure
        return results.firstOrNull { it.success } ?: results.lastOrNull() 
            ?: NotificationResult(false, NotificationType.DESKTOP, "No notification services configured")
    }
    
    override suspend fun sendAppointmentFoundNotification(
        settings: NotificationSettings,
        search: AppointmentSearch,
        result: SearchResult
    ): List<NotificationResult> {
        val results = mutableListOf<NotificationResult>()
        val content = createAppointmentNotificationContent(search, result)
        
        if (settings.emailEnabled && settings.emailAddress != null) {
            emailService?.let {
                if (it.isConfigured()) {
                    results.add(it.sendNotification(settings.emailAddress, content.subject, content.body))
                }
            }
        }
        
        if (settings.telegramEnabled && settings.telegramChatId != null) {
            telegramService?.let {
                if (it.isConfigured()) {
                    results.add(it.sendNotification(settings.telegramChatId, content.subject, content.body))
                }
            }
        }
        
        if (settings.desktopNotificationEnabled) {
            desktopService?.let {
                if (it.isConfigured()) {
                    results.add(it.sendNotification("desktop", content.subject, content.body))
                }
            }
        }
        
        return results
    }
    
    override suspend fun isConfigured(): Boolean {
        return (emailService?.isConfigured() == true) ||
               (telegramService?.isConfigured() == true) ||
               (desktopService?.isConfigured() == true)
    }
    
    suspend fun sendTestNotification(settings: NotificationSettings): List<NotificationResult> {
        val results = mutableListOf<NotificationResult>()
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        val subject = "Termin Tracker Test"
        val body = "Dies ist eine Test-Benachrichtigung vom Termin Tracker.\nZeitstempel: $timestamp"
        
        if (settings.emailEnabled && settings.emailAddress != null) {
            emailService?.let {
                if (it.isConfigured()) {
                    results.add(it.sendNotification(settings.emailAddress, subject, body))
                }
            }
        }
        
        if (settings.telegramEnabled && settings.telegramChatId != null) {
            telegramService?.let {
                if (it.isConfigured()) {
                    results.add(it.sendNotification(settings.telegramChatId, subject, body))
                }
            }
        }
        
        if (settings.desktopNotificationEnabled) {
            desktopService?.let {
                if (it.isConfigured()) {
                    results.add(it.sendNotification("desktop", subject, body))
                }
            }
        }
        
        return results
    }
}
