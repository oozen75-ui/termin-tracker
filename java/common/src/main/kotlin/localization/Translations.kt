package com.termintracker.localization

import com.termintracker.model.AppointmentType
import com.termintracker.model.Language

object Translations {
    private var currentLanguage: Language = Language.GERMAN

    fun setLanguage(language: Language) {
        currentLanguage = language
    }

    fun getCurrentLanguage(): Language = currentLanguage

    fun t(key: String): String {
        return translations[currentLanguage]?.get(key) ?: key
    }

    // Convenience methods for common keys
    fun appTitle(): String = t("app.title")
    fun personalInfo(): String = t("personal.info")
    fun firstName(): String = t("first.name")
    fun lastName(): String = t("last.name")
    fun birthDate(): String = t("birth.date")
    fun address(): String = t("address")
    fun city(): String = t("city")
    fun district(): String = t("district")
    fun postalCode(): String = t("postal.code")
    fun street(): String = t("street")
    fun houseNumber(): String = t("house.number")
    fun save(): String = t("save")
    fun cancel(): String = t("cancel")
    fun edit(): String = t("edit")
    fun delete(): String = t("delete")
    fun add(): String = t("add")
    fun search(): String = t("search")
    fun settings(): String = t("settings")
    fun language(): String = t("language")
    fun appointment(): String = t("appointment")
    fun appointments(): String = t("appointments")
    fun newAppointment(): String = t("new.appointment")
    fun appointmentType(): String = t("appointment.type")
    fun appointmentDate(): String = t("appointment.date")
    fun appointmentTime(): String = t("appointment.time")
    fun reminder(): String = t("reminder")
    fun reminderBefore(): String = t("reminder.before")
    fun minutes(): String = t("minutes")
    fun documents(): String = t("documents")
    fun requiredDocuments(): String = t("required.documents")
    fun uploadFile(): String = t("upload.file")
    fun exportICS(): String = t("export.ics")
    fun exportJSON(): String = t("export.json")
    fun importJSON(): String = t("import.json")
    fun autoFill(): String = t("auto.fill")
    fun notes(): String = t("notes")
    fun completed(): String = t("completed")
    fun pending(): String = t("pending")
    fun today(): String = t("today")
    fun upcoming(): String = t("upcoming")
    fun past(): String = t("past")

    fun getAppointmentTypeName(type: AppointmentType): String {
        return when (currentLanguage) {
            Language.GERMAN -> when (type) {
                AppointmentType.KVR -> "KVR - Bürgeramt"
                AppointmentType.AMT -> "Amt - Behörde"
                AppointmentType.KRANKENKASSE -> "Krankenkasse"
                AppointmentType.BANK -> "Bank"
                AppointmentType.SONSTIGES -> "Sonstiges"
            }
            Language.ENGLISH -> when (type) {
                AppointmentType.KVR -> "KVR - Citizen's Office"
                AppointmentType.AMT -> "Office - Authority"
                AppointmentType.KRANKENKASSE -> "Health Insurance"
                AppointmentType.BANK -> "Bank"
                AppointmentType.SONSTIGES -> "Other"
            }
            Language.TURKISH -> when (type) {
                AppointmentType.KVR -> "KVR - Vatandaşlık Ofisi"
                AppointmentType.AMT -> "Daire - Kurum"
                AppointmentType.KRANKENKASSE -> "Sağlık Sigortası"
                AppointmentType.BANK -> "Banka"
                AppointmentType.SONSTIGES -> "Diğer"
            }
        }
    }

