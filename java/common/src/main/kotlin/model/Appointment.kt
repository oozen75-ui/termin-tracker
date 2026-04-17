package com.termintracker.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Serializable
data class Appointment(
    val id: Long = 0,
    val title: String,
    val type: AppointmentType,
    val dateTime: LocalDateTime,
    val location: String = "",
    val address: Address = Address(),
    val notes: String = "",
    val reminderMinutes: Int = 30,
    val isReminderEnabled: Boolean = true,
    val documents: List<Document> = emptyList(),
    val requiredDocuments: List<RequiredDocument> = emptyList(),
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val updatedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val isCompleted: Boolean = false
)

@Serializable
data class Address(
    val street: String = "",
    val houseNumber: String = "",
    val postalCode: String = "",
    val city: String = "",
    val district: String = "",
    val country: String = "Deutschland",
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    fun toDisplayString(): String {
        val parts = mutableListOf<String>()
        if (street.isNotBlank()) {
            parts.add("$street ${houseNumber}".trim())
        }
        if (postalCode.isNotBlank() && city.isNotBlank()) {
            parts.add("$postalCode $city")
        }
        if (district.isNotBlank() && !parts.any { it.contains(district) }) {
            parts.add("($district)")
        }
        return parts.joinToString(", ")
    }

    fun isComplete(): Boolean {
        return street.isNotBlank() && postalCode.isNotBlank() && city.isNotBlank()
    }
}

@Serializable
data class Document(
    val id: Long = 0,
    val name: String,
    val filePath: String? = null,
    val isUploaded: Boolean = false,
    val uploadDate: LocalDateTime? = null,
    val notes: String = ""
)

@Serializable
data class RequiredDocument(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val isRequired: Boolean = true,
    val isChecked: Boolean = false,
    val appointmentType: AppointmentType? = null
)

@Serializable
data class AppointmentExport(
    val appointments: List<Appointment>,
    val exportDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val version: String = "1.0"
)
