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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.SoftSkill
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun SoftSkillsScreen(state: CareerUiState, onSave: (SoftSkill) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var selectedSkill by remember { mutableStateOf<SoftSkill?>(null) }

    if (selectedSkill != null) {
        SoftSkillEditScreen(
            skill = selectedSkill!!,
            state = state,
            onSave = { onSave(it); selectedSkill = null },
            onBack = { selectedSkill = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Habilidades Blandas", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }
            LazyColumn {
                items(state.softSkills) { skill ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedSkill = skill }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(skill.icon, style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(skill.name_es, style = MaterialTheme.typography.titleMedium)
                                Text(skill.name_en, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
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
fun SoftSkillEditScreen(skill: SoftSkill, state: CareerUiState, onSave: (SoftSkill) -> Unit, onBack: () -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var icon by remember { mutableStateOf(skill.icon) }
    var nameEn by remember { mutableStateOf(skill.name_en) }
    var nameEs by remember { mutableStateOf(skill.name_es) }

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

            SectionField("Icono (emoji)", icon) { icon = it }
            SectionField("Nombre (EN)", nameEn) { nameEn = it }
            SectionField("Nombre (ES)", nameEs) { nameEs = it }

            SaveButton(
                onClick = { onSave(skill.copy(icon = icon, name_en = nameEn, name_es = nameEs)) },
                isLoading = state.isLoading
            )
        }
    }
}
