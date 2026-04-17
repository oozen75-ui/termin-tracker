package com.termintracker.calendar

import com.termintracker.model.Appointment
import com.termintracker.utils.Logger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.format.DateTimeFormatter

/**
 * Google Calendar entegrasyonu için export fonksiyonları
 * iCal format (.ics) ve Google Calendar URL desteği
 */
object CalendarExport {
    
    private val icalFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
    private val json = Json { prettyPrint = true }
    
    /**
     * Randevuyu iCal (.ics) formatına dönüştür
     */
    fun exportToICal(appointment: Appointment): String {
        val startTime = appointment.date.atTime(appointment.time)
        val endTime = startTime.plusMinutes(appointment.durationMinutes.toLong())
        
        val uid = "${appointment.id}@termin-tracker"
        val created = java.time.LocalDateTime.now().format(icalFormatter)
        val dtStart = startTime.format(icalFormatter)
        val dtEnd = endTime.format(icalFormatter)
        
        return buildString {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//Termin Tracker//DE")
            appendLine("CALSCALE:GREGORIAN")
            appendLine("METHOD:PUBLISH")
            appendLine("BEGIN:VEVENT")
            appendLine("UID:$uid")
            appendLine("DTSTART:$dtStart")
            appendLine("DTEND:$dtEnd")
            appendLine("SUMMARY:${escapeICal(appointment.type.displayName)}")
            appendLine("DESCRIPTION:${escapeICal(buildDescription(appointment))}")
            appendLine("LOCATION:${escapeICal(appointment.location)}")
            appendLine("DTSTAMP:$created")
            appendLine("CREATED:$created")
            appendLine("END:VEVENT")
            appendLine("END:VCALENDAR")
        }
    }
    
    /**
     * Google Calendar'e direkt ekleme URL'i oluştur
     */
    fun createGoogleCalendarUrl(appointment: Appointment): String {
        val startTime = appointment.date.atTime(appointment.time)
        val endTime = startTime.plusMinutes(appointment.durationMinutes.toLong())
        
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
        val dates = "${startTime.format(dateFormat)}/${endTime.format(dateFormat)}"
        
        val params = mapOf(
            "action" to "TEMPLATE",
            "text" to appointment.type.displayName,
            "dates" to dates,
            "details" to buildDescription(appointment),
            "location" to appointment.location,
            "sf" to "true",
            "output" to "xml"
        )
        
        val queryString = params.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
        }
        
        return "https://calendar.google.com/calendar/render?$queryString"
    }
    
    /**
     * Outlook Calendar URL'i oluştur
     */
    fun createOutlookCalendarUrl(appointment: Appointment): String {
        val startTime = appointment.date.atTime(appointment.time)
        val endTime = startTime.plusMinutes(appointment.durationMinutes.toLong())
        
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        
        val params = mapOf(
            "subject" to appointment.type.displayName,
            "startdt" to startTime.format(dateFormat),
            "enddt" to endTime.format(dateFormat),
            "body" to buildDescription(appointment),
            "location" to appointment.location
        )
        
        val queryString = params.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, StandardCharsets.UTF_8)}"
        }
        
        return "https://outlook.live.com/calendar/0/deeplink/compose?$queryString"
    }
    
    /**
     * iCal dosyasını kaydet
     */
    fun saveICalToFile(appointment: Appointment, directory: File): File {
        val fileName = "termin-${appointment.id}-${appointment.date}.ics"
        val file = File(directory, fileName)
        file.writeText(exportToICal(appointment))
        Logger.info("iCal exported to: ${file.absolutePath}")
        return file
    }
    
    /**
     * Birden fazla randevuyu tek iCal dosyasına export et
     */
    fun exportMultipleToICal(appointments: List<Appointment>): String {
        val created = java.time.LocalDateTime.now().format(icalFormatter)
        
        return buildString {
            appendLine("BEGIN:VCALENDAR")
            appendLine("VERSION:2.0")
            appendLine("PRODID:-//Termin Tracker//DE")
            appendLine("CALSCALE:GREGORIAN")
            appendLine("METHOD:PUBLISH")
            
            appointments.forEach { appointment ->
                val startTime = appointment.date.atTime(appointment.time)
                val endTime = startTime.plusMinutes(appointment.durationMinutes.toLong())
                val uid = "${appointment.id}@termin-tracker"
                val dtStart = startTime.format(icalFormatter)
                val dtEnd = endTime.format(icalFormatter)
                
                appendLine("BEGIN:VEVENT")
                appendLine("UID:$uid")
                appendLine("DTSTART:$dtStart")
                appendLine("DTEND:$dtEnd")
                appendLine("SUMMARY:${escapeICal(appointment.type.displayName)}")
                appendLine("DESCRIPTION:${escapeICal(buildDescription(appointment))}")
                appendLine("LOCATION:${escapeICal(appointment.location)}")
                appendLine("DTSTAMP:$created")
                appendLine("CREATED:$created")
                appendLine("END:VEVENT")
            }
            
            appendLine("END:VCALENDAR")
        }
    }
    
    private fun buildDescription(appointment: Appointment): String {
        return buildString {
            appendLine("Randevu Türü: ${appointment.type.displayName}")
            appendLine("Tarih: ${appointment.date}")
            appendLine("Saat: ${appointment.time}")
            appendLine("Konum: ${appointment.location}")
            appointment.notes?.let { appendLine("Notlar: $it") }
            appendLine("Oluşturuldu: Termin Tracker v1.1.0")
        }.trim()
    }
    
    private fun escapeICal(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\n", "\\n")
            .replace("\r", "")
    }
    
    @Serializable
    data class CalendarExportConfig(
        val includeNotes: Boolean = true,
        val includeLocation: Boolean = true,
        val reminderMinutes: Int = 30
    )
}