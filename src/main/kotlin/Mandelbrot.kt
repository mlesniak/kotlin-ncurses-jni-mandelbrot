package com.mlesniak.main

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class Mandelbrot {
    // TODO(mlesniak) better name?
    data class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double)

    data class Configuration(
        val width: Int,
        val height: Int,
        val maxIterations: Int
    )

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
}

// TODO(mlesniak) split into own class and implement ncurses interface
class AsciiRenderer {
    // synchronized(lock) {
    //     for (x in 0 until width) {
    //         val c = asciiChar(maxIteration, values[x])
    //         val col = color(maxIteration, values[x])
    //         addch(x, y, c, col)
    //     }
    //     refresh()
    // }

}