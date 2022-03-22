package com.mlesniak.main

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

data class Viewport(val x1: Double, val y1: Double, val x2: Double, val y2: Double)

typealias Image = Map<Int, Array<Int>>

/**
 * Implements a parallelized mandelbrot computation parallelizing the computation
 * by rows of the Viewport.
 */
class Mandelbrot {
    companion object {
        fun compute(config: Configuration, viewport: Viewport): Image {
            val wStep = (viewport.x2 - viewport.x1).absoluteValue / config.width
            val hStep = (viewport.y2 - viewport.y1).absoluteValue / config.height
            // TODO(mlesniak) Use parallel stream instead?
            val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
            val image = ConcurrentHashMap<Int, Array<Int>>()

            for (y in 0 until config.height) {
                pool.submit {
                    val values = Array(config.width) { 0 }
                    val y1 = viewport.y1 - y * hStep
                    for (x in 0 until config.width) {
                        val x1 = x * wStep + viewport.x1
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

        // Implementation remark: In an experiment, we tried to use BigDecimals
        // for higher precision, but even with parallelization, computational
        // runtime became so high that waiting for it to finish made no fun.
        private fun checkIteration(x0: Double, y0: Double, maxIteration: Int): Int {
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
