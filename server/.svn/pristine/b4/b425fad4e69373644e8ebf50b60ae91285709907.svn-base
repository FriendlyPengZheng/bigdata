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
	-D mapred.map.tasks=20 \
        -conf ${HADOOP_CONF} \
        -jobName "Level $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.level.LoginMapper \
        -addInput ${DAY_DIR}/$date/basic/aclvup-*,com.taomee.bigdata.task.level.LevelupMapper \
        -combinerClass com.taomee.bigdata.task.level.LevelReducer \
        -reducerClass com.taomee.bigdata.task.level.LevelReducer \
        -output ${DAY_DIR}/$date/level

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Level Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.level.LevelSumMapper \
        -combinerClass com.taomee.bigdata.task.level.LevelSumReducer \
        -reducerClass com.taomee.bigdata.task.level.LevelSumReducer \
        -input ${DAY_DIR}/$date/level/part-* \
        -output ${SUM_DIR}/$date/level

$DB_UPLOAD -type 2 -date $date -task 122 -path ${SUM_DIR}/$date/level/part-*

yesterday=`date -d "$date -1 day" +%Y%m%d`
path=${ALL_DIR}/$yesterday/level/part-*
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.level.AllLevelMapper "
fi
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Level All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/basic/aclvup-*,com.taomee.bigdata.task.level.SourceLevelMapper \
	$inputs \
        -reducerClass com.taomee.bigdata.task.level.AllLevelReducer \
	-addMos "lvuptime,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${ALL_DIR}/$date/level

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Level Up Time $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.level.AllLevelSumMapper \
        -reducerClass com.taomee.bigdata.task.level.AllLevelSumReducer \
        -input ${ALL_DIR}/$date/level/lvuptime-* \
        -output ${SUM_DIR}/$date/lvuptime
$DB_UPLOAD -type 2 -date $date -task 330 -path ${SUM_DIR}/$date/lvuptime/part-*
#${HADOOP_PATH}hadoop fs -rm -skipTrash ${ALL_DIR}/$date/level/lvuptime-*
