XX       = g++
CFLAGS	 = -Wall -Wno-non-virtual-dtor -ggdb -fPIC 
INCLUDES = -I../../ -I../../../../stat-lib/
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = ../../../bin/proto-so/proto_ustring.so

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) -shared -o $(TARGET) $(OBJS)
	rm -rf *.o

%.o: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o $(TARGET)
	
c: clean

r: clean all
