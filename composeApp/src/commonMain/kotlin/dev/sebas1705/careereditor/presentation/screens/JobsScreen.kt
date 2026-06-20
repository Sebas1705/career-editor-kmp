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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Job
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun JobsScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Job) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Job?>(null) }

    if (selected != null) {
        JobEditScreen(
            job = selected!!,
            state = state,
            selectedLangCode = selectedLangCode,
            onSelectLang = onSelectLang,
            onSave = { onSave(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize()) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobEditScreen(
    job: Job,
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Job) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var company    by remember(job) { mutableStateOf(job.company) }
    var companyUrl by remember(job) { mutableStateOf(job.companyUrl) }
    var startDate  by remember(job) { mutableStateOf(job.startDate) }

    var role   by remember(job) { mutableStateOf(job.role) }
    var type   by remember(job) { mutableStateOf(job.type) }
    var period by remember(job) { mutableStateOf(job.period) }
    var desc   by remember(job) { mutableStateOf(job.desc) }

    val lang = selectedLangCode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(company.ifBlank { job.id }, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LanguageTabs(state.languages.supported, lang, onSelectLang)

            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                FieldGroup("Empresa (no localizable)") {
                    SectionField("Empresa", company, { company = it }, required = true, singleLine = true)
                    SectionField("URL empresa", companyUrl, { companyUrl = it }, singleLine = true)
                    SectionField("Fecha inicio (YYYY-MM)", startDate, { startDate = it }, singleLine = true)
                }

                FieldGroup("Rol, periodo y tipo [$lang]") {
                    SectionField(
                        label = "Rol",
                        value = role[lang] ?: "",
                        onValueChange = { role = role + (lang to it) },
                        required = true, singleLine = true
                    )
                    SectionField(
                        label = "Periodo",
                        value = period[lang] ?: "",
                        onValueChange = { period = period + (lang to it) },
                        singleLine = true
                    )
                    SectionField(
                        label = "Modalidad",
                        value = type[lang] ?: "",
                        onValueChange = { type = type + (lang to it) },
                        singleLine = true,
                        supportingText = "Ej: Hybrid, Remote, On-site"
                    )
                }

                FieldGroup("Descripción [$lang]") {
                    SectionField(
                        label = "Descripción",
                        value = desc[lang] ?: "",
                        onValueChange = { desc = desc + (lang to it) }
                    )
                }

                SaveButton(
                    onClick = {
                        onSave(job.copy(
                            company = company, companyUrl = companyUrl, startDate = startDate,
                            role = role, type = type, period = period, desc = desc
                        ))
                    },
                    isLoading = state.isLoading,
                    enabled = company.isNotBlank()
                )
            }
        }
    }
}
