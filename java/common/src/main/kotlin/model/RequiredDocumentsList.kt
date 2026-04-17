package com.termintracker.model

object RequiredDocumentsList {
    
    fun getRequiredDocuments(type: AppointmentType): List<RequiredDocument> {
        return when (type) {
            AppointmentType.KVR -> getKVRDocuments()
            AppointmentType.AMT -> getAmtDocuments()
            AppointmentType.KRANKENKASSE -> getKrankenkasseDocuments()
            AppointmentType.BANK -> getBankDocuments()
            AppointmentType.SONSTIGES -> getSonstigesDocuments()
        }
    }

    private fun getKVRDocuments(): List<RequiredDocument> = listOf(
        RequiredDocument(1, "Reisepass / Pass", "Gültiger Reisepass", true, appointmentType = AppointmentType.KVR),
        RequiredDocument(2, "Meldebestätigung", "Wohnungsgeberbestätigung", true, appointmentType = AppointmentType.KVR),
        RequiredDocument(3, "Visum / Aufenthaltstitel", "Falls zutreffend", false, appointmentType = AppointmentType.KVR),
        RequiredDocument(4, "Geburtsurkunde", "Beglaubigte Übersetzung", false, appointmentType = AppointmentType.KVR),
        RequiredDocument(5, "Heiratsurkunde", "Falls verheiratet", false, appointmentType = AppointmentType.KVR),
        RequiredDocument(6, "Personalausweis", "Falls vorhanden", false, appointmentType = AppointmentType.KVR)
    )

    private fun getAmtDocuments(): List<RequiredDocument> = listOf(
        RequiredDocument(7, "Personalausweis", "Gültiger Ausweis", true, appointmentType = AppointmentType.AMT),
        RequiredDocument(8, "Meldebescheinigung", "Aktuelle Meldebescheinigung", true, appointmentType = AppointmentType.AMT),
        RequiredDocument(9, "Formular", "Vorausgefülltes Antragsformular", true, appointmentType = AppointmentType.AMT),
        RequiredDocument(10, "Nachweis", "Weitere Nachweise je nach Anliegen", false, appointmentType = AppointmentType.AMT)
    )

    private fun getKrankenkasseDocuments(): List<RequiredDocument> = listOf(
        RequiredDocument(11, "Personalausweis", "Gültiger Ausweis", true, appointmentType = AppointmentType.KRANKENKASSE),
        RequiredDocument(12, "Meldebescheinigung", "Aktuelle Meldebescheinigung", true, appointmentType = AppointmentType.KRANKENKASSE),
        RequiredDocument(13, "Lohnnachweis", "Gehaltsabrechnung oder Arbeitsvertrag", false, appointmentType = AppointmentType.KRANKENKASSE),
        RequiredDocument(14, "Kontodaten", "IBAN für Beitragsabbuchung", true, appointmentType = AppointmentType.KRANKENKASSE),
        RequiredDocument(15, "Bescheinigung", "Bescheinigung der alten Versicherung", false, appointmentType = AppointmentType.KRANKENKASSE)
    )

    private fun getBankDocuments(): List<RequiredDocument> = listOf(
        RequiredDocument(16, "Personalausweis", "Gültiger Reisepass oder Personalausweis", true, appointmentType = AppointmentType.BANK),
        RequiredDocument(17, "Meldebescheinigung", "Aktuelle Meldebescheinigung", true, appointmentType = AppointmentType.BANK),
        RequiredDocument(18, "Steuer-ID", "Deutsche Steueridentifikationsnummer", true, appointmentType = AppointmentType.BANK),
        RequiredDocument(19, "Arbeitsvertrag", "Oder Einkommensnachweis", false, appointmentType = AppointmentType.BANK),
        RequiredDocument(20, "Schufa-Auskunft", "Falls vorhanden", false, appointmentType = AppointmentType.BANK)
    )

    private fun getSonstigesDocuments(): List<RequiredDocument> = listOf(
        RequiredDocument(21, "Personalausweis", "Gültiger Ausweis", true, appointmentType = AppointmentType.SONSTIGES),
        RequiredDocument(22, "Terminbestätigung", "E-Mail oder Brief", false, appointmentType = AppointmentType.SONSTIGES),
        RequiredDocument(23, "Unterlagen", "Alle relevanten Unterlagen", false, appointmentType = AppointmentType.SONSTIGES)
    )

    fun getDocumentsForLanguage(type: AppointmentType, language: String): List<RequiredDocument> {
        val baseDocs = getRequiredDocuments(type)
        return when (language.lowercase()) {
            "de" -> baseDocs
            "en" -> baseDocs.map { translateToEnglish(it) }
            "tr" -> baseDocs.map { translateToTurkish(it) }
            else -> baseDocs
        }
    }

    private fun translateToEnglish(doc: RequiredDocument): RequiredDocument {
        val translations = mapOf(
            "Reisepass / Pass" to "Passport",
            "Meldebestätigung" to "Registration Confirmation",
            "Visum / Aufenthaltstitel" to "Visa / Residence Permit",
            "Geburtsurkunde" to "Birth Certificate",
            "Heiratsurkunde" to "Marriage Certificate",
            "Personalausweis" to "ID Card",
            "Meldebescheinigung" to "Registration Certificate",
            "Formular" to "Form",
            "Nachweis" to "Proof / Evidence",
            "Lohnnachweis" to "Proof of Income",
            "Kontodaten" to "Bank Account Details",
            "Bescheinigung" to "Certificate",
            "Steuer-ID" to "Tax ID",
            "Arbeitsvertrag" to "Employment Contract",
            "Schufa-Auskunft" to "Credit Report",
            "Terminbestätigung" to "Appointment Confirmation",
            "Unterlagen" to "Documents"
        )
        return doc.copy(
            name = translations[doc.name] ?: doc.name,
            description = doc.description
        )
    }

    private fun translateToTurkish(doc: RequiredDocument): RequiredDocument {
        val translations = mapOf(
            "Reisepass / Pass" to "Pasaport",
            "Meldebestätigung" to "Kayıt Onayı",
            "Visum / Aufenthaltstitel" to "Vize / İkamet İzni",
            "Geburtsurkunde" to "Doğum Belgesi",
            "Heiratsurkunde" to "Evlenme Belgesi",
            "Personalausweis" to "Kimlik Kartı",
            "Meldebescheinigung" to "Kayıt Belgesi",
            "Formular" to "Form",
            "Nachweis" to "Kanıt / Belge",
            "Lohnnachweis" to "Gelir Belgesi",
            "Kontodaten" to "Banka Hesap Bilgileri",
            "Bescheinigung" to "Sertifika",
            "Steuer-ID" to "Vergi Kimlik Numarası",
            "Arbeitsvertrag" to "İş Sözleşmesi",
            "Schufa-Auskunft" to "Kredi Raporu",
            "Terminbestätigung" to "Randevu Onayı",
            "Unterlagen" to "Evraklar"
        )
        return doc.copy(
            name = translations[doc.name] ?: doc.name,
            description = doc.description
        )
    }
}
