package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.addch
import com.mlesniak.main.NCurses.Companion.clear
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import com.mlesniak.main.NCurses.Companion.refresh
import java.io.File
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

data class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double)
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

    renderLoop@ while (true) {
        val w = (zoom.x2 - zoom.x1).absoluteValue
        val h = (zoom.y2 - zoom.y1).absoluteValue
        val wStep = w / width
        val hStep = h / height

        val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        val lock = Object()

        clear()
        for (y in 0 until height) {
            pool.submit {
                val values = if (image[y] != null) image[y]!! else {
                    val values = Array(width) { 0 }
                    val y1 = zoom.y1 - y * hStep
                    for (x in 0 until width) {
                        val x1 = x * wStep + zoom.x1
                        val iterations = checkIteration(x1, y1, maxIteration)
                        values[x] = iterations
                    }
                    values
                }

                image[y] = values
                synchronized(lock) {
                    for (x in 0 until width) {
                        val c = asciiChar(maxIteration, values[x])
                        val col = color(maxIteration, values[x])
                        addch(x, y, c, col)
                    }
                    refresh()
                }
            }
        }

        pool.shutdown()
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

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

                // left mouse click
                409 -> { // TODO(mlesniak) NCurses.Button_1 pressed
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
