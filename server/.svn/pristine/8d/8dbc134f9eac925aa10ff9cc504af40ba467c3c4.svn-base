XX       = g++
AR       = ar rcs 
#NOATIME  = -DSUPPORT_NOATIME
NOATIME  = 
CFLAGS   = -DNDEBUG $(NOATIME) -std=c++0x -Wall -Wnon-virtual-dtor -O2 -ggdb -fPIC 
LIBS     = -ldl 
TARGET   = libstat-common.a 
INCLUDES = -I.
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES)) 
SUBOBJS  = 
		   

all: $(TARGET)

$(TARGET) : $(OBJS) $(SUBOBJS)
#	$(XX) $(CFLAGS) $(OBJS) $(SUBOBJS) -o $(TARGET) $(LIBS)
	$(AR) $(TARGET) $(OBJS) $(SUBOBJS)
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
