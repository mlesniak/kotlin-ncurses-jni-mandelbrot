# Overview

## Build

    mvn clean package

## C Commands

    gcc -c -I$JAVA_HOME/include -I$JAVA_HOME/include/darwin demo.c
    gcc -shared -o native.so demo.o

