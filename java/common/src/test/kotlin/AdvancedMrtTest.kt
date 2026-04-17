package com.termintracker

import com.termintracker.scraper.AdvancedDoctolibScraper
import com.termintracker.model.search.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.junit.Test

class AdvancedMrtTest {
    @Test
    fun testAdvancedHalsWeichteileMrt() {
        println("🚀 GELİŞMİŞ ARAMA: Hals-Weichteile MRT - Berlin")
        println("================================================")
        println("⏰ Başlangıç: ${java.time.LocalTime.now()}")
        println()
        
        val scraper = AdvancedDoctolibScraper()
        
        val location = SearchLocation(
            city = "Berlin",
            postalCode = "10115",
            radiusKm = 10
        )
        
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dateRange = DateRange(
            startDate = today,
            endDate = today.plus(30, DateTimeUnit.DAY)
        )
        
        runBlocking {
            try {
                println("⏳ Selenium başlatılıyor (30-45 saniye)...")
                val results = scraper.searchAppointments(
                    category = AppointmentCategory.MRT,
                    location = location,
                    dateRange = dateRange,
                    timeRange = null
                )
                
                println()
                println("✅ ARAMA TAMAMLANDI!")
                println("Bulunan sonuç sayısı: ${results.size}")
                println()
                
                if (results.isEmpty()) {
                    println("⚠️ Sonuç bulunamadı. Muhtemelen nedenler:")
                    println("   - Doctolib bot koruması")
                    println("   - Yanlış sayfa yapısı")
                    println("   - Sayfa yüklenmedi")
                } else {
                    results.forEachIndexed { index, result ->
                        println("${index + 1}. ${result.sourceName}")
                        println("   📍 ${result.address}")
                        println()
                    }
                }
                
                println("⏰ Bitiş: ${java.time.LocalTime.now()}")
                
            } catch (e: Exception) {
                println("❌ HATA: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }
}