package com.termintracker.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.termintracker.viewmodel.OnlineSearchViewModel
import com.termintracker.model.search.*
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineSearchScreen(viewModel: OnlineSearchViewModel = remember { OnlineSearchViewModel() }) {
    var currentTab by remember { mutableStateOf(0) }
    val tabs = listOf("Yeni Arama", "Aktif Aramalar", "Sonuçlar")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = currentTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (currentTab) {
            0 -> NewSearchTab(viewModel) { currentTab = 1 }
            1 -> ActiveSearchesTab(viewModel)
            2 -> SearchResultsTab(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSearchTab(viewModel: OnlineSearchViewModel, onSearchStarted: () -> Unit) {
    var searchName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AppointmentCategory.HAUSARZT) }
    var startDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var endDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault()).plus(DatePeriod(days = 14))) }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf(10) }
    var interval by remember { mutableStateOf(15) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val categories = AppointmentCategory.entries.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Online Randevu Ara",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = searchName,
            onValueChange = { searchName = it },
            label = { Text("Arama Adı (opsiyonel)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Category dropdown
        ExposedDropdownMenuBox(
            expanded = showCategoryDropdown,
            onExpandedChange = { showCategoryDropdown = it }
        ) {
            OutlinedTextField(
                value = selectedCategory.getDisplayName(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Randevu Türü") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showCategoryDropdown) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = showCategoryDropdown,
                onDismissRequest = { showCategoryDropdown = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.getDisplayName()) },
                        onClick = {
                            selectedCategory = category
                            showCategoryDropdown = false
                        }
                    )
                }
            }
        }

        // Date range
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = startDate.toString(),
                onValueChange = { },
                label = { Text("Başlangıç") },
                readOnly = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = endDate.toString(),
                onValueChange = { },
                label = { Text("Bitiş") },
                readOnly = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Location with manual search button
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
                Icon(Icons.Default.Search, contentDescription = "Şehir Ara")
            }
        }

        OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("Posta Kodu (opsiyonel)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Radius and Interval sliders
        Column {
            Text("Yarıçap: $radius km", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = radius.toFloat(),
                onValueChange = { radius = it.toInt() },
                valueRange = 5f..50f
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text("Arama sıklığı: $interval dk", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = interval.toFloat(),
                onValueChange = { interval = it.toInt() },
                valueRange = 5f..60f,
                steps = 11
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (city.isNotEmpty()) {
                    viewModel.startOnlineSearch(
                        name = searchName.ifEmpty { "Arama ${System.currentTimeMillis()}" },
                        appointmentCategory = selectedCategory,
                        startDate = startDate,
                        endDate = endDate,
                        city = city,
                        postalCode = postalCode.ifEmpty { null },
                        radiusKm = radius,
                        intervalMinutes = interval
                    )
                    onSearchStarted()
                }
            },
            enabled = city.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Aramayı Başlat")
        }
    }

    if (showCityDialog) {
        CitySearchDialog(
            onDismiss = { showCityDialog = false },
            onCitySelected = { selectedCity ->
                city = selectedCity
            }
        )
    }
}

@Composable
fun ActiveSearchesTab(viewModel: OnlineSearchViewModel) {
    val activeSearches = remember { mutableStateListOf<Long>() }

    LaunchedEffect(Unit) {
        activeSearches.clear()
        activeSearches.addAll(viewModel.getActiveSearches())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Aktif Aramalar",
            style = MaterialTheme.typography.headlineSmall
        )

        if (activeSearches.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aktif arama yok")
            }
        } else {
            LazyColumn {
                items(activeSearches) { searchId ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Arama #$searchId")
                                Text("Aktif", style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = {
                                    viewModel.stopSearch(searchId)
                                    activeSearches.remove(searchId)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Durdur")
                            }
                        }
                    }
                }
            }
        }

        if (activeSearches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.stopAllSearches()
                    activeSearches.clear()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tüm Aramaları Durdur")
            }
        }
    }
}

@Composable
fun SearchResultsTab(viewModel: OnlineSearchViewModel) {
    var results by remember { mutableStateOf<List<SearchResult>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.onResultsFound = { newResults ->
            results = newResults
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bulunan Randevular",
            style = MaterialTheme.typography.headlineSmall
        )

        if (results.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Henüz randevu bulunmadı")
            }
        } else {
            LazyColumn {
                items(results) { result ->
                    SearchResultCard(result)
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(result: SearchResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = result.sourceName,
                style = MaterialTheme.typography.titleMedium
            )
            result.doctorName?.let {
                Text(
                    text = "Dr. $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${result.appointmentDate} ${result.appointmentTime ?: ""}",
                style = MaterialTheme.typography.bodyLarge
            )
            result.address?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            result.bookingUrl?.let { url ->
                Text(
                    text = "Online: $url",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
