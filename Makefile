CC=gcc
CFLAGS=-g  -Wall -DPIC -fPIC -O2 -march=native
LDLIBS=-lm -lpthread

CHK_LIBOSMOCORE_RV=$(shell pkg-config --exists libosmocore; echo $$?)
ifeq ($(CHK_LIBOSMOCORE_RV),0)
CFLAGS+=`pkg-config libosmocore --cflags` -DHAVE_LIBOSMOCORE
LDLIBS+=`pkg-config libosmocore --libs`
endif

LIB_NAME := libiridium

ifeq ($(OS),Windows_NT)
	TARGET := $(LIB_NAME).dll
	JAVA_OS := win32
	ifeq ($(PROCESSOR_ARCHITECTURE),AMD64)
		OS_PATH := amd64/Windows
	endif
else
    TARGET := $(LIB_NAME).so
	JAVA_OS := linux
	UNAME_S := $(shell uname -s)
	UNAME_P := $(shell uname -p)
	ifeq ($(UNAME_S),Linux)
		ifeq ($(UNAME_P),x86_64)
		    OS_PATH := amd64/Linux
    	endif
	endif
endif

JAVA_HOME = /usr/lib/jvm/java-1.8.0-openjdk-amd64

OUTPUT_PATH := src/main/resources/NATIVE/$(OS_PATH)/

SOURCES = \
	native/ambe.c \
	native/ecc.c \
	native/frame.c \
	native/mbelib.c \
	native/math.c \
	native/synth.c \
	native/tables.c \
	native/tone.c

all: $(TARGET) copy

copy:
	mkdir -p $(OUTPUT_PATH)
	strip -g $(TARGET)
	cp $(TARGET) $(OUTPUT_PATH)
	chmod +x copy_dependencies.sh
	./copy_dependencies.sh $(TARGET) $(OUTPUT_PATH)

$(TARGET): native/iridium.cpp iridium_wrap.cpp native/mbelib.h libambe.a
	g++ -std=c++11 $(CFLAGS) -shared -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/$(JAVA_OS)  -o $@ $^ -L. -lambe $(LDLIBS)

libambe.a: $(SOURCES:.c=.o)
	ar -cvq $@ $^

iridium_wrap.cpp: native/iridium.i
	mkdir -p src/main/java/l2m/decoder/iridium
	swig -c++ \
		-java -package l2m.decoder.iridium \
		-outdir src/main/java/l2m/decoder/iridium -o $@ $<

mvn:
	mvn clean install

clean:
	$(RM) -f native/*.o *.so *.a *_wrap.cpp *.wav $(OBJS)
