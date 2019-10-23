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

yesterday=`date -d "${date} -1 day" +%Y%m%d`
no_in_week=`date -d "${date}" +%u`
n=`expr ${no_in_week} - 1`
this_monday=`date -d "${date} -${n} day" +%Y%m%d`
last_monday=`date -d "$this_monday -7 day" +%Y%m%d`

n=1  #次周流失
#task_id=32,100
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "nday=${n}" \
	-jobName "Payer ${n} Week Lost $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${WEEK_DIR}/$last_monday/payer/part*,com.taomee.bigdata.task.nday.NDay0Mapper \
	-addInput ${WEEK_DIR}/$this_monday/account/part*,com.taomee.bigdata.task.nday.NDay1Mapper \
	-reducerClass com.taomee.bigdata.task.lost.LostReducer \
	-output ${WEEK_DIR}/$this_monday/payer-lost-week

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D percent=percent \
	-conf ${HADOOP_CONF} \
	-jobName "Payer ${n} Week Lost Sum $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${WEEK_DIR}/$this_monday/payer-lost-week/part* \
	-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$this_monday/payer-lost-week

#$DB_UPLOAD -type 2 -date $this_monday -task 32  -path ${SUM_DIR}/$this_monday/payer-lost-week/part*
#$DB_UPLOAD -type 2 -date $this_monday -task 100 -path ${SUM_DIR}/$this_monday/payer-lost-week/percent*
