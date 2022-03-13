package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.addch
import com.mlesniak.main.NCurses.Companion.cols
import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import com.mlesniak.main.NCurses.Companion.timeout
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    System.loadLibrary("native")

    init()
    timeout(1)
    while (true) {
        randomChar('*')
        randomChar('.')
        repeat(10) {
            randomChar(' ')
        }

        val res = getch()
        if (res != -1) {
            break
        }
    }

    endwin()
}

private fun randomChar(c: Char) {
    val x = Random.nextInt(0..cols())
    val y = Random.nextInt(0..lines())
    addch(x, y, c)
}
