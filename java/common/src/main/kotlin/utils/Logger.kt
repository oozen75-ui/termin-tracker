package com.termintracker.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val logFile: File by lazy {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".termin_tracker")
        appDir.mkdirs()
        File(appDir, "app.log").apply {
            if (!exists()) createNewFile()
        }
    }
    
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    enum class Level { DEBUG, INFO, WARN, ERROR }
    
    @JvmStatic
    fun log(level: Level, message: String, throwable: Throwable? = null) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[$timestamp] [${level.name}] $message"
        
        // Console output
        when (level) {
            Level.ERROR -> System.err.println(logMessage)
            else -> println(logMessage)
        }
        
        // File output
        logFile.appendText("$logMessage\n")
        throwable?.let {
            logFile.appendText("Exception: ${it.message}\n")
            logFile.appendText(it.stackTraceToString() + "\n")
        }
    }
    
    @JvmStatic
    fun debug(message: String) = log(Level.DEBUG, message)
    
    @JvmStatic
    fun info(message: String) = log(Level.INFO, message)
    
    @JvmStatic
    fun warn(message: String, throwable: Throwable? = null) = log(Level.WARN, message, throwable)
    
    @JvmStatic
    fun error(message: String, throwable: Throwable? = null) = log(Level.ERROR, message, throwable)
    
    fun getLogFile(): File = logFile
}