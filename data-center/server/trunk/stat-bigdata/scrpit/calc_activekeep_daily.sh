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

for n in {1,7,14}
do
	nday=`date -d "$date -${n} day" +%Y%m%d`
	#task_id=21
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "stid=_lgac_" \
	-jobName "Active ${n} day Keep $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$nday/basic/lgac-*,com.taomee.bigdata.task.common.SourceActiveMapper \
	-addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.nday.SourceNDay${n}Mapper \
	-reducerClass com.taomee.bigdata.task.keep.KeepReducer \
	-output ${DAY_DIR}/$date/active-keep-${n}

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-jobName "Active ${n} Keep Sum $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${DAY_DIR}/$date/active-keep-${n}/part-* \
	-output ${SUM_DIR}/$date/active-keep-${n}

	$DB_UPLOAD -type 2 -date $nday -task 21 -path ${SUM_DIR}/$date/active-keep-${n}/part* 
done
