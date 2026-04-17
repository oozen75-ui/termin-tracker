package com.termintracker.scraper

import com.termintracker.model.search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class GenericScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.GENERIC
    override val name: String = "Generisch"
    
    override fun getBaseUrl(): String = ""
    
    override suspend fun isAvailable(): Boolean = true
    
    override suspend fun searchAppointments(
        category: AppointmentCategory,
        location: SearchLocation,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        // Generic scraper - requires specific URLs to scrape
        emptyList()
    }
    
    suspend fun scrapeGenericWebsite(
        websiteUrl: String,
        selectors: GenericSelectors,
        category: AppointmentCategory,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            applyRateLimit()
            
            val doc = Jsoup.connect(websiteUrl)
                .timeout(15000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .get()
            
            val elements = doc.select(selectors.containerSelector)
            
            elements.forEach { element ->
                parseGenericElement(element, selectors, websiteUrl)?.let {
                    results.add(it)
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        results
    }
    
    private fun parseGenericElement(
        element: Element,
        selectors: GenericSelectors,
        sourceUrl: String
    ): SearchResult? {
        try {
            val dateText = selectors.dateSelector?.let { 
                element.selectFirst(it)?.text() 
            }
            
            val timeText = selectors.timeSelector?.let { 
                element.selectFirst(it)?.text() 
            }
            
            val name = selectors.nameSelector?.let { 
                element.selectFirst(it)?.text()?.trim() 
            } ?: "Unbekannt"
            
            val address = selectors.addressSelector?.let { 
                element.selectFirst(it)?.text() 
            }
            
            val date = dateText?.let { parseDate(it) }
            val time = timeText?.let { parseTime(it) }
            
            if (date != null) {
                return SearchResult(
                    searchId = 0,
                    sourceType = SearchType.GENERIC,
                    sourceName = "Generisch",
                    sourceUrl = sourceUrl,
                    appointmentDate = date,
                    appointmentTime = time,
                    doctorName = name,
                    address = address,
                    bookingUrl = sourceUrl
                )
            }
        } catch (e: Exception) {
            // Return null if parsing fails
        }
        
        return null
    }
    
    data class GenericSelectors(
        val containerSelector: String,
        val dateSelector: String? = null,
        val timeSelector: String? = null,
        val nameSelector: String? = null,
        val addressSelector: String? = null
    )
}
