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
import dev.sebas1705.careereditor.data.model.Education
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun EducationScreen(
    state: CareerUiState,
    onSave: (Education) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Education?>(null) }

    if (selected != null) {
        EducationEditScreen(
            education = selected!!,
            state = state,
            onSave = { onSave(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            SectionHeader("Formación Académica", "${state.education.size} entradas")
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                Text(edu.degree.es, style = MaterialTheme.typography.titleMedium)
                                Text(edu.school, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                Text(edu.period.es, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    state: CareerUiState,
    onSave: (Education) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var iconVal by remember { mutableStateOf(education.icon) }
    var degreeEnVal by remember { mutableStateOf(education.degree.en) }
    var degreeEsVal by remember { mutableStateOf(education.degree.es) }
    var schoolVal by remember { mutableStateOf(education.school) }
    var periodEnVal by remember { mutableStateOf(education.period.en) }
    var periodEsVal by remember { mutableStateOf(education.period.es) }
    var detailEnVal by remember { mutableStateOf(education.detail.en) }
    var detailEsVal by remember { mutableStateOf(education.detail.es) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(education.school, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            FieldGroup("Titulación") {
                SectionField(label = "Icono (emoji)", value = iconVal, onValueChange = { iconVal = it }, singleLine = true)
                SectionField(label = "Titulación (EN)", value = degreeEnVal, onValueChange = { degreeEnVal = it }, required = true, singleLine = true)
                SectionField(label = "Titulación (ES)", value = degreeEsVal, onValueChange = { degreeEsVal = it }, required = true, singleLine = true)
                SectionField(label = "Centro educativo", value = schoolVal, onValueChange = { schoolVal = it }, required = true, singleLine = true)
            }
            Spacer(Modifier.height(8.dp))
            FieldGroup("Periodo y detalle") {
                SectionField(label = "Periodo (EN)", value = periodEnVal, onValueChange = { periodEnVal = it }, singleLine = true)
                SectionField(label = "Periodo (ES)", value = periodEsVal, onValueChange = { periodEsVal = it }, singleLine = true)
                SectionField(label = "Detalle (EN)", value = detailEnVal, onValueChange = { detailEnVal = it })
                SectionField(label = "Detalle (ES)", value = detailEsVal, onValueChange = { detailEsVal = it })
            }

            SaveButton(
                onClick = {
                    onSave(education.copy(
                        icon = iconVal,
                        degree = LocalizedText(degreeEnVal, degreeEsVal),
                        school = schoolVal,
                        period = LocalizedText(periodEnVal, periodEsVal),
                        detail = LocalizedText(detailEnVal, detailEsVal)
                    ))
                },
                isLoading = state.isLoading,
                enabled = degreeEsVal.isNotBlank() && schoolVal.isNotBlank()
            )
        }
    }
}
