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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Certification
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState
import dev.sebas1705.careereditor.utils.toSlug

@Composable
fun CertificationsScreen(
    state: CareerUiState,
    onSave: (Certification) -> Unit,
    onCreate: (Certification) -> Unit,
    onDelete: (String) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Certification?>(null) }
    var creatingNew by remember { mutableStateOf(false) }

    when {
        selected != null -> CertificationEditScreen(
            cert = selected!!,
            isNew = false,
            existingCerts = state.certifications,
            state = state,
            onSave = { onSave(it); selected = null },
            onCreate = {},
            onDelete = { onDelete(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
        creatingNew -> CertificationEditScreen(
            cert = Certification(),
            isNew = true,
            existingCerts = state.certifications,
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
                    Icon(Icons.Default.Add, "Nueva certificación")
                }
            }
        ) { scaffoldPadding ->
            Column(Modifier.fillMaxSize().padding(scaffoldPadding).padding(16.dp)) {
                SectionHeader("Certificaciones", "${state.certifications.size} certificados")
                if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
                state.error?.let { ErrorBanner(it, onClearError) }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.certifications) { cert ->
                        Card(
                            onClick = { selected = cert },
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(cert.name, style = MaterialTheme.typography.titleMedium)
                                Text(cert.issuer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                Text(cert.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
fun CertificationEditScreen(
    cert: Certification,
    isNew: Boolean,
    existingCerts: List<Certification>,
    state: CareerUiState,
    onSave: (Certification) -> Unit,
    onCreate: (Certification) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var template by remember { mutableStateOf<Certification?>(null) }
    val base = if (isNew) template else cert

    var id by remember { mutableStateOf("") }
    var idEditedByUser by remember { mutableStateOf(false) }

    var name   by remember(base) { mutableStateOf(base?.name ?: "") }
    var issuer by remember(base) { mutableStateOf(base?.issuer ?: "") }
    var date   by remember(base) { mutableStateOf(base?.date ?: "") }
    var url    by remember(base) { mutableStateOf(base?.url ?: "") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showTemplatePicker by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            itemName = cert.name.ifBlank { cert.id },
            onConfirm = { showDeleteDialog = false; onDelete(cert.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }

    fun buildCert(): Certification {
        val cid = if (isNew) id else cert.id
        return Certification(id = cid, name = name, issuer = issuer, date = date, url = url.ifBlank { null })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Nueva certificación" else cert.issuer.ifBlank { cert.id }, maxLines = 1) },
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

            if (isNew && existingCerts.isNotEmpty()) {
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
                                existingCerts.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text("${c.name} – ${c.issuer}") },
                                        onClick = { template = c; showTemplatePicker = false; idEditedByUser = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isNew) IdField(value = id, onValueChange = { id = it; idEditedByUser = true })

            FieldGroup("Información del certificado") {
                SectionField(
                    label = "Nombre", value = name, required = true, singleLine = true,
                    onValueChange = {
                        name = it
                        if (isNew && !idEditedByUser) id = it.toSlug()
                    }
                )
                SectionField(label = "Emisor", value = issuer, onValueChange = { issuer = it }, required = true, singleLine = true)
                SectionField(label = "Fecha", value = date, onValueChange = { date = it }, singleLine = true, supportingText = "Ej: Dec 2025")
                SectionField(label = "URL del certificado", value = url, onValueChange = { url = it }, singleLine = true)
            }

            val saveEnabled = name.isNotBlank() && issuer.isNotBlank() && (!isNew || id.isNotBlank())
            if (isNew) {
                Button(
                    onClick = { onCreate(buildCert()) },
                    enabled = !state.isLoading && saveEnabled,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp)); Text("Creando...")
                    } else Text("Crear certificación")
                }
            } else {
                SaveButton(onClick = { onSave(buildCert()) }, isLoading = state.isLoading, enabled = saveEnabled)
            }
        }
    }
}
