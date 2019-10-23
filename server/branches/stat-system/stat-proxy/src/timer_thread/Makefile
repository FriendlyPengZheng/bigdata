XX       = g++
INCLUDES = -I../../../stat-lib/
CFLAGS   = -Wall -O -g -Wno-non-virtual-dtor
LIBS     = -L../../../stat-lib/ -lstat -lpthread -ltaomee
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = $(OBJS)

all: $(TARGET)
	
%.o: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o
	
c: clean

r: clean all
