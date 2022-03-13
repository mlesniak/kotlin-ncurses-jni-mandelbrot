package com.mlesniak.main

class NCurses {
    companion object {
        @JvmStatic
        external fun init()
        @JvmStatic
        external fun getch(): Int
        @JvmStatic
        external fun endwin()
    }
}