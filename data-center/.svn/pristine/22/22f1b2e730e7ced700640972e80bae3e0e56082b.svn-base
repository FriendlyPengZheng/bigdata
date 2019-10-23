#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX			= g++
LIBS		= -L./ -lant
INCLUDES	= -I/usr/include/glib-2.0 -I/usr/lib/glib-2.0/include/ -I../
FLAGS		= -std=c++0x -Wall -DDEBUG -g -shared -fPIC
SOURCE		= $(wildcard *.cpp)
OBJS		= $(patsubst %.cpp, %.o, $(SOURCE))
TARGET		= ../bin/writer.so

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) $(FLAGS) $(OBJS) -o $(TARGET) $(LIBS)

%.o: %.cpp
	$(XX) $(FLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o $(TARGET)
