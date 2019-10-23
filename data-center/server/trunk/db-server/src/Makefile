#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX			= g++
LIBS		= `mysql_config --libs` -lhiredis -ldl ../../stat-common/libstat-common.a
INCLUDES	= -I./ -I../../async_server/include/ -I../../stat-common/
FLAGS		= -std=c++0x -Wall -ggdb -fPIC -MMD -export-dynamic -O2 `mysql_config --cflags`
SOURCE		= $(wildcard *.cpp)
OBJS		= $(patsubst %.cpp, %.o, $(SOURCE))
TARGET		= ../bin/libdbserver.so

all: $(TARGET)

$(TARGET): $(OBJS) $(SUBOBJS)
	$(XX) -shared -o $(TARGET) $(OBJS) $(LIBS)
	rm -f *.o

%.o: %.cpp Makefile
	$(XX) $(FLAGS) $(INCLUDES) -c $< -o $@
	@-mv -f $*.d .dep.$@

-include /dev/null $(wildcard .dep.*)

#----------------------------------
# recursively make into folders co-
# ntaining source files for SUBOBJS
# (if any)
#----------------------------------

$(SUBOBJS):
	@make_all_sub_dir()\
        {\
                for dir in `ls`; do \
                        if [ -d $$dir ]; then \
                                cd $$dir;\
                                make_all_sub_dir;\
                                if [ -f makefile -o -f Makefile ]; then\
                                        make;\
                                fi;\
                                cd ..;\
                        fi;\
		done;\
	};\
        make_all_sub_dir

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
