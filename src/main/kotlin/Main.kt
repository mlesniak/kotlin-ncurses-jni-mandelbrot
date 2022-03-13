package com.mlesniak.main

fun main() {
    System.load("/Users/m/Documents/kotlin-jni/native.so")
    println("Hello World!")
    foo(20)
}

external fun foo(n: Int)