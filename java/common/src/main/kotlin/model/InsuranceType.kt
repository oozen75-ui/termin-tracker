package com.termintracker.model

enum class InsuranceType(val displayNameDe: String, val displayNameTr: String, val displayNameEn: String) {
    GESETZLICH("Gesetzlich versichert (GKV)", "Sosyal Sigortalı (GKV)", "Statutory Insurance (GKV)"),
    PRIVAT("Privat versichert (PKV)", "Özel Sigortalı (PKV)", "Private Insurance (PKV)"),
    BOTH("Beides möglich", "Her ikisi de", "Both possible");
    
    fun getDisplayName(language: Language = Language.GERMAN): String {
        return when (language) {
            Language.GERMAN -> displayNameDe
            Language.TURKISH -> displayNameTr
            Language.ENGLISH -> displayNameEn
        }
    }
}