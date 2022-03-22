package com.mlesniak.main

import java.io.File

val debugStartTime = System.currentTimeMillis()

/**
 * Simple debug method to log the string representation of anything which is passed
 * with an according timestamp showing milliseconds since application startup.
 *
 * If the string represenation is prefixed with newlines, these are printed before
 * the timestamp.
 *
 * Note that this method is not very fast and should not be used for time-critical
 * logging purposes.
 */
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
