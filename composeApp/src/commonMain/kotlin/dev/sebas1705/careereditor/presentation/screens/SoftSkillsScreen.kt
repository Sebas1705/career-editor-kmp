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
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun SoftSkillsScreen(
    state: CareerUiState,
    onSave: (SoftSkill) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<SoftSkill?>(null) }

    if (selected != null) {
        SoftSkillEditScreen(
            skill = selected!!,
            state = state,
            onSave = { onSave(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
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
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(skill.icon, style = MaterialTheme.typography.headlineSmall)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(skill.name_es, style = MaterialTheme.typography.titleMedium)
                                Text(skill.name_en, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    onSave: (SoftSkill) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var iconVal by remember { mutableStateOf(skill.icon) }
    var nameEnVal by remember { mutableStateOf(skill.name_en) }
    var nameEsVal by remember { mutableStateOf(skill.name_es) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(skill.name_es) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            FieldGroup("Editar soft skill") {
                SectionField(label = "Icono (emoji)", value = iconVal, onValueChange = { iconVal = it }, singleLine = true)
                SectionField(label = "Nombre (EN)", value = nameEnVal, onValueChange = { nameEnVal = it }, required = true, singleLine = true)
                SectionField(label = "Nombre (ES)", value = nameEsVal, onValueChange = { nameEsVal = it }, required = true, singleLine = true)
            }

            SaveButton(
                onClick = { onSave(skill.copy(icon = iconVal, name_en = nameEnVal, name_es = nameEsVal)) },
                isLoading = state.isLoading,
                enabled = nameEnVal.isNotBlank() && nameEsVal.isNotBlank()
            )
        }
    }
}
