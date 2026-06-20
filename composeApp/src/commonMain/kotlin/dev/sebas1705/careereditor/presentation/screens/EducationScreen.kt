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
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun EducationScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Education) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Education?>(null) }

    if (selected != null) {
        EducationEditScreen(
            education = selected!!,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationEditScreen(
    education: Education,
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Education) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var icon   by remember(education) { mutableStateOf(education.icon) }
    var school by remember(education) { mutableStateOf(education.school) }
    var degree by remember(education) { mutableStateOf(education.degree) }
    var period by remember(education) { mutableStateOf(education.period) }
    var detail by remember(education) { mutableStateOf(education.detail) }

    val lang = selectedLangCode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(school.ifBlank { education.id }, maxLines = 1) },
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

                FieldGroup("Centro (no localizable)") {
                    SectionField("Icono (emoji)", icon, { icon = it }, singleLine = true)
                    SectionField("Centro educativo", school, { school = it }, required = true, singleLine = true)
                }

                FieldGroup("Titulación, periodo y detalle [$lang]") {
                    SectionField(
                        label = "Titulación",
                        value = degree[lang] ?: "",
                        onValueChange = { degree = degree + (lang to it) },
                        required = true, singleLine = true
                    )
                    SectionField(
                        label = "Periodo",
                        value = period[lang] ?: "",
                        onValueChange = { period = period + (lang to it) },
                        singleLine = true
                    )
                    SectionField(
                        label = "Detalle",
                        value = detail[lang] ?: "",
                        onValueChange = { detail = detail + (lang to it) }
                    )
                }

                SaveButton(
                    onClick = {
                        onSave(education.copy(icon = icon, school = school, degree = degree, period = period, detail = detail))
                    },
                    isLoading = state.isLoading,
                    enabled = school.isNotBlank()
                )
            }
        }
    }
}
