package com.termintracker.viewmodel

import com.termintracker.scraper.*
import com.termintracker.scheduler.*
import com.termintracker.notification.*
import com.termintracker.model.search.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

class OnlineSearchViewModel {
    private val notificationService = CompositeNotificationService(
        emailService = EmailNotificationService(),
        telegramService = TelegramNotificationService(),
        desktopService = DesktopNotificationService()
    )
    private lateinit var scheduler: AppointmentSearchScheduler
    
    var onResultsFound: ((List<SearchResult>) -> Unit)? = null
    var onSearchStatusChanged: ((Boolean) -> Unit)? = null
    
    init {
        scheduler = AppointmentSearchScheduler(notificationService) { results, config ->
            // Notify UI
            onResultsFound?.invoke(results)
        }
    }
    
    fun getAvailableScrapers(): List<String> {
        return ScraperFactory.getAvailableScrapers().map { it.name }
    }
    
    fun startOnlineSearch(
        name: String,
        appointmentCategory: AppointmentCategory,
        startDate: LocalDate,
        endDate: LocalDate,
        preferredTimeStart: LocalTime? = null,
        preferredTimeEnd: LocalTime? = null,
        city: String,
        postalCode: String? = null,
        radiusKm: Int = 10,
        intervalMinutes: Int = 15,
        email: String? = null,
        telegramChatId: String? = null
    ): Long {
        val searchId = generateSearchId()
        
        val appointmentSearch = AppointmentSearch(
            id = searchId,
            name = name,
            searchType = SearchType.DOCTOLIB,
            appointmentCategory = appointmentCategory,
            location = SearchLocation(
                city = city,
                postalCode = postalCode,
                radiusKm = radiusKm
            ),
            dateRange = DateRange(
                startDate = startDate,
                endDate = endDate
            ),
            timeRange = if (preferredTimeStart != null && preferredTimeEnd != null) {
                TimeRange(
                    startTime = preferredTimeStart,
                    endTime = preferredTimeEnd
                )
            } else null,
            checkIntervalMinutes = intervalMinutes
        )
        
        val notificationSettings = NotificationSettings(
            emailEnabled = !email.isNullOrBlank(),
            emailAddress = email,
            telegramEnabled = !telegramChatId.isNullOrBlank(),
            telegramChatId = telegramChatId
        )
        
        val config = SearchScheduleConfig(
            searchId = searchId,
            appointmentSearch = appointmentSearch,
            intervalMinutes = intervalMinutes,
            notificationSettings = notificationSettings
        )
        
        return if (scheduler.startSearch(config)) {
            onSearchStatusChanged?.invoke(true)
            searchId
        } else {
            -1
        }
    }
    
    fun stopSearch(searchId: Long): Boolean {
        val stopped = scheduler.stopSearch(searchId)
        if (stopped) {
            onSearchStatusChanged?.invoke(scheduler.getActiveSearchIds().isNotEmpty())
        }
        return stopped
    }
    
    fun stopAllSearches() {
        scheduler.stopAllSearches()
        onSearchStatusChanged?.invoke(false)
    }
    
    fun isSearchActive(searchId: Long): Boolean {
        return scheduler.isSearchActive(searchId)
    }
    
    fun getActiveSearches(): List<Long> {
        return scheduler.getActiveSearchIds()
    }
    
    fun configureEmailNotifications(smtpHost: String, smtpPort: Int, username: String, password: String) {
        // Email service is already initialized in CompositeNotificationService
        // Configuration is handled in the service
    }
    
    fun configureTelegramNotifications(botToken: String) {
        // Telegram service is already initialized
    }
    
    fun getAvailableNotificationChannels(): List<String> {
        return listOf("Email", "Telegram", "Desktop")
    }
    
    private fun generateSearchId(): Long {
        return System.currentTimeMillis()
    }
    
    fun shutdown() {
        scheduler.shutdown()
    }
}
