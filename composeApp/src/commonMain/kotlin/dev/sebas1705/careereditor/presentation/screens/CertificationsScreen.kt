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
import dev.sebas1705.careereditor.data.model.Certification
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun CertificationsScreen(
    state: CareerUiState,
    onSave: (Certification) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var selected by remember { mutableStateOf<Certification?>(null) }

    if (selected != null) {
        CertificationEditScreen(
            cert = selected!!,
            state = state,
            onSave = { onSave(it); selected = null },
            onBack = { selected = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
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
                            Text(cert.name.es, style = MaterialTheme.typography.titleMedium)
                            Text(cert.issuer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            Text(cert.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    state: CareerUiState,
    onSave: (Certification) -> Unit,
    onBack: () -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var nameEnVal by remember { mutableStateOf(cert.name.en) }
    var nameEsVal by remember { mutableStateOf(cert.name.es) }
    var issuerVal by remember { mutableStateOf(cert.issuer) }
    var dateVal by remember { mutableStateOf(cert.date) }
    var descEnVal by remember { mutableStateOf(cert.desc.en) }
    var descEsVal by remember { mutableStateOf(cert.desc.es) }
    var urlVal by remember { mutableStateOf(cert.url) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cert.issuer, maxLines = 1) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            FieldGroup("Información del certificado") {
                SectionField(label = "Nombre (EN)", value = nameEnVal, onValueChange = { nameEnVal = it }, required = true, singleLine = true)
                SectionField(label = "Nombre (ES)", value = nameEsVal, onValueChange = { nameEsVal = it }, required = true, singleLine = true)
                SectionField(label = "Emisor", value = issuerVal, onValueChange = { issuerVal = it }, required = true, singleLine = true)
                SectionField(label = "Fecha", value = dateVal, onValueChange = { dateVal = it }, singleLine = true, supportingText = "Ej: Dec 2025")
                SectionField(label = "URL del certificado", value = urlVal, onValueChange = { urlVal = it }, singleLine = true)
            }
            Spacer(Modifier.height(8.dp))
            FieldGroup("Descripción") {
                SectionField(label = "Descripción (EN)", value = descEnVal, onValueChange = { descEnVal = it })
                SectionField(label = "Descripción (ES)", value = descEsVal, onValueChange = { descEsVal = it })
            }

            SaveButton(
                onClick = {
                    onSave(cert.copy(
                        name = LocalizedText(nameEnVal, nameEsVal),
                        issuer = issuerVal, date = dateVal,
                        desc = LocalizedText(descEnVal, descEsVal),
                        url = urlVal
                    ))
                },
                isLoading = state.isLoading,
                enabled = nameEnVal.isNotBlank() && issuerVal.isNotBlank()
            )
        }
    }
}
