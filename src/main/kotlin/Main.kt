package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.MOUSE_CLICK
import com.mlesniak.main.NCurses.Companion.MousePosition
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.initNCurses
import com.mlesniak.main.NCurses.Companion.lines
import kotlin.math.absoluteValue

// TODO(mlesniak) Caching
fun main() {
    initNCurses()
    val config = Configuration(
        width = cols(),
        height = lines(),
        maxIterations = 256
    )

    var viewport = Viewport(
        -2.0, 1.2,
        1.0, -1.2
    )
    renderLoop@ while (true) {
        val image = Mandelbrot.compute(config, viewport)
        AsciiRenderer.render(config, image)

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
                    // To simplify the C code, we create the object which
                    // stores the mouse position in the JVM space.
                    val mousePosition = MousePosition()
                    NCurses.getevent(mousePosition)

                    // Compute new center of the viewport based on the clicked
                    // mouse position.
                    val wStep = (viewport.x2 - viewport.x1).absoluteValue / config.width
                    val hStep = (viewport.y2 - viewport.y1).absoluteValue / config.height
                    val cx = mousePosition.x * wStep + viewport.x1
                    val cy = viewport.y1 - mousePosition.y * hStep

                    viewport = viewport.copy(
                        x1 = cx - (viewport.x2 - viewport.x1).absoluteValue / 4.0,
                        x2 = cx + (viewport.x2 - viewport.x1).absoluteValue / 4.0,
                        y1 = cy + (viewport.y2 - viewport.y1).absoluteValue / 4.0,
                        y2 = cy - (viewport.y2 - viewport.y1).absoluteValue / 4.0,
                    )
                    break
                }
            }
        }
    }

    endwin()
}
