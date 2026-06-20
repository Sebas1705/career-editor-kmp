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
import dev.sebas1705.careereditor.data.model.Job
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun JobsScreen(state: CareerUiState, onSave: (Job) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var selectedJob by remember { mutableStateOf<Job?>(null) }

    if (selectedJob != null) {
        JobEditScreen(
            job = selectedJob!!,
            state = state,
            onSave = { onSave(it); selectedJob = null },
            onBack = { selectedJob = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Experiencia Laboral", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }
            LazyColumn {
                items(state.jobs) { job ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedJob = job }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(job.role.es, style = MaterialTheme.typography.titleMedium)
                            Text(job.company, style = MaterialTheme.typography.bodyMedium)
                            Text(job.period.es, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobEditScreen(job: Job, state: CareerUiState, onSave: (Job) -> Unit, onBack: () -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var roleEn by remember { mutableStateOf(job.role.en) }
    var roleEs by remember { mutableStateOf(job.role.es) }
    var company by remember { mutableStateOf(job.company) }
    var companyUrl by remember { mutableStateOf(job.companyUrl) }
    var periodEn by remember { mutableStateOf(job.period.en) }
    var periodEs by remember { mutableStateOf(job.period.es) }
    var typeEn by remember { mutableStateOf(job.type.en) }
    var typeEs by remember { mutableStateOf(job.type.es) }
    var descEn by remember { mutableStateOf(job.desc.en) }
    var descEs by remember { mutableStateOf(job.desc.es) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job.company) },
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

            SectionField("Empresa", company) { company = it }
            SectionField("URL Empresa", companyUrl) { companyUrl = it }
            SectionField("Rol (EN)", roleEn) { roleEn = it }
            SectionField("Rol (ES)", roleEs) { roleEs = it }
            SectionField("Periodo (EN)", periodEn) { periodEn = it }
            SectionField("Periodo (ES)", periodEs) { periodEs = it }
            SectionField("Tipo (EN)", typeEn) { typeEn = it }
            SectionField("Tipo (ES)", typeEs) { typeEs = it }
            SectionField("Descripción (EN)", descEn) { descEn = it }
            SectionField("Descripción (ES)", descEs) { descEs = it }

            SaveButton(
                onClick = {
                    onSave(job.copy(
                        role = LocalizedText(roleEn, roleEs),
                        company = company, companyUrl = companyUrl,
                        period = LocalizedText(periodEn, periodEs),
                        type = LocalizedText(typeEn, typeEs),
                        desc = LocalizedText(descEn, descEs)
                    ))
                },
                isLoading = state.isLoading
            )
        }
    }
}
