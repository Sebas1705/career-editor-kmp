package dev.sebas1705.careereditor.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.data.model.Language

@Composable
fun SectionField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = false,
    required: Boolean = false,
    supportingText: String? = null
) {
    val isError = required && value.isBlank()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(if (required) "$label *" else label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 5,
        isError = isError,
        supportingText = when {
            isError -> ({ Text("Campo obligatorio", color = MaterialTheme.colorScheme.error) })
            supportingText != null -> ({ Text(supportingText, style = MaterialTheme.typography.labelSmall) })
            else -> null
        },
        shape = MaterialTheme.shapes.medium,
        keyboardOptions = KeyboardOptions(imeAction = if (singleLine) ImeAction.Next else ImeAction.Default)
    )
}

@Composable
fun SaveButton(onClick: () -> Unit, isLoading: Boolean = false, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = !isLoading && enabled,
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(48.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.width(8.dp))
            Text("Guardando...")
        } else {
            Text("Guardar cambios")
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text("Cargando datos...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚠ $message",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss, contentPadding = PaddingValues(0.dp)) {
                    Text("✕", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
    }
}

@Composable
fun SuccessBanner(onDismiss: () -> Unit) {
    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "✓ Guardado correctamente",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss, contentPadding = PaddingValues(0.dp)) {
                    Text("✕", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun TagChip(label: String) {
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(Modifier.padding(bottom = 12.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Tab row for switching between content languages when editing.
 * Only renders when there are 2+ supported languages.
 */
@Composable
fun LanguageTabs(
    languages: List<Language>,
    selectedCode: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (languages.size < 2) return
    val selectedIndex = languages.indexOfFirst { it.code == selectedCode }.coerceAtLeast(0)
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        languages.forEach { lang ->
            Tab(
                selected = lang.code == selectedCode,
                onClick = { onSelect(lang.code) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(lang.code.uppercase(), style = MaterialTheme.typography.labelLarge)
                        Text(lang.labelNative, style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        }
    }
}
