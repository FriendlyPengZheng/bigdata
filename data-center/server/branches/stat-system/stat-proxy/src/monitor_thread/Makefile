XX       = g++
CFLAGS   = -Wall -O -ggdb -Wno-non-virtual-dtor
LIBS     = -lpthread -ltaomee -lgsoap++
INCLUDES = -I../../../stat-lib/
SOURCES  = monitor_thread.cpp soapC.cpp soapServer.cpp
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = $(OBJS)

all: $(TARGET)

soapC.cpp soapServer.cpp: monitor.h
	soapcpp2 -S -x -w -L ./monitor.h

$(OBJS): $(SOURCES)
	$(XX) $(CFLAGS) $(INCLUDES) -c monitor_thread.cpp -o monitor_thread.o
	$(XX) $(CFLAGS) $(INCLUDES) -c soapServer.cpp -o soapServer.o
	$(XX) $(CFLAGS) $(INCLUDES) -c soapC.cpp -o soapC.o

clean:
	rm -f *.o soap* 
	
c: clean

r: clean all
