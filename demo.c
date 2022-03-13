#include <jni.h>
#include <stdlib.h>

JNIEXPORT void JNICALL Java_com_mlesniak_main_MainKt_foo (JNIEnv *env, jobject obj, jint value) {
    printf("%d\n", value*2); 
}

