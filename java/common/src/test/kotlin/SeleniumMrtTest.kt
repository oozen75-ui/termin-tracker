package com.termintracker

import com.termintracker.scraper.SeleniumDoctolibScraper
import com.termintracker.model.search.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.junit.Test

class SeleniumMrtTest {
    @Test
    fun testHalsWeichteileMrt() {
        println("🚀 GERÇEK ARAMA: Hals-Weichteile MRT - Berlin")
        println("================================================")
        
        val scraper = SeleniumDoctolibScraper()
        
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
                println("⏳ Arama başlatılıyor... (30-60 saniye)")
                val results = scraper.searchAppointments(
                    category = AppointmentCategory.MRT,
                    location = location,
                    dateRange = dateRange,
                    timeRange = null
                )
                
                println()
                println("✅ ARAMA TAMAMLANDI! Sonuç: ${results.size}")
                results.forEachIndexed { index, result ->
                    println("${index + 1}. ${result.sourceName}")
                    println("   📍 ${result.address}")
                }
                
            } catch (e: Exception) {
                println("❌ HATA: ${e.message}")
                throw e
            }
        }
    }
}
