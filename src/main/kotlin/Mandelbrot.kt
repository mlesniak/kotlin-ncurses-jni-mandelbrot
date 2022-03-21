package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.addch
import com.mlesniak.main.NCurses.Companion.refresh
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

data class Configuration(
    val width: Int,
    val height: Int,
    val maxIterations: Int
)

class Mandelbrot {

    // TODO(mlesniak) better name?
    data class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double)

    companion object {
        fun compute(config: Configuration, rect: Rect): Map<Int, Array<Int>> {
            val image = ConcurrentHashMap<Int, Array<Int>>()

            val wStep = (rect.x2 - rect.x1).absoluteValue / config.width
            val hStep = (rect.y2 - rect.y1).absoluteValue / config.height

            val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

            for (y in 0 until config.height) {
                pool.submit {
                    val values = Array(config.width) { 0 }
                    val y1 = rect.y1 - y * hStep
                    for (x in 0 until config.width) {
                        val x1 = x * wStep + rect.x1
                        val iterations = checkIteration(x1, y1, config.maxIterations)
                        values[x] = iterations
                    }

                    image[y] = values
                }
            }

            pool.shutdown()
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)

            return image
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
    }
}

// TODO(mlesniak) split into own class and implement ncurses interface
class AsciiRenderer {
    companion object {
        fun render(config: Configuration, iterationImage: Map<Int, Array<Int>>) = with(config) {
            for (y in 0 until height) {
                val values = iterationImage[y]!!
                for (x in 0 until width) {
                    val c = asciiChar(maxIterations, values[x])
                    val col = color(maxIterations, values[x])
                    addch(x, y, c, col)
                }
                refresh()
            }
        }

        // synchronized(lock) {
        //     for (x in 0 until width) {
        //         val c = asciiChar(maxIteration, values[x])
        //         val col = color(maxIteration, values[x])
        //         addch(x, y, c, col)
        //     }
        //     refresh()
        // }
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
    }
}
