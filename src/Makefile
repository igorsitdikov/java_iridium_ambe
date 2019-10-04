CC=gcc
CFLAGS=-g  -Wall -DPIC -fPIC -O2 -march=native
LDLIBS=-lm -lpthread

CHK_LIBOSMOCORE_RV=$(shell pkg-config --exists libosmocore; echo $$?)
ifeq ($(CHK_LIBOSMOCORE_RV),0)
CFLAGS+=`pkg-config libosmocore --cflags` -DHAVE_LIBOSMOCORE
LDLIBS+=`pkg-config libosmocore --libs`
endif

BUILD_DIR = $(PWD)
JAVA_HOME = /usr/lib/jvm/java-11-oracle/include
DEST_DIR = /usr/lib
#DEST_DIR = /usr/lib/x86_64-linux-gnu

#COPY_FILES = $(DEST_DIR)/liblib.so $(DEST_DIR)/libiridium.so
COPY_FILES = $(DEST_DIR)/libiridium.so

OBJS=iridium_wrap.cpp mbelib.o tone.o tables.o synth.o math.o frame.o ecc.o ambe.o libambe.a libiridium.so

#all: $(OBJS) $(COPY_FILES)
all: $(OBJS)

copy: $(COPY_FILES)

$(DEST_DIR)/%.so: $(BUILD_DIR)/%.so
	sudo cp -f $< $@

libiridium.so: iridium.cpp iridium_wrap.cpp mbelib.h libambe.a
	g++ -std=c++11 $(CFLAGS) -shared -I$(JAVA_HOME) -I$(JAVA_HOME)/linux  -o $@ $^ -L. -lambe $(LDLIBS)

libambe.so: mbelib.c ambe.c ecc.c frame.c math.c synth.c tables.c tone.c
	$(CC) $(CFLAGS) -shared -o $@ $^ $(LDLIBS)

libambe.a: frame.o math.o synth.o ecc.o ambe.o tone.o tables.o mbelib.o
	ar -cvq $@ $^

iridium_wrap.cpp: iridium.i
	swig -java -package l2m.lissen2me -c++ -o $@ $^

clean:
	rm -f *.o *.so *.a *.wav libJNI.java lib.java iridium.java $(OBJS)
