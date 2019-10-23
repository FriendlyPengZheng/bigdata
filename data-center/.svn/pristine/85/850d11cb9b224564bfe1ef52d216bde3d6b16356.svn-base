#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX			= gcc
LIBS		= -ldl ./libstatlogger.so
#INCLUDES	= -I/usr/include/glib-2.0 -I/usr/lib/glib-2.0/include/ -I../
INCLUDES	= -I./ -I../async_server/include/ -I../stat-logger/
FLAGS		=  -Wall -DDEBUG -g -fPIC -MMD
#FLAGS		= -std=c++0x -Wall -DDEBUG -g -shared -fPIC -MMD
SOURCE		= $(wildcard *.c)
OBJS		= $(patsubst %.c, %.o, $(SOURCE))
TARGET		= ./test

all: $(TARGET)
%.o: %.c Makefile
	$(XX) $(FLAGS) $(INCLUDES) -c $< -o $@
	@-mv -f $*.d .dep.$@

$(TARGET): $(OBJS)
	#$(AR) rcs $@ $^
	$(XX) $(FLAGS) $(OBJS) -o $(TARGET) $(LIBS)
	rm -f *.o


clean:
	rm -rf *.o .dep.* $(TARGET)

#-include /dev/null $(wildcard .dep.*)
