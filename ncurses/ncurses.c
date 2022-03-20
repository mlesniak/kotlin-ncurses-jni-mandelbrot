/**
 * This file defines basic JNI wrapper functions to call ncurses library functions
 * to initialize (and terminate) curses handling and draw a single character at a
 * given position with a graytone color.
 */

#include <jni.h>

#include <ncurses.h>
#include <stdlib.h>

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_init(JNIEnv *env, jclass obj) {
    // Default ncurses handling.
    initscr();
    cbreak();
    noecho();
    curs_set(0);

    // React on mouse button handler in terminals.
    mousemask(BUTTON1_CLICKED, NULL);
    keypad(stdscr, TRUE);

    // Create a color palette from 0 (black) to 23 (white). These colors are then
    // used via COLOR_PAIR in an attron call.
    start_color();
    int startColor = 233;
    for (int i = startColor; i <= 255; i++) {
        init_pair(i - startColor + 1, i, 0);
    }
}


JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_lines(JNIEnv *env, jclass obj) {
    return LINES;
}


JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_cols(JNIEnv *env, jclass obj) {
    return COLS;
}


JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_endwin(JNIEnv *env, jclass obj) {
    endwin();
}


JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_getch(JNIEnv *env, jclass obj) {
    return getch();
}


JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_refresh(JNIEnv *env, jclass obj) {
    refresh();
}


JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_clear(JNIEnv *env, jclass obj) {
    clear();
}


JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_addch(JNIEnv *env, jclass obj, jint x, jint y, jchar c, jint color) {
    attron(COLOR_PAIR(color));
    mvaddch(y, x, c);
}


JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_timeout(JNIEnv *env, jclass obj, jint t) {
    timeout(t);
}


/**
 * If the event was a mouse event registered via mousemask, fills the passed object fields named
 * x and y with the corresponding positions of the mouse cursor and returns true.
 *
 * If the event was not a mouse event, doesn't touch the passed object and returns false.
 */
JNIEXPORT jboolean Java_com_mlesniak_main_NCurses_getevent(JNIEnv *env, jclass obj, jobject jobj) {
    MEVENT event;

    if (getmouse(&event) == OK) {
        // Retrieve class instance of passed object and set field values x and y. This is easier
        // than creating an instance in C and returning it.
        jclass cls = (*env)->GetObjectClass(env, jobj);

        jfieldID x = (*env)->GetFieldID(env, cls, "x", "I");
        (*env)->SetIntField(env, jobj, x, event.x);

        jfieldID y = (*env)->GetFieldID(env, cls, "y", "I");
        (*env)->SetIntField(env, jobj, y, event.y);

        return true;
    }

    return false;
}
