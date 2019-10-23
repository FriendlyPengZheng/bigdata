CXX=g++
INCLUDES = -I../../../stat-lib/
CFLAGS=-g -ggdb -Wall -Wno-non-virtual-dtor
SOURCES=c_client_ip_filter.cpp c_client_ip_filter_facade.cpp

.PHONY : all
	

all : $(SOURCES)
	$(CXX) -c $(CFLAGS) $(INCLUDES) $(SOURCES)

clean : 
	rm -rf *.o
