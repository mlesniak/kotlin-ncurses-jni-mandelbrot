package com.mlesniak.main

import java.io.File

fun debug(msg: Any) {
    File("debug.log").appendText("$msg\n")
}
