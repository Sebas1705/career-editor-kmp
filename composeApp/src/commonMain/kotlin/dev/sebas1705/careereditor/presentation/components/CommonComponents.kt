package dev.sebas1705.careereditor.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sebas1705.careereditor.data.model.Language
import dev.sebas1705.careereditor.utils.toSlug

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

/**
 * Cabecera de archivo — la firma visual de Folio, en una sola línea.
 * El TopAppBar ya nombra la sección; aquí solo la ruta monospace
 * (`~/portfolio/<slug>`) y el dato de la sección como contador `[...]`.
 */
@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(Modifier.padding(bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // weight + ellipsis: la ruta cede espacio antes de pisar el contador
            Text(
                "~/portfolio/${title.toSlug()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            if (subtitle != null) {
                Spacer(Modifier.width(12.dp))
                Text(
                    "[$subtitle]",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

/**
 * Lettermark de Folio dibujado en Compose: cuadrado índigo redondeado al 18%,
 * "F" en blanco y punto teal — el mismo diseño que el icono de la app.
 */
@Composable
fun FolioMark(size: Dp = 56.dp) {
    Box(
        Modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(size * 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "F",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = (size.value * 0.46f).sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(size * 0.14f)
                .size(size * 0.14f)
                .background(MaterialTheme.colorScheme.secondary, CircleShape)
        )
    }
}

// ── Dynamic list field (add/remove chips) ─────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DynamicListField(
    label: String,
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Añadir elemento"
) {
    var input by remember { mutableStateOf("") }

    fun addItem() {
        val v = input.trim()
        if (v.isNotBlank() && v !in items) { onItemsChange(items + v); input = "" }
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            if (items.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items.forEach { item ->
                        InputChip(
                            selected = false,
                            onClick = { onItemsChange(items - item) },
                            label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                            trailingIcon = { Icon(Icons.Default.Close, "Eliminar", modifier = Modifier.size(14.dp)) },
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { addItem() })
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(onClick = ::addItem, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Add, "Añadir")
                }
            }
        }
    }
}

// ── Id field ─────────────────────────────────────────────────────────────────

@Composable
fun IdField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.lowercase().replace(Regex("[^a-z0-9-]"), "")) },
        label = { Text("ID (slug) *") },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        supportingText = { Text("Solo letras minúsculas, números y guiones", style = MaterialTheme.typography.labelSmall) }
    )
}

// ── Delete confirmation ────────────────────────────────────────────────────────

@Composable
fun DeleteButton(onClick: () -> Unit, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun DeleteConfirmDialog(itemName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar") },
        text = { Text("¿Eliminar \"$itemName\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
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
                // Una sola línea: dos textos apilados hacían el tab demasiado alto
                text = {
                    Text(
                        "${lang.code.uppercase()} · ${lang.labelNative}",
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            )
        }
    }
}
