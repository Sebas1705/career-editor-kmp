package dev.sebas1705.careereditor.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
        // Header
        if (state.personal.name.isNotBlank()) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                state.personal.name.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(state.personal.name, style = MaterialTheme.typography.titleLarge)
                        Text(state.personal.role.values.firstOrNull() ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(state.personal.location.values.firstOrNull() ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        } else if (state.isLoading) {
            SkeletonCard(Modifier.fillMaxWidth().height(96.dp))
        }

        Spacer(Modifier.height(24.dp))

        Text("Resumen del perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // Stats grid
        val stats = listOf(
            Triple("Trabajos", state.jobs.size, Icons.Default.Settings),
            Triple("Proyectos", state.projects.size, Icons.Default.Edit),
            Triple("Habilidades", state.skills.size, Icons.Default.Star),
            Triple("Educación", state.education.size, Icons.Default.Home),
            Triple("Certs", state.certifications.size, Icons.Default.Add),
            Triple("Soft Skills", state.softSkills.size, Icons.Default.Favorite),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(stats) { (label, count, icon) ->
                StatCard(label = label, count = count, icon = icon, isLoading = state.isLoading)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Quick actions
        Text("Acciones rápidas", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        QuickActionItem(
            icon = Icons.Default.Person,
            title = "Editar información personal",
            subtitle = state.personal.email.ifBlank { "Nombre, bio, redes sociales" },
            onClick = { onNavigate("personal") }
        )
        QuickActionItem(
            icon = Icons.Default.Settings,
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

        Spacer(Modifier.height(8.dp))

        // API status
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(8.dp)) {}
                Spacer(Modifier.width(8.dp))
                Text(
                    if (state.isLoading) "Conectando con la API..." else "API conectada · career-api.sebas1705.workers.dev",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatCard(label: String, count: Int, icon: ImageVector, isLoading: Boolean) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading && count == 0) {
                Box(Modifier.size(32.dp).padding(4.dp)) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            } else {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text(
                if (isLoading && count == 0) "-" else count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline)
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
