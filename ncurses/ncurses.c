#include <jni.h>

#include <ncurses.h>
#include <stdlib.h>

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_init(JNIEnv *env, jclass obj) {
    initscr();
    cbreak();
    noecho();
}

JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_getch(JNIEnv *env, jclass obj) {
    return getch();
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_endwin(JNIEnv *env, jclass obj) {
    endwin();
}
