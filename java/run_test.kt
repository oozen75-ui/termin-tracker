@file:DependsOn("org.jsoup:jsoup:1.17.2")
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun main() {
    println("🔍 Hals-Weichteile MRT Arama - Berlin")
    println("=====================================")
    
    try {
        val doc: Document = Jsoup.connect("https://www.doctolib.de/radiologie/mrt/berlin")
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .timeout(15000)
            .get()
        
        val results = doc.select(".dl-search-result")
        println("✅ Bağlantı başarılı!")
        println("Bulunan sonuç sayısı: ${results.size}")
        
        results.take(3).forEachIndexed { index, element ->
            val name = element.select(".dl-search-result-name").text()
            val address = element.select(".dl-search-result-address").text()
            println("${index + 1}. $name")
            println("   📍 $address")
            println()
        }
        
    } catch (e: Exception) {
        println("⚠️ Hata: ${e.message}")
        println("ℹ️  Muhtemel nedenler:")
        println("   - Gece saati rate limiting")
        println("   - Bot koruması")
        println("   - JavaScript gereksinimi")
    }
}
