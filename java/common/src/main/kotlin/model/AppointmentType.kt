package com.termintracker.model

enum class AppointmentType(val displayName: String) {
    KVR("KVR - Bürgeramt"),
    AMT("Amt - Behörde"),
    KRANKENKASSE("Krankenkasse"),
    BANK("Bank"),
    SONSTIGES("Sonstiges");

    companion object {
        fun fromString(value: String): AppointmentType {
            return entries.find { it.name == value || it.displayName == value } ?: SONSTIGES
        }
    }
}
