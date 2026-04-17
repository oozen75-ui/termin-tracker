package com.termintracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    selectedDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    var currentDate by remember { mutableStateOf(selectedDateTime.date) }
    var currentTime by remember { mutableStateOf(selectedDateTime.time) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Date Button
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.CalendarToday, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(currentDate.toString())
        }

        // Time Button
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Schedule, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("${currentTime.hour.toString().padStart(2, '0')}:${currentTime.minute.toString().padStart(2, '0')}")
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = currentDate.toEpochDays().toLong() * 24 * 60 * 60 * 1000
            )
            
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(16.dp)
            )
            
            LaunchedEffect(datePickerState.selectedDateMillis) {
                datePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = (millis / (24 * 60 * 60 * 1000)).toInt()
                    currentDate = LocalDate.fromEpochDays(epochDays)
                    val newDateTime = LocalDateTime(currentDate, currentTime)
                    onDateTimeSelected(newDateTime)
                }
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Select Time") },
            text = {
                // Simple time selector using text fields
                var hourText by remember { mutableStateOf(currentTime.hour.toString().padStart(2, '0')) }
                var minuteText by remember { mutableStateOf(currentTime.minute.toString().padStart(2, '0')) }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { 
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                hourText = it
                            }
                        },
                        label = { Text("Hour") },
                        modifier = Modifier.weight(1f)
                    )
                    Text(":", modifier = Modifier.padding(top = 16.dp))
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { 
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                minuteText = it
                            }
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                LaunchedEffect(hourText, minuteText) {
                    val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: currentTime.hour
                    val minute = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: currentTime.minute
                    currentTime = LocalTime(hour, minute)
                    val newDateTime = LocalDateTime(currentDate, currentTime)
                    onDateTimeSelected(newDateTime)
                }
            }
        )
    }
}

@Composable
fun ReminderSelector(
    reminderMinutes: Int,
    isEnabled: Boolean,
    onReminderChanged: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val reminderOptions = listOf(
        5 to "5 min",
        10 to "10 min",
        15 to "15 min",
        30 to "30 min",
        60 to "1 hour",
        120 to "2 hours",
        1440 to "1 day",
        2880 to "2 days"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Switch(
            checked = isEnabled,
            onCheckedChange = { onReminderChanged(reminderMinutes, it) }
        )
        
        if (isEnabled) {
            Text("Remind before:")
            
            var expanded by remember { mutableStateOf(false) }
            
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    val selected = reminderOptions.find { it.first == reminderMinutes }
                    Text(selected?.second ?: "${reminderMinutes} min")
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    reminderOptions.forEach { (minutes, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onReminderChanged(minutes, true)
                                expanded = false
                            }
                        )
                    }
                }
            }
        } else {
            Text("Reminder disabled", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
