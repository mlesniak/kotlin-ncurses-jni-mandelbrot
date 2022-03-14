package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.addch
import com.mlesniak.main.NCurses.Companion.clear
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import com.mlesniak.main.NCurses.Companion.timeout
import java.io.File
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.random.nextInt

data class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double)
data class Pos(val x: Int = 0, val y: Int = 0)

fun main() {
    System.loadLibrary("native")
    init()

    var zoom = Rect(-2.0, 1.2, 1.0, -1.2)
    val maxIteration = 128

    val width = cols()
    val height = lines()

    while (true) {
        val w = (zoom.x2 - zoom.x1).absoluteValue
        val h = (zoom.y2 - zoom.y1).absoluteValue
        val wStep = w / width
        val hStep = h / height

        for (y in 0 until height) {
            val y1 = zoom.y1 - y * hStep
            for (x in 0 until width) {
                val x1 = x * wStep + zoom.x1

                val iterations = checkIteration(x1, y1, maxIteration)
                val c = asciiChar(maxIteration, iterations)
                addch(x, y, c)
            }
        }

        val ch = getch()
        when (ch) {
            'q'.code -> break
            'z'.code -> {
                zoom = zoom.copy(
                    x1 = zoom.x1 / 2,
                    y1 = zoom.y1 / 2,
                    x2 = zoom.x2 / 2,
                    y2 = zoom.y2 / 2,
                )
                clear()
            }
            409 -> {
                val p = Pos()
                NCurses.getevent(p)
                File("out").appendText("${p}\n")
            }
        }
    }

    endwin()
}

fun asciiChar(maxIteration: Int, iter: Int): Char {
    // val density = "Ñ@#W$9876543210?!abc;:+=-,._ ".reversed()
    val density = "Ñ@#W$9876543210?!abc;:+=-,. ".reversed()

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

private fun ncursesDemo() {
    fun randomChar(c: Char) {
        val x = Random.nextInt(0..cols())
        val y = Random.nextInt(0..lines())
        addch(x, y, c)
    }

    System.loadLibrary("native")

    init()
    timeout(1)
    while (true) {
        // refresh()

        randomChar('*')
        randomChar('.')
        repeat(50) {
            randomChar(' ')
        }

        val res = getch()
        if (res != -1) {
            break
        }
    }

    endwin()
}
