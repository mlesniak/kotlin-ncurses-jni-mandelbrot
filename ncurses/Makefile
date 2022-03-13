all:
	gcc -c -I${JAVA_HOME}/include -I${JAVA_HOME}/include/darwin ncurses.c
	# Works only for MacOS
	gcc -shared -lncurses -o libnative.dylib ncurses.o
	cp libnative.dylib ../target

clean:
	rm -f ncurses.o libnative.dylib

