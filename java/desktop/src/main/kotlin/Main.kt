import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Termin Tracker v1.0.1",
        resizable = true
    ) {
        MaterialTheme {
            Surface {
                TerminTrackerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminTrackerApp() {
    val scrollState = rememberScrollState()
    
    // Form verileri
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    var selectedDistrict by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    // Şehir listesi (offline)
    val cities = listOf(
        "Berlin", "Hamburg", "München", "Köln", "Frankfurt",
        "Stuttgart", "Düsseldorf", "Dortmund", "Essen", "Leipzig"
    )
    
    // İlçe listesi (basit)
    val districts = when (selectedCity) {
        "Berlin" -> listOf("Mitte", "Charlottenburg", "Kreuzberg", "Neukölln")
        "Hamburg" -> listOf("Altona", "Eimsbüttel", "Wandsbek")
        "München" -> listOf("Schwabing", "Ludwigsvorstadt", "Sendling")
        "Köln" -> listOf("Altstadt", "Neustadt", "Ehrenfeld")
        "Frankfurt" -> listOf("Innenstadt", "Westend", "Sachsenhausen")
        else -> listOf("Merkez", "Nord", "Ost", "Süd", "West")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Termin Tracker v1.0.1") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Kişisel Bilgiler Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Kişisel Bilgiler",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // İsim
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("İsim *") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Soyisim
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Soyisim *") },
                        leadingIcon = { Icon(Icons.Default.PersonOutline, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Doğum tarihi
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = { Text("Doğum Tarihi") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("GG.AA.YYYY") }
                    )
                }
            }
            
            // Adres Bilgileri Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Adres Bilgileri",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Şehir Dropdown
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCity,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Şehir *") },
                            leadingIcon = { Icon(Icons.Default.LocationCity, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                    }
                    
                    // İlçe Dropdown
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedDistrict,
                            onValueChange = {},
                            readOnly = true,
                            enabled = selectedCity.isNotEmpty(),
                            label = { Text("İlçe / Bezirk") },
                            leadingIcon = { Icon(Icons.Default.Map, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                    }
                    
                    // Posta Kodu
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Posta Kodu (PLZ)") },
                        leadingIcon = { Icon(Icons.Default.MarkunreadMailbox, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Adres
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Sokak / Cadde / No") },
                        leadingIcon = { Icon(Icons.Default.Home, null) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Kaydet Butonu
            Button(
                onClick = {
                    println("Kaydedildi: $firstName $lastName, $selectedCity")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = firstName.isNotBlank() && lastName.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kaydet")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun TerminTrackerAppPreview() {
    MaterialTheme {
        TerminTrackerApp()
    }
}
