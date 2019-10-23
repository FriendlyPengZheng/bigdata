CC         = gcc
XX         = g++
CFLAGS     = -Wall -fPIC  -static
CXXFLAGS   = -Wall -fPIC  -Wno-non-virtual-dtor -static
LIBS       = `mysql_config --libs` -ldl -L/usr/local/BerkeleyDB.5.1/lib/ -ldb
INCLUDES   = -I./ -I/usr/local/BerkeleyDB.5.1/include/
CSOURCES   = $(wildcard *.c)
CXXSOURCES = $(wildcard */*.cpp)
COBJS      = $(patsubst %.c,%.o, $(CSOURCES))
CXXOBJS    = $(patsubst %.cpp,%.o, $(CXXSOURCES))
SONAME	   = libstat.so.1
LINKNAME   = libstat.so
TARGET     = libstat.so.1.5.0

all: $(TARGET)

$(TARGET): $(COBJS) $(CXXOBJS)
	$(XX) -shared -Wl,-soname,$(SONAME) -o $(TARGET) $(COBJS) $(CXXOBJS) $(LIBS) 
	/sbin/ldconfig -n .
	ln -sf $(SONAME) $(LINKNAME)
	ar -rs libstat.a $(COBJS) $(CXXOBJS)

%.o: %.cpp
	$(XX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

%.o: %.c
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o */*.o $(TARGET)
	rm -rf libstat.*
	
c: clean

r: clean all

