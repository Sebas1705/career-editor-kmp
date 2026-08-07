package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Skill
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState
import dev.sebas1705.careereditor.utils.toSlug

@Composable
fun SkillsScreen(
    state: CareerUiState,
    onSave: (Skill) -> Unit,
    onCreate: (Skill) -> Unit,
    onDelete: (String) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Skill?>(null) }
    var creatingNew by remember { mutableStateOf(false) }

    when {
        selected != null -> SkillEditScreen(
            skill = selected!!,
            isNew = false,
            existingSkills = state.skills,
            state = state,
            onSave = { onSave(it); selected = null },
            onCreate = {},
            onDelete = { onDelete(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        creatingNew -> SkillEditScreen(
            skill = Skill(),
            isNew = true,
            existingSkills = state.skills,
            state = state,
            onSave = {},
            onCreate = { onCreate(it); creatingNew = false },
            onDelete = {},
            onBack = { creatingNew = false },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        else -> Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { creatingNew = true }) {
                    Icon(Icons.Default.Add, "Nueva habilidad")
                }
            }
        ) { scaffoldPadding ->
            Column(Modifier.fillMaxSize().padding(scaffoldPadding).padding(16.dp)) {
                SectionHeader("Habilidades Técnicas", "${state.skills.size} habilidades")
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                val grouped = state.skills.groupBy { it.category }
                LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) { // el FAB no tapa la última fila
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
    isNew: Boolean,
    existingSkills: List<Skill>,
    state: CareerUiState,
    onSave: (Skill) -> Unit,
    onCreate: (Skill) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var template by remember { mutableStateOf<Skill?>(null) }
    val base = if (isNew) template else skill

    var id by remember { mutableStateOf("") }
    var idEditedByUser by remember { mutableStateOf(false) }

    var nameVal     by remember(base) { mutableStateOf(base?.name ?: "") }
    var categoryVal by remember(base) { mutableStateOf(base?.category ?: "") }
    var levelVal    by remember(base) { mutableStateOf(base?.level ?: 1) }
    var iconUrlVal  by remember(base) { mutableStateOf(base?.icon_url ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemName = skill.name.ifBlank { skill.id },
            onConfirm = { showDeleteDialog = false; onDelete(skill.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    fun buildSkill(): Skill {
        val sid = if (isNew) id else skill.id
        return Skill(id = sid, name = nameVal, category = categoryVal, level = levelVal, icon_url = iconUrlVal.ifBlank { null })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Nueva habilidad" else skill.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (!isNew) DeleteButton(onClick = { showDeleteDialog = true }, enabled = !state.isLoading)
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            if (isNew && existingSkills.isNotEmpty()) {
                OutlinedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Plantilla (opcional)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = showTemplatePicker,
                            onExpandedChange = { showTemplatePicker = it }
                        ) {
                            OutlinedTextField(
                                value = template?.name ?: "Sin plantilla",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Copiar datos de...") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTemplatePicker) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = showTemplatePicker, onDismissRequest = { showTemplatePicker = false }) {
                                DropdownMenuItem(text = { Text("Sin plantilla") }, onClick = { template = null; showTemplatePicker = false })
                                existingSkills.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text("${s.name} (${s.category})") },
                                        onClick = { template = s; showTemplatePicker = false; idEditedByUser = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isNew) IdField(value = id, onValueChange = { id = it; idEditedByUser = true })

            FieldGroup("Datos de la habilidad") {
                SectionField(
                    label = "Nombre", value = nameVal, required = true, singleLine = true,
                    onValueChange = {
                        nameVal = it
                        if (isNew && !idEditedByUser) id = it.toSlug()
                    }
                )
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
                    Slider(value = levelVal.toFloat(), onValueChange = { levelVal = it.toInt() }, valueRange = 1f..4f, steps = 2)
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        listOf("Básico", "Intermedio", "Avanzado", "Experto").forEach {
                            Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            val saveEnabled = nameVal.isNotBlank() && categoryVal.isNotBlank() && (!isNew || id.isNotBlank())
            if (isNew) {
                Button(
                    onClick = { onCreate(buildSkill()) },
                    enabled = !state.isLoading && saveEnabled,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp)); Text("Creando...")
                    } else Text("Crear habilidad")
                }
            } else {
                SaveButton(onClick = { onSave(buildSkill()) }, isLoading = state.isLoading, enabled = saveEnabled)
            }
        }
    }
}
