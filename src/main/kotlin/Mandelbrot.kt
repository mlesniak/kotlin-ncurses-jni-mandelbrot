package com.mlesniak.main

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.absoluteValue

data class Viewport(val x1: Double, val y1: Double, val x2: Double, val y2: Double)

typealias Image = Map<Int, Array<Int>>

/**
 * Implements a mandelbrot computation parallelizing the computation by rows.
 */
class Mandelbrot {
    companion object {
        fun compute(config: Configuration, viewport: Viewport): Image {
            val wStep = (viewport.x2 - viewport.x1).absoluteValue / config.width
            val hStep = (viewport.y2 - viewport.y1).absoluteValue / config.height
            val image = ConcurrentHashMap<Int, Array<Int>>()

            (0 until config.height).toList().parallelStream().forEach { pixelRow ->
                val values = Array(config.width) { 0 }
                val y1 = viewport.y1 - pixelRow * hStep
                for (pixelCol in 0 until config.width) {
                    val x1 = pixelCol * wStep + viewport.x1
                    val iterations = checkIteration(x1, y1, config.maxIterations)
                    values[pixelCol] = iterations
                }
                image[pixelRow] = values
            }

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
            // https://en.wikipedia.org/wiki/Mandelbrot_set#Computer_drawings.
            //
            // We compute the real and imaginary parts on the complex plane
            // separately.
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
