package com.mlesniak.main

fun main() {
    System.load("/Users/m/Documents/kotlin-jni/target/native.so")
    NCurses.init()
    val res = NCurses.getch()
    NCurses.endwin()

    println(res)
}
