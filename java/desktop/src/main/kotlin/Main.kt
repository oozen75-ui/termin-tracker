import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.termintracker.components.*
import com.termintracker.database.DatabaseManager
import com.termintracker.localization.Translations
import com.termintracker.model.*
import com.termintracker.viewmodel.AppointmentViewModel
import com.termintracker.viewmodel.OnlineSearchViewModel
import com.termintracker.utils.Logger
import com.termintracker.utils.AppInfo
import com.termintracker.desktop.utils.GlobalExceptionHandler
import kotlinx.coroutines.*
import kotlinx.datetime.*
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess

// Screen navigation
sealed class Screen {
    object Appointments : Screen()
    object PersonalInfo : Screen()
    object OnlineSearch : Screen()
    object Settings : Screen()
}

fun main() = application {
    // Initialize global exception handler
    GlobalExceptionHandler.initialize()
    
    // Initialize logger
    Logger.info("${AppInfo.FULL_NAME} starting...")
    Logger.info(AppInfo.getBuildInfo())
    
    DatabaseManager.initialize()
    Logger.info("Database initialized")
    
    val viewModel = remember { AppointmentViewModel() }
    
    LaunchedEffect(Unit) {
        val lang = viewModel.personalInfo.value.preferredLanguage
        Translations.setLanguage(lang)
        Logger.info("Language set to: $lang")
    }
    
    Window(
        onCloseRequest = {
            viewModel.dispose()
            DatabaseManager.close()
            Logger.info("Application closing...")
            exitApplication()
        },
        title = "Termin Tracker v1.0.2",
        resizable = true,
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        MaterialTheme {
            Surface {
                // Global exception dialog
                GlobalExceptionHandler.ExceptionDialog()
                
                TerminTrackerApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminTrackerApp(viewModel: AppointmentViewModel) {
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val personalInfo by viewModel.personalInfo.collectAsState()
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Appointments) }
    var showAddAppointment by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Termin Tracker v1.0.2") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    var langExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { langExpanded = true }) {
                            Text(
                                when (personalInfo.preferredLanguage) {
                                    Language.GERMAN -> "DE"
                                    Language.ENGLISH -> "EN"
                                    Language.TURKISH -> "TR"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        DropdownMenu(
                            expanded = langExpanded,
                            onDismissRequest = { langExpanded = false }
                        ) {
                            Language.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang.displayName) },
                                    onClick = {
                                        viewModel.setLanguage(lang)
                                        langExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentScreen == Screen.Appointments) {
                FloatingActionButton(
                    onClick = { showAddAppointment = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Yeni Randevu")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CalendarToday, null) },
                    label = { Text("Randevular") },
                    selected = currentScreen == Screen.Appointments,
                    onClick = { currentScreen = Screen.Appointments }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Kişisel") },
                    selected = currentScreen == Screen.PersonalInfo,
                    onClick = { currentScreen = Screen.PersonalInfo }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Online Ara") },
                    selected = currentScreen == Screen.OnlineSearch,
                    onClick = { currentScreen = Screen.OnlineSearch }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Ayarlar") },
                    selected = currentScreen == Screen.Settings,
                    onClick = { currentScreen = Screen.Settings }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.Appointments -> AppointmentsScreen(
                    appointments = appointments,
                    viewModel = viewModel,
                    onEditAppointment = { appointment ->
                        selectedAppointment = appointment
                        showAddAppointment = true
                    }
                )
                Screen.PersonalInfo -> PersonalInfoScreen(
                    personalInfo = personalInfo,
                    onSave = { viewModel.savePersonalInfo(it) }
                )
                Screen.OnlineSearch -> OnlineSearchScreen(
                    viewModel = remember { OnlineSearchViewModel() }
                )
                Screen.Settings -> SettingsScreen(viewModel = viewModel)
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
    
    if (showAddAppointment) {
        AppointmentFormDialog(
            appointment = selectedAppointment,
            onDismiss = {
                showAddAppointment = false
                selectedAppointment = null
            },
            onSave = { appointment ->
                if (selectedAppointment == null) {
                    viewModel.addAppointment(appointment)
                } else {
                    viewModel.updateAppointment(appointment)
                }
                showAddAppointment = false
                selectedAppointment = null
            },
            onDelete = { appointment ->
                viewModel.deleteAppointment(appointment.id)
                showAddAppointment = false
                selectedAppointment = null
            }
        )
    }
}

@Composable
fun AppointmentsScreen(
    appointments: List<Appointment>,
    viewModel: AppointmentViewModel,
    onEditAppointment: (Appointment) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Randevular",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (appointments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Henüz randevu eklenmemiş",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onClick = { onEditAppointment(appointment) }
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = appointment.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = appointment.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = appointment.dateTime.toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PersonalInfoScreen(
    personalInfo: PersonalInfo,
    onSave: (PersonalInfo) -> Unit
) {
    var firstName by remember { mutableStateOf(personalInfo.firstName) }
    var lastName by remember { mutableStateOf(personalInfo.lastName) }
    var email by remember { mutableStateOf(personalInfo.email) }
    var phone by remember { mutableStateOf(personalInfo.phone) }
    var addressText by remember { mutableStateOf(personalInfo.defaultAddress.toDisplayString()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Kişisel Bilgiler",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Ad") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Soyad") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = addressText,
            onValueChange = { addressText = it },
            label = { Text("Adres") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                onSave(
                    personalInfo.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone,
                        defaultAddress = Address(street = addressText)
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet")
        }
    }
}

@Composable
fun SettingsScreen(viewModel: AppointmentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ayarlar",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Veri Yönetimi",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { /* TODO: Export */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verileri Dışa Aktar")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { /* TODO: Import */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verileri İçe Aktar")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = { /* TODO: Clear all data */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Tüm Verileri Sil", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
