package com.termintracker.utils

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    private val logFilePath: String by lazy {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".termin_tracker")
        appDir.mkdirs()
        File(appDir, "app.log").apply {
            if (!exists()) createNewFile()
        }.absolutePath
    }
    
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    enum class Level { DEBUG, INFO, WARN, ERROR }
    
    @JvmStatic
    fun log(level: Level, message: String, throwable: Throwable? = null) {
        val timestamp = LocalDateTime.now().format(formatter)
        val logMessage = "[$timestamp] [${level.name}] $message"
        
        when (level) {
            Level.ERROR -> System.err.println(logMessage)
            else -> println(logMessage)
        }
        
        File(logFilePath).appendText("$logMessage\n")
        throwable?.let {
            File(logFilePath).appendText("Exception: ${it.message}\n")
            File(logFilePath).appendText(it.stackTraceToString() + "\n")
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
    
    fun getLogFile(): File = File(logFilePath)
}