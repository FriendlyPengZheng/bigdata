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

for n in {1..8}
do
	nday=`date -d "$this_monday -${n} week" +%Y%m%d`
	#task_id=22
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "Active ${n} day Keep Week $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${WEEK_DIR}/$nday/account/part-*,com.taomee.bigdata.task.nday.NDay0Mapper \
	-addInput ${WEEK_DIR}/$this_monday/account/part-*,com.taomee.bigdata.task.nday.NDay${n}Mapper \
	-reducerClass com.taomee.bigdata.task.keep.KeepReducer \
	-output ${WEEK_DIR}/$this_monday/active-keep-${n}-week

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-jobName "Active ${n} Keep Week Sum $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${WEEK_DIR}/$this_monday/active-keep-${n}-week/part* \
	-output ${SUM_DIR}/$this_monday/active-keep-${n}-week

	$DB_UPLOAD -type 2 -date $nday -task 22 -path ${SUM_DIR}/$this_monday/active-keep-${n}-week/part* 
done
