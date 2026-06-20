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
import dev.sebas1705.careereditor.data.model.Skill
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun SkillsScreen(state: CareerUiState, onSave: (Skill) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }

    if (selectedSkill != null) {
        SkillEditScreen(
            skill = selectedSkill!!,
            state = state,
            onSave = { onSave(it); selectedSkill = null },
            onBack = { selectedSkill = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Habilidades Técnicas", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            val grouped = state.skills.groupBy { it.category }
            LazyColumn {
                grouped.forEach { (category, skills) ->
                    item {
                        Text(category, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                    }
                    items(skills) { skill ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp).clickable { selectedSkill = skill }
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(skill.name, modifier = Modifier.weight(1f))
                                repeat(4) { i ->
                                    val filled = i < skill.level
                                    Text(if (filled) "●" else "○", color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
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
fun SkillEditScreen(skill: Skill, state: CareerUiState, onSave: (Skill) -> Unit, onBack: () -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var name by remember { mutableStateOf(skill.name) }
    var category by remember { mutableStateOf(skill.category) }
    var level by remember { mutableStateOf(skill.level) }
    var iconUrl by remember { mutableStateOf(skill.icon_url ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(skill.name) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            SectionField("Nombre", name) { name = it }
            SectionField("Categoría", category) { category = it }
            SectionField("URL del icono", iconUrl) { iconUrl = it }

            Text("Nivel: $level / 4", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
            Slider(
                value = level.toFloat(),
                onValueChange = { level = it.toInt() },
                valueRange = 1f..4f,
                steps = 2
            )

            SaveButton(
                onClick = {
                    onSave(skill.copy(
                        name = name, category = category, level = level,
                        icon_url = iconUrl.ifBlank { null }
                    ))
                },
                isLoading = state.isLoading
            )
        }
    }
}
