package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.data.model.Project
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun ProjectsScreen(state: CareerUiState, onSave: (Project) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var selectedProject by remember { mutableStateOf<Project?>(null) }

    if (selectedProject != null) {
        ProjectEditScreen(
            project = selectedProject!!,
            state = state,
            onSave = { onSave(it); selectedProject = null },
            onBack = { selectedProject = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Proyectos", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }
            LazyColumn {
                items(state.projects) { project ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedProject = project }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(project.name, style = MaterialTheme.typography.titleMedium)
                            Text(project.context, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text(project.desc.es, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditScreen(project: Project, state: CareerUiState, onSave: (Project) -> Unit, onBack: () -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var name by remember { mutableStateOf(project.name) }
    var context by remember { mutableStateOf(project.context) }
    var descEn by remember { mutableStateOf(project.desc.en) }
    var descEs by remember { mutableStateOf(project.desc.es) }
    var longDescEn by remember { mutableStateOf(project.long_desc.en) }
    var longDescEs by remember { mutableStateOf(project.long_desc.es) }
    var tags by remember { mutableStateOf(project.tags.joinToString(", ")) }
    var github by remember { mutableStateOf(project.github ?: "") }
    var demo by remember { mutableStateOf(project.demo ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            SectionField("Nombre", name) { name = it }
            SectionField("Contexto (work/academic/personal)", context) { context = it }
            SectionField("Descripción corta (EN)", descEn) { descEn = it }
            SectionField("Descripción corta (ES)", descEs) { descEs = it }
            SectionField("Descripción larga (EN)", longDescEn) { longDescEn = it }
            SectionField("Descripción larga (ES)", longDescEs) { longDescEs = it }
            SectionField("Tags (separados por coma)", tags) { tags = it }
            SectionField("GitHub URL", github) { github = it }
            SectionField("Demo URL", demo) { demo = it }

            SaveButton(
                onClick = {
                    onSave(project.copy(
                        name = name, context = context,
                        desc = LocalizedText(descEn, descEs),
                        long_desc = LocalizedText(longDescEn, longDescEs),
                        tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                        github = github.ifBlank { null },
                        demo = demo.ifBlank { null }
                    ))
                },
                isLoading = state.isLoading
            )
        }
    }
}
