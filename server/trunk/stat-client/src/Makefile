#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX			= g++
LIBS		= -L. -L ../../stat-common/ -lstat-common
INCLUDES	= -I. -I ../../stat-common
FLAGS		= -std=c++0x -Wall -Wnon-virtual-dtor -DNDEBUG -O2 -ggdb -shared -fPIC -MMD
#FLAGS		= -std=c++0x -Wall -DNDEBUG -O2 -shared -fPIC -MMD
SOURCE		= $(wildcard *.cpp)
OBJS		= $(patsubst %.cpp, %.o, $(SOURCE))
TARGET		= ../bin/stat-client.so

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) $(FLAGS) $(OBJS) -o $(TARGET) $(LIBS)

%.o: %.cpp Makefile
	$(XX) $(FLAGS) $(INCLUDES) -c $< -o $@
	@-mv -f $*.d .dep.$@

clean:
	rm -rf *.o .dep.* $(TARGET)

c: clean

-include /dev/null $(wildcard .dep.*)
