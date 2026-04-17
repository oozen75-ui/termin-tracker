package com.termintracker.data

object Localization {
    private var currentLanguage = "de"
    
    val supportedLanguages = listOf("de", "en", "tr")
    
    fun setLanguage(language: String) {
        if (language in supportedLanguages) {
            currentLanguage = language
        }
    }
    
    fun getLanguage(): String = currentLanguage
    
    fun getString(key: String): String {
        return strings[currentLanguage]?.get(key) 
            ?: strings["de"]?.get(key) 
            ?: key
    }
    
    private val strings = mapOf(
        "de" to mapOf(
            "app_name" to "Termin Tracker",
            "personal_info" to "Persönliche Informationen",
            "first_name" to "Vorname",
            "last_name" to "Nachname",
            "birth_date" to "Geburtsdatum",
            "address_info" to "Adressinformationen",
            "city" to "Stadt",
            "district" to "Bezirk",
            "postal_code" to "PLZ",
            "street" to "Straße / Nr",
            "appointment_info" to "Termin Informationen",
            "appointment_type" to "Termin Typ",
            "appointment_date" to "Termin Datum",
            "appointment_time" to "Termin Zeit",
            "notes" to "Notizen",
            "reminder" to "Erinnerung",
            "minutes_before" to "Minuten vorher",
            "documents" to "Benötigte Dokumente",
            "save" to "Speichern",
            "cancel" to "Abbrechen",
            "delete" to "Löschen",
            "edit" to "Bearbeiten",
            "export" to "Exportieren",
            "import" to "Importieren",
            "settings" to "Einstellungen",
            "language" to "Sprache",
            "appointments" to "Termine",
            "no_appointments" to "Keine Termine",
            "add_appointment" to "Termin hinzufügen",
            "search_address" to "Adresse suchen",
            "file_attached" to "Datei angehängt",
            "select_file" to "Datei auswählen"
        ),
        "en" to mapOf(
            "app_name" to "Termin Tracker",
            "personal_info" to "Personal Information",
            "first_name" to "First Name",
            "last_name" to "Last Name",
            "birth_date" to "Birth Date",
            "address_info" to "Address Information",
            "city" to "City",
            "district" to "District",
            "postal_code" to "Postal Code",
            "street" to "Street / No",
            "appointment_info" to "Appointment Information",
            "appointment_type" to "Appointment Type",
            "appointment_date" to "Appointment Date",
            "appointment_time" to "Appointment Time",
            "notes" to "Notes",
            "reminder" to "Reminder",
            "minutes_before" to "minutes before",
            "documents" to "Required Documents",
            "save" to "Save",
            "cancel" to "Cancel",
            "delete" to "Delete",
            "edit" to "Edit",
            "export" to "Export",
            "import" to "Import",
            "settings" to "Settings",
            "language" to "Language",
            "appointments" to "Appointments",
            "no_appointments" to "No Appointments",
            "add_appointment" to "Add Appointment",
            "search_address" to "Search Address",
            "file_attached" to "File attached",
            "select_file" to "Select File"
        ),
        "tr" to mapOf(
            "app_name" to "Randevu Takip",
            "personal_info" to "Kişisel Bilgiler",
            "first_name" to "İsim",
            "last_name" to "Soyisim",
            "birth_date" to "Doğum Tarihi",
            "address_info" to "Adres Bilgileri",
            "city" to "Şehir",
            "district" to "İlçe",
            "postal_code" to "Posta Kodu",
            "street" to "Sokak / No",
            "appointment_info" to "Randevu Bilgileri",
            "appointment_type" to "Randevu Tipi",
            "appointment_date" to "Randevu Tarihi",
            "appointment_time" to "Randevu Saati",
            "notes" to "Notlar",
            "reminder" to "Hatırlatma",
            "minutes_before" to "dakika önce",
            "documents" to "Gerekli Evraklar",
            "save" to "Kaydet",
            "cancel" to "İptal",
            "delete" to "Sil",
            "edit" to "Düzenle",
            "export" to "Dışa Aktar",
            "import" to "İçe Aktar",
            "settings" to "Ayarlar",
            "language" to "Dil",
            "appointments" to "Randevular",
            "no_appointments" to "Randevu Yok",
            "add_appointment" to "Randevu Ekle",
            "search_address" to "Adres Ara",
            "file_attached" to "Dosya eklendi",
            "select_file" to "Dosya Seç"
        )
    )
}
