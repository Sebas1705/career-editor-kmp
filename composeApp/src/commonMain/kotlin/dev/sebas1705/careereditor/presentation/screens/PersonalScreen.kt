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
fun PersonalScreen(state: CareerUiState, onSave: (Personal) -> Unit, onClearSuccess: () -> Unit, onClearError: () -> Unit) {
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

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Información Personal", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
        state.error?.let { ErrorBanner(it, onClearError) }

        SectionField("Nombre", name) { name = it }
        SectionField("Saludo (EN)", greetingEn) { greetingEn = it }
        SectionField("Saludo (ES)", greetingEs) { greetingEs = it }
        SectionField("Rol (EN)", roleEn) { roleEn = it }
        SectionField("Rol (ES)", roleEs) { roleEs = it }
        SectionField("Tagline (EN)", taglineEn) { taglineEn = it }
        SectionField("Tagline (ES)", taglineEs) { taglineEs = it }
        SectionField("Bio (EN)", bioEn) { bioEn = it }
        SectionField("Bio (ES)", bioEs) { bioEs = it }
        SectionField("Email", email) { email = it }
        SectionField("Ubicación (EN)", locationEn) { locationEn = it }
        SectionField("Ubicación (ES)", locationEs) { locationEs = it }
        SectionField("URL del CV", cvUrl) { cvUrl = it }
        SectionField("GitHub", github) { github = it }
        SectionField("LinkedIn", linkedin) { linkedin = it }
        SectionField("Codewars", codewars) { codewars = it }

        SaveButton(
            onClick = {
                onSave(
                    Personal(
                        name = name, greetingEn = greetingEn, greetingEs = greetingEs,
                        roleEn = roleEn, roleEs = roleEs, taglineEn = taglineEn,
                        taglineEs = taglineEs, bioEn = bioEn, bioEs = bioEs,
                        email = email, locationEn = locationEn, locationEs = locationEs,
                        cvUrl = cvUrl, github = github, linkedin = linkedin, codewars = codewars
                    )
                )
            },
            isLoading = state.isLoading
        )
    }
}
