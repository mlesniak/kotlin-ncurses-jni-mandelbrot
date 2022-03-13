all:
	gcc -c -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin ncurses.c
	gcc -shared -lncurses -o native.so ncurses.o
	cp native.so ../target

clean:
	rm -f ncurses.o native.so

