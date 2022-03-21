package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.MOUSE_CLICK
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import org.w3c.dom.css.Rect
import java.io.File
import java.util.Date
import java.util.concurrent.ConcurrentHashMap

val initialZoomLevel = Mandelbrot.Rect(
    -2.0, 1.2,
    1.0, -1.2
)
val maxIterations = 256

data class CacheEntry(val zoom: Rect, val values: ConcurrentHashMap<Int, Array<Int>>)
data class Pos(val x: Int = 0, val y: Int = 0)

// TODO(mlesniak) Refactoring and clean up
// TODO(mlesniak) Add caching on top of it
fun main() {
    System.loadLibrary("native")
    init()
    debug("\n\n\n${Date()}")

    var zoomLevel = initialZoomLevel

    val config = Configuration(cols(), lines(), maxIterations)

    val zoomCache = mutableListOf<CacheEntry>()
    var image = ConcurrentHashMap<Int, Array<Int>>()

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
                    // val p = Pos()
                    // if (!NCurses.getevent(p)) {
                    //     // Ignore other events.
                    //     continue
                    // }
                    // val cx = p.x * wStep + zoom.x1
                    // val cy = zoom.y1 - p.y * hStep
                    //
                    // zoomCache += CacheEntry(zoom, image)
                    // image = ConcurrentHashMap()
                    // zoom = zoom.copy(
                    //     x1 = cx - (zoom.x2 - zoom.x1).absoluteValue / 4.0,
                    //     x2 = cx + (zoom.x2 - zoom.x1).absoluteValue / 4.0,
                    //     y1 = cy + (zoom.y2 - zoom.y1).absoluteValue / 4.0,
                    //     y2 = cy - (zoom.y2 - zoom.y1).absoluteValue / 4.0,
                    // )
                    break
                }
            }
        }
    }

    endwin()
}

private fun debug(msg: Any) {
    File("debug.log").appendText("$msg\n")
}
