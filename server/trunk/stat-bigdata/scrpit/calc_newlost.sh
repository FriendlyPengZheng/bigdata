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

for n in 7 14
do
	nday=`date -d "$date -${n} day" +%Y%m%d`
	inputs=""
	for((i=0;i<=`expr ${n} - 1`;i++));
	do
		last_day=`date -d "$date -$i day" +%Y%m%d`
		${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/basic/lgac-*
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput ${DAY_DIR}/$last_day/basic/lgac-*,com.taomee.bigdata.task.nday.SourceNDay${n}Mapper "
		fi
	done
	#task_id=25,93
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "stid=_lgac_" \
	-D "nday=${n}" \
	-jobName "New ${n} day Lost $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${ALL_DIR}/$nday/account-all/firstLog-*,com.taomee.bigdata.task.common.ORMapper \
	$inputs \
	-reducerClass com.taomee.bigdata.task.lost.LostReducer \
	-output ${DAY_DIR}/$date/new-lost-${n}

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
	-D percent=percent \
	-conf ${HADOOP_CONF} \
	-jobName "New ${n} day Lost Sum $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${DAY_DIR}/$date/new-lost-${n}/part-* \
	-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$date/new-lost-${n}

	$DB_UPLOAD -type 2 -date $date -task 25 -path ${SUM_DIR}/$date/new-lost-${n}/part*
	$DB_UPLOAD -type 2 -date $date -task 93 -path ${SUM_DIR}/$date/new-lost-${n}/percent*
done
