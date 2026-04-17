package com.termintracker.scraper

import com.termintracker.model.search.*
import com.termintracker.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*

/**
 * Pasaport ve Vize Scraper
 * NOT: Konsolosluklar genellikle kendi sistemlerini kullanır
 */
class PassportVisaScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.PASSPORT_VISA
    override val name: String = "Pasaport & Vize"
    
    override fun getBaseUrl(): String = "https://berlin.bk.mfa.gov.tr"
    
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
            when (category) {
                AppointmentCategory.PASSPORT_TURKISH -> {
                    results.add(
                        SearchResult(
                            id = 3L,
                            searchId = searchId,
                            sourceType = searchType,
                            sourceName = "Türk Pasaportu",
                            sourceUrl = "https://berlin.bk.mfa.gov.tr",
                            appointmentDate = dateRange.startDate,
                            appointmentTime = LocalTime(9, 0),
                            clinicName = "Berlin Türk Başkonsolosluğu",
                            address = "Heerdter Straße 34, 14193 Berlin",
                            phoneNumber = "+49 30 275850",
                            bookingUrl = "https://berlin.bk.mfa.gov.tr",
                            isAvailable = true,
                            notes = "Pasaport randevusu için:\n" +
                                   "Telefon: +49 30 275850\n" +
                                   "Email: berlin@konsolosluk.gov.tr\n" +
                                   "Saat: 09:00-12:00 (Pzt-Cuma)\n" +
                                   "Gerekli: Eski pasaport, biyometrik fotoğraf, kimlik"
                        )
                    )
                }
                AppointmentCategory.VISA_TURKISH -> {
                    results.add(
                        SearchResult(
                            id = 4L,
                            searchId = searchId,
                            sourceType = searchType,
                            sourceName = "Türk Vizesi",
                            sourceUrl = "https://www.evisa.gov.tr",
                            appointmentDate = dateRange.startDate,
                            appointmentTime = LocalTime(10, 0),
                            clinicName = "E-Visa Sistemi",
                            address = "Online başvuru",
                            bookingUrl = "https://www.evisa.gov.tr",
                            isAvailable = true,
                            notes = "Türk vizesi için:\n" +
                                   "1. E-Visa: https://www.evisa.gov.tr (Turistik)\n" +
                                   "2. Konsolosluk: Çalışma/öğrenci vizesi\n" +
                                   "Gerekli: Pasaport, fotoğraf, seyahat belgesi"
                        )
                    )
                }
                AppointmentCategory.PASSPORT_GERMAN -> {
                    results.add(
                        SearchResult(
                            id = 5L,
                            searchId = searchId,
                            sourceType = searchType,
                            sourceName = "Alman Pasaportu",
                            sourceUrl = "https://service.berlin.de/dienstleistung/120691",
                            appointmentDate = dateRange.startDate,
                            appointmentTime = LocalTime(9, 0),
                            clinicName = "Bürgeramt",
                            address = location.city,
                            bookingUrl = "https://service.berlin.de/dienstleistung/120691",
                            isAvailable = true,
                            notes = "Alman pasaportu için:\n" +
                                   "Berlin Service Portal üzerinden randevu alın\n" +
                                   "Gerekli: Eski pasaport, kimlik, biyometrik fotoğraf, doğum belgesi"
                        )
                    )
                }
                AppointmentCategory.VISA_SCHENGEN -> {
                    results.add(
                        SearchResult(
                            id = 6L,
                            searchId = searchId,
                            sourceType = searchType,
                            sourceName = "Schengen Vizesi",
                            sourceUrl = "https://www.schengenvisainfo.com",
                            appointmentDate = dateRange.startDate,
                            appointmentTime = LocalTime(9, 0),
                            clinicName = "Konsolosluk",
                            address = "Hedef ülke",
                            bookingUrl = "https://www.schengenvisainfo.com",
                            isAvailable = true,
                            notes = "Schengen vizesi için:\n" +
                                   "Hedef ülkenin konsolosluğu üzerinden başvuru\n" +
                                   "Gerekli: Pasaport, seyahat sigortası, konaklama, mali durum"
                        )
                    )
                }
                else -> {
                    Logger.warn("Unknown passport/visa category: $category")
                }
            }
            
        } catch (e: Exception) {
            Logger.error("Passport/Visa search error", e)
        }
        
        results
    }
}