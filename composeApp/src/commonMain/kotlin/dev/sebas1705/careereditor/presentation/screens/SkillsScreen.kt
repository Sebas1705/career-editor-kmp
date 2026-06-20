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
import dev.sebas1705.careereditor.data.model.Skill
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun SkillsScreen(
    state: CareerUiState,
    onSave: (Skill) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Skill?>(null) }

    if (selected != null) {
        SkillEditScreen(
            skill = selected!!,
            state = state,
            onSave = { onSave(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            SectionHeader("Habilidades Técnicas", "${state.skills.size} habilidades")
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            val grouped = state.skills.groupBy { it.category }
            LazyColumn {
                grouped.forEach { (category, skills) ->
                    item {
                        Text(
                            category,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                        )
                    }
                    items(skills) { skill ->
                        Card(
                            onClick = { selected = skill },
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(skill.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                LevelDots(level = skill.level)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelDots(level: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(4) { i ->
            Surface(
                shape = MaterialTheme.shapes.extraSmall,
                color = if (i < level) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(width = 10.dp, height = 10.dp)
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillEditScreen(
    skill: Skill,
    state: CareerUiState,
    onSave: (Skill) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var nameVal by remember { mutableStateOf(skill.name) }
    var categoryVal by remember { mutableStateOf(skill.category) }
    var levelVal by remember { mutableStateOf(skill.level) }
    var iconUrlVal by remember { mutableStateOf(skill.icon_url ?: "") }

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

            FieldGroup("Datos de la habilidad") {
                SectionField(label = "Nombre", value = nameVal, onValueChange = { nameVal = it }, required = true, singleLine = true)
                SectionField(label = "Categoría", value = categoryVal, onValueChange = { categoryVal = it }, required = true, singleLine = true)
                SectionField(label = "URL del icono", value = iconUrlVal, onValueChange = { iconUrlVal = it }, singleLine = true)
            }
            Spacer(Modifier.height(8.dp))

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Nivel de dominio", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        Text("$levelVal / 4", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = levelVal.toFloat(),
                        onValueChange = { levelVal = it.toInt() },
                        valueRange = 1f..4f,
                        steps = 2
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        listOf("Básico", "Intermedio", "Avanzado", "Experto").forEach {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            SaveButton(
                onClick = {
                    onSave(skill.copy(
                        name = nameVal, category = categoryVal, level = levelVal,
                        icon_url = iconUrlVal.ifBlank { null }
                    ))
                },
                isLoading = state.isLoading,
                enabled = nameVal.isNotBlank() && categoryVal.isNotBlank()
            )
        }
    }
}
