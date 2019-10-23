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
this_month=`date -d "${date}" +%Y%m`
last_month=`date -d "${this_month}01 -1 month" +%Y%m`
llast_month_end=`date -d "${last_month}01 -1 day" +%Y%m%d`

#task_id=24
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "Back $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${ALL_DIR}/$llast_month_end/account-all/part*,com.taomee.bigdata.task.nday.NDay0Mapper \
	-addInput ${MONTH_DIR}/${last_month}/account/part*,com.taomee.bigdata.task.nday.NDay1Mapper \
	-addInput ${MONTH_DIR}/${this_month}/account/part*,com.taomee.bigdata.task.nday.NDay2Mapper \
	-reducerClass com.taomee.bigdata.task.back.BackReducer \
	-output ${MONTH_DIR}/$this_month/account-back

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-jobName "Back Sum $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${MONTH_DIR}/$this_month/account-back/part* \
	-output ${SUM_DIR}/$this_month/account-back

#$DB_UPLOAD -type 2 -date ${this_month}01 -task 24 -path ${SUM_DIR}/$this_month/account-back/part*
