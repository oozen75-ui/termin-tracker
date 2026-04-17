package com.termintracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.termintracker.service.OpenStreetMapService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<OpenStreetMapService.AddressResult>,
    onResultSelected: (OpenStreetMapService.AddressResult) -> Unit,
    onClearResults: () -> Unit,
    isLoading: Boolean = false,
    label: String = "Address",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded && searchResults.isNotEmpty(),
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                if (it.length >= 3) {
                    onSearch(it)
                    expanded = true
                } else if (it.isEmpty()) {
                    onClearResults()
                }
            },
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Row {
                        if (value.isNotEmpty()) {
                            IconButton(onClick = { onValueChange(""); onClearResults() }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded && searchResults.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            searchResults.forEach { result ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(result.displayName, maxLines = 2)
                            Text(
                                "${result.address.street} ${result.address.houseNumber}, ${result.address.postalCode} ${result.address.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onResultSelected(result)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AddressForm(
    street: String,
    onStreetChange: (String) -> Unit,
    houseNumber: String,
    onHouseNumberChange: (String) -> Unit,
    postalCode: String,
    onPostalCodeChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    district: String,
    onDistrictChange: (String) -> Unit,
    onAutoFill: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Address",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (onAutoFill != null) {
                    Button(onClick = onAutoFill) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-fill")
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = street,
                    onValueChange = onStreetChange,
                    label = { Text("Street") },
                    modifier = Modifier.weight(2f),
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                
                OutlinedTextField(
                    value = houseNumber,
                    onValueChange = onHouseNumberChange,
                    label = { Text("No.") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = onPostalCodeChange,
                    label = { Text("Postal Code") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.MarkunreadMailbox, contentDescription = null) }
                )
                
                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChange,
                    label = { Text("City") },
                    modifier = Modifier.weight(2f),
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) }
                )
            }
            
            OutlinedTextField(
                value = district,
                onValueChange = onDistrictChange,
                label = { Text("District / Bezirk") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Map, contentDescription = null) }
            )
        }
    }
}

@Composable
fun AddressDisplay(
    address: com.termintracker.model.Address,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (address.street.isNotBlank()) {
                Text("${address.street} ${address.houseNumber}".trim())
            }
            if (address.postalCode.isNotBlank() || address.city.isNotBlank()) {
                Text("${address.postalCode} ${address.city}".trim())
            }
            if (address.district.isNotBlank()) {
                Text(
                    address.district,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (address.latitude != null && address.longitude != null) {
                Text(
                    "📍 ${String.format("%.6f", address.latitude)}, ${String.format("%.6f", address.longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
