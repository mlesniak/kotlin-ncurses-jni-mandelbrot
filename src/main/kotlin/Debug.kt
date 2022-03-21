package com.mlesniak.main

import java.io.File

val debugStartTime = System.currentTimeMillis()

fun debug(msg: Any) {
    val timestamp = String.format("%-10d", System.currentTimeMillis() - debugStartTime)
    File("debug.log").appendText("$timestamp $msg\n")
}
