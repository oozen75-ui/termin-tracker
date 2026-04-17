package com.termintracker.viewmodel

import androidx.compose.runtime.*
import com.termintracker.model.*
import com.termintracker.repository.AppointmentRepository
import com.termintracker.repository.PersonalInfoRepository
import com.termintracker.service.OpenStreetMapService
import com.termintracker.utils.IcsExporter
import com.termintracker.utils.JsonExportImport
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.*
import java.io.File

class AppointmentViewModel {
    private val appointmentRepo = AppointmentRepository()
    private val personalInfoRepo = PersonalInfoRepository()
    private val osmService = OpenStreetMapService()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // State
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _personalInfo = MutableStateFlow(PersonalInfo())
    val personalInfo: StateFlow<PersonalInfo> = _personalInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchResults = MutableStateFlow<List<OpenStreetMapService.AddressResult>>(emptyList())
    val searchResults: StateFlow<List<OpenStreetMapService.AddressResult>> = _searchResults.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    init {
        loadAppointments()
        loadPersonalInfo()
    }

    // Load operations
    fun loadAppointments() {
        scope.launch {
            _isLoading.value = true
            _appointments.value = appointmentRepo.getAll()
            _isLoading.value = false
        }
    }

    fun loadPersonalInfo() {
        scope.launch {
            personalInfoRepo.get()?.let {
                _personalInfo.value = it
            }
        }
    }

    // CRUD Operations
    fun addAppointment(appointment: Appointment) {
        scope.launch {
            _isLoading.value = true
            val id = appointmentRepo.save(appointment)
            if (id > 0) {
                loadAppointments()
                showNotification("Appointment saved successfully")
            }
            _isLoading.value = false
        }
    }

    fun updateAppointment(appointment: Appointment) {
        scope.launch {
            _isLoading.value = true
            if (appointmentRepo.update(appointment)) {
                loadAppointments()
                showNotification("Appointment updated successfully")
            }
            _isLoading.value = false
        }
    }

    fun deleteAppointment(id: Long) {
        scope.launch {
            if (appointmentRepo.delete(id)) {
                loadAppointments()
                showNotification("Appointment deleted")
            }
        }
    }

    fun markAppointmentComplete(id: Long, completed: Boolean) {
        scope.launch {
            if (appointmentRepo.markAsCompleted(id, completed)) {
                loadAppointments()
            }
        }
    }

    // Document operations
    fun updateRequiredDocumentCheck(appointmentId: Long, documentId: Long, checked: Boolean) {
        // In a real implementation, this would update the database
        // For now, we'll just refresh the list
        loadAppointments()
    }

    // Address search
    fun searchAddress(query: String) {
        scope.launch {
            _isLoading.value = true
            _searchResults.value = osmService.searchAddress(query)
            _isLoading.value = false
        }
    }

    fun geocodeAddress(street: String, houseNumber: String, city: String, postalCode: String) {
        scope.launch {
            _isLoading.value = true
            _searchResults.value = osmService.geocodeAddress(street, houseNumber, city, postalCode)
            _isLoading.value = false
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }

    // Export/Import
    fun exportToJson(filePath: String): Boolean {
        return try {
            val result = JsonExportImport.exportToFile(
                _appointments.value,
                filePath,
                _personalInfo.value
            )
            if (result) {
                showNotification("Exported to JSON successfully")
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            showNotification("Export failed: ${e.message}")
            false
        }
    }

    fun importFromJson(filePath: String): Boolean {
        return try {
            val appointments = JsonExportImport.importFromFile(filePath)
            if (appointments != null) {
                scope.launch {
                    appointments.forEach { appointmentRepo.save(it) }
                    loadAppointments()
                }
                
                // Try to import personal info
                val jsonString = File(filePath).readText()
                JsonExportImport.importPersonalInfoFromJson(jsonString)?.let {
                    personalInfoRepo.save(it)
                    _personalInfo.value = it
                }
                
                showNotification("Imported from JSON successfully")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showNotification("Import failed: ${e.message}")
            false
        }
    }

    fun exportToIcs(filePath: String): Boolean {
        return try {
            val result = IcsExporter.exportToFile(_appointments.value, filePath)
            if (result) {
                showNotification("Exported to ICS successfully")
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            showNotification("Export failed: ${e.message}")
            false
        }
    }

    // Personal info
    fun savePersonalInfo(info: PersonalInfo) {
        scope.launch {
            if (personalInfoRepo.save(info)) {
                _personalInfo.value = info
                showNotification("Personal info saved")
            }
        }
    }

    // Language
    fun setLanguage(language: Language) {
        com.termintracker.localization.Translations.setLanguage(language)
        val updatedInfo = _personalInfo.value.copy(preferredLanguage = language)
        savePersonalInfo(updatedInfo)
    }

    // Notification
    private fun showNotification(message: String) {
        _notificationMessage.value = message
        scope.launch {
            delay(3000)
            _notificationMessage.value = null
        }
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }

    // Getters for filtered lists
    fun getTodayAppointments(): List<Appointment> {
        return _appointments.value.filter { 
            it.dateTime.date == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date 
        }
    }

    fun getUpcomingAppointments(): List<Appointment> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return _appointments.value.filter { it.dateTime > now }.sortedBy { it.dateTime }
    }

    fun getPastAppointments(): List<Appointment> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return _appointments.value.filter { it.dateTime < now }.sortedByDescending { it.dateTime }
    }

    // Get required documents for appointment type
    fun getRequiredDocumentsForType(type: AppointmentType): List<RequiredDocument> {
        return RequiredDocumentsList.getRequiredDocuments(type)
    }

    fun dispose() {
        scope.cancel()
    }
}
