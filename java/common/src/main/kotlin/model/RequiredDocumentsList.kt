package com.termintracker.model

object RequiredDocumentsList {
    fun getRequiredDocuments(type: AppointmentType): List<RequiredDocument> {
        return when (type) {
            // Behörde (Resmi Kurumlar)
            AppointmentType.KVR -> listOf(
                RequiredDocument(name = "Anmeldeformular", isRequired = true),
                RequiredDocument(name = "Mietvertrag", isRequired = true),
                RequiredDocument(name = "Personalausweis/Reisepass", isRequired = true),
                RequiredDocument(name = "Meldebestätigung", isRequired = false)
            )
            
            AppointmentType.AMT -> listOf(
                RequiredDocument(name = "Vollmacht", isRequired = false),
                RequiredDocument(name = "Antragsformular", isRequired = true),
                RequiredDocument(name = "Unterlagen je nach Antrag", isRequired = true)
            )
            
            AppointmentType.BMV_KFZ -> listOf(
                RequiredDocument(name = "Fahrzeugschein", isRequired = true),
                RequiredDocument(name = "Versicherungsnachweis", isRequired = true),
                RequiredDocument(name = "Identitätsnachweis", isRequired = true),
                RequiredDocument(name = "TÜV-Bericht", isRequired = true),
                RequiredDocument(name = "EC-Karte für Gebühren", isRequired = true)
            )
            
            AppointmentType.AUSLAENDERBEHOERDE -> listOf(
                RequiredDocument(name = "Reisepass", isRequired = true),
                RequiredDocument(name = "Biometrisches Passfoto", isRequired = true),
                RequiredDocument(name = "Mietvertrag/Meldebescheinigung", isRequired = true),
                RequiredDocument(name = "Krankenversicherungsnachweis", isRequired = true),
                RequiredDocument(name = "Finanzierungsnachweis", isRequired = true),
                RequiredDocument(name = "Arbeitsvertrag", isRequired = false),
                RequiredDocument(name = "Deutschkenntnisnachweis", isRequired = false)
            )
            
            // Sağlık
            AppointmentType.KRANKENKASSE -> listOf(
                RequiredDocument(name = "Versicherungskarte", isRequired = true),
                RequiredDocument(name = "Personalausweis", isRequired = true),
                RequiredDocument(name = "Bankverbindung", isRequired = false)
            )
            
            AppointmentType.HAUSARZT -> listOf(
                RequiredDocument(name = "Versicherungskarte", isRequired = true),
                RequiredDocument(name = "Personalausweis", isRequired = true),
                RequiredDocument(name = "Befunde/Untersuchungsergebnisse", isRequired = false),
                RequiredDocument(name = "Medikamentenliste", isRequired = false)
            )
            
            AppointmentType.FACHARZT -> listOf(
                RequiredDocument(name = "Versicherungskarte", isRequired = true),
                RequiredDocument(name = "Personalausweis", isRequired = true),
                RequiredDocument(name = "Überweisung vom Hausarzt", isRequired = true),
                RequiredDocument(name = "Vorbefunde", isRequired = false),
                RequiredDocument(name = "Aktuelle Medikamentenliste", isRequired = false)
            )
            
            AppointmentType.ZAHNARZT -> listOf(
                RequiredDocument(name = "Versicherungskarte", isRequired = true),
                RequiredDocument(name = "Personalausweis", isRequired = true),
                RequiredDocument(name = "Röntgenbilder (falls vorhanden)", isRequired = false)
            )
            
            AppointmentType.HNO -> listOf(
                RequiredDocument(name = "Versicherungskarte", isRequired = true),
                RequiredDocument(name = "Personalausweis", isRequired = true),
                RequiredDocument(name = "Überweisung vom Hausarzt", isRequired = true),
                RequiredDocument(name = "Vorbefunde/Untersuchungsergebnisse", isRequired = false),
                RequiredDocument(name = "Aktuelle Medikamentenliste", isRequired = false),
                RequiredDocument(name = "Allergiepass (falls vorhanden)", isRequired = false)
            )
            
            // Finans
            AppointmentType.BANK -> listOf(
                RequiredDocument(name = "Personalausweis/Reisepass", isRequired = true),
                RequiredDocument(name = "Meldebescheinigung", isRequired = true),
                RequiredDocument(name = "Steueridentifikationsnummer", isRequired = true),
                RequiredDocument(name = "Gehaltsnachweis/Mieteinnahmen", isRequired = false)
            )
            
            AppointmentType.STEUER -> listOf(
                RequiredDocument(name = "Steueridentifikationsnummer", isRequired = true),
                RequiredDocument(name = "Lohnsteuerbescheinigung", isRequired = true),
                RequiredDocument(name = "Belege für Werbungskosten", isRequired = false),
                RequiredDocument(name = "Kontoauszüge", isRequired = false),
                RequiredDocument(name = "Vorjahressteuererklärung", isRequired = false)
            )
            
            // Diğer
            AppointmentType.SONSTIGES -> listOf(
                RequiredDocument(name = "Je nach Terminart", isRequired = true)
            )
        }
    }
}