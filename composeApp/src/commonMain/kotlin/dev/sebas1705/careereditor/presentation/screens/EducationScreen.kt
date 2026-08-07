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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Education
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState
import dev.sebas1705.careereditor.utils.toSlug

@Composable
fun EducationScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Education) -> Unit,
    onCreate: (Education) -> Unit,
    onDelete: (String) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Education?>(null) }
    var creatingNew by remember { mutableStateOf(false) }

    when {
        selected != null -> EducationEditScreen(
            education = selected!!,
            isNew = false,
            existingEducation = state.education,
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
        creatingNew -> EducationEditScreen(
            education = Education(),
            isNew = true,
            existingEducation = state.education,
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
                    Icon(Icons.Default.Add, "Nueva educación")
                }
            }
        ) { scaffoldPadding ->
            Column(Modifier.fillMaxSize().padding(scaffoldPadding)) {
                LanguageTabs(state.languages.supported, selectedLangCode, onSelectLang)
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    SectionHeader("Formación Académica", "${state.education.size} entradas")
                    if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                    state.error?.let { ErrorBanner(it, onClearError) }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 88.dp) // el FAB no tapa la última tarjeta
                    ) {
                        items(state.education) { edu ->
                            Card(
                                onClick = { selected = edu },
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(16.dp)) {
                                    Text(edu.icon, style = MaterialTheme.typography.headlineMedium)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(edu.degree.resolve(selectedLangCode), style = MaterialTheme.typography.titleMedium)
                                        Text(edu.school, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                        Text(edu.period.resolve(selectedLangCode), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun EducationEditScreen(
    education: Education,
    isNew: Boolean,
    existingEducation: List<Education>,
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Education) -> Unit,
    onCreate: (Education) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var template by remember { mutableStateOf<Education?>(null) }
    val base = if (isNew) template else education

    var id by remember { mutableStateOf("") }
    var idEditedByUser by remember { mutableStateOf(false) }

    var icon   by remember(base) { mutableStateOf(base?.icon ?: "") }
    var school by remember(base) { mutableStateOf(base?.school ?: "") }
    var degree by remember(base) { mutableStateOf(base?.degree ?: emptyMap()) }
    var period by remember(base) { mutableStateOf(base?.period ?: emptyMap()) }
    var detail by remember(base) { mutableStateOf(base?.detail ?: emptyMap()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }
    val lang = selectedLangCode

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemName = school.ifBlank { education.id },
            onConfirm = { showDeleteDialog = false; onDelete(education.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    fun buildEducation(): Education {
        val eid = if (isNew) id else education.id
        return Education(id = eid, icon = icon, school = school, degree = degree, period = period, detail = detail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Nueva educación" else school.ifBlank { education.id }, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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

                if (isNew && existingEducation.isNotEmpty()) {
                    OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Plantilla (opcional)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                            Spacer(Modifier.height(4.dp))
                            ExposedDropdownMenuBox(
                                expanded = showTemplatePicker,
                                onExpandedChange = { showTemplatePicker = it }
                            ) {
                                OutlinedTextField(
                                    value = template?.school ?: "Sin plantilla",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Copiar datos de...") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTemplatePicker) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(expanded = showTemplatePicker, onDismissRequest = { showTemplatePicker = false }) {
                                    DropdownMenuItem(text = { Text("Sin plantilla") }, onClick = { template = null; showTemplatePicker = false })
                                    existingEducation.forEach { e ->
                                        DropdownMenuItem(
                                            text = { Text(e.school) },
                                            onClick = { template = e; showTemplatePicker = false; idEditedByUser = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isNew) IdField(value = id, onValueChange = { id = it; idEditedByUser = true })

                FieldGroup("Centro (no localizable)") {
                    SectionField("Icono (emoji)", icon, { icon = it }, singleLine = true)
                    SectionField(
                        label = "Centro educativo", value = school, required = true, singleLine = true,
                        onValueChange = {
                            school = it
                            if (isNew && !idEditedByUser) id = it.toSlug()
                        }
                    )
                }

                FieldGroup("Titulación, periodo y detalle [$lang]") {
                    SectionField(
                        label = "Titulación", value = degree[lang] ?: "", required = true, singleLine = true,
                        onValueChange = { degree = degree + (lang to it) }
                    )
                    SectionField(
                        label = "Periodo", value = period[lang] ?: "", singleLine = true,
                        onValueChange = { period = period + (lang to it) }
                    )
                    SectionField(
                        label = "Detalle", value = detail[lang] ?: "",
                        onValueChange = { detail = detail + (lang to it) }
                    )
                }

                val saveEnabled = school.isNotBlank() && (!isNew || id.isNotBlank())
                if (isNew) {
                    Button(
                        onClick = { onCreate(buildEducation()) },
                        enabled = !state.isLoading && saveEnabled,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(8.dp)); Text("Creando...")
                        } else Text("Crear entrada")
                    }
                } else {
                    SaveButton(onClick = { onSave(buildEducation()) }, isLoading = state.isLoading, enabled = saveEnabled)
                }
            }
        }
    }
}
