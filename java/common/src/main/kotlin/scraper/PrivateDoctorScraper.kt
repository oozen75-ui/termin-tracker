package com.termintracker.scraper

import com.termintracker.model.search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class PrivateDoctorScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.PRIVATE_DOCTOR
    override val name: String = "Privat Arzt"
    
    override fun getBaseUrl(): String = ""
    
    override suspend fun isAvailable(): Boolean = true
    
    override suspend fun searchAppointments(
        category: AppointmentCategory,
        location: SearchLocation,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        // Base implementation - requires specific doctor website URLs
        emptyList()
    }
    
    suspend fun searchSpecificDoctor(
        doctorUrl: String,
        category: AppointmentCategory,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            applyRateLimit()
            
            val doc = Jsoup.connect(doctorUrl)
                .timeout(15000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .get()
            
            // Try to find the doctor's name
            val doctorName = doc.selectFirst(
                "h1, .doctor-name, .practitioner-name, [data-doctor-name]"
            )?.text()?.trim() ?: "Unbekannter Arzt"
            
            // Try to find the address
            val address = doc.selectFirst(
                ".address, .contact-address, [data-address]"
            )?.text()
            
            // Look for appointment availability
            val availabilityElements = doc.select(
                ".availability, .verfuegbarkeit, .next-appointment, " +
                ".termin-verfuegbar, .calendar-slot"
            )
            
            availabilityElements.forEach { element ->
                parseDoctorAvailability(element, doctorName, address, doctorUrl)?.let {
                    results.add(it)
                }
            }
            
            // Look for online booking widget
            val bookingWidget = doc.selectFirst(
                "#booking-widget, .online-termin, .appointment-widget, iframe[src*='termin']"
            )
            
            if (bookingWidget != null && results.isEmpty()) {
                // Add a placeholder result indicating online booking is available
                results.add(SearchResult(
                    searchId = 0,
                    sourceType = SearchType.PRIVATE_DOCTOR,
                    sourceName = "Privat Arzt",
                    sourceUrl = doctorUrl,
                    appointmentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    doctorName = doctorName,
                    address = address,
                    notes = "Online-Terminbuchung verfügbar",
                    bookingUrl = doctorUrl
                ))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        results
    }
    
    private fun parseDoctorAvailability(
        element: Element,
        doctorName: String,
        address: String?,
        sourceUrl: String
    ): SearchResult? {
        try {
            // Try various date formats
            val dateText = element.selectFirst(
                ".date, .datum, [data-date]"
            )?.text() ?: element.text()
            
            val timeText = element.selectFirst(
                ".time, .uhrzeit, [data-time]"
            )?.text()
            
            val date = parseDate(dateText)
            val time = timeText?.let { parseTime(it) }
            
            if (date != null) {
                return SearchResult(
                    searchId = 0,
                    sourceType = SearchType.PRIVATE_DOCTOR,
                    sourceName = "Privat Arzt",
                    sourceUrl = sourceUrl,
                    appointmentDate = date,
                    appointmentTime = time,
                    doctorName = doctorName,
                    address = address,
                    bookingUrl = sourceUrl
                )
            }
        } catch (e: Exception) {
            // Return null if parsing fails
        }
        
        return null
    }
}
