package com.termintracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.termintracker.model.Appointment
import com.termintracker.model.AppointmentType
import com.termintracker.model.Address
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormDialog(
    appointment: Appointment?,
    onDismiss: () -> Unit,
    onSave: (Appointment) -> Unit,
    onDelete: ((Appointment) -> Unit)? = null
) {
    val isEditing = appointment != null
    
    var title by remember { mutableStateOf(appointment?.title ?: "") }
    var doctorName by remember { mutableStateOf(appointment?.location ?: "") }
    var date by remember { mutableStateOf(appointment?.dateTime?.toString()?.substring(0, 10) ?: "") }
    var time by remember { mutableStateOf(appointment?.dateTime?.toString()?.substring(11, 16) ?: "") }
    var notes by remember { mutableStateOf(appointment?.notes ?: "") }
    var selectedType by remember { mutableStateOf(appointment?.type ?: AppointmentType.SONSTIGES) }
    
    // Address fields with manual autocomplete
    var city by remember { mutableStateOf(appointment?.address?.city ?: "") }
    var district by remember { mutableStateOf(appointment?.address?.district ?: "") }
    var postalCode by remember { mutableStateOf(appointment?.address?.postalCode ?: "") }
    var streetAddress by remember { mutableStateOf(appointment?.address?.street ?: "") }
    
    // Dialog states
    var showCityDialog by remember { mutableStateOf(false) }
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showPostalCodeDialog by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Randevu Düzenle" else "Yeni Randevu") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text("Doktor/Hastane") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Appointment type
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Randevu Türü") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        AppointmentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Date and Time
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Tarih (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Saat (HH:MM)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Address Info Card with Manual Autocomplete
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Adres Bilgileri",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // City with manual search
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("Şehir") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showCityDialog = true },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Ara")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // District with manual search
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("İlçe") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showDistrictDialog = true },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Ara")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Postal Code with manual search
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = postalCode,
                                onValueChange = { postalCode = it },
                                label = { Text("Posta Kodu") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showPostalCodeDialog = true },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Ara")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Street Address with manual search
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = streetAddress,
                                onValueChange = { streetAddress = it },
                                label = { Text("Sokak Adresi") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { showAddressDialog = true },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Ara")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notlar") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newAppointment = Appointment(
                        id = appointment?.id ?: 0,
                        title = title,
                        type = selectedType,
                        dateTime = parseDateTime(date, time),
                        location = doctorName,
                        address = Address(
                            city = city,
                            district = district,
                            postalCode = postalCode,
                            street = streetAddress
                        ),
                        notes = notes
                    )
                    onSave(newAppointment)
                },
                enabled = title.isNotEmpty()
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            Row {
                if (isEditing && onDelete != null) {
                    OutlinedButton(
                        onClick = { appointment?.let { onDelete(it) } },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Sil")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                OutlinedButton(onClick = onDismiss) {
                    Text("İptal")
                }
            }
        }
    )
    
    // Address Search Dialogs
    if (showCityDialog) {
        CitySearchDialog(
            onDismiss = { showCityDialog = false },
            onCitySelected = { selectedCity ->
                city = selectedCity
                showCityDialog = false
            }
        )
    }
    
    if (showDistrictDialog) {
        DistrictSearchDialog(
            city = city,
            onDismiss = { showDistrictDialog = false },
            onDistrictSelected = { selectedDistrict ->
                district = selectedDistrict
                showDistrictDialog = false
            }
        )
    }
    
    if (showPostalCodeDialog) {
        PostalCodeSearchDialog(
            onDismiss = { showPostalCodeDialog = false },
            onPostalCodeSelected = { selectedCode ->
                postalCode = selectedCode
                showPostalCodeDialog = false
            }
        )
    }
    
    if (showAddressDialog) {
        StreetAddressSearchDialog(
            onDismiss = { showAddressDialog = false },
            onAddressSelected = { selectedAddress ->
                streetAddress = selectedAddress
                showAddressDialog = false
            }
        )
    }
}

// Helper function to parse date and time
private fun parseDateTime(date: String, time: String): LocalDateTime {
    val dateParts = date.split("-").map { it.toIntOrNull() ?: 1 }
    val timeParts = time.split(":").map { it.toIntOrNull() ?: 0 }
    
    val year = dateParts.getOrNull(0) ?: 2024
    val month = dateParts.getOrNull(1) ?: 1
    val day = dateParts.getOrNull(2) ?: 1
    val hour = timeParts.getOrNull(0) ?: 0
    val minute = timeParts.getOrNull(1) ?: 0
    
    return LocalDateTime(year, month, day, hour, minute)
}
