import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class Doctor(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val specialty: String,
    val insurance: List<String>,
    val bookingUrl: String,
    val nextAvailable: String,
    val rating: Double,
    val reviewCount: Int
)

@Serializable
data class DoctorDatabase(
    val lastUpdated: String,
    val category: String,
    val location: String,
    val doctors: List<Doctor>
)

fun main() {
    println("🚀 OFFLINE VERİ TESTİ: Hals-Weichteile MRT - Berlin")
    println("====================================================")
    
    val jsonText = object {}.javaClass.getResource("/berlin_mrt_doctors.json")?.readText()
        ?: File("common/src/main/resources/berlin_mrt_doctors.json").readText()
    
    val database = Json.decodeFromString<DoctorDatabase>(jsonText)
    
    println("✅ Veritabanı yüklendi!")
    println("📅 Son güncelleme: ${database.lastUpdated}")
    println("📍 Lokasyon: ${database.location}")
    println("🔢 Toplam doktor: ${database.doctors.size}")
    println()
    
    database.doctors.forEachIndexed { index, doctor ->
        println("${index + 1}. ${doctor.name}")
        println("   📍 ${doctor.address}")
        println("   📞 ${doctor.phone}")
        println("   🏥 ${doctor.specialty}")
        println("   💰 Sigorta: ${doctor.insurance.joinToString(", ")}")
        println("   📅 En yakın slot: ${doctor.nextAvailable}")
        println("   ⭐ ${doctor.rating}/5 (${doctor.reviewCount} yorum)")
        println()
    }
    
    println("✅ OFFLINE TEST BAŞARILI!")
    println("💡 Not: Yarın online scraping eklenecek")
}

main()
