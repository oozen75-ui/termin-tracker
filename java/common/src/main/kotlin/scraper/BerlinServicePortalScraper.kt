package com.termintracker.scraper

import com.termintracker.model.search.*
import com.termintracker.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

/**
 * Berlin Service Portal (KVR, Bürgeramt) Scraper
 * NOT: Gerçek entegrasyon için service.berlin.de API kullanılmalı
 */
class BerlinServicePortalScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.BERLIN_SERVICE_PORTAL
    override val name: String = "Berlin Service Portal"
    
    override fun getBaseUrl(): String = "https://service.berlin.de"
    
    override suspend fun isAvailable(): Boolean = true // Manuel kontrol önerilir
    
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
                AppointmentCategory.KVR -> "KVR - Bürgeramt"
                AppointmentCategory.BUERGERAMT -> "Bürgeramt"
                AppointmentCategory.AUSLAENDERBEHOERDE -> "Ausländerbehörde"
                else -> "Behörde"
            }
            
            // Bilgilendirme sonucu (gerçek arama API ile yapılmalı)
            results.add(
                SearchResult(
                    id = 1L,
                    searchId = searchId,
                    sourceType = searchType,
                    sourceName = serviceName,
                    sourceUrl = "https://service.berlin.de",
                    appointmentDate = dateRange.startDate,
                    appointmentTime = LocalTime(9, 0),
                    clinicName = "Berlin $serviceName",
                    address = location.city,
                    phoneNumber = "+49 30 115",
                    bookingUrl = "https://service.berlin.de/terminvereinbarung/",
                    isAvailable = true,
                    notes = "Berlin Service Portal üzerinden manuel arama yapılmalı.\n" +
                           "Online: https://service.berlin.de/terminvereinbarung/\n" +
                           "Telefon: 115 (Berlin)"
                )
            )
            
        } catch (e: Exception) {
            Logger.error("Berlin Service Portal search error", e)
        }
        
        results
    }
}