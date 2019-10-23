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

bid=300
n=1
inputs=""
nday=`date -d "$date -${n} day" +%Y%m%d`
path=${DAY_DIR}/$nday/day-online/part*
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.online.UserOnlineMapper "
fi
path=${DAY_DIR}/$nday/level/part*
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.keep.LevelMapper "
fi
path=${DAY_DIR}/$nday/basic/acpay*
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.keep.SourcePayMapper "
fi
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "New ${n} day Keep Analysis $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/new-keep-${n}/part*,com.taomee.bigdata.task.keep.GrepUserMapper \
	$inputs \
	-reducerClass com.taomee.bigdata.task.keep.KeepAnalyReducer \
	-output ${DAY_DIR}/$date/new-keep-${n}-analy

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-D cntdistr=2,4,6,11,21,51 \
	-D sumdistr=11,61,181,601,1801,3601,7201,14401 \
	-jobName "New $n day Keep Analysis Sum $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.keep.KeepAnalySumMapper \
	-reducerClass com.taomee.bigdata.task.lost.LostAnalySumReducer \
	-input ${DAY_DIR}/$date/new-keep-${n}-analy/part* \
	-addMos "level,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-addMos "olsum,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-addMos "olcnt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-addMos "pay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-output ${SUM_DIR}/$date/new-keep-${n}-analy
$DB_UPLOAD -type 2 -date $nday -task `expr $bid + 0` -path ${SUM_DIR}/$date/new-keep-${n}-analy/level*
$DB_UPLOAD -type 2 -date $nday -task `expr $bid + 1` -path ${SUM_DIR}/$date/new-keep-${n}-analy/olsum*
$DB_UPLOAD -type 2 -date $nday -task `expr $bid + 2` -path ${SUM_DIR}/$date/new-keep-${n}-analy/olcnt*
$DB_UPLOAD -type 2 -date $nday -task `expr $bid + 3` -path ${SUM_DIR}/$date/new-keep-${n}-analy/pay*
