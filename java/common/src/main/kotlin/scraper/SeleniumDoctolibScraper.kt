package com.termintracker.scraper

import com.termintracker.model.search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import io.github.bonigarcia.wdm.WebDriverManager
import java.time.Duration

class SeleniumDoctolibScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.DOCTOLIB
    override val name: String = "Doctolib-Selenium"
    
    private val baseUrl = "https://www.doctolib.de"
    
    init {
        WebDriverManager.firefoxdriver().setup()
    }
    
    override fun getBaseUrl(): String = baseUrl
    
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val options = FirefoxOptions()
            options.addArguments("--headless")
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
            
            val driver = FirefoxDriver(options)
            driver.get(baseUrl)
            val available = driver.title.contains("Doctolib")
            driver.quit()
            available
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
        var driver: WebDriver? = null
        
        try {
            // Browser setup
            val options = FirefoxOptions()
            options.addArguments("--headless")
            options.addArguments("--no-sandbox")
            options.addArguments("--disable-dev-shm-usage")
            options.addArguments("--disable-blink-features=AutomationControlled")
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            
            driver = FirefoxDriver(options)
            val wait = WebDriverWait(driver, Duration.ofSeconds(15))
            
            // Build search URL for MRT
            val searchTerm = when (category) {
                AppointmentCategory.MRT -> "mrt"
                else -> category.name.lowercase()
            }
            
            val url = "$baseUrl/$searchTerm/${location.city.lowercase()}"
            println("🔍 Selenium Arama: $url")
            driver.get(url)
            
            // Wait for results to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dl-search-result")))
            
            // Extract results
            val resultElements = driver.findElements(By.cssSelector(".dl-search-result"))
            
            resultElements.take(5).forEachIndexed { index, element ->
                try {
                    val name = element.findElement(By.cssSelector(".dl-search-result-name")).text
                    val address = element.findElement(By.cssSelector(".dl-search-result-address")).text
                    
                    results.add(SearchResult(
                        id = System.currentTimeMillis() + index,
                        searchId = 0L,
                        sourceType = SearchType.DOCTOLIB,
                        sourceName = name,
                        sourceUrl = url,
                        appointmentDate = dateRange.startDate,
                        appointmentTime = null,
                        doctorName = name,
                        clinicName = name,
                        address = address,
                        phoneNumber = null,
                        bookingUrl = url,
                        isAvailable = true,
                        foundAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                        isNotified = false,
                        notifiedAt = null
                    ))
                    
                    println("✅ Bulundu: $name")
                    
                } catch (e: Exception) {
                    println("⚠️ Element parse hatası: ${e.message}")
                }
            }
            
        } catch (e: Exception) {
            println("❌ Selenium hatası: ${e.message}")
            e.printStackTrace()
        } finally {
            driver?.quit()
        }
        
        results
    }
}