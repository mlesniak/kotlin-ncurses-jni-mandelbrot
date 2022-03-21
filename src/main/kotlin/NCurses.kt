package com.mlesniak.main

/**
 * This class defines basic ncurses functions to be used in combination with
 * the JNI wrapper defined in ncurses/ncurses.c and contains methods for basic
 * graphical terminal functions.
 *
 * The names mirror the names of ncurses, i.e. can be found via man curses,
 * man curs_color, etc.
 */
class NCurses {
    companion object {
        // Value returned by getch for a mouse click.
        const val MOUSE_CLICK = 409

        data class MousePosition(val x: Int = 0, val y: Int = 0)

        fun initNCurses() {
            System.loadLibrary("native")
            init()
        }

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
        external fun getevent(p: MousePosition): Boolean
    }
}
