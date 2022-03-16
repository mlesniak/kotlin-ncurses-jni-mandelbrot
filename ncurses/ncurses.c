#include <jni.h>

#include <ncurses.h>
#include <stdlib.h>

// TODO(mlesniak) Documentation
JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_init(JNIEnv *env, jclass obj) {
    initscr();
    cbreak();
    noecho();
    curs_set(0);

    mousemask(BUTTON1_CLICKED | BUTTON2_CLICKED, NULL);
    keypad(stdscr, TRUE);

    start_color();

    // 24 colors: 233 .. 255 in 1..23 and 0 for black and 24 for bright white.
    int startColor = 233;
    for (int i = startColor; i <= 255; i++) {
        init_pair(i - startColor + 1, i, 0);
    }
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

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_clear(JNIEnv *env, jclass obj) {
    clear();
}


JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_lines(JNIEnv *env, jclass obj) {
    return LINES;
}

JNIEXPORT jint JNICALL Java_com_mlesniak_main_NCurses_cols(JNIEnv *env, jclass obj) {
    return COLS;
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_addch(JNIEnv *env, jclass obj, jint x, jint y, jchar c, jint color) {
    attron(COLOR_PAIR(color));
    mvaddch(y, x, c);
}

JNIEXPORT void JNICALL Java_com_mlesniak_main_NCurses_timeout(JNIEnv *env, jclass obj, jint t) {
    timeout(t);
}

JNIEXPORT jboolean Java_com_mlesniak_main_NCurses_getevent(JNIEnv *env, jclass obj, jobject jobj) {
    MEVENT event;
    if (getmouse(&event) == OK) {
//        char s[80];
//        sprintf(s, "%d/%d", event.x, event.y);
//        mvaddstr(0,0, s);
//        refresh();
        // https://stackoverflow.com/questions/40004522/how-to-get-values-from-jobject-in-c-using-jni

        jclass cls = (*env)->GetObjectClass(env, jobj);
        jfieldID x = (*env)->GetFieldID(env, cls, "x", "I");
        (*env)->SetIntField(env, jobj, x, event.x);
        jfieldID y = (*env)->GetFieldID(env, cls, "y", "I");
        (*env)->SetIntField(env, jobj, y, event.y);
        return true;
    }
    return false;
}


