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
first_week_day=`date -d "${date} -${n} day" +%Y%m%d`
last_week_end=`date -d "${first_week_day} -1 day" +%Y%m%d`
inputs=""
for((i=0;i<=${n};i++));
do
        last_day=`date -d "$date -$i day" +%Y%m%d`
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/basic/lgpl*
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput ${DAY_DIR}/$last_day/basic/lgpl*,com.taomee.bigdata.task.common.SourceORMapper "
	fi
done
if [[ $inputs == "" ]]; then
        echo "empty inputs"
        exit 1
fi

#task_id=15
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
	-D "stid=_lgpl_" \
	-D "ncount=0" \
        -jobName "Player Week $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
        -combinerClass  com.taomee.bigdata.task.common.SetCombiner \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -output ${WEEK_DIR}/$first_week_day/player

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Player Week Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.common.SetSumMapper \
        -combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -input ${WEEK_DIR}/$first_week_day/player/part* \
        -output ${SUM_DIR}/$first_week_day/player-week/

#$DB_UPLOAD -type 2 -date $first_week_day -task 15 -path ${SUM_DIR}/$first_week_day/player-week/part*
