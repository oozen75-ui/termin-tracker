package com.termintracker.utils

import com.termintracker.model.Appointment
import com.termintracker.model.AppointmentExport
import com.termintracker.model.PersonalInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object JsonExportImport {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    data class FullExport(
        val personalInfo: PersonalInfo,
        val appointments: List<Appointment>,
        val exportDate: kotlinx.datetime.LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        val version: String = "1.0"
    )

    fun exportToJson(appointments: List<Appointment>, personalInfo: PersonalInfo? = null): String {
        val export = if (personalInfo != null) {
            json.encodeToString(FullExport(personalInfo, appointments))
        } else {
            json.encodeToString(AppointmentExport(appointments))
        }
        return export
    }

    fun exportToFile(appointments: List<Appointment>, filePath: String, personalInfo: PersonalInfo? = null): Boolean {
        return try {
            val jsonString = exportToJson(appointments, personalInfo)
            File(filePath).writeText(jsonString)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importFromJson(jsonString: String): List<Appointment> {
        return try {
            // Try full export first
            val fullExport = json.decodeFromString<FullExport>(jsonString)
            fullExport.appointments
        } catch (e: Exception) {
            // Try appointment-only export
            val export = json.decodeFromString<AppointmentExport>(jsonString)
            export.appointments
        }
    }

    fun importFromFile(filePath: String): List<Appointment>? {
        return try {
            val jsonString = File(filePath).readText()
            importFromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun importPersonalInfoFromJson(jsonString: String): PersonalInfo? {
        return try {
            val fullExport = json.decodeFromString<FullExport>(jsonString)
            fullExport.personalInfo
        } catch (e: Exception) {
            null
        }
    }
}
