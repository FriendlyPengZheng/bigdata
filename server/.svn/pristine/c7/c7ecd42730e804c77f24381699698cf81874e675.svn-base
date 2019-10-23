XX          = gcc
TARGET      = check-single

.PHONY: all clean c

all: $(TARGET)
c: clean

$(TARGET): check_single.c
	$(XX) -o $(TARGET) $<

clean:
	rm -fr $(TARGET)
