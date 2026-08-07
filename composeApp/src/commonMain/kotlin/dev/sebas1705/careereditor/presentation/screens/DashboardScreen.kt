package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sebas1705.careereditor.presentation.viewmodel.CareerUiState

@Composable
fun DashboardScreen(state: CareerUiState, onNavigate: (String) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Ficha del perfil publicado ───────────────────────────────────────
        if (state.personal.name.isNotBlank()) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            state.personal.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(state.personal.name, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            state.personal.role.values.firstOrNull() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            state.personal.location.values.firstOrNull() ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        } else if (state.isLoading) {
            SkeletonCard(Modifier.fillMaxWidth().height(96.dp))
        }

        Spacer(Modifier.height(24.dp))

        SectionEyebrow("~/portfolio", "Resumen del perfil")
        Spacer(Modifier.height(12.dp))

        // ── Contadores por entidad ───────────────────────────────────────────
        val stats = listOf(
            Triple("trabajos", state.jobs.size, "jobs"),
            Triple("proyectos", state.projects.size, "projects"),
            Triple("skills", state.skills.size, "skills"),
            Triple("educación", state.education.size, "education"),
            Triple("certs", state.certifications.size, "certifications"),
            Triple("soft-skills", state.softSkills.size, "soft-skills"),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(stats) { (label, count, route) ->
                StatCard(
                    label = label,
                    count = count,
                    isLoading = state.isLoading,
                    onClick = { onNavigate(route) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        SectionEyebrow("~/editar", "Acciones rápidas")
        Spacer(Modifier.height(12.dp))

        QuickActionItem(
            icon = Icons.Default.Person,
            title = "Editar información personal",
            subtitle = state.personal.email.ifBlank { "Nombre, bio, redes sociales" },
            onClick = { onNavigate("personal") }
        )
        QuickActionItem(
            icon = Icons.Default.Build,
            title = "Actualizar experiencia laboral",
            subtitle = "${state.jobs.size} entrada${if (state.jobs.size != 1) "s" else ""}",
            onClick = { onNavigate("jobs") }
        )
        QuickActionItem(
            icon = Icons.Default.Edit,
            title = "Gestionar proyectos",
            subtitle = "${state.projects.size} proyecto${if (state.projects.size != 1) "s" else ""}",
            onClick = { onNavigate("projects") }
        )

        Spacer(Modifier.height(16.dp))

        // ── Estado de la API, en voz de terminal ─────────────────────────────
        ApiStatusStrip(isLoading = state.isLoading)
    }
}

/** Eyebrow mono + título: el mismo lenguaje que la cabecera de archivo. */
@Composable
private fun SectionEyebrow(path: String, title: String) {
    Column {
        Text(path, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(2.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun StatCard(label: String, count: Int, isLoading: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp).fillMaxWidth()) {
            if (isLoading && count == 0) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                Spacer(Modifier.height(6.dp))
            } else {
                Text(
                    count.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun ApiStatusStrip(isLoading: Boolean) {
    val pulse by rememberInfiniteTransition(label = "api-pulse").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "api-pulse-alpha"
    )
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(
                        if (isLoading) MaterialTheme.colorScheme.tertiary.copy(alpha = pulse)
                        else MaterialTheme.colorScheme.secondary.copy(alpha = pulse),
                        CircleShape
                    )
            )
            Spacer(Modifier.width(10.dp))
            Text(
                if (isLoading) "conectando · career-api.sebas1705.workers.dev"
                else "ok · career-api.sebas1705.workers.dev",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SkeletonCard(modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {}
}
