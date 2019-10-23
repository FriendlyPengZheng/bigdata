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

no_in_week=`date -d "${date}" +%u`
if [[ $no_in_week -le "4" ]]; then
	n=`expr ${no_in_week} + 2`
	first_version_week_day=`date -d "${date} -${n} day" +%Y%m%d`
else
	n=`expr ${no_in_week} - 5`
	first_version_week_day=`date -d "${date} -${n} day" +%Y%m%d`
fi
echo $first_version_week_day

inputs=""
for((i=0;i<=${n};i++));
do
        last_day=`date -d "$date -$i day" +%Y%m%d`
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/basic/newpl-*
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput ${DAY_DIR}/$last_day/basic/newpl-*,com.taomee.bigdata.task.common.SourceORMapper "
	fi
done
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

#task_id=363
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
	-D "stid=_newpl_" \
	-D "ncount=0" \
        -jobName "Player New Version Week $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
        -combinerClass  com.taomee.bigdata.task.common.SetCombiner \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
	-output ${WEEK_VERSION_DIR}/$first_version_week_day/player-new

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Player New Version Week Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.common.SetSumMapper \
        -combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
	-input ${WEEK_VERSION_DIR}/$first_version_week_day/player-new/part-* \
	-output ${SUM_DIR}/$first_version_week_day/player-new-version-week/

$DB_UPLOAD -type 2 -date $first_version_week_day -task 363 -path ${SUM_DIR}/$first_version_week_day/player-new-version-week/part*
