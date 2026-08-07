package dev.sebas1705.careereditor

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebas1705.careereditor.presentation.components.LoadingScreen
import dev.sebas1705.careereditor.presentation.screens.*
import dev.sebas1705.careereditor.presentation.viewmodel.AuthState
import dev.sebas1705.careereditor.presentation.viewmodel.CareerViewModel
import dev.sebas1705.careereditor.ui.theme.CareerTheme

private enum class Section(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    DASHBOARD("Inicio", Icons.Default.Home, "dashboard"),
    PERSONAL("Personal", Icons.Default.Person, "personal"),
    JOBS("Trabajos", Icons.Default.Build, "jobs"),
    PROJECTS("Proyectos", Icons.Default.Edit, "projects"),
    SKILLS("Skills", Icons.Default.Star, "skills"),
    EDUCATION("Educación", Icons.Default.DateRange, "education"),
    CERTIFICATIONS("Certs", Icons.Default.Done, "certifications"),
    SOFT_SKILLS("Soft", Icons.Default.Favorite, "soft-skills"),
    SETTINGS("Ajustes", Icons.Default.Settings, "settings")
}

private val bottomNavItems = listOf(
    Section.DASHBOARD,
    Section.PERSONAL,
    Section.PROJECTS,
    Section.SKILLS,
    Section.SETTINGS
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    CareerTheme {
        val viewModel: CareerViewModel = viewModel { CareerViewModel() }
        val authState by viewModel.authState.collectAsState()
        val state by viewModel.uiState.collectAsState()
        val settingsState by viewModel.settingsState.collectAsState()
        val loginLoading by viewModel.loginLoading.collectAsState()
        val loginError by viewModel.loginError.collectAsState()
        val selectedLangCode by viewModel.selectedLangCode.collectAsState()

        AnimatedContent(
            targetState = authState,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { auth ->
            when (auth) {
                AuthState.Checking -> LoadingScreen()

                AuthState.Unauthenticated -> LoginScreen(
                    isLoading = loginLoading,
                    error = loginError,
                    onLogin = viewModel::login,
                    onAnonymous = viewModel::loginAnonymous,
                    onClearError = viewModel::clearLoginError
                )

                AuthState.Authenticated -> MainScaffold(
                    viewModel = viewModel,
                    state = state,
                    settingsState = settingsState,
                    selectedLangCode = selectedLangCode
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(
    viewModel: CareerViewModel,
    state: dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState,
    settingsState: dev.sebas1705.careereditor.presentation.viewmodel.SettingsUiState,
    selectedLangCode: String
) {
    var currentSection by remember { mutableStateOf(Section.DASHBOARD) }
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pendingRoute) {
        pendingRoute?.let { route ->
            Section.entries.find { it.route == route }?.let { currentSection = it }
            pendingRoute = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentSection.label) },
                actions = {
                    if (currentSection != Section.SETTINGS) {
                        IconButton(onClick = { viewModel.loadAll() }) {
                            Icon(Icons.Default.Refresh, "Recargar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { section ->
                    NavigationBarItem(
                        selected = currentSection == section,
                        onClick = {
                            currentSection = section
                            viewModel.clearSaveSuccess()
                            viewModel.clearError()
                        },
                        icon = { Icon(section.icon, section.label) },
                        // Estilo sans explícito: el labelMedium del tema es mono
                        // (más ancho) y NavigationBar lo usa por defecto — con
                        // él, labels como "Proyectos" partían en dos líneas.
                        label = {
                            Text(
                                section.label,
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        // imePadding: en Android el teclado empuja el contenido en vez de taparlo
        Box(Modifier.fillMaxSize().padding(padding).imePadding()) {
            AnimatedContent(
                targetState = currentSection,
                transitionSpec = {
                    slideInHorizontally { it / 4 } + fadeIn() togetherWith
                            slideOutHorizontally { -it / 4 } + fadeOut()
                }
            ) { section ->
                when (section) {
                    Section.DASHBOARD -> DashboardScreen(
                        state = state,
                        onNavigate = { route ->
                            Section.entries.find { it.route == route }?.let { currentSection = it }
                        }
                    )
                    Section.PERSONAL -> PersonalScreen(
                        state = state,
                        selectedLangCode = selectedLangCode,
                        onSelectLang = viewModel::selectLanguage,
                        onSave = viewModel::savePersonal,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.JOBS -> JobsScreen(
                        state = state,
                        selectedLangCode = selectedLangCode,
                        onSelectLang = viewModel::selectLanguage,
                        onSave = viewModel::saveJob,
                        onCreate = viewModel::createJob,
                        onDelete = viewModel::deleteJob,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.PROJECTS -> ProjectsScreen(
                        state = state,
                        onSave = viewModel::saveProject,
                        onCreate = viewModel::createProject,
                        onDelete = viewModel::deleteProject,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.SKILLS -> SkillsScreen(
                        state = state,
                        onSave = viewModel::saveSkill,
                        onCreate = viewModel::createSkill,
                        onDelete = viewModel::deleteSkill,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.EDUCATION -> EducationScreen(
                        state = state,
                        selectedLangCode = selectedLangCode,
                        onSelectLang = viewModel::selectLanguage,
                        onSave = viewModel::saveEducation,
                        onCreate = viewModel::createEducation,
                        onDelete = viewModel::deleteEducation,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.CERTIFICATIONS -> CertificationsScreen(
                        state = state,
                        onSave = viewModel::saveCertification,
                        onCreate = viewModel::createCertification,
                        onDelete = viewModel::deleteCertification,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.SOFT_SKILLS -> SoftSkillsScreen(
                        state = state,
                        selectedLangCode = selectedLangCode,
                        onSelectLang = viewModel::selectLanguage,
                        onSave = viewModel::saveSoftSkill,
                        onCreate = viewModel::createSoftSkill,
                        onDelete = viewModel::deleteSoftSkill,
                        onClearSuccess = viewModel::clearSaveSuccess,
                        onClearError = viewModel::clearError
                    )
                    Section.SETTINGS -> SettingsScreen(
                        state = settingsState,
                        onSave = viewModel::saveSettings,
                        onLogout = viewModel::logout,
                        onClearMessage = viewModel::clearSettingsMessage
                    )
                }
            }
        }
    }
}
