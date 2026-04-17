package com.termintracker.notification

import com.termintracker.model.search.NotificationType
import com.termintracker.model.search.NotificationSettings
import com.termintracker.model.search.AppointmentSearch
import com.termintracker.model.search.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.Image

class DesktopNotificationService : BaseNotificationService() {

    private var trayIcon: TrayIcon? = null
    private var tray: SystemTray? = null

    init {
        initializeTray()
    }

    private fun initializeTray() {
        if (!SystemTray.isSupported()) {
            println("System tray not supported")
            return
        }

        try {
            tray = SystemTray.getSystemTray()

            // Create a simple icon
            val image: Image = Toolkit.getDefaultToolkit().createImage(
                byteArrayOf(0x44, 0x88.toByte(), 0xCC.toByte(), 0xFF.toByte())
            )

            trayIcon = TrayIcon(image, "Termin Tracker").apply {
                isImageAutoSize = true
                toolTip = "Termin Tracker - Online Termin Suche"
            }

            tray?.add(trayIcon)
        } catch (e: Exception) {
            println("Failed to initialize system tray: ${e.message}")
        }
    }

    override suspend fun sendNotification(
        recipient: String,
        subject: String,
        content: String
    ): NotificationResult = withContext(Dispatchers.Main) {
        try {
            if (trayIcon == null || tray == null) {
                // Try to reinitialize
                initializeTray()

                if (trayIcon == null) {
                    return@withContext NotificationResult(
                        false,
                        NotificationType.DESKTOP,
                        "Desktop notifications not supported on this system"
                    )
                }
            }

            trayIcon?.displayMessage(
                subject,
                content.take(200), // Limit message length
                TrayIcon.MessageType.INFO
            )

            NotificationResult(true, NotificationType.DESKTOP)
        } catch (e: Exception) {
            NotificationResult(false, NotificationType.DESKTOP, e.message)
        }
    }

    override suspend fun sendAppointmentFoundNotification(
        settings: NotificationSettings,
        search: AppointmentSearch,
        result: SearchResult
    ): List<NotificationResult> {
        val content = createAppointmentNotificationContent(search, result)
        return listOf(sendNotification("Termin Tracker", content.subject, content.body))
    }

    override suspend fun isConfigured(): Boolean {
        return trayIcon != null && tray != null
    }

    fun dispose() {
        try {
            trayIcon?.let { tray?.remove(it) }
            trayIcon = null
        } catch (e: Exception) {
            // Ignore
        }
    }
}
