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
import dev.sebas1705.careereditor.data.model.SoftSkill
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun SoftSkillsScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (SoftSkill) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<SoftSkill?>(null) }

    if (selected != null) {
        SoftSkillEditScreen(
            skill = selected!!,
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
                SectionHeader("Habilidades Blandas", "${state.softSkills.size} habilidades")
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.softSkills) { skill ->
                        Card(
                            onClick = { selected = skill },
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(skill.name.resolve(selectedLangCode), style = MaterialTheme.typography.titleMedium)
                                    Text(skill.id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun SoftSkillEditScreen(
    skill: SoftSkill,
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (SoftSkill) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember(skill) { mutableStateOf(skill.name) }
    val lang = selectedLangCode

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(skill.name.resolve(lang).ifBlank { skill.id }) },
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

                FieldGroup("Nombre [$lang]") {
                    SectionField(
                        label = "Nombre",
                        value = name[lang] ?: "",
                        onValueChange = { name = name + (lang to it) },
                        required = true,
                        singleLine = true
                    )
                }

                SaveButton(
                    onClick = { onSave(skill.copy(name = name)) },
                    isLoading = state.isLoading,
                    enabled = (name[lang] ?: "").isNotBlank()
                )
            }
        }
    }
}
