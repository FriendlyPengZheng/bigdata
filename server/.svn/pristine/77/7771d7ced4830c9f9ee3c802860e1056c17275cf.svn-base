#----------------------------------
# example Makefile used to generate
# binary .so file
#----------------------------------

XX			= g++
LIBS		= -L. -L ../../stat-common/ -lstat-common `pkg-config --libs protobuf` -lssl -ldl ./libstatlogger64bit.so
INCLUDES	= -I. -I ../../stat-common
FLAGS		= -std=c++0x -Wall -Wnon-virtual-dtor -DNDEBUG -O2 -fno-strict-aliasing -ggdb -shared -fPIC `pkg-config --cflags protobuf`
SOURCE		= $(wildcard *.cpp)
OBJS		= $(patsubst %.cpp, %.o, $(SOURCE))
TARGET		= ../bin/libsdkserver.so
PROTO_SRC   = $(wildcard ./proto/*.proto)

.PHONY: proto clean

all: proto $(TARGET)

proto:
	protoc --cpp_out=./ --proto_path=proto/ $(PROTO_SRC)
	$(XX) $(FLAGS) $(INCLUDES) -c ./*.cc

$(TARGET): $(OBJS)
	$(XX) $(FLAGS) *.o -o $(TARGET) $(LIBS)

%.o: %.cpp Makefile
	$(XX) $(FLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf *.o .dep.* $(TARGET) *.pb.cc *.pb.h

c: clean
