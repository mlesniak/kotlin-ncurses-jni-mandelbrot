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
import kotlin.math.absoluteValue

data class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double)
data class Pos(val x: Int = 0, val y: Int = 0)

// TODO(mlesniak) Refactoring and clean up
// TODO(mlesniak) Parallelization on rows via TaskPools?
// TODO(mlesniak) Higher precision using BigDecimal?
fun main() {
    System.loadLibrary("native")
    init()
    debug("\n\n${Date()}")

    var lastRenderTime = System.currentTimeMillis()

    var zoom = Rect(-2.0, 1.2, 1.0, -1.2)
    val maxIteration = 256

    val width = cols()
    val height = lines()
    var lastClick = -1L

    renderLoop@ while (true) {
        val w = (zoom.x2 - zoom.x1).absoluteValue
        val h = (zoom.y2 - zoom.y1).absoluteValue
        val wStep = w / width
        val hStep = h / height

        debug("Last render was ${System.currentTimeMillis() - lastRenderTime}")
        lastRenderTime = System.currentTimeMillis()

        clear()
        for (y in 0 until height) {
            val y1 = zoom.y1 - y * hStep
            for (x in 0 until width) {
                val x1 = x * wStep + zoom.x1

                val iterations = checkIteration(x1, y1, maxIteration)
                val c = asciiChar(maxIteration, iterations)
                val col = color(maxIteration, iterations)
                addch(x, y, c, col)
            }
        }

        while (true) {
            val ch = getch()
            when (ch) {
                'q'.code -> break@renderLoop

                // left mouse click
                409 -> {
                    if (System.currentTimeMillis() - lastClick < 200) {
                        continue
                    }
                    val delta = System.currentTimeMillis() - lastClick
                    lastClick = System.currentTimeMillis()

                    val p = Pos()
                    NCurses.getevent(p)
                    val cx = p.x * wStep + zoom.x1
                    val cy = zoom.y1 - p.y * hStep

                    zoom = zoom.copy(
                        x1 = cx - (zoom.x2 - zoom.x1) / 4.0,
                        x2 = cx + (zoom.x2 - zoom.x1) / 4.0,
                        y1 = cy - (zoom.y2 - zoom.y1) / 4.0,
                        y2 = cy + (zoom.y2 - zoom.y1) / 4.0,
                    )
                    debug("zoom=$zoom -- $delta")
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
