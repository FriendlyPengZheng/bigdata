XX       = g++
CFLAGS   = -Wall -Wno-non-virtual-dtor -O -g
LIBS     =   
TARGET   = ./server_thread_test
INCLUDES = -I.
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
 
all: $(TARGET)
    
$(TARGET) : $(OBJS)
	$(XX) $(OBJS) -o $(TARGET) $(LIBS)  
	rm -rf *.o
                                                                                
%.o: %.cpp  
	$(XX) $(CFLAGS) $(INCLUDES) -c $< -o $@
    
clean:  
	rm -rf *.o $(TARGET)            
    
c: clean 

r: clean all
