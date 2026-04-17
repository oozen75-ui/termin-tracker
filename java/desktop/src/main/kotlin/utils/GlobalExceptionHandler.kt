package com.termintracker.desktop.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.termintracker.utils.Logger
import kotlin.system.exitProcess

object GlobalExceptionHandler {
    private val uncaughtExceptions = mutableStateOf<Throwable?>(null)
    
    fun initialize() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Logger.error("Uncaught exception in thread ${thread.name}", throwable)
            uncaughtExceptions.value = throwable
        }
    }
    
    @Composable
    fun ExceptionDialog() {
        uncaughtExceptions.value?.let { exception ->
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Bir Hata Oluştu") },
                text = {
                    Text("Uygulamada beklenmeyen bir hata oluştu.\n\n" +
                        "Hata: ${exception.message}\n\n" +
                        "Log dosyası: ${Logger.getLogFile().absolutePath}")
                },
                confirmButton = {
                    Button(onClick = { exitProcess(1) }) {
                        Text("Uygulamayı Kapat")
                    }
                },
                dismissButton = {
                    Button(onClick = { uncaughtExceptions.value = null }) {
                        Text("Devam Et (Riskli)")
                    }
                }
            )
        }
    }
}