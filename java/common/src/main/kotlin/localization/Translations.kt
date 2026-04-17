package com.termintracker.localization

import com.termintracker.model.AppointmentType
import com.termintracker.model.Language

object Translations {
    private var currentLanguage: Language = Language.GERMAN
    
    fun setLanguage(language: Language) {
        currentLanguage = language
    }
    
    fun getLanguage(): Language = currentLanguage
    
    fun t(key: String): String {
        return translations[currentLanguage]?.get(key) ?: key
    }
    
    // Appointment Type Names
    fun getAppointmentTypeName(type: AppointmentType): String {
        return when (currentLanguage) {
            Language.GERMAN -> when (type) {
                AppointmentType.KVR -> "KVR - Bürgeramt"
                AppointmentType.AMT -> "Amt - Behörde"
                AppointmentType.BMV_KFZ -> "BMV - Kfz-Zulassung"
                AppointmentType.AUSLAENDERBEHOERDE -> "Ausländerbehörde"
                AppointmentType.KRANKENKASSE -> "Krankenkasse"
                AppointmentType.HAUSARZT -> "Hausarzt"
                AppointmentType.FACHARZT -> "Facharzt"
                AppointmentType.ZAHNARZT -> "Zahnarzt"
                AppointmentType.BANK -> "Bank"
                AppointmentType.STEUER -> "Steuerberater"
                AppointmentType.SONSTIGES -> "Sonstiges"
            }
            Language.ENGLISH -> when (type) {
                AppointmentType.KVR -> "KVR - Citizen's Office"
                AppointmentType.AMT -> "Office - Authority"
                AppointmentType.BMV_KFZ -> "BMV - Vehicle Registration"
                AppointmentType.AUSLAENDERBEHOERDE -> "Immigration Office"
                AppointmentType.KRANKENKASSE -> "Health Insurance"
                AppointmentType.HAUSARZT -> "General Practitioner"
                AppointmentType.FACHARZT -> "Specialist Doctor"
                AppointmentType.ZAHNARZT -> "Dentist"
                AppointmentType.BANK -> "Bank"
                AppointmentType.STEUER -> "Tax Advisor"
                AppointmentType.SONSTIGES -> "Other"
            }
            Language.TURKISH -> when (type) {
                AppointmentType.KVR -> "KVR - Vatandaşlık Ofisi"
                AppointmentType.AMT -> "Daire - Kurum"
                AppointmentType.BMV_KFZ -> "BMV - Araç Kayıt"
                AppointmentType.AUSLAENDERBEHOERDE -> "Göçmen Bürosu"
                AppointmentType.KRANKENKASSE -> "Sağlık Sigortası"
                AppointmentType.HAUSARZT -> "Aile Hekimi"
                AppointmentType.FACHARZT -> "Uzman Doktor"
                AppointmentType.ZAHNARZT -> "Diş Hekimi"
                AppointmentType.BANK -> "Banka"
                AppointmentType.STEUER -> "Mali Müşavir"
                AppointmentType.SONSTIGES -> "Diğer"
            }
        }
    }
    
    // Category Names
    fun getCategoryName(category: String): String {
        return when (currentLanguage) {
            Language.GERMAN -> when (category) {
                "Behörde" -> "Behörde"
                "Sağlık" -> "Gesundheit"
                "Finans" -> "Finanzen"
                else -> "Sonstiges"
            }
            Language.ENGLISH -> category
            Language.TURKISH -> category
        }
    }
    
    private val translations = mapOf(
        Language.GERMAN to mapOf(
            "app.title" to "Termin Tracker",
            "appointments" to "Termine",
            "add" to "Hinzufügen",
            "edit" to "Bearbeiten",
            "delete" to "Löschen",
            "save" to "Speichern",
            "cancel" to "Abbrechen",
            "search" to "Suchen",
            "settings" to "Einstellungen",
            "language" to "Sprache",
            "notifications" to "Benachrichtigungen",
            "reminder" to "Erinnerung",
            "location" to "Ort",
            "date" to "Datum",
            "time" to "Uhrzeit",
            "type" to "Typ",
            "notes" to "Notizen",
            "documents" to "Dokumente",
            "status.pending" to "Ausstehend",
            "status.confirmed" to "Bestätigt",
            "status.completed" to "Abgeschlossen",
            "status.cancelled" to "Storniert",
            "error.generic" to "Ein Fehler ist aufgetreten",
            "success.saved" to "Erfolgreich gespeichert",
            "confirm.delete" to "Möchten Sie diesen Termin wirklich löschen?",
            "no.results" to "Keine Ergebnisse gefunden",
            "loading" to "Laden...",
            "welcome" to "Willkommen bei Termin Tracker",
            "next.appointment" to "Nächster Termin",
            "past" to "Vergangen"
        ),
        Language.ENGLISH to mapOf(
            "app.title" to "Termin Tracker",
            "appointments" to "Appointments",
            "add" to "Add",
            "edit" to "Edit",
            "delete" to "Delete",
            "save" to "Save",
            "cancel" to "Cancel",
            "search" to "Search",
            "settings" to "Settings",
            "language" to "Language",
            "notifications" to "Notifications",
            "reminder" to "Reminder",
            "location" to "Location",
            "date" to "Date",
            "time" to "Time",
            "type" to "Type",
            "notes" to "Notes",
            "documents" to "Documents",
            "status.pending" to "Pending",
            "status.confirmed" to "Confirmed",
            "status.completed" to "Completed",
            "status.cancelled" to "Cancelled",
            "error.generic" to "An error occurred",
            "success.saved" to "Saved successfully",
            "confirm.delete" to "Do you really want to delete this appointment?",
            "no.results" to "No results found",
            "loading" to "Loading...",
            "welcome" to "Welcome to Termin Tracker",
            "next.appointment" to "Next Appointment",
            "past" to "Past"
        ),
        Language.TURKISH to mapOf(
            "app.title" to "Termin Tracker",
            "appointments" to "Randevular",
            "add" to "Ekle",
            "edit" to "Düzenle",
            "delete" to "Sil",
            "save" to "Kaydet",
            "cancel" to "İptal",
            "search" to "Ara",
            "settings" to "Ayarlar",
            "language" to "Dil",
            "notifications" to "Bildirimler",
            "reminder" to "Hatırlatma",
            "location" to "Konum",
            "date" to "Tarih",
            "time" to "Saat",
            "type" to "Tür",
            "notes" to "Notlar",
            "documents" to "Belgeler",
            "status.pending" to "Bekliyor",
            "status.confirmed" to "Onaylandı",
            "status.completed" to "Tamamlandı",
            "status.cancelled" to "İptal Edildi",
            "error.generic" to "Bir hata oluştu",
            "success.saved" to "Başarıyla kaydedildi",
            "confirm.delete" to "Bu randevuyu silmek istediğinize emin misiniz?",
            "no.results" to "Sonuç bulunamadı",
            "loading" to "Yükleniyor...",
            "welcome" to "Termin Tracker'a hoş geldiniz",
            "next.appointment" to "Sonraki Randevu",
            "past" to "Geçmiş"
        )
    )
}