CC = g++
FLAGS = -Wall -MMD
INCLUDE = -I$(PATH) -I.
#LIBS = -ltaomee -ltaomee++
LIBS = -lfcgi
BINS = ../bin/misc_server

#SOURCES = $(wildcard *.cpp, *.c)
SOURCES = $(wildcard *.cpp)
OBJECTS = $(patsubst %.cpp, %.o, $(SOURCES))

all: dev

dev: FLAGS += -ggdb -DENABLE_TRACE_LOG
rel: FLAGS += -02

dev rel: $(OBJECTS)
	$(CC) $(FLAGS) $(LIBS) $(OBJECTS) -o $(BINS)
	@-rm -f $(OBJECTS)


%.o: %.cpp
	$(CC) $(FLAGS) $(INCLUDE) -c $< -o $@
	@-rm -f $*.d

.PHONY: clean all

clean:
	@-rm -f *.o *.d $(BINS) ../core*

c: clean

rebuild: clean all

r: rebuild
