package com.termintracker.scraper

import com.termintracker.model.search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import io.github.bonigarcia.wdm.WebDriverManager
import java.time.Duration

class AdvancedDoctolibScraper : BaseScraper() {
    
    override val searchType: SearchType = SearchType.DOCTOLIB
    override val name: String = "Doctolib-Advanced"
    
    private val baseUrl = "https://www.doctolib.de"
    
    init {
        WebDriverManager.firefoxdriver().setup()
    }
    
    override fun getBaseUrl(): String = baseUrl
    
    private fun createDriver(): WebDriver {
        // Gerçekçi Firefox profili
        val profile = FirefoxProfile()
        profile.setPreference("general.useragent.override", 
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0")
        profile.setPreference("dom.webdriver.enabled", false)
        profile.setPreference("useAutomationExtension", false)
        profile.setPreference("permissions.default.image", 2) // Resimleri yükleme (hız için)
        
        val options = FirefoxOptions()
        options.setProfile(profile)
        options.addArguments("--headless")
        options.addArguments("--width=1920")
        options.addArguments("--height=1080")
        options.addArguments("--disable-blink-features=AutomationControlled")
        
        return FirefoxDriver(options)
    }
    
    override suspend fun isAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val driver = createDriver()
            driver.get(baseUrl)
            Thread.sleep(5000)
            val available = driver.title.contains("Doctolib") && 
                          !driver.title.contains("Bot") &&
                          !driver.title.contains("403")
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
            println("🚀 Gelişmiş Selenium başlatılıyor...")
            driver = createDriver()
            val wait = WebDriverWait(driver, Duration.ofSeconds(30))
            val actions = Actions(driver)
            
            // Önce ana sayfaya git
            driver.get(baseUrl)
            Thread.sleep(8000) // 8 saniye bekle
            
            // Cookie consent varsa kabul et
            try {
                val acceptButton = driver.findElement(By.cssSelector("[data-test-id='cookie-banner-accept']"))
                acceptButton.click()
                Thread.sleep(2000)
                println("✅ Cookie kabul edildi")
            } catch (e: Exception) {
                // Cookie yoksa devam et
            }
            
            // Arama URL'si oluştur
            val searchTerm = when (category) {
                AppointmentCategory.MRT -> "mrt"
                AppointmentCategory.HNO -> "hno-arzt"
                else -> category.name.lowercase()
            }
            
            // Doctolib arama sayfasına git
            val searchUrl = "$baseUrl/suche?search[$searchTerm]=1&search[location]=${location.city}"
            println("🔍 Arama: $searchUrl")
            driver.get(searchUrl)
            
            // Uzun bekleme
            Thread.sleep(15000) // 15 saniye
            
            // Sayfayı kaydır (JavaScript yüklenmesi için)
            actions.scrollByAmount(0, 500).perform()
            Thread.sleep(3000)
            actions.scrollByAmount(0, 500).perform()
            Thread.sleep(3000)
            
            // Farklı selector'lar dene
            val possibleSelectors = listOf(
                ".dl-search-result",
                "[data-test-id='search-result-item']",
                ".search-result",
                ".establishment-card",
                ".doctor-card",
                "article.dl-card",
                ".result-item"
            )
            
            var foundElements = 0
            for (selector in possibleSelectors) {
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)))
                    val elements = driver.findElements(By.cssSelector(selector))
                    foundElements = elements.size
                    println("✅ Selector bulundu: $selector (${elements.size} element)")
                    
                    elements.take(5).forEachIndexed { index, element ->
                        try {
                            val name = element.findElement(By.cssSelector("h3, .name, [data-test-id='doctor-name']")).text
                            val address = try {
                                element.findElement(By.cssSelector(".address, .location, [data-test-id='address']")).text
                            } catch (e: Exception) { "" }
                            
                            results.add(SearchResult(
                                id = System.currentTimeMillis() + index,
                                searchId = 0L,
                                sourceType = SearchType.DOCTOLIB,
                                sourceName = name,
                                sourceUrl = searchUrl,
                                appointmentDate = dateRange.startDate,
                                appointmentTime = null,
                                doctorName = name,
                                clinicName = name,
                                address = address,
                                phoneNumber = null,
                                bookingUrl = searchUrl,
                                isAvailable = true,
                                foundAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                                isNotified = false,
                                notifiedAt = null
                            ))
                            
                        } catch (e: Exception) {
                            println("⚠️ Element parse hatası: ${e.message}")
                        }
                    }
                    
                    if (results.isNotEmpty()) break
                    
                } catch (e: Exception) {
                    println("❌ Selector çalışmadı: $selector")
                }
            }
            
            if (results.isEmpty() && foundElements == 0) {
                println("⚠️ Hiçbir selector çalışmadı. Sayfa kaynağı kaydediliyor...")
                java.io.File("/tmp/doctolib_error.html").writeText(driver.pageSource)
                println("💾 Sayfa kaynağı: /tmp/doctolib_error.html")
            }
            
        } catch (e: Exception) {
            println("❌ Hata: ${e.message}")
            e.printStackTrace()
        } finally {
            driver?.quit()
        }
        
        results
    }
}