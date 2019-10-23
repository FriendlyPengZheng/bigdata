export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

date=$1

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi

year_month=`date -d "$date" +%Y%m`
yesterday=`date -d "$date -1 day" +%Y%m%d`

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.child.java.opts=-Xmx8000m \
        -conf ${HADOOP_CONF} \
        -jobName "Active Device $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/basic/lgac*,com.taomee.bigdata.task.device.ActiveDeviceMapper \
        -CombinerClass com.taomee.bigdata.task.device.ActiveDeviceCombiner \
        -reducerClass com.taomee.bigdata.task.device.ActiveDeviceReducer \
        -output ${DAY_DIR}/$date/active-device

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Active Device Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.device.ActiveDeviceSumMapper \
        -combinerClass  com.taomee.bigdata.task.device.DeviceSumCombiner \
        -reducerClass com.taomee.bigdata.task.device.DeviceSumReducer \
        -input ${DAY_DIR}/$date/active-device/part* \
	-addMos "ie,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "os,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
        -output ${SUM_DIR}/$date/active-device

#${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$date/active-device/part*
#$DB_UPLOAD -type 2 -date $date -task 129 -path ${SUM_DIR}/$date/active-device/ie*
#$DB_UPLOAD -type 2 -date $date -task 133 -path ${SUM_DIR}/$date/active-device/os*
