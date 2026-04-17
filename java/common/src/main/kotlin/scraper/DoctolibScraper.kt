package com.termintracker.scraper

import com.termintracker.model.search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.time.format.DateTimeFormatter

class DoctolibScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.DOCTOLIB
    override val name: String = "Doctolib"
    
    private val baseUrl = "https://www.doctolib.de"
    private val searchUrl = "$baseUrl/suche"
    
    override fun getBaseUrl(): String = baseUrl
    
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            applyRateLimit()
            val connection = Jsoup.connect(baseUrl)
                .timeout(10000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            connection.execute().statusCode() == 200
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun searchAppointments(
        category: AppointmentCategory,
        location: SearchLocation,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            // Step 1: Search for practitioners
            val practitioners = searchPractitioners(category, location)
            
            // Step 2: Check availability for each practitioner
            practitioners.forEach { practitioner ->
                try {
                    applyRateLimit()
                    val availability = checkPractitionerAvailability(
                        practitioner,
                        dateRange,
                        timeRange
                    )
                    results.addAll(availability)
                } catch (e: Exception) {
                    // Continue with next practitioner
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        results
    }
    
    private suspend fun searchPractitioners(
        category: AppointmentCategory,
        location: SearchLocation
    ): List<PractitionerInfo> = withContext(Dispatchers.IO) {
        val practitioners = mutableListOf<PractitionerInfo>()
        
        try {
            applyRateLimit()
            
            // Build search URL
            val searchTerm = buildSearchTerm(category, location)
            val encodedSearch = URLEncoder.encode(searchTerm, "UTF-8")
            val url = "$searchUrl?search=$encodedSearch"
            
            val doc = Jsoup.connect(url)
                .timeout(15000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .get()
            
            // Parse practitioner listings
            val listings = doc.select("div.dl-search-result, div.search-result-card, article.dl-search-result")
            
            listings.take(10).forEach { element ->
                val practitioner = parsePractitionerElement(element)
                if (practitioner != null) {
                    practitioners.add(practitioner)
                }
            }
            
            // Alternative selectors if first didn't work
            if (practitioners.isEmpty()) {
                val altListings = doc.select("[data-test-id='search-result'], .search-result, .doctor-card")
                altListings.take(10).forEach { element ->
                    val practitioner = parsePractitionerElement(element)
                    if (practitioner != null) {
                        practitioners.add(practitioner)
                    }
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        practitioners
    }
    
    private fun buildSearchTerm(category: AppointmentCategory, location: SearchLocation): String {
        val categoryTerm = when (category) {
            AppointmentCategory.MRT -> "MRT"
            AppointmentCategory.NUKLEARMEDIZIN -> "Nuklearmedizin"
            AppointmentCategory.MAMMOGRAFIE -> "Mammographie"
            AppointmentCategory.HAUSARZT -> "Hausarzt Allgemeinmedizin"
            AppointmentCategory.FACHARZT -> "Facharzt"
            AppointmentCategory.ZAHNARZT -> "Zahnarzt"
            AppointmentCategory.THERAPIE -> "Therapie"
            AppointmentCategory.IMPFUNG -> "Impfung"
            AppointmentCategory.SONSTIGES -> "Arzt"
            // Resmi kurumlar Doctolib'de yok
            else -> "Arzt"
        }
        
        return "$categoryTerm ${location.city}".trim()
    }
    
    private fun parsePractitionerElement(element: Element): PractitionerInfo? {
        try {
            // Try multiple selectors for name
            val name = element.selectFirst(
                "h3.dl-search-result-name, .doctor-name, h2, h3, .result-title, [data-test-id='doctor-name']"
            )?.text()?.trim() ?: return null
            
            // Try multiple selectors for specialty
            val specialty = element.selectFirst(
                ".dl-search-result-speciality, .specialty, .doctor-specialty, [data-test-id='doctor-specialty']"
            )?.text()?.trim() ?: ""
            
            // Try multiple selectors for address
            val address = element.selectFirst(
                ".dl-search-result-address, .address, .doctor-address, [data-test-id='doctor-address']"
            )?.text()?.trim() ?: ""
            
            // Try multiple selectors for link
            val linkElement = element.selectFirst("a[href]")
            val profileUrl = linkElement?.attr("abs:href") ?: ""
            
            // Extract ID from URL
            val id = extractPractitionerId(profileUrl)
            
            return PractitionerInfo(
                id = id,
                name = name,
                specialty = specialty,
                address = address,
                profileUrl = profileUrl
            )
        } catch (e: Exception) {
            return null
        }
    }
    
    private fun extractPractitionerId(url: String): String {
        val regex = Regex("""/([^/]+)$""")
        val match = regex.find(url)
        return match?.groupValues?.get(1) ?: url.hashCode().toString()
    }
    
    private suspend fun checkPractitionerAvailability(
        practitioner: PractitionerInfo,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        
        try {
            applyRateLimit()
            
            // Navigate to practitioner profile
            val doc = Jsoup.connect(practitioner.profileUrl)
                .timeout(15000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .get()
            
            // Look for availability indicators
            val availabilitySection = doc.selectFirst(
                ".availabilities, .calendar-container, #availabilities, [data-test-id='availabilities']"
            )
            
            if (availabilitySection != null) {
                // Parse available slots
                val slots = parseAvailabilitySlots(availabilitySection, practitioner, dateRange, timeRange)
                results.addAll(slots)
            }
            
            // Also check for next available appointment
            val nextAvailableElement = doc.selectFirst(
                ".next-availability, .next-slot, .prochain-rdv, [data-test-id='next-availability']"
            )
            
            if (nextAvailableElement != null && results.isEmpty()) {
                val nextAvailableText = nextAvailableElement.text()
                parseNextAvailable(nextAvailableText, practitioner, dateRange)?.let {
                    results.add(it)
                }
            }
            
        } catch (e: Exception) {
            // Continue with empty results
        }
        
        results
    }
    
    private fun parseAvailabilitySlots(
        element: Element,
        practitioner: PractitionerInfo,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> {
        val slots = mutableListOf<SearchResult>()
        
        try {
            // Look for day elements
            val dayElements = element.select(".day, .calendar-day, .availability-day, [data-date]")
            
            dayElements.forEach { dayEl ->
                val dateStr = dayEl.attr("data-date") ?: dayEl.selectFirst(".date, .day-date")?.text()
                val date = dateStr?.let { parseGermanDate(it) }
                
                if (date != null && isDateInRange(date, dateRange)) {
                    // Look for time slots
                    val timeElements = dayEl.select(".slot, .time-slot, .available-slot, .hour")
                    
                    timeElements.forEach { timeEl ->
                        val timeText = timeEl.text().trim()
                        val time = parseTime(timeText)
                        
                        if (time != null && isTimeInRange(time, timeRange)) {
                            val bookingUrl = timeEl.attr("abs:href").takeIf { it.isNotEmpty() }
                                ?: practitioner.profileUrl
                            
                            slots.add(SearchResult(
                                searchId = 0, // Will be set by repository
                                sourceType = SearchType.DOCTOLIB,
                                sourceName = "Doctolib",
                                sourceUrl = practitioner.profileUrl,
                                appointmentDate = date,
                                appointmentTime = time,
                                doctorName = practitioner.name,
                                clinicName = practitioner.specialty,
                                address = practitioner.address,
                                bookingUrl = bookingUrl
                            ))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Return empty list
        }
        
        return slots
    }
    
    private fun parseNextAvailable(
        text: String,
        practitioner: PractitionerInfo,
        dateRange: DateRange
    ): SearchResult? {
        // Try to extract date from text like "Nächster Termin: Montag, 15. April 2024"
        val datePattern = Regex("""(\d{1,2})\.\s*([A-Za-zäöü]+)\s*(\d{4})?""")
        val match = datePattern.find(text)
        
        if (match != null) {
            val day = match.groupValues[1].toInt()
            val monthName = match.groupValues[2].lowercase()
            val year = match.groupValues[3].takeIf { it.isNotEmpty() }?.toInt() 
                ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
            
            val month = parseGermanMonth(monthName)
            
            if (month != null) {
                val date = LocalDate(year, month, day)
                if (isDateInRange(date, dateRange)) {
                    return SearchResult(
                        searchId = 0,
                        sourceType = SearchType.DOCTOLIB,
                        sourceName = "Doctolib",
                        sourceUrl = practitioner.profileUrl,
                        appointmentDate = date,
                        doctorName = practitioner.name,
                        clinicName = practitioner.specialty,
                        address = practitioner.address,
                        notes = "Nächster verfügbarer Termin (keine Uhrzeit angegeben)"
                    )
                }
            }
        }
        
        return null
    }
    
    private fun parseGermanDate(dateStr: String): LocalDate? {
        // Handle various German date formats
        val patterns = listOf(
            Regex("""(\d{1,2})\.\s*(\d{1,2})\.\s*(\d{4})"""),      // 15.4.2024
            Regex("""(\d{1,2})\.\s*([A-Za-zäöü]+)\s*(\d{4})?""")    // 15. April 2024
        )
        
        for (pattern in patterns) {
            val match = pattern.find(dateStr)
            if (match != null) {
                val day = match.groupValues[1].toInt()
                
                val month = if (match.groupValues[2].toIntOrNull() != null) {
                    match.groupValues[2].toInt()
                } else {
                    parseGermanMonth(match.groupValues[2].lowercase())
                }
                
                val year = match.groupValues.getOrNull(3)?.toIntOrNull() 
                    ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                
                if (month != null) {
                    return try {
                        LocalDate(year, month, day)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
        return null
    }
    
    private fun parseGermanMonth(monthName: String): Int? {
        return when (monthName.lowercase().replace("ä", "ae").replace("ö", "oe").replace("ü", "ue")) {
            "januar", "jan" -> 1
            "februar", "feb" -> 2
            "maerz", "mar", "märz" -> 3
            "april", "apr" -> 4
            "mai" -> 5
            "juni", "jun" -> 6
            "juli", "jul" -> 7
            "august", "aug" -> 8
            "september", "sep", "sept" -> 9
            "oktober", "okt" -> 10
            "november", "nov" -> 11
            "dezember", "dez" -> 12
            else -> null
        }
    }
    
    private fun isDateInRange(date: LocalDate, range: DateRange): Boolean {
        return date >= range.startDate && date <= range.endDate
    }
    
    private fun isTimeInRange(time: LocalTime, range: TimeRange?): Boolean {
        if (range == null) return true
        return time >= range.startTime && time <= range.endTime
    }
    
    // Data class for practitioner info
    private data class PractitionerInfo(
        val id: String,
        val name: String,
        val specialty: String,
        val address: String,
        val profileUrl: String
    )
}
