package com.termintracker.service

import com.termintracker.model.Appointment
import kotlinx.coroutines.*
import kotlinx.datetime.*
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import kotlin.time.Duration.Companion.minutes

class NotificationService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var checkJob: Job? = null
    private var trayIcon: TrayIcon? = null
    
    init {
        initializeSystemTray()
    }
    
    private fun initializeSystemTray() {
        if (SystemTray.isSupported()) {
            try {
                val tray = SystemTray.getSystemTray()
                val image = Toolkit.getDefaultToolkit().createImage(javaClass.getResource("/icon.png"))
                    ?: Toolkit.getDefaultToolkit().createImage(ByteArray(16))
                trayIcon = TrayIcon(image, "Termin Tracker").apply {
                    isImageAutoSize = true
                    toolTip = "Termin Tracker"
                }
                tray.add(trayIcon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun startReminderCheck(getAppointments: () -> List<Appointment>) {
        checkJob?.cancel()
        checkJob = scope.launch {
            while (isActive) {
                checkReminders(getAppointments())
                delay(60_000) // Check every minute
            }
        }
    }
    
    fun stopReminderCheck() {
        checkJob?.cancel()
        checkJob = null
    }
    
    private fun checkReminders(appointments: List<Appointment>) {
        val now = Clock.System.now()
        
        appointments.filter { it.isReminderEnabled && !it.isCompleted }.forEach { appointment ->
            val appointmentInstant = appointment.dateTime.toInstant(TimeZone.currentSystemDefault())
            val reminderInstant = appointmentInstant.minus(appointment.reminderMinutes.minutes)
            
            // Check if we should show reminder (within 1 minute window)
            val diff = now.epochSeconds - reminderInstant.epochSeconds
            
            if (diff in 0..60) {
                showNotification(
                    "Termin Reminder",
                    "${appointment.title} in ${appointment.reminderMinutes} minutes"
                )
            }
        }
    }
    
    private fun showNotification(title: String, message: String) {
        if (SystemTray.isSupported() && trayIcon != null) {
            trayIcon?.displayMessage(title, message, TrayIcon.MessageType.INFO)
        }
        // Also print to console for debugging
        println("[NOTIFICATION] $title: $message")
    }
    
    fun dispose() {
        stopReminderCheck()
        if (SystemTray.isSupported() && trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon)
        }
    }
}
