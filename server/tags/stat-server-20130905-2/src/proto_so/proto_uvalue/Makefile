XX       = g++
CFLAGS	 = -Wall -Wno-non-virtual-dtor -O -ggdb -fPIC -shared -export-dynamic `mysql_config --cflags` 
LIBS	 = -lpthread -ldl ../../../../stat-lib/libstat.a `mysql_config --libs`
INCLUDES = -I. -I../../ -I../../../../stat-lib/ 
SOURCES  = $(wildcard *.cpp) $(wildcard  ../../data-persistent/*.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET	 = proto_uvalue.so

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

