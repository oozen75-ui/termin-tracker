package com.termintracker.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonalInfo(
    val id: Long = 1,
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val email: String = "",
    val phone: String = "",
    val defaultAddress: Address = Address(),
    val preferredLanguage: Language = Language.GERMAN
)

enum class Language(val code: String, val displayName: String) {
    GERMAN("de", "Deutsch"),
    ENGLISH("en", "English"),
    TURKISH("tr", "Türkçe");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: GERMAN
        }
    }
}
