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
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun JobsScreen(
    state: CareerUiState,
    onSave: (Job) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Job?>(null) }

    if (selected != null) {
        JobEditScreen(
            job = selected!!,
            state = state,
            onSave = { onSave(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
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
                                    Text(job.role.es, style = MaterialTheme.typography.titleMedium)
                                    Text(job.company, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(job.type.es, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(job.period.es, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    onSave: (Job) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var roleEnVal by remember { mutableStateOf(job.role.en) }
    var roleEsVal by remember { mutableStateOf(job.role.es) }
    var companyVal by remember { mutableStateOf(job.company) }
    var companyUrlVal by remember { mutableStateOf(job.companyUrl) }
    var periodEnVal by remember { mutableStateOf(job.period.en) }
    var periodEsVal by remember { mutableStateOf(job.period.es) }
    var typeEnVal by remember { mutableStateOf(job.type.en) }
    var typeEsVal by remember { mutableStateOf(job.type.es) }
    var descEnVal by remember { mutableStateOf(job.desc.en) }
    var descEsVal by remember { mutableStateOf(job.desc.es) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(job.company, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            FieldGroup("Empresa") {
                SectionField(label = "Empresa", value = companyVal, onValueChange = { companyVal = it }, required = true, singleLine = true)
                SectionField(label = "URL empresa", value = companyUrlVal, onValueChange = { companyUrlVal = it }, singleLine = true)
            }
            Spacer(Modifier.height(8.dp))
            FieldGroup("Rol y periodo") {
                SectionField(label = "Rol (EN)", value = roleEnVal, onValueChange = { roleEnVal = it }, required = true, singleLine = true)
                SectionField(label = "Rol (ES)", value = roleEsVal, onValueChange = { roleEsVal = it }, required = true, singleLine = true)
                SectionField(label = "Periodo (EN)", value = periodEnVal, onValueChange = { periodEnVal = it }, singleLine = true)
                SectionField(label = "Periodo (ES)", value = periodEsVal, onValueChange = { periodEsVal = it }, singleLine = true)
                SectionField(label = "Modalidad (EN)", value = typeEnVal, onValueChange = { typeEnVal = it }, singleLine = true, supportingText = "Ej: Hybrid, Remote, On-site")
                SectionField(label = "Modalidad (ES)", value = typeEsVal, onValueChange = { typeEsVal = it }, singleLine = true)
            }
            Spacer(Modifier.height(8.dp))
            FieldGroup("Descripción") {
                SectionField(label = "Descripción (EN)", value = descEnVal, onValueChange = { descEnVal = it })
                SectionField(label = "Descripción (ES)", value = descEsVal, onValueChange = { descEsVal = it })
            }

            SaveButton(
                onClick = {
                    onSave(job.copy(
                        role = LocalizedText(roleEnVal, roleEsVal),
                        company = companyVal, companyUrl = companyUrlVal,
                        period = LocalizedText(periodEnVal, periodEsVal),
                        type = LocalizedText(typeEnVal, typeEsVal),
                        desc = LocalizedText(descEnVal, descEsVal)
                    ))
                },
                isLoading = state.isLoading,
                enabled = companyVal.isNotBlank() && roleEsVal.isNotBlank()
            )
        }
    }
}
