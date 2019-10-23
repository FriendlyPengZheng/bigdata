#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX			= g++
LIBS		= `mysql_config --libs` -lprotobuf -L/usr/local/lib -ldl ../../stat-common/libstat-common.a
INCLUDES	= -I./ -I../../async_server/include/ -I./-L/usr/local/lib -I../../stat-common/
FLAGS		= -std=c++0x -Wall -Wnon-virtual-dtor -DNDEBUG -O2 -ggdb -fPIC -MMD `mysql_config --cflags`
SOURCE		= $(wildcard *.cpp)
OBJS		= $(patsubst %.cpp, %.o, $(SOURCE)) $(patsubst %.cc, %.o, $(wildcard *.cc))
TARGET		= ../bin/libsdkonline.so

all: $(TARGET)

$(TARGET): $(OBJS)
	$(XX) -shared -o $(TARGET) $(OBJS) $(LIBS)
	rm -f *.o

%.o: %.c* Makefile
	$(XX) $(FLAGS) $(INCLUDES) -c $< -o $@
	@-mv -f $*.d .dep.$@

-include /dev/null $(wildcard .dep.*)

clean:
	rm -rf *.o .dep.* $(TARGET)
	@make_clean_sub_dir()\
        {\
                for dir in `ls`; do \
                        if [ -d $$dir ]; then \
                                cd $$dir;\
                                make_clean_sub_dir;\
                                if [ -f makefile -o -f Makefile ]; then\
                                        make clean;\
                                fi;\
                                cd ..;\
                        fi;\
                done;\
        };\
        make_clean_sub_dir

rebuild: clean all

c: clean

r: rebuild
