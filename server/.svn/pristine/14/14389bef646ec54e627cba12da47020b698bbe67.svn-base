WORKDIR=../
OBJDIR=$(WORKDIR)/obj
SRCDIR=${WORKDIR}/src
SRCS=$(shell find $(SRCDIR) -name '*.java')
OBJS=$(patsubst %.java,%.class, $(subst $(SRCDIR),$(OBJDIR),$(SRCS)))
HADOOP_PATH=/home/ping/hadoop/bin/
HADOOP_CLASSPATH=$(shell ${HADOOP_PATH}hadoop classpath)
TARGET=$(WORKDIR)/hdfs-compression.jar

all: $(OBJS)
	cd $(OBJDIR) && jar cvf $(TARGET) compression

$(OBJDIR)/%.class: $(SRCDIR)/%.java
	javac -classpath $(HADOOP_CLASSPATH):./ $< -d ../obj

clean:
	rm $(OBJS)
