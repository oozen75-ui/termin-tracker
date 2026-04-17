package com.termintracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.termintracker.model.Appointment
import com.termintracker.model.AppointmentType
import com.termintracker.localization.Translations
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCompleteToggle: (Boolean) -> Unit,
    onExport: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val isPast = appointment.dateTime < now
    val isToday = appointment.dateTime.date == now.date
    
    val cardColor = when {
        appointment.isCompleted -> MaterialTheme.colorScheme.surfaceVariant
        isPast -> MaterialTheme.colorScheme.errorContainer
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        appointment.isCompleted -> MaterialTheme.colorScheme.outlineVariant
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getIconForType(appointment.type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = Translations.getAppointmentTypeName(appointment.type),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Checkbox(
                    checked = appointment.isCompleted,
                    onCheckedChange = onCompleteToggle
                )
            }
            
            // Title
            Text(
                text = appointment.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (appointment.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            
            // Date and Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDateTime(appointment.dateTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (isToday) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "TODAY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Location
            if (appointment.address.isComplete()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = appointment.address.toDisplayString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            
            // Documents status
            if (appointment.requiredDocuments.isNotEmpty()) {
                val checkedCount = appointment.requiredDocuments.count { it.isChecked }
                val totalCount = appointment.requiredDocuments.size
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (checkedCount == totalCount) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$checkedCount/$totalCount documents",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (checkedCount == totalCount) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onExport != null) {
                    IconButton(onClick = onExport) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentTypeSelector(
    selectedType: AppointmentType,
    onTypeSelected: (AppointmentType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = Translations.getAppointmentTypeName(selectedType),
            onValueChange = {},
            readOnly = true,
            label = { Text(Translations.appointmentType()) },
            leadingIcon = { Icon(getIconForType(selectedType), contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppointmentType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(Translations.getAppointmentTypeName(type)) },
                    leadingIcon = { Icon(getIconForType(type), contentDescription = null) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun getIconForType(type: AppointmentType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        AppointmentType.KVR -> Icons.Default.AccountBalance
        AppointmentType.AMT -> Icons.Default.Business
        AppointmentType.BMV_KFZ -> Icons.Default.DirectionsCar
        AppointmentType.AUSLAENDERBEHOERDE -> Icons.Default.Fingerprint
        AppointmentType.KRANKENKASSE -> Icons.Default.LocalHospital
        AppointmentType.HAUSARZT -> Icons.Default.MedicalServices
        AppointmentType.FACHARZT -> Icons.Default.Healing
        AppointmentType.ZAHNARZT -> Icons.Default.MedicalServices
        AppointmentType.BANK -> Icons.Default.AccountBalanceWallet
        AppointmentType.STEUER -> Icons.Default.Calculate
        AppointmentType.SONSTIGES -> Icons.Default.MoreHoriz
        else -> Icons.Default.MoreHoriz
    }
}

private fun formatDateTime(dateTime: LocalDateTime): String {
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val month = monthNames[dateTime.monthNumber - 1]
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "${dateTime.dayOfMonth} $month ${dateTime.year}, $hour:$minute"
}
