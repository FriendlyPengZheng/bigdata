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

#task_id=2
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
	-D "stid=_startdev_" \
	-D "ncount=1" \
        -jobName "Device New $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/startdev*,com.taomee.bigdata.task.common.SourceDIF1Mapper \
	-addInput ${ALL_DIR}/$yesterday/device-all/part*,com.taomee.bigdata.task.common.DIF2Mapper \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -combinerClass com.taomee.bigdata.task.common.SetCombiner \
        -output ${DAY_DIR}/$date/device-new

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Device New Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.common.SetSumMapper \
        -combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -input ${DAY_DIR}/$date/device-new/part* \
        -output ${SUM_DIR}/$date/device-new

#$DB_UPLOAD -type 2 -date $date -task 2 -path ${SUM_DIR}/$date/device-new/part*
