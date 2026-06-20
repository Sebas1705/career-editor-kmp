package dev.sebas1705.careereditor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebas1705.careereditor.presentation.components.LoadingScreen
import dev.sebas1705.careereditor.presentation.screens.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerViewModel

private enum class Section(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PERSONAL("Personal", Icons.Default.Person),
    JOBS("Trabajos", Icons.Default.Work),
    PROJECTS("Proyectos", Icons.Default.Code),
    SKILLS("Skills", Icons.Default.Star),
    EDUCATION("Educación", Icons.Default.School),
    CERTIFICATIONS("Certs", Icons.Default.CardMembership),
    SOFT_SKILLS("Soft Skills", Icons.Default.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        val viewModel: CareerViewModel = viewModel { CareerViewModel() }
        val state by viewModel.uiState.collectAsState()
        var currentSection by remember { mutableStateOf(Section.PERSONAL) }

        if (state.isLoading && state.personal.name.isEmpty()) {
            LoadingScreen()
            return@MaterialTheme
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Career Editor") },
                    actions = {
                        IconButton(onClick = { viewModel.loadAll() }) {
                            Icon(Icons.Default.Refresh, "Recargar")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    Section.entries.forEach { section ->
                        NavigationBarItem(
                            selected = currentSection == section,
                            onClick = { currentSection = section; viewModel.clearSaveSuccess(); viewModel.clearError() },
                            icon = { Icon(section.icon, section.label) },
                            label = { Text(section.label) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (currentSection) {
                    Section.PERSONAL -> PersonalScreen(state, viewModel::savePersonal, viewModel::clearSaveSuccess, viewModel::clearError)
                    Section.JOBS -> JobsScreen(state, viewModel::saveJob, viewModel::clearSaveSuccess, viewModel::clearError)
                    Section.PROJECTS -> ProjectsScreen(state, viewModel::saveProject, viewModel::clearSaveSuccess, viewModel::clearError)
                    Section.SKILLS -> SkillsScreen(state, viewModel::saveSkill, viewModel::clearSaveSuccess, viewModel::clearError)
                    Section.EDUCATION -> EducationScreen(state, viewModel::saveEducation, viewModel::clearSaveSuccess, viewModel::clearError)
                    Section.CERTIFICATIONS -> CertificationsScreen(state, viewModel::saveCertification, viewModel::clearSaveSuccess, viewModel::clearError)
                    Section.SOFT_SKILLS -> SoftSkillsScreen(state, viewModel::saveSoftSkill, viewModel::clearSaveSuccess, viewModel::clearError)
                }
            }
        }
    }
}
