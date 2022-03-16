package com.mlesniak.main

/**
 * // TODO(mlesniak) JNI, specialized for this use case, not general ncruses interface
 */
class NCurses {
    companion object {
        @JvmStatic
        external fun init()

        @JvmStatic
        external fun timeout(t: Int)

        @JvmStatic
        external fun addch(x: Int, y: Int, c: Char, color: Int = 25)

        @JvmStatic
        external fun getch(): Int

        @JvmStatic
        external fun endwin()

        @JvmStatic
        external fun clear()

        @JvmStatic
        external fun lines(): Int

        @JvmStatic
        external fun cols(): Int

        @JvmStatic
        external fun refresh(): Int

        @JvmStatic
        external fun getevent(p: Pos): Boolean
    }
}
