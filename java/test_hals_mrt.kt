import com.termintracker.scraper.SeleniumDoctolibScraper
import com.termintracker.model.search.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*

fun main() {
    println("🚀 GERÇEK ARAMA: Hals-Weichteile MRT - Berlin")
    println("================================================")
    
    val scraper = SeleniumDoctolibScraper()
    
    val location = SearchLocation(
        city = "Berlin",
        postalCode = "10115",
        district = "Mitte"
    )
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dateRange = DateRange(
        startDate = today,
        endDate = today.plus(30, DateTimeUnit.DAY)
    )
    
    runBlocking {
        try {
            println("⏳ Arama başlatılıyor... (Bu 30-60 saniye sürebilir)")
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
            
            results.forEachIndexed { index, result ->
                println("${index + 1}. ${result.sourceName}")
                println("   📍 ${result.address}")
                println("   🔗 ${result.bookingUrl}")
                println()
            }
            
        } catch (e: Exception) {
            println("❌ HATA: ${e.message}")
            e.printStackTrace()
        }
    }
}
