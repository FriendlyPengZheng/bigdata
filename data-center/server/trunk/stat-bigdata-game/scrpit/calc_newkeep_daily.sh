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
	#task_id=19
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-conf ${HADOOP_CONF} \
		-D "stid=_lgac_" \
		-D percent=percent \
		-jobName "New ${n} day Keep $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.IntWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-addInput ${ALL_DIR}/$nday/account-all/firstLog*,com.taomee.bigdata.task.common.ORMapper \
		-addInput ${DAY_DIR}/$date/basic/lgac*,com.taomee.bigdata.task.nday.SourceNDay${n}Mapper \
		-reducerClass com.taomee.bigdata.task.keep.KeepReducer \
		-output ${DAY_DIR}/$date/new-keep-${n}

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
		-D percent=percent \
		-conf ${HADOOP_CONF} \
		-jobName "New ${n} nday Keep Sum $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
		-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
		-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
		-input ${DAY_DIR}/$date/new-keep-${n}/part* \
		-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-output ${SUM_DIR}/$date/new-keep-${n}

	$DB_UPLOAD -type 2 -date $nday -task 19 -path ${SUM_DIR}/$date/new-keep-${n}/part*
	$DB_UPLOAD -type 2 -date $nday -task 20 -path ${SUM_DIR}/$date/new-keep-${n}/percent*
done
