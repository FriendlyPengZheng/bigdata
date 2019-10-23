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
	#task_id=28,96
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "stid=_lgac_" \
	-D "nday=${n}" \
	-jobName "Active ${n} day Lost $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$nday/basic/lgac-*,com.taomee.bigdata.task.common.SourceActiveMapper \
	$inputs \
	-reducerClass com.taomee.bigdata.task.lost.LostReducer \
	-output ${DAY_DIR}/$date/active-lost-${n}

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D percent=percent \
	-conf ${HADOOP_CONF} \
	-jobName "Active ${n} day Lost Sum $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${DAY_DIR}/$date/active-lost-${n}/part-* \
	-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$date/active-lost-${n}

	$DB_UPLOAD -type 2 -date $date -task 28 -path ${SUM_DIR}/$date/active-lost-${n}/part*
	$DB_UPLOAD -type 2 -date $date -task 96 -path ${SUM_DIR}/$date/active-lost-${n}/percent*
done
