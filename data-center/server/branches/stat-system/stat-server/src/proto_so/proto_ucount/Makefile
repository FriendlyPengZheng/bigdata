XX       = g++
CFLAGS	 = -Wall -ggdb -shared -fPIC 
INCLUDES = -I./ -I../../ -I../../../../stat-lib/
LIBS     = ../../../../stat-lib/libstat.a `mysql_config --libs`
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = ../../../bin/proto_so/proto_ucount.so

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) -shared -o $(TARGET) $(OBJS) $(LIBS)
	rm -rf *.o

%.o: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o $(TARGET)
	
c: clean

r: clean all

