package com.termintracker.data

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime

@Serializable
data class Appointment(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val city: String,
    val district: String,
    val postalCode: String,
    val address: String,
    val appointmentType: AppointmentType,
    val appointmentDate: String,
    val appointmentTime: String,
    val notes: String = "",
    val reminderMinutes: Int = 60,
    val isCompleted: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class AppointmentType {
    KVR,           // Bürgeramt, KVR
    AMT,           // Diğer resmi daireler
    KRANKENKASSE,  // Sağlık sigortası
    BANK,          // Banka işlemleri
    SONSTIGES      // Diğer
}

fun AppointmentType.getDisplayName(language: String = "de"): String {
    return when (language) {
        "tr" -> when (this) {
            AppointmentType.KVR -> "Bürgeramt/KVR"
            AppointmentType.AMT -> "Resmi Daire"
            AppointmentType.KRANKENKASSE -> "Sağlık Sigortası"
            AppointmentType.BANK -> "Banka"
            AppointmentType.SONSTIGES -> "Diğer"
        }
        "en" -> when (this) {
            AppointmentType.KVR -> "Citizens Office"
            AppointmentType.AMT -> "Government Office"
            AppointmentType.KRANKENKASSE -> "Health Insurance"
            AppointmentType.BANK -> "Bank"
            AppointmentType.SONSTIGES -> "Other"
        }
        else -> when (this) {
            AppointmentType.KVR -> "Bürgeramt/KVR"
            AppointmentType.AMT -> "Amt/Behörde"
            AppointmentType.KRANKENKASSE -> "Krankenkasse"
            AppointmentType.BANK -> "Bank"
            AppointmentType.SONSTIGES -> "Sonstiges"
        }
    }
}

fun AppointmentType.getRequiredDocuments(): List<String> {
    val baseDocs = listOf("passport", "residence_permit")
    return when (this) {
        AppointmentType.KVR -> baseDocs + listOf("registration_form", "rental_contract")
        AppointmentType.AMT -> baseDocs + listOf("appointment_confirmation")
        AppointmentType.KRANKENKASSE -> baseDocs + listOf("insurance_card", "income_proof")
        AppointmentType.BANK -> baseDocs + listOf("income_proof", "schufa")
        AppointmentType.SONSTIGES -> baseDocs
    }
}
