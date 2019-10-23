#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX		= g++
LIBS		= -L./ -l64bitstatlogger
INCLUDES	= -I/usr/include/glib-2.0 -I/usr/lib/glib-2.0/include/ -I./
# 实测O3情况下，性能有20%的提升
FLAGS		=  -Wall -DDEBUG -g -MMD
SOURCE		= $(wildcard *.cpp)
OBJS		= $(patsubst %.cpp, %.o, $(SOURCE))
TARGET		= ./test

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) $(FLAGS) $(OBJS) -o $(TARGET) $(LIBS)

%.o: %.cpp Makefile
	$(XX) $(FLAGS) $(LIBS) $(INCLUDES) -c $< -o $@
	@-mv -f $*.d .dep.$@

clean:
	rm -rf *.o .dep.* $(TARGET)

#-include /dev/null $(wildcard .dep.*)
