package com.termintracker.scraper

import com.termintracker.model.search.*
import com.termintracker.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

/**
 * BMV (Kfz-Zulassung) Scraper
 * NOT: Gerçek entegrasyon için eyalet bazlı API kullanılmalı
 */
class BMVScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.BMV
    override val name: String = "BMV - Kfz-Zulassung"
    
    override fun getBaseUrl(): String = "https://www.berlin.de/bamf"
    
    override suspend fun isAvailable(): Boolean = true
    
    override suspend fun searchAppointments(
        category: AppointmentCategory,
        location: SearchLocation,
        dateRange: DateRange,
        timeRange: TimeRange?
    ): List<SearchResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SearchResult>()
        val searchId = System.currentTimeMillis()
        
        try {
            val serviceName = when (category) {
                AppointmentCategory.BMV_KFZ_ZULASSUNG -> "Kfz-Zulassung"
                AppointmentCategory.BMV_KFZ_ABMELDUNG -> "Kfz-Abmeldung"
                AppointmentCategory.BMV_KFZ_UMMELDUNG -> "Kfz-Ummeldung"
                AppointmentCategory.BMV_WUNSCHKENNZEICHEN -> "Wunschkennzeichen"
                else -> "Kfz-Service"
            }
            
            results.add(
                SearchResult(
                    id = 2L,
                    searchId = searchId,
                    sourceType = searchType,
                    sourceName = "BMV - $serviceName",
                    sourceUrl = "https://www.berlin.de/bamf",
                    appointmentDate = dateRange.startDate,
                    appointmentTime = LocalTime(8, 0),
                    clinicName = "Kfz-Zulassungsstelle ${location.city}",
                    address = location.city,
                    bookingUrl = "https://www.berlin.de/bamf/termin/",
                    isAvailable = true,
                    notes = "Kfz-Zulassung için online randevu:\n" +
                           "1. Berlin Service Portal: https://service.berlin.de\n" +
                           "2. Gerekli belgeler: Fahrzeugschein, Versicherungsnachweis, Identitätsnachweis\n" +
                           "3. Ücret: Kart ile ödeme"
                )
            )
            
        } catch (e: Exception) {
            Logger.error("BMV search error", e)
        }
        
        results
    }
}