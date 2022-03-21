package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.clear

/**
 * Renderer which uses NCurses (provided via JNI bindings) to render a viewport
 * of the Mandelbrot set to the terminal.
 */
class AsciiRenderer {
    companion object {
        fun render(config: Configuration, iterationImage: Image) = with(config) {
            clear()
            for (y in 0 until height) {
                val values = iterationImage[y]!!
                for (x in 0 until width) {
                    val c = asciiChar(maxIterations, values[x])
                    val col = color(maxIterations, values[x])
                    NCurses.addch(x, y, c, col)
                }

                // Allows for a bit more interactivity since we don't wait until
                // the complete picture has been rendered.
                NCurses.refresh()
            }
        }

        private fun color(maxIteration: Int, iter: Int): Int {
            val colorStretchFactor = 20
            val maxColors = 24
            val colors = (1..maxColors).toList() + List(colorStretchFactor) { 24 }
            return colors[((colors.size - 1).toDouble() / maxIteration * iter).toInt()]
        }

        private fun asciiChar(maxIteration: Int, iter: Int): Char {
            val density = " .,-=+:;cba!?0123456789\$W#@Ñ"
            val index = ((density.length - 1).toDouble() / maxIteration * iter).toInt()
            return density[index]
        }
    }
}
