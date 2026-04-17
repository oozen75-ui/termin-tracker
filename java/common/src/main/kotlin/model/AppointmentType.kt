package com.termintracker.model

enum class AppointmentType(val displayName: String, val category: String) {
    // Behörde (Resmi Kurumlar)
    KVR("KVR - Bürgeramt", "Behörde"),
    AMT("Amt - Behörde", "Behörde"),
    BMV_KFZ("BMV - Kfz-Zulassung", "Behörde"),
    AUSLAENDERBEHOERDE("Ausländerbehörde", "Behörde"),
    
    // Sağlık
    KRANKENKASSE("Krankenkasse", "Sağlık"),
    HAUSARZT("Hausarzt", "Sağlık"),
    FACHARZT("Facharzt", "Sağlık"),
    ZAHNARZT("Zahnarzt", "Sağlık"),
    HNO("HNO-Arzt (Kulak Burun Boğaz)", "Sağlık"),
    
    // Finans
    BANK("Bank", "Finans"),
    STEUER("Steuerberater", "Finans"),
    
    // Diğer
    SONSTIGES("Sonstiges", "Diğer");

    companion object {
        fun fromString(value: String): AppointmentType {
            return entries.find { it.name == value || it.displayName == value } ?: SONSTIGES
        }
        
        fun getByCategory(category: String): List<AppointmentType> {
            return entries.filter { it.category == category }
        }
        
        fun getCategories(): List<String> {
            return entries.map { it.category }.distinct()
        }
    }
}
