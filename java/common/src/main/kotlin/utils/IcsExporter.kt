package com.termintracker.utils

import com.termintracker.model.Appointment
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.hours

object IcsExporter {
    
    fun exportToIcs(appointments: List<Appointment>): String {
        val sb = StringBuilder()
        
        // ICS Header
        sb.appendLine("BEGIN:VCALENDAR")
        sb.appendLine("VERSION:2.0")
        sb.appendLine("PRODID:-//Termin Tracker//DE")
        sb.appendLine("CALSCALE:GREGORIAN")
        sb.appendLine("METHOD:PUBLISH")
        sb.appendLine("X-WR-CALNAME:Termin Tracker Termine")
        
        appointments.forEach { appointment ->
            sb.appendLine("BEGIN:VEVENT")
            sb.appendLine("UID:termin-${appointment.id}@termin-tracker")
            sb.appendLine("DTSTAMP:${formatDateTimeICS(Clock.System.now())}")
            
            // Convert to UTC for ICS format
            val instant = appointment.dateTime.toInstant(TimeZone.currentSystemDefault())
            val endInstant = instant.plus(1.hours)
            
            sb.appendLine("DTSTART:${formatDateTimeICS(instant)}")
            sb.appendLine("DTEND:${formatDateTimeICS(endInstant)}")
            sb.appendLine("SUMMARY:${escapeIcsText(appointment.title)}")
            
            val location = appointment.address.toDisplayString()
            if (location.isNotBlank()) {
                sb.appendLine("LOCATION:${escapeIcsText(location)}")
            }
            
            if (appointment.notes.isNotBlank()) {
                sb.appendLine("DESCRIPTION:${escapeIcsText(appointment.notes)}")
            }
            
            // Add reminder if enabled
            if (appointment.isReminderEnabled) {
                sb.appendLine("BEGIN:VALARM")
                sb.appendLine("ACTION:DISPLAY")
                sb.appendLine("DESCRIPTION:Reminder")
                sb.appendLine("TRIGGER:-PT${appointment.reminderMinutes}M")
                sb.appendLine("END:VALARM")
            }
            
            sb.appendLine("END:VEVENT")
        }
        
        sb.appendLine("END:VCALENDAR")
        
        return sb.toString()
    }
    
    fun exportToFile(appointments: List<Appointment>, filePath: String): Boolean {
        return try {
            val icsContent = exportToIcs(appointments)
            java.io.File(filePath).writeText(icsContent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun formatDateTimeICS(instant: Instant): String {
        // Format: 20240115T143000Z
        return instant.toString()
            .replace("-", "")
            .replace(":", "")
            .substring(0, 15) + "Z"
    }
    
    private fun escapeIcsText(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
}
