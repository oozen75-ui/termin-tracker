package com.termintracker.model.search

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentSearch(
    val id: Long = 0,
    val name: String,
    val searchType: SearchType,
    val appointmentCategory: AppointmentCategory,
    val location: SearchLocation,
    val dateRange: DateRange,
    val timeRange: TimeRange? = null,
    val isActive: Boolean = true,
    val checkIntervalMinutes: Int = 15,
    val maxSearchDurationDays: Int = 30,
    val matchCriteria: MatchCriteria = MatchCriteria.EXACT,
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val createdAt: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0, 0),
    val lastCheckedAt: LocalDateTime? = null,
    val nextCheckAt: LocalDateTime? = null
)

@Serializable
enum class SearchType {
    DOCTOLIB,               // Sağlık - Doctolib
    CLINIC_WEBSITE,         // Sağlık - Klinik web sitesi
    PRIVATE_DOCTOR,         // Sağlık - Özel doktor
    BERLIN_SERVICE_PORTAL,  // Resmi - Berlin Service Portal (KVR, Bürgeramt)
    BMV,                    // Resmi - BMV Kfz-Zulassung
    PASSPORT_VISA,          // Resmi - Pasaport/Vize
    GENERIC                 // Diğer
}

@Serializable
enum class AppointmentCategory {
    // Sağlık
    MRT,                    // MRI
    NUKLEARMEDIZIN,         // Nuclear Medicine
    MAMMOGRAFIE,            // Mammography
    HAUSARZT,               // General Practitioner
    FACHARZT,               // Specialist
    ZAHNARZT,               // Dentist
    HNO,                    // Hals-Nasen-Ohren (Kulak Burun Boğaz)
    THERAPIE,               // Therapy
    IMPFUNG,                // Vaccination
    
    // Resmi Kurumlar
    KVR,                    // KVR - Bürgeramt
    BUERGERAMT,             // Bürgeramt
    AUSLAENDERBEHOERDE,     // Ausländerbehörde
    BMV_KFZ_ZULASSUNG,      // Kfz-Zulassung
    BMV_KFZ_ABMELDUNG,      // Kfz-Abmeldung
    BMV_KFZ_UMMELDUNG,      // Kfz-Ummeldung
    BMV_WUNSCHKENNZEICHEN,  // Wunschkennzeichen
    PASSPORT_TURKISH,       // Türk pasaportu
    PASSPORT_GERMAN,        // Alman pasaportu
    VISA_TURKISH,           // Türk vizesi
    VISA_SCHENGEN,          // Schengen vizesi
    
    // Diğer
    SONSTIGES               // Other
}

@Serializable
data class SearchLocation(
    val city: String,
    val postalCode: String? = null,
    val street: String? = null,
    val radiusKm: Int = 10,
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Serializable
data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

@Serializable
data class TimeRange(
    val startTime: LocalTime,
    val endTime: LocalTime
)

@Serializable
enum class MatchCriteria {
    EXACT,          // Only exact matches
    CLOSE,          // Within 3 days of requested date
    FLEXIBLE        // Any available slot in range
}

@Serializable
data class NotificationSettings(
    val emailEnabled: Boolean = false,
    val emailAddress: String? = null,
    val telegramEnabled: Boolean = false,
    val telegramChatId: String? = null,
    val desktopNotificationEnabled: Boolean = true,
    val notifyOnNewSlot: Boolean = true,
    val notifyOnBetterSlot: Boolean = false
)

@Serializable
data class SearchResult(
    val id: Long = 0,
    val searchId: Long,
    val sourceType: SearchType,
    val sourceName: String,
    val sourceUrl: String,
    val appointmentDate: LocalDate,
    val appointmentTime: LocalTime? = null,
    val doctorName: String? = null,
    val clinicName: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val bookingUrl: String? = null,
    val isAvailable: Boolean = true,
    val foundAt: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0, 0),
    val isNotified: Boolean = false,
    val notifiedAt: LocalDateTime? = null,
    val isBooked: Boolean = false,
    val notes: String? = null
)

@Serializable
data class SentNotification(
    val id: Long = 0,
    val searchId: Long,
    val resultId: Long? = null,
    val notificationType: NotificationType,
    val recipient: String,
    val subject: String? = null,
    val content: String,
    val sentAt: LocalDateTime = LocalDateTime(2024, 1, 1, 0, 0, 0),
    val status: NotificationStatus = NotificationStatus.PENDING,
    val errorMessage: String? = null
)

@Serializable
enum class NotificationType {
    EMAIL,
    TELEGRAM,
    DESKTOP
}

@Serializable
enum class NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    DELIVERED
}

// Extension functions
fun AppointmentCategory.getDisplayName(): String = when (this) {
    AppointmentCategory.MRT -> "MRT (Magnetresonanztomographie)"
    AppointmentCategory.NUKLEARMEDIZIN -> "Nuklearmedizin"
    AppointmentCategory.MAMMOGRAFIE -> "Mammographie"
    AppointmentCategory.HAUSARZT -> "Hausarzt"
    AppointmentCategory.FACHARZT -> "Facharzt"
    AppointmentCategory.ZAHNARZT -> "Zahnarzt"
    AppointmentCategory.THERAPIE -> "Therapie"
    AppointmentCategory.IMPFUNG -> "Impfung"
    AppointmentCategory.SONSTIGES -> "Sonstiges"
    // Resmi kurumlar (yapım aşamasında)
    else -> "Sonstiges"
}

fun SearchType.getDisplayName(): String = when (this) {
    SearchType.DOCTOLIB -> "Doctolib"
    SearchType.CLINIC_WEBSITE -> "Klinik Website"
    SearchType.PRIVATE_DOCTOR -> "Privat Arzt"
    SearchType.GENERIC -> "Generisch"
    // Resmi kurumlar (yapım aşamasında)
    else -> "Generisch"
}

fun MatchCriteria.getDisplayName(): String = when (this) {
    MatchCriteria.EXACT -> "Nur exakte Übereinstimmung"
    MatchCriteria.CLOSE -> "±3 Tage flexibel"
    MatchCriteria.FLEXIBLE -> "Beliebiger Termin im Zeitraum"
}
