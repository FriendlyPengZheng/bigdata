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

inputs=""
for((i=0;i<=${n};i++));
do
        last_day=`date -d "$date -$i day" +%Y%m%d`
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/day-online/part*
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput ${DAY_DIR}/$last_day/day-online/part*,com.taomee.bigdata.task.online.UserOnlineMapper "
	fi
done
if [[ $inputs == "" ]]; then
        echo "empty inputs"
        exit 1
fi

#task_id=46,47,49,106,108
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "Account Online Week $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
	-addInput ${WEEK_DIR}/$first_week_day/account/part-*,com.taomee.bigdata.task.online.GrepUserMapper \
	-reducerClass com.taomee.bigdata.task.online.UserOnlineReducer \
	-output ${WEEK_DIR}/$first_week_day/account-online
	
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D mosCountDayDistr=countdaydistr \
	-D countDayDistr=all \
	-D mosCountDistr=countdistr \
	-D countDistr=2,4,6,11,21,51,101,201 \
	-D mosLengthDistr=lengthdistr \
	-D lengthDistr=61,181,601,1801,3601,7201,14401,21601,36001,54001,72001 \
	-D mosLengthAll=lengthall \
	-D mosCountAll=countall \
	-conf ${HADOOP_CONF} \
        -jobName "Account Online Week Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.online.UserOnlineSumMapper \
        -combinerClass com.taomee.bigdata.task.online.UserOnlineSumCombiner \
        -reducerClass com.taomee.bigdata.task.online.UserOnlineSumReducer \
	-input ${WEEK_DIR}/$first_week_day/account-online/part-* \
	-addMos "countdaydistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$first_week_day/account-online-week

$DB_UPLOAD -type 2 -date $first_week_day -task 46  -path ${SUM_DIR}/$first_week_day/account-online-week/countdaydistr-*
$DB_UPLOAD -type 2 -date $first_week_day -task 47  -path ${SUM_DIR}/$first_week_day/account-online-week/countdistr-*
$DB_UPLOAD -type 2 -date $first_week_day -task 49  -path ${SUM_DIR}/$first_week_day/account-online-week/lengthdistr-*
$DB_UPLOAD -type 2 -date $first_week_day -task 106 -path ${SUM_DIR}/$first_week_day/account-online-week/lengthall-*
$DB_UPLOAD -type 2 -date $first_week_day -task 108 -path ${SUM_DIR}/$first_week_day/account-online-week/countall-*
