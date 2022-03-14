#include <jni.h>

#include <ncurses.h>
#include <stdlib.h>

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_init(JNIEnv *env, jclass obj) {
    initscr();
    cbreak();
    noecho();
    curs_set(0);
}

JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_getch(JNIEnv *env, jclass obj) {
    return getch();
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_endwin(JNIEnv *env, jclass obj) {
    endwin();
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_refresh(JNIEnv *env, jclass obj) {
    refresh();
}

JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_lines(JNIEnv *env, jclass obj) {
    return LINES;
}

JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_cols(JNIEnv *env, jclass obj) {
    return COLS;
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_addch(JNIEnv *env, jclass obj, jint x, jint y, jchar c) {
    mvaddch(y, x, c);
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_timeout(JNIEnv *env, jclass obj, jint t) {
    timeout(t);
}

