package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.MOUSE_CLICK
import com.mlesniak.main.NCurses.Companion.MousePosition
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.initNCurses
import com.mlesniak.main.NCurses.Companion.lines
import kotlin.math.absoluteValue

val initialZoomLevel = Mandelbrot.Rect(
    -2.0, 1.2,
    1.0, -1.2
)
const val maxIterations = 256

// TODO(mlesniak) Refactoring and clean up
// TODO(mlesniak) Add caching on top of it
fun main() {
    initNCurses()

    var zoomLevel = initialZoomLevel
    val config = Configuration(
        cols(),
        lines(),
        maxIterations
    )

    // TODO(mlesniak) withConfig?
    renderLoop@ while (true) {
        val image = Mandelbrot.compute(config, zoomLevel)
        AsciiRenderer.render(config, image)

        // Keyboard handling.
        while (true) {
            when (getch()) {
                'q'.code -> break@renderLoop

                'z'.code -> {
                    // if (zoomCache.isNotEmpty()) {
                    //     val entry = zoomCache.last()
                    //     zoom = entry.zoom
                    //     image = entry.values
                    //     zoomCache.removeLast()
                    //     break
                    // }
                }

                MOUSE_CLICK -> {
                    val mousePosition = MousePosition()
                    if (!NCurses.getevent(mousePosition)) {
                        // Ignore other events.
                        continue
                    }

                    val wStep = (zoomLevel.x2 - zoomLevel.x1).absoluteValue / config.width
                    val hStep = (zoomLevel.y2 - zoomLevel.y1).absoluteValue / config.height
                    val cx = mousePosition.x * wStep + zoomLevel.x1
                    val cy = zoomLevel.y1 - mousePosition.y * hStep

                    zoomLevel = zoomLevel.copy(
                        x1 = cx - (zoomLevel.x2 - zoomLevel.x1).absoluteValue / 4.0,
                        x2 = cx + (zoomLevel.x2 - zoomLevel.x1).absoluteValue / 4.0,
                        y1 = cy + (zoomLevel.y2 - zoomLevel.y1).absoluteValue / 4.0,
                        y2 = cy - (zoomLevel.y2 - zoomLevel.y1).absoluteValue / 4.0,
                    )
                    break
                }
            }
        }
    }

    endwin()
}
