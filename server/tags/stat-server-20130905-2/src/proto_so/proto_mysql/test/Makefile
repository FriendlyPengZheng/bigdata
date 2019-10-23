XX       = g++
CFLAGS   = -Wall -ggdb
LIBS     = -L../../../../../stat-lib/ -lstat 
INCLUDES = -I../../../ -I../../../../../stat-lib/
SOURCES  = $(wildcard *.cpp)
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = $(patsubst %.cpp,%,$(SOURCES))
 
all: $(TARGET)

%: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) $(LIBS) $< -o $@

clean:  
	rm -rf *.o $(TARGET)            

test: $(TARGET)
	@export LD_LIBRARY_PATH=../../../../../stat-lib/:../../../../bin/proto-so/:$LD_LIBRARY_PATH && set +x; \
	for test in $(TARGET); do \
		./$$test;  \
	done

c: clean 

r: clean all
