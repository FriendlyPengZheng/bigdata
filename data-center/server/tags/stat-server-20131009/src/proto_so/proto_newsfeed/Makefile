XX       = g++
CFLAGS	= -Wall -Wno-non-virtual-dtor -O -ggdb -fPIC -shared -export-dynamic
LIBS	= -ldl ../../../../stat-lib/libstat.a
INCLUDES = -I. -I../../ -I../../../../stat-lib/
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET	= proto_newsfeed.so

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) $(CFLAGS) $(OBJS) -o $(TARGET)  $(LIBS) $(INCLUDES)
	mv $(TARGET) ../../../bin/proto_so/
	
%.o: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o $(TARGET)
	rm -rf ../../../bin/proto_so/$(TARGET)
	
c: clean

r: clean all


