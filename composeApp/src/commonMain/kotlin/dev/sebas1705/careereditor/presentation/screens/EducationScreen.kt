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
import dev.sebas1705.careereditor.data.model.Education
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun EducationScreen(state: CareerUiState, onSave: (Education) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var selectedEdu by remember { mutableStateOf<Education?>(null) }

    if (selectedEdu != null) {
        EducationEditScreen(
            education = selectedEdu!!,
            state = state,
            onSave = { onSave(it); selectedEdu = null },
            onBack = { selectedEdu = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Formación Académica", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }
            LazyColumn {
                items(state.education) { edu ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedEdu = edu }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("${edu.icon} ${edu.degree.es}", style = MaterialTheme.typography.titleMedium)
                            Text(edu.school, style = MaterialTheme.typography.bodyMedium)
                            Text(edu.period.es, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationEditScreen(education: Education, state: CareerUiState, onSave: (Education) -> Unit, onBack: () -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var degreeEn by remember { mutableStateOf(education.degree.en) }
    var degreeEs by remember { mutableStateOf(education.degree.es) }
    var school by remember { mutableStateOf(education.school) }
    var periodEn by remember { mutableStateOf(education.period.en) }
    var periodEs by remember { mutableStateOf(education.period.es) }
    var detailEn by remember { mutableStateOf(education.detail.en) }
    var detailEs by remember { mutableStateOf(education.detail.es) }
    var icon by remember { mutableStateOf(education.icon) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(education.school) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            SectionField("Icono (emoji)", icon) { icon = it }
            SectionField("Titulación (EN)", degreeEn) { degreeEn = it }
            SectionField("Titulación (ES)", degreeEs) { degreeEs = it }
            SectionField("Centro educativo", school) { school = it }
            SectionField("Periodo (EN)", periodEn) { periodEn = it }
            SectionField("Periodo (ES)", periodEs) { periodEs = it }
            SectionField("Detalle (EN)", detailEn) { detailEn = it }
            SectionField("Detalle (ES)", detailEs) { detailEs = it }

            SaveButton(
                onClick = {
                    onSave(education.copy(
                        degree = LocalizedText(degreeEn, degreeEs),
                        school = school,
                        period = LocalizedText(periodEn, periodEs),
                        detail = LocalizedText(detailEn, detailEs),
                        icon = icon
                    ))
                },
                isLoading = state.isLoading
            )
        }
    }
}
