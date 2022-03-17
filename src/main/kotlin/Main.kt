package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.addch
import com.mlesniak.main.NCurses.Companion.clear
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import java.io.File
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.random.Random

data class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double)
data class Pos(val x: Int = 0, val y: Int = 0)

// TODO(mlesniak) Parallelization on rows via TaskPools?
// TODO(mlesniak) Add LRU cache for zoombing back
// TODO(mlesniak) Refactoring and clean up
// TODO(mlesniak) Higher precision using BigDecimal?
fun main() {
    System.loadLibrary("native")
    init()

    var zoom = Rect(-2.0, 1.2, 1.0, -1.2)
    val maxIteration = 256

    val width = cols()
    val height = lines()
    val zooms = mutableListOf<Rect>()

    debug("\n\n\n${Date()}")

    renderLoop@ while (true) {
        val w = (zoom.x2 - zoom.x1).absoluteValue
        val h = (zoom.y2 - zoom.y1).absoluteValue
        val wStep = w / width
        val hStep = h / height

        val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        // val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        val lock = Object()

        clear()
        val m = ConcurrentHashMap<Int, Array<Int>>()
        for (y in 0 until height) {
            pool.submit {
                var i = 0
                val mx = Random.nextLong(100, 1000) * 10000
                while (i < mx) {
                    i++
                }
                // Thread.sleep(Random.nextLong(100, 1000))
                debug("staring row $y with $i")
                val y1 = zoom.y1 - y * hStep
                val values = Array<Int>(width) { 0 }
                for (x in 0 until width) {
                    val x1 = x * wStep + zoom.x1
                    val iterations = checkIteration(x1, y1, maxIteration)
                    values[x] = iterations
                }
                // Thread.sleep(Random.nextLong(100, 1000))

                m[y] = values
                // synchronized(lock) {
                //     debug("drawing row $y:")
                //     for (x in 0 until width) {
                //         val c = asciiChar(maxIteration, values[x])
                //         val col = color(maxIteration, values[x])
                //         addch(x, y, c, col)
                //     }
                //     refresh()
                // }
                debug("drawing row $y -- finished")
            }
        }
        pool.shutdown()
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

        for (y in 0 until height) {
            val values = m[y]!!
            debug("drawing row $y:")
            for (x in 0 until width) {
                val c = asciiChar(maxIteration, values[x])
                val col = color(maxIteration, values[x])
                addch(x, y, c, col)
            }
        }

        while (true) {
            val ch = getch()
            when (ch) {
                'q'.code -> break@renderLoop

                'z'.code -> {
                    if (zooms.isNotEmpty()) {
                        zoom = zooms.last()
                        zooms.removeLast()
                        break
                    }
                }

                // left mouse click
                409 -> {
                    val p = Pos()
                    if (!NCurses.getevent(p)) {
                        // Ignore other events.
                        continue
                    }
                    val cx = p.x * wStep + zoom.x1
                    val cy = zoom.y1 - p.y * hStep

                    // TODO(mlesniak) zoom buggy
                    zooms += zoom
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
    File("out").appendText("$msg\n")
}
