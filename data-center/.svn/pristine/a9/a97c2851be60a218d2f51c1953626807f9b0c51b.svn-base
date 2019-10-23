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

bid=333
for n in 0 6 13
do
	nday=`date -d "$date -$n day" +%Y%m%d`
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-conf ${HADOOP_CONF} \
		-jobName "$t New Level $n day $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-addInput ${ALL_DIR}/$nday/account-all/first*,com.taomee.bigdata.task.newlevel.NewMapper \
		-addInput ${ALL_DIR}/$date/level/part*,com.taomee.bigdata.task.newlevel.LevelMapper \
		-reducerClass com.taomee.bigdata.task.newlevel.LevelReducer \
		-output ${DAY_DIR}/$date/newlevel-$n

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=2 \
		-conf ${HADOOP_CONF} \
		-jobName "New Level $n day Sum $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.LongWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass com.taomee.bigdata.task.level.LevelSumMapper \
		-combinerClass com.taomee.bigdata.task.level.LevelSumReducer \
		-reducerClass com.taomee.bigdata.task.level.LevelSumReducer \
		-input ${DAY_DIR}/$date/newlevel-$n/part* \
		-output ${SUM_DIR}/$date/newlevel-$n

#	$DB_UPLOAD -type 2 -date $nday -task $bid -path ${SUM_DIR}/$date/newlevel-$n/partG*
	bid=`expr $bid + 1`
done
