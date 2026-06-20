package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Personal
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun PersonalScreen(
    state: CareerUiState,
    onSave: (Personal) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember(state.personal) { mutableStateOf(state.personal.name) }
    var greetingEn by remember(state.personal) { mutableStateOf(state.personal.greetingEn) }
    var greetingEs by remember(state.personal) { mutableStateOf(state.personal.greetingEs) }
    var roleEn by remember(state.personal) { mutableStateOf(state.personal.roleEn) }
    var roleEs by remember(state.personal) { mutableStateOf(state.personal.roleEs) }
    var taglineEn by remember(state.personal) { mutableStateOf(state.personal.taglineEn) }
    var taglineEs by remember(state.personal) { mutableStateOf(state.personal.taglineEs) }
    var bioEn by remember(state.personal) { mutableStateOf(state.personal.bioEn) }
    var bioEs by remember(state.personal) { mutableStateOf(state.personal.bioEs) }
    var email by remember(state.personal) { mutableStateOf(state.personal.email) }
    var locationEn by remember(state.personal) { mutableStateOf(state.personal.locationEn) }
    var locationEs by remember(state.personal) { mutableStateOf(state.personal.locationEs) }
    var cvUrl by remember(state.personal) { mutableStateOf(state.personal.cvUrl) }
    var github by remember(state.personal) { mutableStateOf(state.personal.github) }
    var linkedin by remember(state.personal) { mutableStateOf(state.personal.linkedin) }
    var codewars by remember(state.personal) { mutableStateOf(state.personal.codewars) }

    val isValid = name.isNotBlank() && email.isNotBlank()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionHeader("Información Personal", "Datos de presentación del perfil")
        if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
        state.error?.let { ErrorBanner(it, onClearError) }

        FieldGroup("Identidad") {
            SectionField("Nombre completo", name, { name = it }, required = true, singleLine = true)
            SectionField("Email", email, { email = it }, required = true, singleLine = true)
            SectionField("Ubicación (EN)", locationEn, { locationEn = it }, singleLine = true)
            SectionField("Ubicación (ES)", locationEs, { locationEs = it }, singleLine = true)
        }

        Spacer(Modifier.height(8.dp))

        FieldGroup("Textos de presentación") {
            SectionField("Saludo (EN)", greetingEn, { greetingEn = it }, singleLine = true)
            SectionField("Saludo (ES)", greetingEs, { greetingEs = it }, singleLine = true)
            SectionField("Rol (EN)", roleEn, { roleEn = it }, singleLine = true)
            SectionField("Rol (ES)", roleEs, { roleEs = it }, singleLine = true)
            SectionField("Tagline (EN)", taglineEn, { taglineEn = it })
            SectionField("Tagline (ES)", taglineEs, { taglineEs = it })
            SectionField("Bio (EN)", bioEn, { bioEn = it })
            SectionField("Bio (ES)", bioEs, { bioEs = it })
        }

        Spacer(Modifier.height(8.dp))

        FieldGroup("Links y redes") {
            SectionField("URL del CV", cvUrl, { cvUrl = it }, singleLine = true)
            SectionField("GitHub", github, { github = it }, singleLine = true)
            SectionField("LinkedIn", linkedin, { linkedin = it }, singleLine = true)
            SectionField("Codewars", codewars, { codewars = it }, singleLine = true)
        }

        SaveButton(
            onClick = {
                onSave(Personal(
                    name = name, greetingEn = greetingEn, greetingEs = greetingEs,
                    roleEn = roleEn, roleEs = roleEs, taglineEn = taglineEn,
                    taglineEs = taglineEs, bioEn = bioEn, bioEs = bioEs,
                    email = email, locationEn = locationEn, locationEs = locationEs,
                    cvUrl = cvUrl, github = github, linkedin = linkedin, codewars = codewars
                ))
            },
            isLoading = state.isLoading,
            enabled = isValid
        )
    }
}

@Composable
fun FieldGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 4.dp))
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}
