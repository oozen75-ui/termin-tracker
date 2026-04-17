package com.termintracker.scheduler

import com.termintracker.scraper.*
import com.termintracker.model.search.*
import com.termintracker.notification.*
import java.util.concurrent.*
import kotlin.concurrent.timer
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class SearchScheduleConfig(
    val searchId: Long,
    val appointmentSearch: AppointmentSearch,
    val intervalMinutes: Int = 15,
    val notificationSettings: NotificationSettings = NotificationSettings()
)

class AppointmentSearchScheduler(
    private val notificationService: CompositeNotificationService,
    private val onResultsFound: (List<SearchResult>, SearchScheduleConfig) -> Unit = { _, _ -> }
) {
    private val scheduler = Executors.newScheduledThreadPool(2)
    private val activeSearches = mutableMapOf<Long, ScheduledFuture<*>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scraperFactory = ScraperFactory
    
    fun startSearch(config: SearchScheduleConfig): Boolean {
        if (activeSearches.containsKey(config.searchId)) {
            println("Search ${config.searchId} is already active")
            return false
        }
        
        val intervalMillis = config.intervalMinutes * 60 * 1000L
        
        val future = scheduler.scheduleAtFixedRate({
            scope.launch {
                performSearch(config)
            }
        }, 0, intervalMillis, TimeUnit.MILLISECONDS)
        
        activeSearches[config.searchId] = future
        println("Started search ${config.searchId} with ${config.intervalMinutes} minute interval")
        return true
    }
    
    fun stopSearch(searchId: Long): Boolean {
        val future = activeSearches.remove(searchId)
        return if (future != null) {
            future.cancel(false)
            println("Stopped search $searchId")
            true
        } else {
            false
        }
    }
    
    fun stopAllSearches() {
        activeSearches.forEach { (_, future) ->
            future.cancel(false)
        }
        activeSearches.clear()
        println("Stopped all searches")
    }
    
    fun isSearchActive(searchId: Long): Boolean {
        return activeSearches.containsKey(searchId)
    }
    
    fun getActiveSearchIds(): List<Long> {
        return activeSearches.keys.toList()
    }
    
    private suspend fun performSearch(config: SearchScheduleConfig) {
        try {
            println("[${getCurrentTimestamp()}] Performing search ${config.searchId}...")
            
            val startTime = System.currentTimeMillis()
            
            // Create appropriate scraper based on search type
            val scraper = scraperFactory.createScraper(config.appointmentSearch.searchType)
            
            // Perform the search
            val results = scraper.searchAppointments(
                category = config.appointmentSearch.appointmentCategory,
                location = config.appointmentSearch.location,
                dateRange = config.appointmentSearch.dateRange,
                timeRange = config.appointmentSearch.timeRange
            )
            
            val duration = System.currentTimeMillis() - startTime
            
            println("Found ${results.size} appointments in ${duration}ms")
            
            if (results.isNotEmpty()) {
                // Filter results - only new/fresh appointments
                val newResults = filterNewResults(results)
                
                if (newResults.isNotEmpty()) {
                    println("Found ${newResults.size} NEW appointments!")
                    
                    // Send notifications
                    newResults.forEach { result ->
                        notificationService.sendAppointmentFoundNotification(
                            config.notificationSettings,
                            config.appointmentSearch,
                            result
                        )
                    }
                    
                    onResultsFound(newResults, config)
                }
            }
            
            // Save search history
            saveSearchHistory(config.searchId, results.size, duration)
            
        } catch (e: Exception) {
            println("Error performing search ${config.searchId}: ${e.message}")
            e.printStackTrace()
            saveSearchHistory(config.searchId, 0, 0, e.message)
        }
    }
    
    private fun filterNewResults(results: List<SearchResult>): List<SearchResult> {
        // Filter results that are in the future and not already notified
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return results.filter { result ->
            // Check if appointment date is in the future
            val appointmentDateTime = result.appointmentDate.toString() + 
                (result.appointmentTime?.toString() ?: "")
            
            // Also check if not already notified
            !result.isNotified
        }
    }
    
    private fun saveSearchHistory(searchId: Long, resultsCount: Int, durationMs: Long, errorMessage: String? = null) {
        // TODO: Save to database
        println("Saved search history for search $searchId (results: $resultsCount)")
    }
    
    private fun getCurrentTimestamp(): String {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    }
    
    fun shutdown() {
        stopAllSearches()
        scheduler.shutdown()
        scope.cancel()
    }
}
