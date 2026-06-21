package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Project
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState
import dev.sebas1705.careereditor.utils.toSlug

private val contextIcons = mapOf("work" to "💼", "academic" to "🎓", "personal" to "⭐")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectsScreen(
    state: CareerUiState,
    onSave: (Project) -> Unit,
    onCreate: (Project) -> Unit,
    onDelete: (String) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Project?>(null) }
    var creatingNew by remember { mutableStateOf(false) }

    when {
        selected != null -> ProjectEditScreen(
            project = selected!!,
            isNew = false,
            existingProjects = state.projects,
            state = state,
            onSave = { onSave(it); selected = null },
            onCreate = {},
            onDelete = { onDelete(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        creatingNew -> ProjectEditScreen(
            project = Project(),
            isNew = true,
            existingProjects = state.projects,
            state = state,
            onSave = {},
            onCreate = { onCreate(it); creatingNew = false },
            onDelete = {},
            onBack = { creatingNew = false },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        else -> Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { creatingNew = true }) {
                    Icon(Icons.Default.Add, "Nuevo proyecto")
                }
            }
        ) { scaffoldPadding ->
            Column(Modifier.fillMaxSize().padding(scaffoldPadding).padding(16.dp)) {
                SectionHeader("Proyectos", "${state.projects.size} proyectos")
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.projects) { project ->
                        Card(
                            onClick = { selected = project },
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
                                    FlowRow {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditScreen(
    project: Project,
    isNew: Boolean,
    existingProjects: List<Project>,
    state: CareerUiState,
    onSave: (Project) -> Unit,
    onCreate: (Project) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var template by remember { mutableStateOf<Project?>(null) }
    val base = if (isNew) template else project

    var id by remember { mutableStateOf("") }
    var idEditedByUser by remember { mutableStateOf(false) }

    var name    by remember(base) { mutableStateOf(base?.name ?: "") }
    var context by remember(base) { mutableStateOf(base?.context ?: "") }
    var desc    by remember(base) { mutableStateOf(base?.desc ?: emptyMap()) }
    var tags    by remember(base) { mutableStateOf(base?.tags ?: emptyList()) }
    var github  by remember(base) { mutableStateOf(base?.github ?: "") }
    var demo    by remember(base) { mutableStateOf(base?.demo ?: "") }

    var editingLang by remember { mutableStateOf(state.languages.default.ifBlank { "en" }) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemName = project.name,
            onConfirm = { showDeleteDialog = false; onDelete(project.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    fun buildProject(): Project {
        val pid = if (isNew) id else project.id
        return Project(
            id = pid, name = name, context = context, desc = desc, tags = tags,
            github = github.ifBlank { null }, demo = demo.ifBlank { null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Nuevo proyecto" else project.name, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (!isNew) DeleteButton(onClick = { showDeleteDialog = true }, enabled = !state.isLoading)
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LanguageTabs(state.languages.supported, editingLang, onSelect = { editingLang = it })

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                if (isNew && existingProjects.isNotEmpty()) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Plantilla (opcional)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(4.dp))
                            ExposedDropdownMenuBox(
                                expanded = showTemplatePicker,
                                onExpandedChange = { showTemplatePicker = it }
                            ) {
                                OutlinedTextField(
                                    value = template?.name ?: "Sin plantilla",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Copiar datos de...") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTemplatePicker) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(expanded = showTemplatePicker, onDismissRequest = { showTemplatePicker = false }) {
                                    DropdownMenuItem(text = { Text("Sin plantilla") }, onClick = { template = null; showTemplatePicker = false })
                                    existingProjects.forEach { p ->
                                        DropdownMenuItem(
                                            text = { Text(p.name) },
                                            onClick = { template = p; showTemplatePicker = false; idEditedByUser = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isNew) IdField(value = id, onValueChange = { id = it; idEditedByUser = true })

                FieldGroup("General (no localizable)") {
                    SectionField(
                        label = "Nombre del proyecto", value = name, required = true, singleLine = true,
                        onValueChange = {
                            name = it
                            if (isNew && !idEditedByUser) id = it.toSlug()
                        }
                    )
                    SectionField(
                        label = "Contexto", value = context, singleLine = true, supportingText = "work / academic / personal",
                        onValueChange = { context = it }
                    )
                    SectionField("GitHub URL", github, { github = it }, singleLine = true)
                    SectionField("Demo URL", demo, { demo = it }, singleLine = true)
                }

                DynamicListField(
                    label = "Tags",
                    items = tags,
                    onItemsChange = { tags = it },
                    placeholder = "Ej: Kotlin, Firebase..."
                )

                FieldGroup("Descripción [$editingLang]") {
                    SectionField(
                        label = "Descripción",
                        value = desc[editingLang] ?: "",
                        onValueChange = { desc = desc + (editingLang to it) }
                    )
                }

                val saveEnabled = name.isNotBlank() && (!isNew || id.isNotBlank())
                if (isNew) {
                    Button(
                        onClick = { onCreate(buildProject()) },
                        enabled = !state.isLoading && saveEnabled,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(8.dp)); Text("Creando...")
                        } else Text("Crear proyecto")
                    }
                } else {
                    SaveButton(onClick = { onSave(buildProject()) }, isLoading = state.isLoading, enabled = saveEnabled)
                }
            }
        }
    }
}
