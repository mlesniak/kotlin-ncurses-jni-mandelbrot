package com.mlesniak.main

import com.mlesniak.main.NCurses.Companion.endwin
import com.mlesniak.main.NCurses.Companion.getch
import com.mlesniak.main.NCurses.Companion.init
import com.mlesniak.main.NCurses.Companion.lines

fun main() {
    System.load("/Users/m/Documents/kotlin-jni/target/native.so")
    init()
    NCurses.addch(10, 10, '*')
    val res = getch()
    endwin()

    println(res)
    println(lines())
}
