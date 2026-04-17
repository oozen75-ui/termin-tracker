package com.termintracker.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.termintracker.network.OpenStreetMapService
import com.termintracker.network.AddressSearchResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchDialog(
    onDismiss: () -> Unit,
    onCitySelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AddressSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val openStreetMapService = remember { OpenStreetMapService() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Şehir Ara") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Şehir adı") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                isSearching = true
                                scope.launch {
                                    searchResults = openStreetMapService.searchCities(searchQuery)
                                    isSearching = false
                                }
                            }
                        },
                        enabled = searchQuery.isNotEmpty() && !isSearching
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Ara")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (searchResults.isNotEmpty()) {
                    LazyColumn {
                        items(searchResults) { result ->
                            ListItem(
                                headlineContent = { Text(result.displayName) },
                                leadingContent = { Icon(Icons.Default.LocationOn, null) },
                                modifier = Modifier.clickable {
                                    onCitySelected(result.displayName)
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty() && !isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sonuç bulunamadı")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictSearchDialog(
    city: String,
    onDismiss: () -> Unit,
    onDistrictSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AddressSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val openStreetMapService = remember { OpenStreetMapService() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("İlçe Ara") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Text(
                    text = "Şehir: $city",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("İlçe adı") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                isSearching = true
                                scope.launch {
                                    searchResults = openStreetMapService.searchDistricts(city, searchQuery)
                                    isSearching = false
                                }
                            }
                        },
                        enabled = searchQuery.isNotEmpty() && !isSearching
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Ara")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (searchResults.isNotEmpty()) {
                    LazyColumn {
                        items(searchResults) { result ->
                            ListItem(
                                headlineContent = { Text(result.displayName) },
                                leadingContent = { Icon(Icons.Default.LocationOn, null) },
                                modifier = Modifier.clickable {
                                    onDistrictSelected(result.displayName)
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty() && !isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sonuç bulunamadı")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostalCodeSearchDialog(
    onDismiss: () -> Unit,
    onPostalCodeSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AddressSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val openStreetMapService = remember { OpenStreetMapService() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Posta Kodu Ara") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Posta kodu veya adres") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                isSearching = true
                                scope.launch {
                                    searchResults = openStreetMapService.searchPostalCodes(searchQuery)
                                    isSearching = false
                                }
                            }
                        },
                        enabled = searchQuery.isNotEmpty() && !isSearching
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Ara")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (searchResults.isNotEmpty()) {
                    LazyColumn {
                        items(searchResults) { result ->
                            ListItem(
                                headlineContent = { Text(result.postalCode ?: "Bilinmiyor") },
                                supportingContent = { Text(result.displayName) },
                                leadingContent = { Icon(Icons.Default.LocationOn, null) },
                                modifier = Modifier.clickable {
                                    onPostalCodeSelected(result.postalCode ?: "")
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty() && !isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sonuç bulunamadı")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreetAddressSearchDialog(
    onDismiss: () -> Unit,
    onAddressSelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AddressSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val openStreetMapService = remember { OpenStreetMapService() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adres Ara") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Sokak adı veya adres") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchQuery.isNotEmpty()) {
                                isSearching = true
                                scope.launch {
                                    searchResults = openStreetMapService.searchAddresses(searchQuery)
                                    isSearching = false
                                }
                            }
                        },
                        enabled = searchQuery.isNotEmpty() && !isSearching
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Ara")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (searchResults.isNotEmpty()) {
                    LazyColumn {
                        items(searchResults) { result ->
                            ListItem(
                                headlineContent = { Text(result.displayName) },
                                leadingContent = { Icon(Icons.Default.LocationOn, null) },
                                modifier = Modifier.clickable {
                                    onAddressSelected(result.displayName)
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else if (searchQuery.isNotEmpty() && !isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sonuç bulunamadı")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}
