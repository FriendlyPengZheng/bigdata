XX       = g++
CFLAGS   = -Wall -O -ggdb -Wno-non-virtual-dtor
LIBS     = -lpthread -ltaomee
INCLUDES = -I../../../stat-lib/
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
