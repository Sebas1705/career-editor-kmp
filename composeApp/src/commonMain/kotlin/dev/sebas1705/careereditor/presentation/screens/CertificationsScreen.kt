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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Certification
import dev.sebas1705.careereditor.data.model.LocalizedText
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun CertificationsScreen(state: CareerUiState, onSave: (Certification) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var selectedCert by remember { mutableStateOf<Certification?>(null) }

    if (selectedCert != null) {
        CertificationEditScreen(
            cert = selectedCert!!,
            state = state,
            onSave = { onSave(it); selectedCert = null },
            onBack = { selectedCert = null },
            onClearSuccess = onClearSuccess,
            onClearError = onClearError
        )
    } else {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Certificaciones", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }
            LazyColumn {
                items(state.certifications) { cert ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedCert = cert }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(cert.name.es, style = MaterialTheme.typography.titleMedium)
                            Text(cert.issuer, style = MaterialTheme.typography.bodyMedium)
                            Text(cert.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificationEditScreen(cert: Certification, state: CareerUiState, onSave: (Certification) -> Unit, onBack: () -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
    var nameEn by remember { mutableStateOf(cert.name.en) }
    var nameEs by remember { mutableStateOf(cert.name.es) }
    var issuer by remember { mutableStateOf(cert.issuer) }
    var date by remember { mutableStateOf(cert.date) }
    var descEn by remember { mutableStateOf(cert.desc.en) }
    var descEs by remember { mutableStateOf(cert.desc.es) }
    var url by remember { mutableStateOf(cert.url) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cert.issuer) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            SectionField("Nombre (EN)", nameEn) { nameEn = it }
            SectionField("Nombre (ES)", nameEs) { nameEs = it }
            SectionField("Emisor", issuer) { issuer = it }
            SectionField("Fecha", date) { date = it }
            SectionField("Descripción (EN)", descEn) { descEn = it }
            SectionField("Descripción (ES)", descEs) { descEs = it }
            SectionField("URL del certificado", url) { url = it }

            SaveButton(
                onClick = {
                    onSave(cert.copy(
                        name = LocalizedText(nameEn, nameEs),
                        issuer = issuer, date = date,
                        desc = LocalizedText(descEn, descEs),
                        url = url
                    ))
                },
                isLoading = state.isLoading
            )
        }
    }
}
