package com.termintracker.scraper

import com.termintracker.model.search.*
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface AppointmentScraper {
    val searchType: SearchType
    val name: String
    
    suspend fun searchAppointments(
        category: AppointmentCategory,
        location: SearchLocation,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult>
    
    suspend fun isAvailable(): Boolean
    
    fun getBaseUrl(): String
}

abstract class BaseScraper : AppointmentScraper {
    
    protected var lastRequestTime: Long = 0
    protected val minRequestIntervalMs: Long = 1000 // 1 second between requests
    
    protected suspend fun applyRateLimit() {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRequest = currentTime - lastRequestTime
        if (timeSinceLastRequest < minRequestIntervalMs) {
            delay(minRequestIntervalMs - timeSinceLastRequest)
        }
        lastRequestTime = System.currentTimeMillis()
    }
    
    protected fun createSearchResult(
        searchId: Long,
        sourceType: SearchType,
        sourceName: String,
        sourceUrl: String,
        date: LocalDate,
        time: LocalTime? = null,
        doctorName: String? = null,
        clinicName: String? = null,
        address: String? = null,
        phoneNumber: String? = null,
        bookingUrl: String? = null
    ): SearchResult {
        return SearchResult(
            searchId = searchId,
            sourceType = sourceType,
            sourceName = sourceName,
            sourceUrl = sourceUrl,
            appointmentDate = date,
            appointmentTime = time,
            doctorName = doctorName,
            clinicName = clinicName,
            address = address,
            phoneNumber = phoneNumber,
            bookingUrl = bookingUrl
        )
    }
    
    protected fun categoryToSearchTerm(category: AppointmentCategory): String {
        return when (category) {
            AppointmentCategory.MRT -> "mrt"
            AppointmentCategory.NUKLEARMEDIZIN -> "nuklearmedizin"
            AppointmentCategory.MAMMOGRAFIE -> "mammographie"
            AppointmentCategory.HAUSARZT -> "hausarzt"
            AppointmentCategory.FACHARZT -> "facharzt"
            AppointmentCategory.ZAHNARZT -> "zahnarzt"
            AppointmentCategory.THERAPIE -> "therapie"
            AppointmentCategory.IMPFUNG -> "impfung"
            AppointmentCategory.SONSTIGES -> ""
            // Resmi kurumlar için search term'ler (yapım aşamasında)
            else -> ""
        }
    }
    
    protected fun parseTime(timeString: String): LocalTime? {
        return try {
            val parts = timeString.split(":")
            if (parts.size >= 2) {
                LocalTime(parts[0].toInt(), parts[1].toInt())
            } else null
        } catch (e: Exception) {
            null
        }
    }
    
    protected fun parseDate(dateString: String): LocalDate? {
        // Try different date formats
        val patterns = listOf(
            Regex("""(\d{2})\.(\d{2})\.(\d{4})"""), // DD.MM.YYYY
            Regex("""(\d{4})-(\d{2})-(\d{2})"""),  // YYYY-MM-DD
            Regex("""(\d{2})/(\d{2})/(\d{4})"""),  // MM/DD/YYYY
            Regex("""(\d{2})-(\d{2})-(\d{4})""")  // DD-MM-YYYY
        )
        
        for (pattern in patterns) {
            val match = pattern.find(dateString)
            if (match != null) {
                return try {
                    when (patterns.indexOf(pattern)) {
                        0 -> LocalDate(match.groupValues[3].toInt(), match.groupValues[2].toInt(), match.groupValues[1].toInt())
                        1 -> LocalDate(match.groupValues[1].toInt(), match.groupValues[2].toInt(), match.groupValues[3].toInt())
                        2 -> LocalDate(match.groupValues[3].toInt(), match.groupValues[1].toInt(), match.groupValues[2].toInt())
                        3 -> LocalDate(match.groupValues[3].toInt(), match.groupValues[2].toInt(), match.groupValues[1].toInt())
                        else -> null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
        return null
    }
}

// Factory for creating scrapers
object ScraperFactory {
    fun createScraper(searchType: SearchType): AppointmentScraper {
        return when (searchType) {
            SearchType.DOCTOLIB -> DoctolibScraper()
            SearchType.CLINIC_WEBSITE -> ClinicWebsiteScraper()
            SearchType.PRIVATE_DOCTOR -> PrivateDoctorScraper()
            SearchType.GENERIC -> GenericScraper()
            // Resmi kurum scraper'ları yapım aşamasında
            else -> GenericScraper()
        }
    }
    
    fun getAvailableScrapers(): List<AppointmentScraper> {
        return listOf(
            DoctolibScraper(),
            ClinicWebsiteScraper(),
            PrivateDoctorScraper(),
            GenericScraper()
        )
    }
}
