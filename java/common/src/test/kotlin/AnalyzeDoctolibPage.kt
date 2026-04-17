package com.termintracker

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.time.Duration

class AnalyzeDoctolibPage {
    @Test
    fun analyzePageStructure() {
        WebDriverManager.firefoxdriver().setup()
        
        val options = FirefoxOptions()
        options.addArguments("--headless")
        options.addArguments("--width=1920")
        options.addArguments("--height=1080")
        
        val driver = FirefoxDriver(options)
        val wait = WebDriverWait(driver, Duration.ofSeconds(20))
        
        try {
            println("🔍 Sayfa yükleniyor...")
            driver.get("https://www.doctolib.de/suche?search[mrt]=1&search[location]=Berlin")
            
            // Daha uzun bekle
            Thread.sleep(10000)
            
            // Sayfa kaynağını al
            val pageSource = driver.pageSource
            File("/tmp/doctolib_page.html").writeText(pageSource)
            println("✅ Sayfa kaydedildi: /tmp/doctolib_page.html")
            
            // Ekran görüntüsü al
            val screenshot = driver.getScreenshotAs(OutputType.FILE)
            screenshot.copyTo(File("/tmp/doctolib_screenshot.png"), overwrite = true)
            println("✅ Ekran görüntüsü kaydedildi: /tmp/doctolib_screenshot.png")
            
            // Elementleri dene
            val selectors = listOf(
                ".dl-search-result",
                "[data-test-id='search-result']",
                ".search-result",
                ".result-card",
                "article",
                ".establishment-card",
                ".doctor-card"
            )
            
            println("\n📋 Element Analizi:")
            selectors.forEach { selector ->
                try {
                    val elements = driver.findElements(By.cssSelector(selector))
                    println("  $selector: ${elements.size} element")
                } catch (e: Exception) {
                    println("  $selector: Hata - ${e.message}")
                }
            }
            
        } catch (e: Exception) {
            println("❌ Hata: ${e.message}")
        } finally {
            driver.quit()
        }
    }
}