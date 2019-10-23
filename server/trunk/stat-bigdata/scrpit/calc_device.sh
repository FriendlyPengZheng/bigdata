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
	-D mapred.map.tasks=20 \
        -conf ${HADOOP_CONF} \
        -jobName "Device $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.device.DeviceMapper \
        -addInput ${DAY_DIR}/$date/basic/newac-*,com.taomee.bigdata.task.device.NewMapper \
	-addInput ${ALL_DIR}/$date/account-all/active*,com.taomee.bigdata.task.device.NewLoginMapper \
        -reducerClass com.taomee.bigdata.task.device.DeviceReducer \
        -output ${DAY_DIR}/$date/device

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Device Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.device.DeviceSumMapper \
        -combinerClass  com.taomee.bigdata.task.device.DeviceSumCombiner \
        -reducerClass com.taomee.bigdata.task.device.DeviceSumReducer \
        -input ${DAY_DIR}/$date/device/part-* \
	-addMos "ie,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "dev,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "os,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "res,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "net,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "isp,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
        -output ${SUM_DIR}/$date/device

$DB_UPLOAD -type 2 -date $date -task 116 -path ${SUM_DIR}/$date/device/ie-*
$DB_UPLOAD -type 2 -date $date -task 117 -path ${SUM_DIR}/$date/device/dev-*
$DB_UPLOAD -type 2 -date $date -task 118 -path ${SUM_DIR}/$date/device/os-*
$DB_UPLOAD -type 2 -date $date -task 119 -path ${SUM_DIR}/$date/device/res-*
$DB_UPLOAD -type 2 -date $date -task 120 -path ${SUM_DIR}/$date/device/net-*
$DB_UPLOAD -type 2 -date $date -task 121 -path ${SUM_DIR}/$date/device/isp-*
