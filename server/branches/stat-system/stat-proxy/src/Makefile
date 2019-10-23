XX       = g++
CFLAGS   = -Wall -O0 -ggdb -Wno-non-virtual-dtor
LIBS     = -L../../stat-lib/ -L/usr/local/BerkeleyDB.5.1/lib -lstat -lpthread -lgsoap++
TARGET   = ../bin/stat_proxy
INCLUDES = -I./include -I../../stat-lib/
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES)) 
SUBOBJS  = ./route_thread/route_thread.o ./server_thread2/server_thread_impl.o ./web_thread/web_thread.o \
           ./route/route_impl.o ./client_thread2/client_thread_impl.o ./monitor_thread/monitor_thread.o \
		   ./monitor_thread/soapC.o ./monitor_thread/soapServer.o ./web_thread/shell_color_list.o \
		   ./client_ip_filter/c_client_ip_filter.o ./client_ip_filter/c_client_ip_filter_facade.o \
		   ./timer_thread/c_timer_thread.o

all: $(TARGET)

$(TARGET) : $(OBJS) $(SUBOBJS)
	$(XX) $(OBJS) $(SUBOBJS) -o $(TARGET) $(LIBS)
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
