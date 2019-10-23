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
no_in_month=`date -d "${date}" +%u`
this_month=`date -d "${date}" +%Y%m`
last_month=`date -d "${this_month}01 -1 month" +%Y%m`

n=1  #次月流失
#task_id=27,95
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "nday=${n}" \
	-jobName "Newer ${n} Month Lost $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${MONTH_DIR}/$last_month/account-new/part-*,com.taomee.bigdata.task.nday.NDay0Mapper \
	-addInput ${MONTH_DIR}/$this_month/account/part-*,com.taomee.bigdata.task.nday.NDay1Mapper \
	-reducerClass com.taomee.bigdata.task.lost.LostReducer \
	-output ${MONTH_DIR}/$this_month/newer-lost-month

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D percent=percent \
	-conf ${HADOOP_CONF} \
	-jobName "Newer ${n} Month Lost Sum $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${MONTH_DIR}/$this_month/newer-lost-month/part-* \
	-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$this_month/newer-lost-month

$DB_UPLOAD -type 2 -date ${this_month}01 -task 27 -path ${SUM_DIR}/$this_month/newer-lost-month/part*
$DB_UPLOAD -type 2 -date ${this_month}01 -task 95 -path ${SUM_DIR}/$this_month/newer-lost-month/percent*
