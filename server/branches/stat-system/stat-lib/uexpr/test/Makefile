# vim: set noexpandtab tabstop=4 softtabstop=4 shiftwidth=4:

XX       = g++
CFLAGS   = -Werror -ggdb
LIBS     = -L../../ -lstat -lreadline -ldb `mysql_config --libs`
INCLUDES = -I../../
SOURCES  = uexpr_test.cpp uexpr_cli.cpp
OBJS     = $(patsubst %.cpp,%.o,$(SOURCES))
TARGET   = $(patsubst %.cpp,%,$(SOURCES)) uexpr_test.so
 
all: $(TARGET)

%: %.cpp
	$(XX) $(CFLAGS) $(INCLUDES) $(LIBS) $< -o $@

uexpr_test.so: uexpr_test_so.cpp
	$(XX) $(CFLAGS) $(INCLUDES) -fPIC -c uexpr_test_so.cpp
	$(XX) -shared -Wl,-soname,uexpr_test.so -o uexpr_test.so uexpr_test_so.o

clean:  
	rm -rf *.o $(TARGET)            
    
c: clean 

r: clean all
