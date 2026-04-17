package com.termintracker.notification

import com.termintracker.model.search.NotificationType
import com.termintracker.model.search.NotificationSettings
import com.termintracker.model.search.AppointmentSearch
import com.termintracker.model.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmailNotificationService(
    private val smtpHost: String = "smtp.gmail.com",
    private val smtpPort: Int = 587,
    private val username: String? = null,
    private val password: String? = null,
    private val useTls: Boolean = true
) : BaseNotificationService() {
    
    private var isInitialized: Boolean = false
    
    init {
        initializeSession()
    }
    
    private fun initializeSession() {
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            return
        }
        isInitialized = true
    }
    
    override suspend fun sendNotification(
        recipient: String,
        subject: String,
        content: String
    ): NotificationResult = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                return@withContext NotificationResult(
                    false,
                    NotificationType.EMAIL,
                    "Email service not configured"
                )
            }
            
            // TODO: Implement actual email sending using JavaMail
            println("Sending email to: $recipient")
            println("Subject: $subject")
            println("Content preview: ${content.take(100)}...")
            
            NotificationResult(true, NotificationType.EMAIL)
        } catch (e: Exception) {
            NotificationResult(false, NotificationType.EMAIL, e.message)
        }
    }
    
    override suspend fun sendAppointmentFoundNotification(
        settings: NotificationSettings,
        search: AppointmentSearch,
        result: SearchResult
    ): List<NotificationResult> {
        val results = mutableListOf<NotificationResult>()
        
        if (settings.emailEnabled) {
            settings.emailAddress?.let { email ->
                val content = createAppointmentNotificationContent(search, result)
                results.add(sendNotification(email, content.subject, content.body))
            }
        }
        
        return results
    }
    
    override suspend fun isConfigured(): Boolean {
        return isInitialized && !username.isNullOrBlank() && !password.isNullOrBlank()
    }
    
    fun updateCredentials(
        newSmtpHost: String = smtpHost,
        newSmtpPort: Int = smtpPort,
        newUsername: String? = username,
        newPassword: String? = password,
        newUseTls: Boolean = useTls
    ) {
        initializeSession()
    }
}
