package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Language
import dev.sebas1705.careereditor.data.model.Personal
import dev.sebas1705.careereditor.presentation.components.*
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun PersonalScreen(
    state: CareerUiState,
    selectedLangCode: String,
    onSelectLang: (String) -> Unit,
    onSave: (Personal) -> Unit,
    onClearSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    val p = state.personal
    var name     by remember(p) { mutableStateOf(p.name) }
    var email    by remember(p) { mutableStateOf(p.email) }
    var cvUrl    by remember(p) { mutableStateOf(p.cvUrl) }
    var github   by remember(p) { mutableStateOf(p.github) }
    var linkedin by remember(p) { mutableStateOf(p.linkedin) }
    var codewars by remember(p) { mutableStateOf(p.codewars) }

    // Mutable copies of all localized maps — edits accumulate across lang switches
    var greeting by remember(p) { mutableStateOf(p.greeting) }
    var role     by remember(p) { mutableStateOf(p.role) }
    var tagline  by remember(p) { mutableStateOf(p.tagline) }
    var bio      by remember(p) { mutableStateOf(p.bio) }
    var location by remember(p) { mutableStateOf(p.location) }

    val lang = selectedLangCode
    val isValid = name.isNotBlank() && email.isNotBlank()

    Column(Modifier.fillMaxSize()) {
        // Language switcher
        LanguageTabs(
            languages = state.languages.supported,
            selectedCode = lang,
            onSelect = onSelectLang
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader("Información Personal", "Editando: ${state.languages.supported.find { it.code == lang }?.label ?: lang}")
            if (state.saveSuccess) SuccessBanner(onDismiss = onClearSuccess)
            state.error?.let { ErrorBanner(it, onClearError) }

            FieldGroup("Identidad (no localizable)") {
                SectionField("Nombre completo", name, { name = it }, required = true, singleLine = true)
                SectionField("Email", email, { email = it }, required = true, singleLine = true)
            }

            Spacer(Modifier.height(8.dp))

            FieldGroup("Presentación [$lang]") {
                SectionField(
                    label = "Saludo",
                    value = greeting[lang] ?: "",
                    onValueChange = { greeting = greeting + (lang to it) },
                    singleLine = true
                )
                SectionField(
                    label = "Rol",
                    value = role[lang] ?: "",
                    onValueChange = { role = role + (lang to it) },
                    singleLine = true
                )
                SectionField(
                    label = "Tagline",
                    value = tagline[lang] ?: "",
                    onValueChange = { tagline = tagline + (lang to it) }
                )
                SectionField(
                    label = "Bio",
                    value = bio[lang] ?: "",
                    onValueChange = { bio = bio + (lang to it) }
                )
                SectionField(
                    label = "Ubicación",
                    value = location[lang] ?: "",
                    onValueChange = { location = location + (lang to it) },
                    singleLine = true
                )
            }

            Spacer(Modifier.height(8.dp))

            FieldGroup("Links y redes (no localizables)") {
                SectionField("URL del CV", cvUrl, { cvUrl = it }, singleLine = true)
                SectionField("GitHub", github, { github = it }, singleLine = true)
                SectionField("LinkedIn", linkedin, { linkedin = it }, singleLine = true)
                SectionField("Codewars", codewars, { codewars = it }, singleLine = true)
            }

            SaveButton(
                onClick = {
                    onSave(p.copy(
                        name = name, email = email, cvUrl = cvUrl,
                        github = github, linkedin = linkedin, codewars = codewars,
                        greeting = greeting, role = role, tagline = tagline,
                        bio = bio, location = location
                    ))
                },
                isLoading = state.isLoading,
                enabled = isValid
            )
        }
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
    Spacer(Modifier.height(8.dp))
}
