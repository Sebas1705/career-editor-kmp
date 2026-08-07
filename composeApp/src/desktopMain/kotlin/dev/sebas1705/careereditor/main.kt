package dev.sebas1705.careereditor

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Folio",
        icon = FolioIconPainter,
        state = rememberWindowState(width = 900.dp, height = 700.dp)
    ) {
        // Tamaño mínimo: por debajo de esto los stats de 3 columnas y los
        // TopAppBar con acciones empiezan a solaparse.
        LaunchedEffect(Unit) { window.minimumSize = Dimension(680, 560) }
        App()
    }
}
