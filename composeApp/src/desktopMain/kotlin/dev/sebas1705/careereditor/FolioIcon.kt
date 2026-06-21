package dev.sebas1705.careereditor

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density

/**
 * Programmatic "Folio" app icon for the Desktop window.
 *
 * Design: rounded indigo square, bold white "F" lettermark, teal accent dot.
 * Matches the Android adaptive icon defined in ic_launcher_foreground.xml.
 */
val FolioIconPainter: Painter = object : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        val w = size.width
        val h = size.height
        val s = w / 108f          // scale factor relative to the 108-unit design grid

        // ── Rounded indigo background ────────────────────────────────────────
        drawRoundRect(
            color = Color(0xFF5B6CF9),
            size = size,
            cornerRadius = CornerRadius(w * 0.18f, h * 0.18f)
        )

        // ── White "F" lettermark ─────────────────────────────────────────────
        //   Vertical stroke:  x 35–49, y 28–80
        //   Top bar:          x 35–73, y 28–40
        //   Middle bar:       x 35–63, y 50–62
        val fPath = Path().apply {
            moveTo(35f * s, 28f * s)
            lineTo(73f * s, 28f * s); lineTo(73f * s, 40f * s); lineTo(49f * s, 40f * s)
            lineTo(49f * s, 50f * s); lineTo(63f * s, 50f * s); lineTo(63f * s, 62f * s)
            lineTo(49f * s, 62f * s); lineTo(49f * s, 80f * s); lineTo(35f * s, 80f * s)
            close()
        }
        drawPath(fPath, Color.White)

        // ── Teal accent dot ──────────────────────────────────────────────────
        drawCircle(
            color = Color(0xFF00B8A9),
            radius = 7f * s,
            center = Offset(76f * s, 76f * s)
        )
    }
}
