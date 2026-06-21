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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Job
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.data.model.resolveList
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState
import dev.sebas1705.careereditor.utils.toSlug

@Composable
fun JobsScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Job) -> Unit,
    onCreate: (Job) -> Unit,
    onDelete: (String) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Job?>(null) }
    var creatingNew by remember { mutableStateOf(false) }

    when {
        selected != null -> JobEditScreen(
            job = selected!!,
            isNew = false,
            existingJobs = state.jobs,
            state = state,
            selectedLangCode = selectedLangCode,
            onSelectLang = onSelectLang,
            onSave = { onSave(it); selected = null },
            onCreate = {},
            onDelete = { onDelete(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        creatingNew -> JobEditScreen(
            job = Job(),
            isNew = true,
            existingJobs = state.jobs,
            state = state,
            selectedLangCode = selectedLangCode,
            onSelectLang = onSelectLang,
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
                    Icon(Icons.Default.Add, "Nuevo trabajo")
                }
            }
        ) { scaffoldPadding ->
            Column(Modifier.fillMaxSize().padding(scaffoldPadding)) {
                LanguageTabs(state.languages.supported, selectedLangCode, onSelectLang)
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    SectionHeader("Experiencia Laboral", "${state.jobs.size} posiciones")
                    if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                    state.error?.let { ErrorBanner(it, onClearError) }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.jobs) { job ->
                            Card(
                                onClick = { selected = job },
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(job.role.resolve(selectedLangCode), style = MaterialTheme.typography.titleMedium)
                                            Text(job.company, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                        }
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(job.type.resolve(selectedLangCode), style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(job.period.resolve(selectedLangCode), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun JobEditScreen(
    job: Job,
    isNew: Boolean,
    existingJobs: List<Job>,
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Job) -> Unit,
    onCreate: (Job) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    // Template for "copy from existing" (only used when isNew = true)
    var template by remember { mutableStateOf<Job?>(null) }
    val base = if (isNew) template else job

    // Auto-generated id from company (only for new items)
    var id by remember { mutableStateOf("") }
    var idEditedByUser by remember { mutableStateOf(false) }

    var company    by remember(base) { mutableStateOf(base?.company ?: "") }
    var companyUrl by remember(base) { mutableStateOf(base?.companyUrl ?: "") }
    var startDate  by remember(base) { mutableStateOf(base?.startDate ?: "") }
    var endDate    by remember(base) { mutableStateOf(base?.endDate ?: "") }
    var role       by remember(base) { mutableStateOf(base?.role ?: emptyMap()) }
    var type       by remember(base) { mutableStateOf(base?.type ?: emptyMap()) }
    var period     by remember(base) { mutableStateOf(base?.period ?: emptyMap()) }
    var desc       by remember(base) { mutableStateOf(base?.desc ?: emptyMap()) }
    var projects   by remember(base) { mutableStateOf(base?.projects ?: emptyList()) }
    var achievements by remember(base) { mutableStateOf(base?.achievements ?: emptyMap()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }

    val lang = selectedLangCode

    fun buildJob(): Job {
        val jobId = if (isNew) id else job.id
        return Job(
            id = jobId, company = company, companyUrl = companyUrl,
            startDate = startDate, endDate = endDate.ifBlank { null },
            role = role, type = type, period = period, desc = desc,
            projects = projects, achievements = achievements
        )
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemName = company.ifBlank { job.id },
            onConfirm = { showDeleteDialog = false; onDelete(job.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Nuevo trabajo" else company.ifBlank { job.id }, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (!isNew) DeleteButton(onClick = { showDeleteDialog = true }, enabled = !state.isLoading)
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LanguageTabs(state.languages.supported, lang, onSelectLang)

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                // ── Copy-from picker (only when creating new) ─────────────────
                if (isNew && existingJobs.isNotEmpty()) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Plantilla (opcional)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(4.dp))
                            ExposedDropdownMenuBox(
                                expanded = showTemplatePicker,
                                onExpandedChange = { showTemplatePicker = it }
                            ) {
                                OutlinedTextField(
                                    value = template?.company ?: "Sin plantilla",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Copiar datos de...") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTemplatePicker) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(expanded = showTemplatePicker, onDismissRequest = { showTemplatePicker = false }) {
                                    DropdownMenuItem(text = { Text("Sin plantilla") }, onClick = { template = null; showTemplatePicker = false })
                                    existingJobs.forEach { j ->
                                        DropdownMenuItem(
                                            text = { Text("${j.company} — ${j.role.resolve("en")}") },
                                            onClick = { template = j; showTemplatePicker = false; idEditedByUser = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── ID (only for new items) ───────────────────────────────────
                if (isNew) {
                    IdField(value = id, onValueChange = { id = it; idEditedByUser = true })
                }

                FieldGroup("Empresa (no localizable)") {
                    SectionField(
                        label = "Empresa", value = company, required = true, singleLine = true,
                        onValueChange = {
                            company = it
                            if (isNew && !idEditedByUser) id = it.toSlug()
                        }
                    )
                    SectionField("URL empresa", companyUrl, { companyUrl = it }, singleLine = true)
                    SectionField("Fecha inicio (YYYY-MM)", startDate, { startDate = it }, singleLine = true)
                    SectionField("Fecha fin (vacío = actual)", endDate, { endDate = it }, singleLine = true, supportingText = "Dejar vacío si es el trabajo actual")
                }

                FieldGroup("Rol, periodo y tipo [$lang]") {
                    SectionField(
                        label = "Rol", value = role[lang] ?: "", required = true, singleLine = true,
                        onValueChange = { role = role + (lang to it) }
                    )
                    SectionField(
                        label = "Periodo", value = period[lang] ?: "", singleLine = true,
                        onValueChange = { period = period + (lang to it) }
                    )
                    SectionField(
                        label = "Modalidad", value = type[lang] ?: "", singleLine = true,
                        supportingText = "Ej: Hybrid, Remote, On-site",
                        onValueChange = { type = type + (lang to it) }
                    )
                }

                FieldGroup("Descripción [$lang]") {
                    SectionField(
                        label = "Descripción", value = desc[lang] ?: "",
                        onValueChange = { desc = desc + (lang to it) }
                    )
                }

                DynamicListField(
                    label = "Proyectos vinculados",
                    items = projects,
                    onItemsChange = { projects = it },
                    placeholder = "ID del proyecto"
                )

                DynamicListField(
                    label = "Logros [$lang]",
                    items = achievements.resolveList(lang),
                    onItemsChange = { achievements = achievements + (lang to it) },
                    placeholder = "Añadir logro..."
                )

                val saveEnabled = company.isNotBlank() && (!isNew || id.isNotBlank())
                if (isNew) {
                    Button(
                        onClick = { onCreate(buildJob()) },
                        enabled = !state.isLoading && saveEnabled,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Creando...")
                        } else { Text("Crear trabajo") }
                    }
                } else {
                    SaveButton(onClick = { onSave(buildJob()) }, isLoading = state.isLoading, enabled = saveEnabled)
                }
            }
        }
    }
}
