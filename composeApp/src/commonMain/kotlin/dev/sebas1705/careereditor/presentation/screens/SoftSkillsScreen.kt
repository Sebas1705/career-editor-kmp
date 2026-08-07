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
import dev.sebas1705.careereditor.data.model.SoftSkill
import dev.sebas1705.careereditor.data.model.resolve
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState
import dev.sebas1705.careereditor.utils.toSlug

@Composable
fun SoftSkillsScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (SoftSkill) -> Unit,
    onCreate: (SoftSkill) -> Unit,
    onDelete: (String) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<SoftSkill?>(null) }
    var creatingNew by remember { mutableStateOf(false) }

    when {
        selected != null -> SoftSkillEditScreen(
            skill = selected!!,
            isNew = false,
            existingSkills = state.softSkills,
            state = state,
            selectedLangCode = selectedLangCode,
            onSelectLang = onSelectLang,
            onSave = { onSave(it); selected = null },
            onCreate = {},
            onDelete = { onDelete(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        creatingNew -> SoftSkillEditScreen(
            skill = SoftSkill(),
            isNew = true,
            existingSkills = state.softSkills,
            state = state,
            selectedLangCode = selectedLangCode,
            onSelectLang = onSelectLang,
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
                    Icon(Icons.Default.Add, "Nueva habilidad blanda")
                }
            }
        ) { scaffoldPadding ->
            Column(Modifier.fillMaxSize().padding(scaffoldPadding)) {
                LanguageTabs(state.languages.supported, selectedLangCode, onSelectLang)
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    SectionHeader("Habilidades Blandas", "${state.softSkills.size} habilidades")
                    if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                    state.error?.let { ErrorBanner(it, onClearError) }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 88.dp) // el FAB no tapa la última tarjeta
                    ) {
                        items(state.softSkills) { skill ->
                            Card(
                                onClick = { selected = skill },
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoftSkillEditScreen(
    skill: SoftSkill,
    isNew: Boolean,
    existingSkills: List<SoftSkill>,
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (SoftSkill) -> Unit,
    onCreate: (SoftSkill) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var template by remember { mutableStateOf<SoftSkill?>(null) }
    val base = if (isNew) template else skill

    var id by remember { mutableStateOf("") }
    var idEditedByUser by remember { mutableStateOf(false) }

    var name by remember(base) { mutableStateOf(base?.name ?: emptyMap()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }
    val lang = selectedLangCode

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemName = skill.name.resolve(lang).ifBlank { skill.id },
            onConfirm = { showDeleteDialog = false; onDelete(skill.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    fun buildSkill(): SoftSkill {
        val sid = if (isNew) id else skill.id
        return SoftSkill(id = sid, name = name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Nueva habilidad" else skill.name.resolve(lang).ifBlank { skill.id }, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } },
                actions = {
                    if (!isNew) DeleteButton(onClick = { showDeleteDialog = true }, enabled = !state.isLoading)
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LanguageTabs(state.languages.supported, lang, onSelectLang)

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
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
                                    value = template?.name?.resolve("en") ?: "Sin plantilla",
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
                                            text = { Text(s.name.resolve("en")) },
                                            onClick = { template = s; showTemplatePicker = false; idEditedByUser = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (isNew) IdField(value = id, onValueChange = { id = it; idEditedByUser = true })

                FieldGroup("Nombre [$lang]") {
                    SectionField(
                        label = "Nombre", value = name[lang] ?: "", required = true, singleLine = true,
                        onValueChange = {
                            name = name + (lang to it)
                            if (isNew && !idEditedByUser) id = it.toSlug()
                        }
                    )
                }

                val saveEnabled = (name[lang] ?: "").isNotBlank() && (!isNew || id.isNotBlank())
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
}
