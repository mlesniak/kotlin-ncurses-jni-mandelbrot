package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.MOUSE_CLICK
import com.mlesniak.main.NCurses.Companion.addch
import com.mlesniak.main.NCurses.Companion.clear
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import com.mlesniak.main.NCurses.Companion.refresh
import org.w3c.dom.css.Rect
import java.io.File
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue



// Ncurses
data class Pos(val x: Int = 0, val y: Int = 0)

data class CacheEntry(val zoom: Rect, val values: ConcurrentHashMap<Int, Array<Int>>)

// TODO(mlesniak) Refactoring and clean up
fun main() {
    System.loadLibrary("native")
    init()
    debug("\n\n\n${Date()}")

    var zoom = Rect(-2.0, 1.2, 1.0, -1.2)
    val maxIteration = 256

    val width = cols()
    val height = lines()

    val zoomCache = mutableListOf<CacheEntry>()
    var image = ConcurrentHashMap<Int, Array<Int>>()

    // TODO(mlesniak) extract computation into own method and class
    // TODO(mlesniak) separate caching
    renderLoop@ while (true) {

        // render for a zoom



        while (true) {
            when (getch()) {
                'q'.code -> break@renderLoop

                'z'.code -> {
                    if (zoomCache.isNotEmpty()) {
                        val entry = zoomCache.last()
                        zoom = entry.zoom
                        image = entry.values
                        zoomCache.removeLast()
                        break
                    }
                }

                MOUSE_CLICK -> {
                    val p = Pos()
                    if (!NCurses.getevent(p)) {
                        // Ignore other events.
                        continue
                    }
                    val cx = p.x * wStep + zoom.x1
                    val cy = zoom.y1 - p.y * hStep

                    zoomCache += CacheEntry(zoom, image)
                    image = ConcurrentHashMap()
                    zoom = zoom.copy(
                        x1 = cx - (zoom.x2 - zoom.x1).absoluteValue / 4.0,
                        x2 = cx + (zoom.x2 - zoom.x1).absoluteValue / 4.0,
                        y1 = cy + (zoom.y2 - zoom.y1).absoluteValue / 4.0,
                        y2 = cy - (zoom.y2 - zoom.y1).absoluteValue / 4.0,
                    )
                    break
                }
            }
        }
    }

    endwin()
}

fun color(maxIteration: Int, iter: Int): Int {
    val colorStretchFactor = 20
    val maxColors = 24
    val colors = (1..maxColors).toList() + List(colorStretchFactor) { 24 }
    return colors[((colors.size - 1).toDouble() / maxIteration * iter).toInt()]
}

fun asciiChar(maxIteration: Int, iter: Int): Char {
    val density = " .,-=+:;cba!?0123456789\$W#@Ñ"

    val index = ((density.length - 1).toDouble() / maxIteration * iter).toInt()
    return density[index]
}

fun checkIteration(x0: Double, y0: Double, maxIteration: Int): Int {
    var x = 0.0
    var y = 0.0
    var iter = 0

    // Explanation for formula at
    // https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings
    while (x * x + y * y <= 2 * 2 && iter < maxIteration) {
        val xTmp = x * x - y * y + x0
        y = 2 * x * y + y0
        x = xTmp
        iter++
    }

    return iter
}

private fun debug(msg: Any) {
    File("debug.log").appendText("$msg\n")
}
