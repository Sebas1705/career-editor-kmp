package dev.sebas1705.careereditor.presentation.screens

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
import dev.sebas1705.careereditor.data.model.Project
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

private val contextIcons = mapOf("work" to "💼", "academic" to "🎓", "personal" to "⭐")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectsScreen(
    state: CareerUiState,
    onSave: (Project) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
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
            SectionHeader("Proyectos", "${state.projects.size} proyectos")
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.projects) { project ->
                    Card(
                        onClick = { selectedProject = project },
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(contextIcons[project.context] ?: "📁", style = MaterialTheme.typography.titleMedium)
                                Column(Modifier.weight(1f)) {
                                    Text(project.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        project.desc.resolve("en"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                            if (project.tags.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                androidx.compose.foundation.layout.FlowRow {
                                    project.tags.take(5).forEach { tag -> TagChip(tag) }
                                    if (project.tags.size > 5) TagChip("+${project.tags.size - 5}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectEditScreen(
    project: Project,
    state: CareerUiState,
    onSave: (Project) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var name    by remember(project) { mutableStateOf(project.name) }
    var context by remember(project) { mutableStateOf(project.context) }
    var desc    by remember(project) { mutableStateOf(project.desc) }
    var tags    by remember(project) { mutableStateOf(project.tags.joinToString(", ")) }
    var github  by remember(project) { mutableStateOf(project.github ?: "") }
    var demo    by remember(project) { mutableStateOf(project.demo ?: "") }

    // Per-project editing uses all languages from the state
    var editingLang by remember { mutableStateOf(state.languages.default.ifBlank { "en" }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.name, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LanguageTabs(state.languages.supported, editingLang, onSelect = { editingLang = it })

            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                FieldGroup("General (no localizable)") {
                    SectionField("Nombre del proyecto", name, { name = it }, required = true, singleLine = true)
                    SectionField(
                        "Contexto", context, { context = it },
                        singleLine = true, supportingText = "work / academic / personal"
                    )
                    SectionField("Tags (separados por coma)", tags, { tags = it }, supportingText = "Ej: Kotlin, Firebase")
                    SectionField("GitHub URL", github, { github = it }, singleLine = true)
                    SectionField("Demo URL", demo, { demo = it }, singleLine = true)
                }

                FieldGroup("Descripción [$editingLang]") {
                    SectionField(
                        label = "Descripción",
                        value = desc[editingLang] ?: "",
                        onValueChange = { desc = desc + (editingLang to it) }
                    )
                }

                SaveButton(
                    onClick = {
                        onSave(project.copy(
                            name = name, context = context, desc = desc,
                            tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            github = github.ifBlank { null },
                            demo = demo.ifBlank { null }
                        ))
                    },
                    isLoading = state.isLoading,
                    enabled = name.isNotBlank()
                )
            }
        }
    }
}
