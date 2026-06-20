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
    var name   by remember(cert) { mutableStateOf(cert.name) }
    var issuer by remember(cert) { mutableStateOf(cert.issuer) }
    var date   by remember(cert) { mutableStateOf(cert.date) }
    var url    by remember(cert) { mutableStateOf(cert.url) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cert.issuer.ifBlank { cert.id }, maxLines = 1) },
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
                SectionField(label = "Nombre", value = name, onValueChange = { name = it }, required = true, singleLine = true)
                SectionField(label = "Emisor", value = issuer, onValueChange = { issuer = it }, required = true, singleLine = true)
                SectionField(label = "Fecha", value = date, onValueChange = { date = it }, singleLine = true, supportingText = "Ej: Dec 2025")
                SectionField(label = "URL del certificado", value = url, onValueChange = { url = it }, singleLine = true)
            }

            SaveButton(
                onClick = { onSave(cert.copy(name = name, issuer = issuer, date = date, url = url)) },
                isLoading = state.isLoading,
                enabled = name.isNotBlank() && issuer.isNotBlank()
            )
        }
    }
}
