XX       = g++
CFLAGS   = -Wall -ggdb
LIBS     = -L../../ -L../ -lstat 
INCLUDES = -I../../
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = $(patsubst %.cpp,%,$(SOURCES))
 
all: $(TARGET)

%: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) $(LIBS) $< -o $@

clean:  
	rm -rf *.o $(TARGET)            
    
c: clean 

r: clean all
