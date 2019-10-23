CXX=g++
INCLUDES = -I../../../stat-lib/
CFLAGS=-g -ggdb -Wall -Wno-non-virtual-dtor
SOURCES=client_thread_impl.cpp

.PHONY : all
	

all : $(SOURCES)
	$(CXX) -c $(CFLAGS) $(INCLUDES) $(SOURCES)

clean : 
	rm -rf *.o
