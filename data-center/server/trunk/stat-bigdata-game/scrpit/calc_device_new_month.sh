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

month=`date -d "${date}" +%m`
next_day_month=`date -d "${date} +1 day" +%m`

year_month=`date -d "$date" +%Y%m`
last_month=`date -d "${year_month}01 -1 day" +%Y%m%d`

#task_id=4
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
	-D "stid=_startdev_" \
	-D "ncount=1" \
        -jobName "Device New Month $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/${year_month}*/basic/startdev*,com.taomee.bigdata.task.common.SourceDIF1Mapper \
	-addInput ${ALL_DIR}/$last_month/device-all/part*,com.taomee.bigdata.task.common.DIF2Mapper \
        -combinerClass  com.taomee.bigdata.task.common.SetCombiner \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -output ${MONTH_DIR}/$year_month/device-new

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Device New Month Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.common.SetSumMapper \
        -combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -input ${MONTH_DIR}/$year_month/device-new/part* \
        -output ${SUM_DIR}/$year_month/device-new-month/

#$DB_UPLOAD -type 2 -date ${year_month}01 -task 4 -path ${SUM_DIR}/$year_month/device-new-month/part*
