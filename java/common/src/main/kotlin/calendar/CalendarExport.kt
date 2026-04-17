package com.termintracker.calendar

import com.termintracker.data.Appointment
import com.termintracker.data.AppointmentType
import com.termintracker.data.getDisplayName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarExport {
    
    fun exportToICS(appointment: Appointment): String {
        val now = LocalDateTime.now()
        val uid = UUID.randomUUID().toString()
        
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val timeFormatter = DateTimeFormatter.ofPattern("HHmmss")
        
        val dateParts = appointment.appointmentDate.split("-")
        val timeParts = appointment.appointmentTime.split(":")
        
        val year = dateParts.getOrElse(0) { "2026" }
        val month = dateParts.getOrElse(1) { "01" }
        val day = dateParts.getOrElse(2) { "01" }
        val hour = timeParts.getOrElse(0) { "09" }
        val minute = timeParts.getOrElse(1) { "00" }
        
        val startDateTime = "$year${month.padStart(2, '0')}${day.padStart(2, '0')}T${hour.padStart(2, '0')}${minute.padStart(2, '0')}00"
        
        // End time (1 hour later by default)
        val endHour = (hour.toInt() + 1).toString().padStart(2, '0')
        val endDateTime = "$year${month.padStart(2, '0')}${day.padStart(2, '0')}T${endHour}${minute.padStart(2, '0')}00"
        
        val summary = "${appointment.appointmentType.getDisplayName()}: ${appointment.firstName} ${appointment.lastName}"
        val location = "${appointment.address}, ${appointment.postalCode} ${appointment.city}"
        
        return buildString {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//Termin Tracker//DE")
            appendLine("BEGIN:VEVENT")
            appendLine("UID:$uid")
            appendLine("DTSTAMP:${now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))}")
            appendLine("DTSTART;TZID=Europe/Berlin:$startDateTime")
            appendLine("DTEND;TZID=Europe/Berlin:$endDateTime")
            appendLine("SUMMARY:$summary")
            appendLine("LOCATION:$location")
            appendLine("DESCRIPTION:${appointment.notes.replace("\n", "\\n")}")
            appendLine("END:VEVENT")
            appendLine("END:VCALENDAR")
        }
    }
    
    fun exportMultipleToICS(appointments: List<Appointment>): String {
        val now = LocalDateTime.now()
        val nowFormatted = now.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"))
        
        return buildString {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//Termin Tracker//DE")
            appendLine("CALSCALE:GREGORIAN")
            appendLine("METHOD:PUBLISH")
            
            appointments.forEach { appointment ->
                val uid = UUID.randomUUID().toString()
                
                val dateParts = appointment.appointmentDate.split("-")
                val timeParts = appointment.appointmentTime.split(":")
                
                val year = dateParts.getOrElse(0) { "2026" }
                val month = dateParts.getOrElse(1) { "01" }
                val day = dateParts.getOrElse(2) { "01" }
                val hour = timeParts.getOrElse(0) { "09" }
                val minute = timeParts.getOrElse(1) { "00" }
                
                val startDateTime = "$year${month.padStart(2, '0')}${day.padStart(2, '0')}T${hour.padStart(2, '0')}${minute.padStart(2, '0')}00"
                val endHour = (hour.toInt() + 1).toString().padStart(2, '0')
                val endDateTime = "$year${month.padStart(2, '0')}${day.padStart(2, '0')}T${endHour}${minute.padStart(2, '0')}00"
                
                val summary = "${appointment.appointmentType.getDisplayName()}: ${appointment.firstName} ${appointment.lastName}"
                val location = "${appointment.address}, ${appointment.postalCode} ${appointment.city}"
                
                appendLine("BEGIN:VEVENT")
                appendLine("UID:$uid")
                appendLine("DTSTAMP:${nowFormatted}")
                appendLine("DTSTART;TZID=Europe/Berlin:$startDateTime")
                appendLine("DTEND;TZID=Europe/Berlin:$endDateTime")
                appendLine("SUMMARY:$summary")
                appendLine("LOCATION:$location")
                appendLine("DESCRIPTION:${appointment.notes.replace("\n", "\\n")}")
                appendLine("END:VEVENT")
            }
            
            appendLine("END:VCALENDAR")
        }
    }
    
    fun getICSFilename(appointment: Appointment): String {
        val safeName = "${appointment.firstName}_${appointment.lastName}"
            .replace(" ", "_")
            .replace("[^a-zA-Z0-9_-]".toRegex(), "")
        return "termin_${safeName}_${appointment.appointmentDate}.ics"
    }
}
