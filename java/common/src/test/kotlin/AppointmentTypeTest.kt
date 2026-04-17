package com.termintracker.common

import com.termintracker.model.Appointment
import com.termintracker.model.AppointmentType
import com.termintracker.model.Language
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.*

/**
 * Temel unit test'ler v1.2.0
 */
class AppointmentTypeTest {
    
    @Test
    fun testAllTypesHaveCategory() {
        AppointmentType.entries.forEach { type ->
            assertNotNull(type.category, "${type.name} must have a category")
            assertTrue(type.category.isNotBlank(), "${type.name} category must not be blank")
        }
    }
    
    @Test
    fun testGetByCategory() {
        val behoerdeTypes = AppointmentType.getByCategory("Behörde")
        assertTrue(behoerdeTypes.isNotEmpty(), "Should have Behörde types")
        assertTrue(behoerdeTypes.contains(AppointmentType.BMV_KFZ), "Should include BMV_KFZ")
        
        val healthTypes = AppointmentType.getByCategory("Sağlık")
        assertTrue(healthTypes.contains(AppointmentType.HAUSARZT), "Should include Hausarzt")
    }
    
    @Test
    fun testCategoriesNotEmpty() {
        val categories = AppointmentType.getCategories()
        assertTrue(categories.isNotEmpty(), "Should have categories")
        assertTrue(categories.contains("Behörde"), "Should have Behörde category")
        assertTrue(categories.contains("Sağlık"), "Should have Sağlık category")
    }
    
    @Test
    fun testFromString() {
        assertEquals(AppointmentType.KVR, AppointmentType.fromString("KVR"))
        assertEquals(AppointmentType.BMV_KFZ, AppointmentType.fromString("BMV_KFZ"))
        assertEquals(AppointmentType.SONSTIGES, AppointmentType.fromString("Unknown"))
    }
}

class AppointmentTest {
    
    @Test
    fun testAppointmentCreation() {
        val appointment = Appointment(
            id = 1L,
            type = AppointmentType.KVR,
            date = LocalDate(2026, 4, 20),
            time = LocalTime(10, 30),
            durationMinutes = 30,
            location = "Berlin Rathaus",
            notes = "Anmeldung için"
        )
        
        assertEquals(1L, appointment.id)
        assertEquals(AppointmentType.KVR, appointment.type)
        assertEquals("Berlin Rathaus", appointment.location)
        assertFalse(appointment.isCompleted)
    }
    
    @Test
    fun testAppointmentEquality() {
        val date = LocalDate(2026, 4, 20)
        val time = LocalTime(10, 0)
        
        val a1 = Appointment(1L, AppointmentType.BANK, date, time, 15, "Bank")
        val a2 = Appointment(1L, AppointmentType.BANK, date, time, 15, "Bank")
        
        assertEquals(a1.id, a2.id)
        assertEquals(a1.date, a2.date)
    }
}

class LanguageTest {
    
    @Test
    fun testLanguageEntries() {
        assertTrue(Language.entries.isNotEmpty(), "Should have language entries")
        assertTrue(Language.entries.contains(Language.GERMAN), "Should have German")
        assertTrue(Language.entries.contains(Language.ENGLISH), "Should have English")
        assertTrue(Language.entries.contains(Language.TURKISH), "Should have Turkish")
    }
}