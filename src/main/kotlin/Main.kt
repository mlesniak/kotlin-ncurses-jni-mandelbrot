package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines
import com.mlesniak.main.NCurses.Companion.timeout

fun main() {
    System.loadLibrary("native")

    init()
    timeout(2000)
    NCurses.addch(10, 10, '*')
    val res = getch()
    endwin()

    println(res)
    println(lines())
}
