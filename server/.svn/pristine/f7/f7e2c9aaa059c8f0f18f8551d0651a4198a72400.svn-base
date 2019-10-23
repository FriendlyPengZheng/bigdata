XX       = g++
CFLAGS   = -Wall -Wno-non-virtual-dtor -O -ggdb `mysql_config --cflags` -fPIC -shared
LIBS     = -ldl ../../stat-lib/libstat.a  `mysql_config --libs`
TARGET   = ../bin/stat_mysql.so
INCLUDES = -I. -I../../stat-lib/
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES)) 
SUBOBJS  = 
		   

all: $(TARGET)

$(TARGET) : $(OBJS) $(SUBOBJS)
	$(XX) $(CFLAGS) $(OBJS) $(SUBOBJS) -o $(TARGET) $(LIBS)
	rm -rf *.o
	
%.o: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) -c $< -o $@	
	
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
	rm -rf *.o $(TARGET)
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