    private val translations = mapOf(
        Language.GERMAN to mapOf(
            "app.title" to "Termin Tracker",
            "personal.info" to "Persönliche Informationen",
            "first.name" to "Vorname",
            "last.name" to "Nachname",
            "birth.date" to "Geburtsdatum",
            "address" to "Adresse",
            "city" to "Stadt",
            "district" to "Bezirk",
            "postal.code" to "PLZ",
            "street" to "Straße",
            "house.number" to "Hausnummer",
            "save" to "Speichern",
            "cancel" to "Abbrechen",
            "edit" to "Bearbeiten",
            "delete" to "Löschen",
            "add" to "Hinzufügen",
            "search" to "Suchen",
            "settings" to "Einstellungen",
            "language" to "Sprache",
            "appointment" to "Termin",
            "appointments" to "Termine",
            "new.appointment" to "Neuer Termin",
            "appointment.type" to "Terminart",
            "appointment.date" to "Termindatum",
            "appointment.time" to "Terminzeit",
            "reminder" to "Erinnerung",
            "reminder.before" to "Erinnerung vor",
            "minutes" to "Minuten",
            "documents" to "Dokumente",
            "required.documents" to "Benötigte Dokumente",
            "upload.file" to "Datei hochladen",
            "export.ics" to "Kalender exportieren (ICS)",
            "export.json" to "JSON exportieren",
            "import.json" to "JSON importieren",
            "auto.fill" to "Automatisch ausfüllen",
            "notes" to "Notizen",
            "completed" to "Abgeschlossen",
            "pending" to "Ausstehend",
            "today" to "Heute",
            "upcoming" to "Kommende",
            "past" to "Vergangene"
        ),
        Language.ENGLISH to mapOf(
            "app.title" to "Termin Tracker",
            "personal.info" to "Personal Information",
            "first.name" to "First Name",
            "last.name" to "Last Name",
            "birth.date" to "Birth Date",
            "address" to "Address",
            "city" to "City",
            "district" to "District",
            "postal.code" to "Postal Code",
            "street" to "Street",
            "house.number" to "House Number",
            "save" to "Save",
            "cancel" to "Cancel",
            "edit" to "Edit",
            "delete" to "Delete",
            "add" to "Add",
            "search" to "Search",
            "settings" to "Settings",
            "language" to "Language",
            "appointment" to "Appointment",
            "appointments" to "Appointments",
            "new.appointment" to "New Appointment",
            "appointment.type" to "Appointment Type",
            "appointment.date" to "Appointment Date",
            "appointment.time" to "Appointment Time",
            "reminder" to "Reminder",
            "reminder.before" to "Remind before",
            "minutes" to "minutes",
            "documents" to "Documents",
            "required.documents" to "Required Documents",
            "upload.file" to "Upload File",
            "export.ics" to "Export Calendar (ICS)",
            "export.json" to "Export JSON",
            "import.json" to "Import JSON",
            "auto.fill" to "Auto-fill",
            "notes" to "Notes",
            "completed" to "Completed",
            "pending" to "Pending",
            "today" to "Today",
            "upcoming" to "Upcoming",
            "past" to "Past"
        ),
        Language.TURKISH to mapOf(
            "app.title" to "Termin Tracker",
            "personal.info" to "Kişisel Bilgiler",
            "first.name" to "İsim",
            "last.name" to "Soyisim",
            "birth.date" to "Doğum Tarihi",
            "address" to "Adres",
            "city" to "Şehir",
            "district" to "İlçe",
            "postal.code" to "Posta Kodu",
            "street" to "Sokak",
            "house.number" to "Ev No",
            "save" to "Kaydet",
            "cancel" to "İptal",
            "edit" to "Düzenle",
            "delete" to "Sil",
            "add" to "Ekle",
            "search" to "Ara",
            "settings" to "Ayarlar",
            "language" to "Dil",
            "appointment" to "Randevu",
            "appointments" to "Randevular",
            "new.appointment" to "Yeni Randevu",
            "appointment.type" to "Randevu Türü",
            "appointment.date" to "Randevu Tarihi",
            "appointment.time" to "Randevu Saati",
            "reminder" to "Hatırlatma",
            "reminder.before" to "Şu süre önce hatırlat",
            "minutes" to "dakika",
            "documents" to "Evraklar",
            "required.documents" to "Gerekli Evraklar",
            "upload.file" to "Dosya Yükle",
            "export.ics" to "Takvimi Dışa Aktar (ICS)",
            "export.json" to "JSON Dışa Aktar",
            "import.json" to "JSON İçe Aktar",
            "auto.fill" to "Otomatik Doldur",
            "notes" to "Notlar",
            "completed" to "Tamamlandı",
            "pending" to "Bekliyor",
            "today" to "Bugün",
            "upcoming" to "Yaklaşan",
            "past" to "Geçmiş"
        )
    )
}
