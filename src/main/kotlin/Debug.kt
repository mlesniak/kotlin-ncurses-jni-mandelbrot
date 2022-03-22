package com.mlesniak.main

import java.io.File

val debugStartTime = System.currentTimeMillis()

fun debug(msg: Any) {
    val s = msg.toString()
    val prefixNewlines = s.takeWhile { it == '\n' }
    val rest = s.drop(prefixNewlines.length)
    val timestamp =
        (System.currentTimeMillis() - debugStartTime).toString()
            .reversed()
            .chunked(3)
            .joinToString("_")
            .reversed()
    val ts = String.format("%10s", timestamp)

    File("debug.log").appendText("$prefixNewlines$ts: $rest\n")
}
